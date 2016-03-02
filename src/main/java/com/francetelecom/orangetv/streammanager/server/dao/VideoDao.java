package com.francetelecom.orangetv.streammanager.server.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.server.model.DbVideoFile;
import com.francetelecom.orangetv.streammanager.shared.dto.AbstractVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoStatus;

public class VideoDao extends AbstractDao<DbVideoFile> implements IVideoDao {

	private static final Logger log = Logger.getLogger(VideoDao.class.getName());

	private static VideoDao instance;

	public static final VideoDao get() {
		if (instance == null) {
			instance = new VideoDao();
		}
		return instance;
	}

	// ---------------------------------------- constructor
	private VideoDao() {
	}

	// ------------------------------ overriding AbstractDao
	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected DbVideoFile buildDbEntry(ResultSet rs) {
		return this.buildDbVideoFile(rs);
	}

	// ----------------------------------------- public methods
	// list des video READY && enabled
	public List<DbVideoFile> listAvailableVideoFile() throws SQLException {
		return super.listEntry(SQL_SELECT_AVAILABLE_VIDEO);
	}

	public List<DbVideoFile> listVideoFile() throws SQLException {
		return super.listEntry(SQL_SELECT_VIDEO);
	}

	public int getMaxVideoId() {

		return super.getMaxId(SQL_SELECT_MAX_VIDEO_ID);
	}

	/**
	 * Met à jour le status d'une video
	 * 
	 * @param videdoId
	 * @param status
	 * @return
	 */
	public boolean updateStatus(int videoId, VideoStatus status) {

		if (videoId < 0 || status == null) {
			return false;
		}

		String sql = SQL_UPDATE_STATUS.format(new Object[] { status.name(), videoId });
		return this.update(sql);
	}

	public boolean updateVideoInfo(AbstractVideoInfo videoInfo) {

		return this.createOrUpdateVideoInfo(videoInfo, SQL_UPDATE_VIDEO_INFO);
	}

	public boolean updatePmtInfos(int videoId, String audioTracks, String subtitleTracks, String tablePmt) {

		String sql = SQL_UPDATE_PMT_INFOS.format(new Object[] { audioTracks, subtitleTracks, tablePmt, videoId });
		return this.update(sql);
	}

	public boolean createVideoInfo(AbstractVideoInfo videoInfo) {

		return this.createOrUpdateVideoInfo(videoInfo, SQL_CREATE_VIDEO_INFO);
	}

	public boolean deleteVideo(int videoId) {

		return super.deleteEntry(videoId, SQL_DELETE_ONE_ENTRY);

	}

	// ---------------------------------------------------------- private
	// methods

	private boolean createOrUpdateVideoInfo(AbstractVideoInfo videoInfo, MessageFormat sqlToFormat) {
		if (videoInfo == null) {
			return false;
		}

		boolean result = true;

		try {

			String name = this.formatSqlString(videoInfo.getName());
			String description = this.formatSqlString(videoInfo.getDescription());
			String resolution = this.formatSqlString(videoInfo.getResolution());
			String format = this.formatSqlString(videoInfo.getFormat());

			//
			// Date date = new Date();
			// String timestamp = Long.toString(date.getTime() / 1000);
			// String strDate = TIMESTAMP_FORMAT.format(date);

			String sql = sqlToFormat.format(new Object[] { videoInfo.getId(), name, description, resolution, format,
					videoInfo.isEnabled(), videoInfo.isOcs(), videoInfo.isCsa5(), videoInfo.isColor(),
					videoInfo.isEntryProtected(), videoInfo.getId() });

			result = this.update(sql);

		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	// ------------------------------------ private

	private DbVideoFile buildDbVideoFile(ResultSet rs) {
		try {
			DbVideoFile entry = new DbVideoFile();

			entry.setId(rs.getInt(DB_VIDEOID));
			entry.setName(rs.getString(DB_FILENAME));
			entry.setDescription(rs.getString(DB_DESC));
			entry.setResolution(rs.getString(DB_RESOLUTION));
			entry.setFormat(rs.getString(DB_FORMAT));
			entry.setColor(rs.getBoolean(DB_COLOR));
			entry.setCsa5(rs.getBoolean(DB_CSA5));
			entry.setEnabled(rs.getBoolean(DB_ENABLED));
			entry.setOcs(rs.getBoolean(DB_OCS));
			entry.setEntryProtected(rs.getBoolean(DB_PROTECTED));
			entry.setStatus(rs.getString(DB_STATUS));

			return entry;
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
		return null;

	}

}
