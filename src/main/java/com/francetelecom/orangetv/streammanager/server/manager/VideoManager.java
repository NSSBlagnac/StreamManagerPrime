package com.francetelecom.orangetv.streammanager.server.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.francetelecom.orangetv.streammanager.server.dao.FullVideoDao;
import com.francetelecom.orangetv.streammanager.server.dao.VideoDao;
import com.francetelecom.orangetv.streammanager.server.manager.AdminManager.MulticatServerInfo;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SIAudioTrack;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SISubtitleTrack;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SITablePMT;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SIVideoTrack;
import com.francetelecom.orangetv.streammanager.server.model.DbFullVideoFile;
import com.francetelecom.orangetv.streammanager.server.model.DbVideoFile;
import com.francetelecom.orangetv.streammanager.server.service.IMyServices;
import com.francetelecom.orangetv.streammanager.server.util.VideoInfoValidator;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdRequest;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoStatus;
import com.francetelecom.orangetv.streammanager.shared.util.EitException;
import com.francetelecom.orangetv.streammanager.shared.util.ValueHelper;

/**
 * Manager pour tout le fonctionnel lie aux videos
 * 
 * @author ndmz2720
 *
 */
public class VideoManager implements IMyServices {

	private static final Logger log = Logger.getLogger(VideoManager.class.getName());
	private static VideoManager instance;

	public static VideoManager get() {
		if (instance == null) {
			instance = new VideoManager();
		}
		return instance;
	}

	private VideoManager() {
	}

	private MulticatServerInfo multicatServerInfo;

	// ------------------------------------- public methods
	void init(MulticatServerInfo multicatServerInfo) {
		this.multicatServerInfo = multicatServerInfo;
	}

	// --------------------------------------

	/*
	 * Suppression de la video en base et des fichiers physiques associés
	 */
	public boolean deleteVideo(int videoId, boolean sessionAtLeastQualifProfile) throws EitException {
		if (videoId == IDto.ID_UNDEFINED || this.multicatServerInfo == null) {
			return false;
		}
		DbFullVideoFile video = FullVideoDao.get().getById(videoId);
		if (video == null) {
			return false;
		}
		if (video.isEntryProtected() && !sessionAtLeastQualifProfile) {
			throw new EitException("This video is protected. You must have qualif user profile!");
		}
		VideoStatus status = video.getStatus();
		String filename = video.getName();
		boolean result = VideoDao.get().deleteVideo(videoId);

		if (result) {
			log.info("Success deleting video " + filename + " in database!");
			if (status == VideoStatus.READY) {
				CmdResponse response = AdminManager.get().deleteVideoFileAndAux(
						this.multicatServerInfo.getMulticatVideoPath(), filename);

				return response.isSuccess();
			} else {
				return result;
			}
		}
		return false;
	}

	/**
	 * Creation de l'entree dans la table 'video'
	 */
	public FullVideoInfo createVideoFile(FullVideoInfo videoInfo, boolean sessionAtLeastQualifProfile)
			throws EitException {
		this.validVideoInfo(videoInfo, sessionAtLeastQualifProfile);

		int maxid = VideoDao.get().getMaxVideoId();
		videoInfo.setId(maxid + 1);
		boolean result = VideoDao.get().createVideoInfo(videoInfo);

		return result ? videoInfo : null;

	}

	public boolean updateVideoInfo(FullVideoInfo videoInfo, boolean sessionAtLeastQualifProfile) throws EitException {

		return VideoDao.get().updateVideoInfo(videoInfo);

	}

	/**
	 * Récupération du fichier temporaire uploader depuis le client vers tomcat
	 * et enregistre
	 * dans une repertoire temporaire en attendant upload vers server multicat
	 * 
	 * @param request
	 * @return
	 */
	public String getGwtUploadedFileInCurrentSession(HttpServletRequest request) {
		Object obj = request.getSession(true).getAttribute(KEY_SESSION_GWTUPLOADED_FILE);
		return (obj == null) ? null : obj.toString();

	}

