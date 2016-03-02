package com.francetelecom.orangetv.streammanager.shared.util;

public interface IEitJsonParser {

	public static final String KEY_EIT_GENERAL = "eitGeneral";
	public static final String KEY_EIT_SECTION_PRESENT = "eitPresentSection";
	public static final String KEY_EIT_SECTION_FOLLOWING = "eitFollowingSection";
	public static final String KEY_EIT_EVENTS = "eitEvents";

	public static final String KEY_PARENTAL_RATING_DESCRIPTOR = "PARENTAL_RATING_DESCRIPTOR";
	public static final String KEY_CONTENT_DESCRIPTOR = "CONTENT_DESCRIPTOR";
	public static final String KEY_CAIDENTIFIER_DESCRIPTOR = "CA_IDENTIFIER_DESCRIPTOR";
	public static final String KEY_SHORT_EVENT_DESCRIPTOR = "SHORT_EVENT_DESCRIPTOR";
	public static final String KEY_EXTENDED_EVENT_DESCRIPTOR = "EXTENDED_EVENT_DESCRIPTOR";
	public static final String KEY_COMPONENT_DESCRIPTORS = "COMPONENT_DESCRIPTOR";
	public static final String KEY_SHORT_SMOOTHING_BUFFER_DESCRIPTOR = "SHORT_SMOOTHING_BUFFER_DESCRIPTOR";
	public static final String KEY_PRIVATE_DESCRIPTOR = "PRIVATE_DESCRIPTOR";

	public static final String KEY_GENERAL_VERSION = "version";
	public static final String KEY_GENERAL_TARGET = "target";

	public static final String KEY_CONTENT_CAT_LEVEL1 = "content_nibble_level_1";
	public static final String KEY_CONTENT_CAT_LEVEL2 = "content_nibble_level_2";

	public static final String KEY_PARENTAL_RATING = "age";
	public static final String KEY_COUNTRY_CODE = "country_code";
	public static final String KEY_SB_LEAK_RATING = "sb_leak_rate";

	public static final String KEY_STREAM_CONTENT = "stream_content";
	public static final String KEY_COMPONENT_TYPE = "component_type";
	public static final String KEY_COMPONENT_TAG = "set_component_tag";

	public static final String KEY_NAME = "name";
	public static final String KEY_TEXT = "text";
	public static final String KEY_LANG = "lang";
	public static final String KEY_ITEMS = "items";

	public static final String KEY_EVENT_NAME = "event_name";
	public static final String KEY_EVENT_TEXT = "event_text";
	public static final String KEY_EVENT_LANG = "event_lang";

	public static final String KEY_PRIVATE_TAG = "tag";
	public static final String KEY_PRIVATE_TOKEN = "token";

	public static final String KEY_DESC = "desc";
}
