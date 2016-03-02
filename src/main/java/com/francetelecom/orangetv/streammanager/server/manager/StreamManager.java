package com.francetelecom.orangetv.streammanager.server.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.server.dao.StreamDao;
import com.francetelecom.orangetv.streammanager.server.manager.AdminManager.MulticatServerInfo;
import com.francetelecom.orangetv.streammanager.server.manager.ProtectionManager.ProtectedStreamDatas;
import com.francetelecom.orangetv.streammanager.server.model.DbFullStreamEntry;
import com.francetelecom.orangetv.streammanager.server.service.IMyServices;
import com.francetelecom.orangetv.streammanager.server.util.StreamInfoValidator;
import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfoForList;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoInfo;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;
import com.francetelecom.orangetv.streammanager.shared.util.EitException;

/**
 * Manager pour tout le fonctionnel lie aux stream
 * 
 * @author ndmz2720
 *
 */
public class StreamManager implements IMyServices {

	private static final Logger log = Logger.getLogger(StreamManager.class.getName());
	private static StreamManager instance;

	public static StreamManager get() {
		if (instance == null) {
			instance = new StreamManager();
		}
		return instance;
	}

	private StreamManager() {
	}

	private ProtectedStreamDatas protectedStreamDatas;

	private Timer timer = new Timer();
	private boolean modeTest = false;

	// ----------------------------- package methods
	void init(MulticatServerInfo multicatServerInfo, ProtectedStreamDatas protectedStreamDatas, boolean modeTest) {
		this.protectedStreamDatas = protectedStreamDatas;
		this.modeTest = modeTest;
	}

	// ------------------------------------- public methods

	/**
	 * Creation de l'entree dans la table 'eit' puis update eit
	 */
	public StreamInfo createStreamInfo(StreamInfo streamInfo, EitInfoModel eitInfo, boolean sessionAtLeastQualifProfile)
			throws EitException {
		this.validStreamInfo(streamInfo, sessionAtLeastQualifProfile);

		int maxid = StreamDao.get().getMaxEitId();
		streamInfo.setId(maxid + 1);
		boolean result = StreamDao.get().createStreamInfo(streamInfo);

		if (result) {

			if (eitInfo != null) {

				eitInfo.getEitGeneral().setTarget(streamInfo.getId() + "");
				result = StreamDao.get().updateEit(eitInfo);
				if (!result) {
					this.deleteStream(streamInfo.getId(), sessionAtLeastQualifProfile);
				}

			}
		}
		return result ? streamInfo : null;
	}

	public boolean startStream(final int streamId) throws EitException {

		log.info("startStream(" + streamId + ")");
		DbFullStreamEntry entry = StreamDao.get().getById(streamId);
		if (entry == null) {
			return false;
		}

		if (StreamDao.get().enableEntry(streamId, true)) {
			// update enabled
			boolean result = StreamDao.get().updateStatus(streamId, StreamStatus.STARTING);
			if (result && !entry.isEnable()) {
				// eventuellement on reactive le stream
				StreamDao.get().enableEntry(streamId, true);
			}

			this.simutateServer(streamId, result, StreamStatus.STARTED);
			return result;
		} else {
			return false;
		}
	}

	public boolean stopStream(final int streamId) throws EitException {

		log.info("stopStream(" + streamId + ")");
		DbFullStreamEntry entry = StreamDao.get().getById(streamId);
		if (entry == null || !entry.isEnable()) {
			return false;
		}

		if (StreamDao.get().enableEntry(streamId, false)) {

			boolean result = StreamDao.get().updateStatus(streamId, StreamStatus.STOPPING);
			this.simutateServer(streamId, result, StreamStatus.STOPPED);
			return result;
		} else {
			return false;
		}
	}

	public boolean updateStreamStatus(int streamId, StreamStatus newStatus, boolean sessionAtLeastAdminProfile)
			throws EitException {

		if (!sessionAtLeastAdminProfile) {
			throw new EitException("Only admin can change stream status directly!");
		}

		boolean result = StreamDao.get().updateStatus(streamId, newStatus);
		if (result && newStatus == StreamStatus.STOPPED) {
			StreamDao.get().enableEntry(streamId, false);
		}
		return result;
	}

	public boolean updateStreamInfo(StreamInfo streamInfo, boolean sessionAtLeastQualifProfile) throws EitException {

		DbFullStreamEntry entry = StreamManager.get().validStreamInfo(streamInfo, sessionAtLeastQualifProfile);
		boolean result = StreamDao.get().updateStreamInfo(streamInfo);

		// met à jour les timestamp pour le supervisor
		if (entry != null && entry.isEitToInject() != streamInfo.isEitToInject()) {
			StreamDao.get().enableEitInjection(streamInfo.getId(), streamInfo.isEitToInject());
		}

		return result;
	}