	/*
	 * Cas où un telechargement de video a ete amorce par l'utilisateur.
	 * Le fichier est en attente sur repertoire /video de tomcat pour etre uploader sur le serveur multicat.
	 * Puis le script pid&ingest doit etre lance.
	 * Puis extraction de la table PMT et mise en base de donnees
	 * Quand tout est terminé le status de la video est READY
	 */
	public void sendVideoToMulticatAndFinishProcess(int videoId, String videoPath) throws EitException {

		log.info("videoId: " + videoId + " - videoPath " + videoPath);
		if (this.multicatServerInfo == null) {
			return;
		}
		VideoDao.get().updateStatus(videoId, VideoStatus.PENDING);
		CmdResponse response = null;

		try {
			// =============================================================
			// copier le fichier depuis tomcat vers repertoire temporaire du
			// server de qualif
			// =============================================================
			response = AdminManager.get().copyFileToRemoteWithLinuxScp(videoPath,
					this.multicatServerInfo.getMulticatVideoPath());
			if (response.isSuccess()) {
				log.config("success in uploading video " + videoPath + " to "
						+ this.multicatServerInfo.getMulticatVideoPath() + "!");
			} else {
				throw new EitException("Error in uploading video " + videoPath + " to "
						+ this.multicatServerInfo.getMulticatVideoPath() + "!");
			}

			// =============================================================
			// lancer le script pour la création fichier aux
			// =============================================================
			String filename = new File(videoPath).getName();
			response = AdminManager.get().createAuxVideoFileWithIngest(this.multicatServerInfo.getMulticatVideoPath(),
					filename);
			if (response.isSuccess()) {

				log.config("Success in generating aux file for " + filename + "!");

			} else {
				throw new EitException("Error in generating aux file for " + filename + "!");
			}

			// =============================================================
			// extraction de la table PMT et mise en base de donnees
			// =============================================================
			response = AdminManager.get().extractPmtTableFromVideofile(this.multicatServerInfo.getMulticatVideoPath(),
					filename);
			if (response.isSuccess()) {
				log.config("Success in extrating PMT table from " + filename + "!");
				this.updateVideoWithPmtTable(videoId, response);
			} else {
				throw new EitException("Error in extrating PMT table from " + filename + "!");
			}

			// update status
			VideoDao.get().updateStatus(videoId, VideoStatus.READY);

		} catch (EitException ex) {
			VideoDao.get().updateStatus(videoId, VideoStatus.ERROR);
			throw ex;
		}
	}

	void updateVideoWithPmtTable(int videoId, CmdResponse response) {

		final StringBuilder sb = new StringBuilder();
		for (String line : response.getResponseLines()) {
			sb.append(line).append("\n");
		}

		try {
			SITablePMT tablePMT = SIStreamManager.get().readTablePMT(sb.toString());

			final Character separator = ',';
			// garantir unicité language
			List<String> audioTracksDb = new ArrayList<>();
			List<String> subtitleTracksDb = new ArrayList<>();

			StringBuilder pmtInfos = new StringBuilder();
			if (tablePMT != null) {

				List<SIVideoTrack> videoTracks = tablePMT.getListVideoTracks();
				if (videoTracks != null) {
					for (SIVideoTrack siVideoTrack : videoTracks) {
						pmtInfos.append("video: ");
						pmtInfos.append(siVideoTrack);
						pmtInfos.append("\n");
					}
				}

				List<SIAudioTrack> audioTracks = tablePMT.getListAudioTracks();
				if (audioTracks != null) {
					for (SIAudioTrack siAudioTrack : audioTracks) {
						pmtInfos.append("audio: ");
						pmtInfos.append(siAudioTrack);
						pmtInfos.append("\n");

						String lang = siAudioTrack.getLanguage();
						lang += (siAudioTrack.isDolby()) ? " dolby" : "";
						if (!audioTracksDb.contains(lang)) {
							audioTracksDb.add(lang);
						}
					}
				}

				List<SISubtitleTrack> subtitleTracks = tablePMT.getListSubtitleTracks();
				if (subtitleTracks != null) {
					for (SISubtitleTrack siSubtitleTrack : subtitleTracks) {
						pmtInfos.append("subtitle: ");
						pmtInfos.append(siSubtitleTrack);
						pmtInfos.append("\n");

						String lang = siSubtitleTrack.getLanguage();
						lang += ((siSubtitleTrack.isHardOfHearing()) ? " hoh" : "");
						if (!subtitleTracksDb.contains(lang)) {
							subtitleTracksDb.add(lang);
						}
					}
				}

				String audioDbToSave = ValueHelper.buildStringFromList(audioTracksDb, separator);
				log.config("audio " + audioDbToSave);
				String subtitleDbToSave = ValueHelper.buildStringFromList(subtitleTracksDb, separator);
				log.config("subtitle " + subtitleDbToSave);

				// sauvegarde en base
				VideoDao.get().updatePmtInfos(videoId, audioDbToSave, subtitleDbToSave, pmtInfos.toString());
			}
		} catch (EitException e) {
			log.severe("Error in updateVideoWithPmtTable() " + e.getMessage());
		}
	}

