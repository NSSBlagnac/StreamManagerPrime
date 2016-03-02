package com.francetelecom.orangetv.streammanager.server.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MyLoggerFormatter extends Formatter {

	private static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("dd-MM kk:mm:ss:SSS");

	// values from JDK Level
	private static final int LOG_LEVEL_FINEST = 300;
	private static final int LOG_LEVEL_FINER = 400;
	private static final int LOG_LEVEL_FINE = 500;
	private static final int LOG_LEVEL_CONFIG = 700;
	private static final int LOG_LEVEL_INFO = 800;
	private static final int LOG_LEVEL_WARNING = 900;
	private static final int LOG_LEVEL_SEVERE = 1000;

	private static final String LEVEL_FINEST = "[FINE ]";
	private static final String LEVEL_FINE = "[TRACE]";
	private static final String LEVEL_CONFIG = "[DEBUG]";
	private static final String LEVEL_INFO = "[INFO ]";
	private static final String LEVEL_WARN = "[WARN ]";
	private static final String LEVEL_ERROR = "[ERROR]";

	private static final String BRACKETS = "() ";
	private static final char SPACE = ' ';
	private static final char POINT = '.';

	@Override
	public String format(LogRecord record) {
		Throwable t = record.getThrown();
		int level = record.getLevel().intValue();
		long time = record.getMillis();

		String name = record.getLoggerName();
		name = StringHelper.getLastSplit(name, POINT);

		String method = record.getSourceMethodName();
		method = StringHelper.getLastSplit(method, POINT);

		String message = formatMessage(record);

		// Use a string buffer for better performance
		StringBuilder buf = new StringBuilder();

		buf.append(TIMESTAMP_FORMAT.format(new Date(time)));
		buf.append(" ");

		// Append a readable representation of the log level.
		switch (level) {
		case LOG_LEVEL_FINEST:
		case LOG_LEVEL_FINER:
			buf.append(LEVEL_FINEST);
			break;
		case LOG_LEVEL_FINE:
			buf.append(LEVEL_FINE);
			break;
		case LOG_LEVEL_CONFIG:
			buf.append(LEVEL_CONFIG);
			break;
		case LOG_LEVEL_INFO:
			buf.append(LEVEL_INFO);
			break;
		case LOG_LEVEL_WARNING:
			buf.append(LEVEL_WARN);
			break;
		case LOG_LEVEL_SEVERE:
			buf.append(LEVEL_ERROR);
			break;
		default:
			buf.append(SPACE);
		}
		buf.append(SPACE);

		// Append the name of the log instance if so configured
		buf.append(StringHelper.fixedLengthString(name, 30));
		buf.append(SPACE);

		buf.append(StringHelper.fixedLengthString(method, 20));
		buf.append(BRACKETS);

		// Append the message
		buf.append(message);

		// Append stack trace if not null
		if (t != null) {
			buf.append(" \n");

			java.io.StringWriter sw = new java.io.StringWriter(1024);
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			t.printStackTrace(pw);
			pw.close();
			buf.append(sw.toString());
		}

		buf.append("\n");
		// Print to the appropriate destination
		return buf.toString();
	}
}