	public StreamInfo getStreamInfo(final int streamId, boolean sessionAtLeastQualifProfile,
			boolean sessionAtLeastAdminProfile) throws EitException {

		log.config("getStreamInfo(" + streamId + ")");
		try {

			StreamInfo streamInfo = null;

			// stream existant
			if (streamId != IDto.ID_UNDEFINED) {
				DbFullStreamEntry dbEitEntry = StreamDao.get().getById(streamId);
				streamInfo = StreamInfoValidator.get().buildStreamInfo(dbEitEntry);

			} else {
				// nouveau stream
				streamInfo = new StreamInfo();
			}

			ProtectionManager.get().setProtectionOnStreamInfo(streamInfo, sessionAtLeastQualifProfile,
					sessionAtLeastAdminProfile);
			StreamInfoValidator.get().populateRangeValuesForStreamInfo(streamInfo, this.protectedStreamDatas);
			List<VideoInfo> listVideoFile = VideoManager.get().getListAvailableVideoInfo(sessionAtLeastQualifProfile);
			streamInfo.setVideoFileList(listVideoFile);

			return streamInfo;

		} catch (Exception e) {
			log.severe(e.toString());
			throw new EitException(e);
		}
	}

	public List<StreamInfoForList> getListStreamInfoWithProtection(boolean userAtLeastProfileQualif,
			boolean profileAdmin) throws EitException {

		try {
			List<DbFullStreamEntry> list = StreamDao.get().listStreamEntry();
			List<StreamInfoForList> listStreamInfo = new ArrayList<>();
			if (list != null) {

				for (DbFullStreamEntry dbStreamEntry : list) {

					StreamInfoForList streamInfo = StreamInfoValidator.get().buildStreamInfoForList(dbStreamEntry);
					ProtectionManager.get().setProtectionOnStreamInfo(streamInfo, userAtLeastProfileQualif,
							profileAdmin);
					listStreamInfo.add(streamInfo);
				}
			}

			return listStreamInfo;
		} catch (Exception e) {
			log.severe(e.toString());
			throw new EitException(e);
		}
	}

	public boolean deleteStream(int streamId, boolean sessionAtLeastQualifProfile) throws EitException {
		if (streamId == IDto.ID_UNDEFINED) {
			return false;
		}
		DbFullStreamEntry entry = StreamDao.get().getById(streamId);
		if (entry == null) {
			return false;
		}
		if (entry.isEntryProtected() && !sessionAtLeastQualifProfile) {
			throw new EitException("This stream is protected. You must have manager user profile!");
		}
		return StreamDao.get().deleteStream(streamId);
	}

	public DbFullStreamEntry validStreamInfo(StreamInfo streamInfo, boolean sessionAtLeastQualifProfile)
			throws EitException {

		if (streamInfo == null) {
			throw new EitException("Invalid datas!");
		}
		if (streamInfo.isEntryProtected() && !sessionAtLeastQualifProfile) {
			throw new EitException("This stream is protected. You must have manager user profile!");
		}

		try {

			boolean createMode = streamInfo.getId() == IDto.ID_UNDEFINED;
			DbFullStreamEntry entry = (createMode) ? null : StreamDao.get().getById(streamInfo.getId());
			StreamStatus status = (entry == null) ? null : entry.getStatus();
			boolean readOnly = !createMode && status != StreamStatus.STOPPED && status != StreamStatus.NEW
					&& status != StreamStatus.START_FAILURE;
			if (!createMode && (entry == null || readOnly)) {
				throw new EitException("stream info " + streamInfo.getId() + " doen't exists or is not stopped!");
			}

			// les règles de validation de valeurs
			StreamInfoValidator.get().validateStreamInfo(streamInfo);

			List<DbFullStreamEntry> listAllEntries = StreamDao.get().listStreamEntry();
			// les règles de validation d'unicité
			StreamInfoValidator.get().validateUnicityRules(streamInfo, listAllEntries);

			// les règles de validation par rapport aux stream protégés (plages
			// de valeur)
			StreamInfoValidator.get().validateProtectedStream(streamInfo, this.protectedStreamDatas);

			return entry;

		} catch (Exception ex) {
			throw new EitException(ex);
		}

	}

	// -------------------------------------------- private methods
	/**
	 * simule en mode test le changement de status suite au start ou au stop
	 * 
	 * @param streamId
	 * @param result
	 * @param finalStatus
	 */
	private void simutateServer(final int streamId, boolean result, final StreamStatus finalStatus) {

		// only for test
		if (this.modeTest && result) {
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					StreamDao.get().updateStatus(streamId, finalStatus);
				}
			}, 1000);

		}
	}
}