	public FullVideoInfo getVideoInfo(int videoId, boolean sessionAtLeastQualifProfile) throws EitException {

		FullVideoInfo videoInfo = null;

		// video existante
		if (videoId != IDto.ID_UNDEFINED) {
			DbFullVideoFile videoFile = FullVideoDao.get().getById(videoId);
			videoInfo = VideoInfoValidator.get().buildFullVideoInfo(videoFile);

		} else {
			// nouvelle video
			videoInfo = new FullVideoInfo();
		}

		ProtectionManager.get().setProtectionOnVideoInfo(videoInfo, sessionAtLeastQualifProfile);

		return videoInfo;
	}

	public List<FullVideoInfo> getListFullVideoInfo(boolean userAtLeastProfileQualif, boolean withProtection)
			throws EitException {

		try {
			List<DbFullVideoFile> list = FullVideoDao.get().listVideoFile();
			List<FullVideoInfo> listVideoInfo = new ArrayList<>();
			if (list != null) {
				for (DbFullVideoFile videoFile : list) {

					FullVideoInfo videoInfo = VideoInfoValidator.get().buildFullVideoInfo(videoFile);
					if (withProtection) {
						ProtectionManager.get().setProtectionOnVideoInfo(videoInfo, userAtLeastProfileQualif);
					}
					listVideoInfo.add(videoInfo);

				}
			}
			return listVideoInfo;
		} catch (Exception e) {
			log.severe(e.toString());
			throw new EitException(e);
		}
	}

	public List<VideoInfo> getListAvailableVideoInfo(boolean userAtLeastProfileQualif) throws EitException {

		try {
			List<DbVideoFile> list = VideoDao.get().listAvailableVideoFile();
			List<VideoInfo> listVideoInfo = new ArrayList<>();
			if (list != null) {
				for (DbVideoFile videoFile : list) {

					VideoInfo videoInfo = VideoInfoValidator.get().buildVideoInfo(videoFile);
					listVideoInfo.add(videoInfo);

				}
			}
			return listVideoInfo;
		} catch (Exception e) {
			log.severe(e.toString());
			throw new EitException(e);
		}
	}

	public DbFullVideoFile validVideoInfo(FullVideoInfo videoInfo, boolean sessionAtLeastQualifProfile)
			throws EitException {

		try {

			if (videoInfo == null) {
				throw new EitException("Invalid datas!");
			}
			if (videoInfo.isEntryProtected() && !sessionAtLeastQualifProfile) {
				throw new EitException("This video is protected. You must have qualif user profile!");
			}

			boolean createMode = videoInfo.getId() == IDto.ID_UNDEFINED;
			DbFullVideoFile entry = (createMode) ? null : FullVideoDao.get().getById(videoInfo.getId());
			VideoStatus status = (entry == null) ? null : entry.getStatus();
			boolean readOnly = false; // TODO a etudier
			if (!createMode && (entry == null || readOnly)) {
				throw new EitException("video info " + videoInfo.getId() + " doen't exists!");
			}

			// les règles de validation de valeurs
			VideoInfoValidator.get().validateVideoInfo(videoInfo);

			List<DbVideoFile> listAllEntries = VideoDao.get().listVideoFile();
			// les règles de validation d'unicité
			VideoInfoValidator.get().validateUnicityRules(videoInfo, listAllEntries);

			return entry;
		} catch (Exception ex) {
			throw new EitException(ex);
		}
	}

}
