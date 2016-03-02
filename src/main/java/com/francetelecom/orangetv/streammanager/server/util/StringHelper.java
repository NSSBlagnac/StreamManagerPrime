package com.francetelecom.orangetv.streammanager.server.util;

import java.util.List;

public class StringHelper {

	public static String getLastSplit(String value, char separator) {

		if (value == null) {
			return null;
		}
		if (value.indexOf(".") >= 0) {
			return value.substring(value.lastIndexOf(".") + 1);
		}
		return value;
	}

	public static String fixedLengthString(String value, int length) {

		if (value != null && value.length() >= length) {
			return value.substring(value.length() - length);
		}
		return String.format("%1$" + length + "s", value);
	}

	public static boolean listContains(List<String> list, String valueToFind) {

		if (list == null || valueToFind == null) {
			return false;
		}
		for (String item : list) {
			if (item != null && item.equals(valueToFind)) {
				return true;
			}
		}
		return false;
	}
}
