package com.francetelecom.orangetv.streammanager.server.dao;

import java.text.MessageFormat;

public interface IStreamDao extends IDao {

	public static final String S = "s";
	public static final String SP = S + ".";

	public static final String TABLE_NAME = "eit";
	public static final String TABLE_ALIAS = TABLE_NAME + AS + S + " ";

	public static final String DB_STREAMID = "ideit";
	public static final String DB_LCN = "lcn";
	public static final String DB_USI = "usi";
	public static final String DB_NAME = "name";
	public static final String DB_DESCRIPTION = "description";

	public static final String DB_USER = "user1"; // pb avec user
	public static final String DB_PRESENT_SECTION = "section_0";
	public static final String DB_FOLLOWING_SECTION = "section_1";
	public static final String DB_VIDEO_FILE = "videofile";

	public static final String DB_VIDEO_ID = "videoid";

	public static final String DB_ENABLED = "enable";

	public static final String DB_ADDRESS = "address";
	public static final String DB_PORT = "port";

	public static final String DB_TSID = "tsid";
	public static final String DB_SID = "sid";
	public static final String DB_ONID = "onid";

	public static final String DB_STATUS = "status";

	public static final String DB_TIMESTAMP = "eit_ts";
	public static final String DB_LASTUPDATE = "lastUpdate";
	public static final String DB_PROTECTED = "protected";

	public static final String DB_EIT_TO_INJECT = "to_inject";

	// alias pour table video
	public static final String ALIAS_VIDEO_ENABLED = "videoEnabled";
	public static final String ALIAS_VIDEO_FILENAME = "videoFilename";
	public static final String ALIAS_VIDEO_STATUS = "videoStatus";

	// =============================
	// CREATE
	// =============================

	public final static String STREAM_ATTRIBUTS = DB_STREAMID + ", " + DB_LCN + ", " + DB_USI + ", " + DB_NAME + ", "
			+ DB_DESCRIPTION + ", " + DB_USER + ", " + DB_VIDEO_FILE + ", " + DB_VIDEO_ID + ", " + DB_ENABLED + ", "
			+ DB_ADDRESS + ", " + DB_PORT + ", " + DB_TSID + ", " + DB_SID + ", " + DB_ONID + ", " + DB_TIMESTAMP
			+ ", " + DB_LASTUPDATE + ", " + DB_PROTECTED + ", " + DB_EIT_TO_INJECT;

	public final static MessageFormat SQL_CREATE_STREAM_INFO = new MessageFormat(
			INSERT
					+ TABLE_NAME
					+ " ("
					+ STREAM_ATTRIBUTS
					+ ") "
					+ VALUES
					+ "("
					+ "{0, number, ####} , {1, number, ####}, {2, number, ####}, ''{3}'', ''{4}'', ''{5}'', ''{6}'', {7, number, ###}, "
					+ "{8}, ''{9}'', {10, number,####}, {11, number,####}, {12, number,##}, {13, number,##}, {14}, ''{15}'', {16}, {17});");

	// =============================
	// UPDATE
	// =============================

	public final static MessageFormat SQL_UPDATE_STREAM_INFO = new MessageFormat(UPDATE + TABLE_NAME + " " + SET
			+ DB_STREAMID + " = {0, number, ####}, " + DB_LCN + " = {1, number, ####}, " + DB_USI
			+ " = {2, number, ####}, " + DB_NAME + " = ''{3}'', " + DB_DESCRIPTION + " = ''{4}'', " + DB_USER
			+ " = ''{5}'', " + DB_VIDEO_FILE + " = ''{6}'', " + DB_VIDEO_ID + " = {7, number, ###}, " + DB_ENABLED
			+ " = {8}, " + DB_ADDRESS + " = ''{9}'', " + DB_PORT + " = {10, number,####}, " + DB_TSID
			+ " = {11,number, ##}, " + DB_SID + " = {12, number, ##}, " + DB_ONID + " = {13, number, ##}, "
			+ DB_TIMESTAMP + " = {14}, " + DB_LASTUPDATE + " = ''{15}'', " + DB_PROTECTED + " = {16}, "
			+ DB_EIT_TO_INJECT + " = {17} " + WHERE + DB_STREAMID + " = {18, number, ###};");

	public final static MessageFormat SQL_UPDATE_STATUS = new MessageFormat(UPDATE + TABLE_NAME + " " + SET + DB_STATUS
			+ " = ''{0}'' " + WHERE + DB_STREAMID + " = {1};");

