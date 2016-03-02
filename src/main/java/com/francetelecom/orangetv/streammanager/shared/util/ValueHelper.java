package com.francetelecom.orangetv.streammanager.shared.util;

import java.util.ArrayList;
import java.util.List;

public class ValueHelper {

	public static int[] reverseArray(int[] items) {

		if (items == null) {
			return null;
		}

		int length = items.length;
		int[] reversedItems = new int[length];
		for (int i = 0; i < length; i++) {
			reversedItems[length - (i + 1)] = items[i];
		}
		return reversedItems;
	}

	public static int getIntValue(String value, int defaultValue) {

		int intValue = defaultValue;
		try {
			intValue = Integer.parseInt(value);
		} catch (NumberFormatException ignored) {
			// do nothing
		}

		return intValue;
	}

	public static boolean isStringEmptyOrNull(String value) {
		return value == null || value.trim().length() == 0;
	}

	public static String tabToString(String[] items) {
		StringBuilder sb = new StringBuilder();
		if (items != null) {
			sb.append("[");
			for (String item : items) {
				sb.append(item);
				sb.append(", ");
			}
			sb.append("]");
		}

		return sb.toString();
	}

	public static List<String> buildListItems(String items) {
		if (items == null) {
			return new ArrayList<>(0);
		}
		String[] tabItems = items.split(",");

		List<String> listItems = new ArrayList<>(tabItems.length);
		for (String item : tabItems) {
			if (item != null) {
				listItems.add(item.trim());
			}
		}
		return listItems;
	}

	public static String buildStringFromList(List<String> list, Character separator) {

		if (list == null || separator == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i < list.size() - 1) {
				sb.append(separator);
			}

		}

		return sb.toString();
	}

}
