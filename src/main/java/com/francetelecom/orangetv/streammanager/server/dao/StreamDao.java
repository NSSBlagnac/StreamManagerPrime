package com.francetelecom.orangetv.streammanager.server.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.server.model.DbFullStreamEntry;
import com.francetelecom.orangetv.streammanager.server.util.EitJsonParser;
import com.francetelecom.orangetv.streammanager.server.util.StreamInfoValidator;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;
import com.francetelecom.orangetv.streammanager.shared.dto.TripletDvb;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel.EitGeneral;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel.EitSection;

public class StreamDao extends AbstractDao<DbFullStreamEntry> implements IStreamDao {

	private static final Logger log = Logger.getLogger(StreamDao.class.getName());

	private static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

	private static StreamDao instance;

	public static final StreamDao get() {
		if (instance == null) {
			instance = new StreamDao();
		}
		return instance;
	}

	// -------------------------------------- constructor
	private StreamDao() {
	}

	// -------------------------- implementing AbstractDao
	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected DbFullStreamEntry buildDbEntry(ResultSet rs) {
		return this.buildDbStreamEntry(rs);
	}

	// ----------------------------- public methods

	/**
	 * Récupère toutes les entrées de la table eit
	 * 
	 * @return
	 */
	// public List<DbStreamEntry> listStreamEntry() throws SQLException {
	//
	// return super.listEntry(SQL_SELECT_ALL_ENTRY);
	// }