	public final static MessageFormat SQL_UPDATE_ENABLE = new MessageFormat(UPDATE + TABLE_NAME + " " + SET
			+ DB_ENABLED + " = {0} , " + DB_LASTUPDATE + " = ''{1}'' " + WHERE + DB_STREAMID + " = {2};");

	public final static MessageFormat SQL_UPDATE_EIT = new MessageFormat(UPDATE + TABLE_NAME + " " + SET
			+ DB_PRESENT_SECTION + " = ''{0}\'', " + DB_FOLLOWING_SECTION + " = ''{1}'', " + DB_TIMESTAMP + " = {2}, "
			+ DB_LASTUPDATE + " = ''{3}'' " + WHERE + DB_STREAMID + " = {4};");

	public final static MessageFormat SQL_UPDATE_EIT_TO_INJECT = new MessageFormat(UPDATE + TABLE_ALIAS + " " + SET
			+ DB_EIT_TO_INJECT + " = {0} , " + DB_LASTUPDATE + " = ''{1}'' " + WHERE + DB_STREAMID + " = {2};");

	// =============================
	// COUNT
	// =============================

	public final static String COUNT_WiTH_VIDEO = SELECT + COUNT_ALL + FROM + TABLE_ALIAS + " " + WHERE + DB_VIDEO_ID
			+ " = {0, number, ####}";

	public final static MessageFormat SQL_COUNT_WiTH_VIDEO = new MessageFormat(COUNT_WiTH_VIDEO + ";");
	public final static MessageFormat SQL_COUNT_ENABLED_WiTH_VIDEO = new MessageFormat(COUNT_WiTH_VIDEO + " " + AND
			+ DB_ENABLED + " = true;");

	// ===================================
	// FULL STREAM
	// ===================================

	// (eit as s left join video as v on v.idvideo = s.videoid)
	public static final String LEFT_JOIN_VIDEO = "(" + TABLE_ALIAS + LEFT_JOIN + IVideoDao.TABLE_ALIAS + ON
			+ IVideoDao.VP + IVideoDao.DB_VIDEOID + "=" + SP + DB_VIDEO_ID + ") ";

	public final static String STREAM_ALIAS_ATTRIBUTS = SP + DB_STREAMID + ", " + SP + DB_LCN + ", " + SP + DB_USI
			+ ", " + SP + DB_NAME + ", " + SP + DB_DESCRIPTION + ", " + SP + DB_USER + ", " + SP + DB_VIDEO_FILE + ", "
			+ SP + DB_VIDEO_ID + ", " + SP + DB_ENABLED + ", " + SP + DB_ADDRESS + ", " + SP + DB_PORT + ", " + SP
			+ DB_TSID + ", " + SP + DB_SID + ", " + SP + DB_ONID + ", " + SP + DB_TIMESTAMP + ", " + SP + DB_LASTUPDATE
			+ ", " + SP + DB_PROTECTED + ", " + SP + DB_EIT_TO_INJECT + ", " + SP + DB_PRESENT_SECTION + ", " + SP
			+ DB_FOLLOWING_SECTION + ", " + SP + DB_STATUS;

	public final static String VIDEO_ALIAS_ATTRIBUTES = IVideoDao.VP + IVideoDao.DB_ENABLED + AS + ALIAS_VIDEO_ENABLED
			+ ", " + IVideoDao.VP + IVideoDao.DB_STATUS + AS + ALIAS_VIDEO_STATUS + ", " + IVideoDao.VP
			+ IVideoDao.DB_FILENAME + AS + ALIAS_VIDEO_FILENAME;

	// jointure table eit X video
	public final static String SQL_SELECT_FULL_STREAM = SELECT + STREAM_ALIAS_ATTRIBUTS + ", " + VIDEO_ALIAS_ATTRIBUTES
			+ " " + FROM + LEFT_JOIN_VIDEO;

	public final static MessageFormat SQL_SELECT_ONE_ENTRY = new MessageFormat(SQL_SELECT_FULL_STREAM + " " + WHERE
			+ DB_STREAMID + " = {0, number, ###}");

	// ===================================
	// DIVERS
	// ===================================

	public final static String SQL_SELECT_MAX_EITID = SELECT + MAX + "(" + DB_STREAMID + ") " + FROM + TABLE_NAME;

	public final static MessageFormat SQL_DELETE_ONE_ENTRY = new MessageFormat(DELETE + FROM + TABLE_NAME + " " + WHERE
			+ DB_STREAMID + " = {0, number, ###};");

}
