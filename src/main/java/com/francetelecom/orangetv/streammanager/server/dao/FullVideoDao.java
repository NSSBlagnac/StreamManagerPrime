package com.francetelecom.orangetv.streammanager.server.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.server.model.DbFullVideoFile;

public class FullVideoDao extends AbstractDao<DbFullVideoFile> implements IVideoDao {

	private static final Logger log = Logger.getLogger(FullVideoDao.class.getName());

	private static FullVideoDao instance;

	public static final FullVideoDao get() {
		if (instance == null) {
			instance = new FullVideoDao();
		}
		return instance;
	}

	// ---------------------------------------- constructor
	private FullVideoDao() {
	}

	// ------------------------------ overriding AbstractDao
	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected DbFullVideoFile buildDbEntry(ResultSet rs) {
		return this.buildDbVideoFile(rs);
	}

	// ------------------------------------------ public methods
	public List<DbFullVideoFile> listVideoFile() throws SQLException {
		return super.listEntry(SQL_SELECT_ALL_FULL_VIDEO);
	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param videoId
	 * @return
	 */
	public DbFullVideoFile getById(int videoId) {

		return super.getById(videoId, SQL_SELECT_ONE_FULL_ENTRY);

	}

	// ------------------------------------------------------ private methods

	private DbFullVideoFile buildDbVideoFile(ResultSet rs) {
		try {
			DbFullVideoFile entry = new DbFullVideoFile();

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

			entry.setAudioTracks(rs.getString(DB_AUDIO));
			entry.setSubtitleTracks(rs.getString(DB_SUBTITLE));
			entry.setTablePmt(rs.getString(DB_PMT));

			// stream attribut (left join)
			entry.setCountStream(rs.getInt(ALIAS_STREAM_COUNT_ID));
			if (entry.getCountStream() > 0) {
				entry.setSumEnabledStream(rs.getInt(ALIAS_STREAM_SUM_ENABLE));
			}

			return entry;
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
		return null;

	}

}