	public List<DbFullStreamEntry> listStreamEntry() throws SQLException {

		return super.listEntry(SQL_SELECT_FULL_STREAM);
	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param streamId
	 * @return
	 */
	public DbFullStreamEntry getById(int streamId) {

		return super.getById(streamId, SQL_SELECT_ONE_ENTRY);

	}

	public boolean deleteStream(int streamId) {

		return super.deleteEntry(streamId, SQL_DELETE_ONE_ENTRY);

	}

	/**
	 * Met à jour le status d'une entrée de la table
	 * 
	 * @param streamId
	 * @param status
	 * @return
	 */
	public boolean updateStatus(int streamId, StreamStatus status) {

		if (streamId < 0 || status == null) {
			return false;
		}

		String sql = SQL_UPDATE_STATUS.format(new Object[] { status.name(), streamId });
		return this.update(sql);
	}

	/**
	 * Active ou désactive un stream, met à jour le timestamp
	 * 
	 * @param streamId
	 * @param enable
	 * @return
	 */
	public boolean enableEntry(int streamId, boolean enable) {

		if (streamId < 0) {
			return false;
		}
		String timestamp = TIMESTAMP_FORMAT.format(new Date());
		String sql = SQL_UPDATE_ENABLE.format(new Object[] { enable, timestamp, streamId });
		return this.update(sql);
	}

	/**
	 * Active ou désactive l'injection des eits, met à jour le timestamp
	 * 
	 * @param streamId
	 * @param enable
	 * @return
	 */
	public boolean enableEitInjection(int streamId, boolean eitToInject) {

		if (streamId < 0) {
			return false;
		}
		String timestamp = TIMESTAMP_FORMAT.format(new Date());
		String sql = SQL_UPDATE_EIT_TO_INJECT.format(new Object[] { eitToInject, timestamp, streamId });
		return this.update(sql);
	}

	public boolean createStreamInfo(StreamInfo streamInfo) {

		return this.createOrUpdateStreamInfo(streamInfo, SQL_CREATE_STREAM_INFO);
	}

	public boolean updateStreamInfo(StreamInfo streamInfo) {

		return this.createOrUpdateStreamInfo(streamInfo, SQL_UPDATE_STREAM_INFO);
	}

	private boolean createOrUpdateStreamInfo(StreamInfo streamInfo, MessageFormat sqlToFormat) {
		if (streamInfo == null) {
			return false;
		}

		boolean result = true;

		try {
			TripletDvb tripletDvb = StreamInfoValidator.get().validateTripletDVB(streamInfo.getTripletDvd());

			String name = this.formatSqlString(streamInfo.getName());
			String description = this.formatSqlString(streamInfo.getDescription());
			String user = this.formatSqlString(streamInfo.getUser());

			Date date = new Date();
			String timestamp = Long.toString(date.getTime() / 1000);
			String strDate = TIMESTAMP_FORMAT.format(date);

			String sql = sqlToFormat.format(new Object[] { streamInfo.getId(), streamInfo.getLcn(),
					streamInfo.getUsi(), name, description, user, streamInfo.getVideoFilename(),
					streamInfo.getVideoId(), streamInfo.isEnable(), streamInfo.getAddress(), streamInfo.getPort(),
					tripletDvb.getTsid(), tripletDvb.getSid(), tripletDvb.getOnid(), timestamp, strDate,
					streamInfo.isEntryProtected(), streamInfo.isEitToInject(), streamInfo.getId() });

			result = this.update(sql);

		} catch (Exception ex) {
			result = false;
		}
		return result;
	}

	public int getMaxEitId() {

		return super.getMaxId(SQL_SELECT_MAX_EITID);
	}

	/**
	 * Mise à jour des eit dans la base pour une entry donnée,
	 * met à jour le timestamp
	 * 
	 * @param eitId
	 * @param eitInfo
	 * @return
	 */
	public boolean updateEit(EitInfoModel eitInfo) {

		if (eitInfo == null) {
			return false;
		}

		EitGeneral eitGeneral = eitInfo.getEitGeneral();
		String target = eitGeneral.getTarget();

		int eitId = -1;
		try {
			eitId = Integer.parseInt(target);
		} catch (NumberFormatException ex) {
			return false;
		}

		EitSection presentSection = eitInfo.getPresentSection();
		String strPresentEvent = null;
		if (presentSection != null && presentSection.getListEvents() != null
				&& presentSection.getListEvents().size() == 1) {
			strPresentEvent = EitJsonParser.buildJson(presentSection.getListEvents().get(0)).toString();
		} else {
			return false;
		}

		EitSection followingSection = eitInfo.getFollowingSection();
		String strFollowingEvent = null;
		if (followingSection != null && followingSection.getListEvents() != null
				&& followingSection.getListEvents().size() == 1) {
			strFollowingEvent = EitJsonParser.buildJson(followingSection.getListEvents().get(0)).toString();
		} else {
			return false;
		}

		Date date = new Date();
		String timestamp = Long.toString(date.getTime() / 1000);
		String strDate = TIMESTAMP_FORMAT.format(date);
		String sql = SQL_UPDATE_EIT
				.format(new Object[] { strPresentEvent, strFollowingEvent, timestamp, strDate, eitId });

		return this.update(sql);
	}

	// ------------------------------------------------------ private methods

	private DbFullStreamEntry buildDbStreamEntry(ResultSet rs) {

		try {
			DbFullStreamEntry entry = new DbFullStreamEntry();
			entry.setId(rs.getInt(DB_STREAMID));
			entry.setLcn(rs.getInt(DB_LCN));
			entry.setUsi(rs.getInt(DB_USI));
			entry.setName(rs.getString(DB_NAME));
			entry.setDescription(rs.getString(DB_DESCRIPTION));
			entry.setUser(rs.getString(DB_USER));
			String presentSection = rs.getString(DB_PRESENT_SECTION);
			entry.setPresentEvent(presentSection);
			entry.setFollowingEvent(rs.getString(DB_FOLLOWING_SECTION));
			entry.setEnable(rs.getBoolean(DB_ENABLED));
			// entry.setVideoFile(rs.getString(DB_VIDEO_FILE));
			entry.setVideoId(rs.getInt(DB_VIDEO_ID));
			entry.setAddress(rs.getString(DB_ADDRESS));
			entry.setPort(rs.getInt(DB_PORT));
			boolean entryProtected = rs.getBoolean(DB_PROTECTED);
			entry.setEntryProtected(entryProtected);
			boolean eitToInject = rs.getBoolean(DB_EIT_TO_INJECT);
			entry.setEitToInject(eitToInject);

			int tsid = rs.getInt(DB_TSID);
			int sid = rs.getInt(DB_SID);
			int onid = rs.getInt(DB_ONID);
			TripletDvb tripletDvb = new TripletDvb(tsid, sid, onid);
			entry.setTripletDvb(tripletDvb);

			entry.setStatus(rs.getString(DB_STATUS));

			// info issues de la table video
			entry.setVideoEnabled(rs.getBoolean(ALIAS_VIDEO_ENABLED));
			entry.setVideoFilename(rs.getString(ALIAS_VIDEO_FILENAME));
			String videoStatus = rs.getString(ALIAS_VIDEO_STATUS);
			entry.setVideoStatus(videoStatus);

			return entry;
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
		return null;
	}

}
