package com.francetelecom.orangetv.streammanager.server.util;

import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.server.model.DbFullVideoFile;
import com.francetelecom.orangetv.streammanager.server.model.DbVideoFile;
import com.francetelecom.orangetv.streammanager.shared.dto.AbstractVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoInfo;
import com.francetelecom.orangetv.streammanager.shared.util.EitException;

public class VideoInfoValidator extends AbstractValidator {

	private static final Logger log = Logger.getLogger(VideoInfoValidator.class.getName());
	private static VideoInfoValidator instance;

	public static VideoInfoValidator get() {
		if (instance == null) {
			instance = new VideoInfoValidator();
		}
		return instance;
	}

	private VideoInfoValidator() {
	}

	public void validateVideoInfo(AbstractVideoInfo videoInfo) throws EitException {

		// les règles de validation de valeurs

		// name length > 3i
		validateString(videoInfo.getName(), 3, "name");
		// format length > 3
		validateString(videoInfo.getFormat(), 3, "format");
		// resolution length > 1
		validateString(videoInfo.getResolution(), 1, "resolution");
	}

	public FullVideoInfo buildFullVideoInfo(DbFullVideoFile videoFile) {

		FullVideoInfo videoInfo = new FullVideoInfo();
		populateVideoInfo(videoInfo, videoFile);

		// attribut calculés issus de table stream
		videoInfo.setUsedByStream(videoFile.getCountStream() > 0);
		if (videoInfo.isUsedByStream()) {
			videoInfo.setAtLastOneStreamEnabled(videoFile.getSumEnabledStream() > 0);
		}

		videoInfo.setAudioTracks(videoFile.getAudioTracks());
		videoInfo.setSubtitleTracks(videoFile.getSubtitleTracks());
		videoInfo.setTablePmt(videoFile.getTablePmt());

		return videoInfo;
	}

	public VideoInfo buildVideoInfo(DbVideoFile videoFile) {

		VideoInfo videoInfo = new VideoInfo();
		populateVideoInfo(videoInfo, videoFile);

		return videoInfo;
	}

	private void populateVideoInfo(AbstractVideoInfo videoInfo, DbVideoFile videoFile) {
		if (videoFile != null) {
			videoInfo.setId(videoFile.getId());
			videoInfo.setName(videoFile.getName());
			videoInfo.setDescription(videoFile.getDescription());
			videoInfo.setFormat(videoFile.getFormat());
			videoInfo.setResolution(videoFile.getResolution());
			videoInfo.setEnabled(videoFile.isEnabled());
			videoInfo.setEntryProtected(videoFile.isEntryProtected());
			videoInfo.setOcs(videoFile.isOcs());
			videoInfo.setCsa5(videoFile.isCsa5());
			videoInfo.setColor(videoFile.isColor());
			videoInfo.setStatus(videoFile.getStatus());

			videoInfo.setUploadPending(false);

		}
	}

	public void validateUnicityRules(AbstractVideoInfo videoInfo, List<DbVideoFile> listAllEntries) throws EitException {

		if (listAllEntries != null) {

			for (DbVideoFile videoFile : listAllEntries) {
				if (videoFile.getId() == videoInfo.getId()) {
					continue; // next entry
				}
				// name unique
				if (videoFile.getName().equals(videoInfo.getName())) {
					throw new EitException("Name " + videoInfo.getName() + " is already in use!");
				}
			}

		}

	}

}
