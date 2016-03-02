package com.francetelecom.orangetv.streammanager.server.dao;

import java.text.MessageFormat;

import com.francetelecom.orangetv.streammanager.shared.dto.VideoStatus;

public interface IVideoDao extends IDao {

	public static final String V = "v";
	public static final String TABLE_NAME = "video";
	public static final String TABLE_ALIAS = TABLE_NAME + AS + V + " ";

	public static final String DB_VIDEOID = "idvideo";

	public static final String DB_FILENAME = "filename";

	public static final String DB_COLOR = "color";
	public static final String DB_RESOLUTION = "resolution";
	public static final String DB_OCS = "ocs";
	public static final String DB_CSA5 = "csa5";
	public static final String DB_FORMAT = "format";
	public static final String DB_PROTECTED = "protected";

	public static final String DB_ENABLED = "enabled";

	public static final String DB_DESC = "description";
	public static final String DB_STATUS = "status";

	public static final String DB_AUDIO = "audio";
	public static final String DB_SUBTITLE = "subtitle";
	public static final String DB_PMT = "pmt";

	// alias pour table video

	// =============================================
	// SIMPLE VIDEO
	// =============================================
	public final static String VIDEO_ATTRIBUTS = DB_VIDEOID + ", " + DB_FILENAME + ", " + DB_DESC + ", "
			+ DB_RESOLUTION + ", " + DB_FORMAT + ", " + DB_ENABLED + ", " + DB_OCS + ", " + DB_CSA5 + ", " + DB_COLOR
			+ ", " + DB_PROTECTED + ", " + DB_STATUS + " ";

	public final static String _SELECT_VIDEO = SELECT + VIDEO_ATTRIBUTS + FROM + TABLE_NAME + " ";

	public final static String SQL_SELECT_VIDEO = _SELECT_VIDEO + ORDER_BY + DB_FILENAME;

	// video available ( UPLOADED || USED || RUNNING )
	public final static String SQL_SELECT_AVAILABLE_VIDEO = _SELECT_VIDEO + WHERE + DB_ENABLED + " = true " + AND
			+ DB_STATUS + " = '" + VideoStatus.READY + "' " + ORDER_BY + DB_FILENAME;

	// =============================================
	// FULL VIDEO
	// =============================================
	public static final String ALIAS_STREAM_COUNT_ID = "countStreamId";
	public static final String ALIAS_STREAM_SUM_ENABLE = "sumStreamEnabled";

	public static final String VP = V + ".";
	public final static String VIDEO_ALIAS_ATTRIBUTS = VP + DB_VIDEOID + ", " + VP + DB_FILENAME + ", " + VP + DB_COLOR
			+ ", " + VP + DB_RESOLUTION + ", " + VP + DB_OCS + ", " + VP + DB_CSA5 + ", " + VP + DB_FORMAT + ", " + VP
			+ DB_PROTECTED + ", " + VP + DB_ENABLED + ", " + VP + DB_DESC + ", " + VP + DB_STATUS + ", " + VP
			+ DB_AUDIO + ", " + VP + DB_SUBTITLE + ", " + VP + DB_PMT + " ";

	// count(s.ideit), sum(s.enable)
	public final static String STREAM_ALIAS_ATTRIBUTS = COUNT + "(" + IStreamDao.SP + IStreamDao.DB_STREAMID + ") "
			+ AS + ALIAS_STREAM_COUNT_ID + ", " + SUM + "(" + IStreamDao.SP + IStreamDao.DB_ENABLED + ") " + AS
			+ ALIAS_STREAM_SUM_ENABLE + " ";

	// (video as v left join eit as s on v.idvideo = s.videoid)
	public static final String LEFT_JOIN_STREAM = "(" + TABLE_ALIAS + LEFT_JOIN + IStreamDao.TABLE_ALIAS + ON + VP
			+ DB_VIDEOID + "=" + IStreamDao.SP + IStreamDao.DB_VIDEO_ID + ") ";

	public final static String _SELECT_FULL_VIDEO = SELECT + VIDEO_ALIAS_ATTRIBUTS + ", " + STREAM_ALIAS_ATTRIBUTS
			+ FROM + LEFT_JOIN_STREAM + " ";

	public final static String SQL_SELECT_ALL_FULL_VIDEO = _SELECT_FULL_VIDEO + GROUP_BY + VP + DB_FILENAME + " "
			+ ORDER_BY + VP + DB_FILENAME;

	public final static MessageFormat SQL_SELECT_ONE_FULL_ENTRY = new MessageFormat(_SELECT_FULL_VIDEO + WHERE + VP
			+ DB_VIDEOID + " = {0, number, ###} " + GROUP_BY + VP + DB_FILENAME + " ");

	// =============================================
	// DIVERS
	// =============================================

	public final static MessageFormat SQL_CREATE_VIDEO_INFO = new MessageFormat(INSERT + TABLE_NAME + " ("
			+ VIDEO_ATTRIBUTS

			+ ") " + VALUES + "("
			+ "{0, number, ####} , ''{1}'', ''{2}'', ''{3}'', ''{4}'', {5}, {6}, {7},{8}, {9}, ''" + VideoStatus.NEW
			+ "'');");

	public final static MessageFormat SQL_UPDATE_STATUS = new MessageFormat(UPDATE + TABLE_NAME + " " + SET + DB_STATUS
			+ " = ''{0}'' " + WHERE + DB_VIDEOID + " = {1};");

	public final static MessageFormat SQL_UPDATE_PMT_INFOS = new MessageFormat(UPDATE + TABLE_NAME + " " + SET
			+ DB_AUDIO + " = ''{0}'', " + DB_SUBTITLE + " = ''{1}'', " + DB_PMT + " = ''{2}'' " + WHERE + DB_VIDEOID
			+ " = {3};");

	public final static MessageFormat SQL_UPDATE_VIDEO_INFO = new MessageFormat(UPDATE + TABLE_NAME + " " + SET
			+ DB_VIDEOID + " = {0, number, ####}, " + DB_FILENAME + " = ''{1}'', " + DB_DESC + " = ''{2}'', "
			+ DB_RESOLUTION + " = ''{3}'', " + DB_FORMAT + " = ''{4}'', " + DB_ENABLED + " = {5}, " + DB_OCS
			+ " = {6}, " + DB_CSA5 + " = {7}, " + DB_COLOR + " = {8}, "

			+ DB_PROTECTED + " = {9} " + WHERE + DB_VIDEOID + " = {10, number, ###};");

	public final static MessageFormat SQL_DELETE_ONE_ENTRY = new MessageFormat(DELETE + FROM + TABLE_NAME + " " + WHERE
			+ DB_VIDEOID + " = {0, number, ###};");

	public final static String SQL_SELECT_MAX_VIDEO_ID = SELECT + MAX + "(" + DB_VIDEOID + ") " + FROM + TABLE_NAME
			+ ";";

}
