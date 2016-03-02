package com.francetelecom.orangetv.streammanager.server.util;

import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import com.francetelecom.orangetv.streammanager.server.model.DbFullStreamEntry;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel.EitGeneral;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel.EitSection;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractComponentDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractComponentDescriptor.StreamContent;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractComponentDescriptor.Type;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.CAIdentifierDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.CAIdentifierDescriptor.CASystemId;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ComponentDescriptorAspectRatio;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ComponentDescriptorAudio;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ComponentDescriptorSubtitle;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentNibbleLevel2;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ExtendedEventDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ParentalRatingDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ParentalRatingDescriptor.ParentalRating;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.PrivateDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.PrivateDescriptor.PrivateToken;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortEventDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortSmoothingBufferDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.divers.LabelAndDescription;
import com.francetelecom.orangetv.streammanager.shared.util.IEitJsonParser;

/**
 * Mapping json String >> EitInfoModel (server side)
 * 
 * @author sylvie
 * 
 */
public class EitJsonParser implements IEitJsonParser {

	private static final Logger log = Logger.getLogger(EitJsonParser.class.getName());

	public static JsonObject buildJson(EitInfoModel eitInfo) {

		return Json.createObjectBuilder().add(KEY_EIT_GENERAL, buildJson(eitInfo.getEitGeneral()))
				.add(KEY_EIT_SECTION_PRESENT, buildJson(eitInfo.getPresentSection()))
				.add(KEY_EIT_SECTION_FOLLOWING, buildJson(eitInfo.getFollowingSection()))

				.build();
	}

	public static EitInfoModel parse(DbFullStreamEntry dbEitEntry) {

		if (dbEitEntry == null) {
			return null;
		}

		EitInfoModel eitInfo = new EitInfoModel();
		EitGeneral eitGeneral = new EitGeneral(EitInfoModel.VERSION, dbEitEntry.getId() + "");
		eitInfo.setEitGeneral(eitGeneral);
		eitInfo.setPresentSection(new EitSection());
		eitInfo.setFollowingSection(new EitSection());

		String jsonPresentEvent = dbEitEntry.getPresentEvent();
		String jsonFollowingEvent = dbEitEntry.getFollowingEvent();

		EitEvent presentEitEvent = parseEitEvent(jsonPresentEvent);
		EitEvent followingEitEvent = parseEitEvent(jsonFollowingEvent);

		eitInfo.getPresentSection().addEitEvent(presentEitEvent);
		eitInfo.getFollowingSection().addEitEvent(followingEitEvent);

		return eitInfo;

	}

	private static EitEvent parseEitEvent(String jsonEvent) {

		EitEvent eitEvent = null;
		JsonReader jsonReader = Json.createReader(new StringReader(jsonEvent));
		JsonObject jsonObj = jsonReader.readObject();
		if (jsonObj != null) {

			eitEvent = parseEitEvent(jsonObj);
		}
		return eitEvent;
	}

	public static EitInfoModel parse(String json) {

		EitInfoModel eitInfo = null;
		// try {
		JsonReader jsonReader = Json.createReader(new StringReader(json));
		JsonObject jsonObj = jsonReader.readObject();
		if (jsonObj != null) {

			eitInfo = new EitInfoModel();
			EitGeneral eitGeneral = parseEitGeneral(jsonObj.get(KEY_EIT_GENERAL));
			if (eitGeneral != null && eitGeneral.getVersion() != null
					&& eitGeneral.getVersion().equals(EitInfoModel.VERSION)) {
				eitInfo.setEitGeneral(eitGeneral);
				eitInfo.setPresentSection(parseEitSection(jsonObj.get(KEY_EIT_SECTION_PRESENT)));
				eitInfo.setFollowingSection(parseEitSection(jsonObj.get(KEY_EIT_SECTION_FOLLOWING)));
			}
		} else {
			log.severe("format json incompatible!!");
			return null;
		}

		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }

		return eitInfo;
	}

	// ---------------------------------------------------------- private
	// methods

	// --------------------
	// --- EitGeneral ---
	// --------------------
	private static JsonObject buildJson(EitGeneral general) {
		return Json.createObjectBuilder().add(KEY_GENERAL_VERSION, general.getVersion())
				.add(KEY_GENERAL_TARGET, general.getTarget()).build();

	}

	private static EitGeneral parseEitGeneral(JsonValue jsonValue) {
		JsonObject jsonObj = transformJsonValueToJsonObject(jsonValue);
		EitGeneral eitGeneral = new EitGeneral(getStringValue(jsonObj, KEY_GENERAL_VERSION), getStringValue(jsonObj,
				KEY_GENERAL_TARGET));

		if (eitGeneral == null || !EitInfoModel.VERSION.equals(eitGeneral.getVersion())) {
			return null;
		}
		return eitGeneral;
	}

	// --------------------
	// --- EitSection ---
	// --------------------
	private static JsonObject buildJson(EitSection section) {
		JsonObjectBuilder objBuilder = Json.createObjectBuilder();

		List<EitEvent> listEitEvents = section.getListEvents();

		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		if (listEitEvents != null) {

			for (int i = 0; i < listEitEvents.size(); i++) {

				EitEvent eitEvent = listEitEvents.get(i);
				arrayBuilder.add(buildJson(eitEvent));
			}
		}
		objBuilder.add(KEY_EIT_EVENTS, arrayBuilder.build());

		return objBuilder.build();
	}

	private static EitSection parseEitSection(JsonValue jsonValue) {
		JsonObject jsonObj = transformJsonValueToJsonObject(jsonValue);
		JsonValue valueEvents = (jsonObj == null) ? null : jsonObj.get(KEY_EIT_EVENTS);

		JsonArray arrayEvents = transformJsonValueToJsonArray(valueEvents);

		EitSection eitSection = new EitSection();

		if (arrayEvents != null) {

			for (JsonValue eventValue : arrayEvents) {
				eitSection.addEitEvent(parseEitEvent(eventValue));
			}
		}

		return eitSection;

	}

	// --------------------
	// --- EitEvent ---
	// --------------------
	public static JsonObject buildJson(EitEvent eitEvent) {
		JsonObjectBuilder objBuilder = Json.createObjectBuilder();

		addDescriptor(objBuilder, buildJson(eitEvent.getShortEventDescriptor()), KEY_SHORT_EVENT_DESCRIPTOR);
		addDescriptor(objBuilder, buildJson(eitEvent.getExtendedEventDescriptor()), KEY_EXTENDED_EVENT_DESCRIPTOR);
		addDescriptor(objBuilder, buildJson(eitEvent.getContentDescriptor()), KEY_CONTENT_DESCRIPTOR);
		addDescriptor(objBuilder, buildJson(eitEvent.getParentalRatingDescriptor()), KEY_PARENTAL_RATING_DESCRIPTOR);
		addDescriptor(objBuilder, buildJson(eitEvent.getCaIdentifierDescriptor()), KEY_CAIDENTIFIER_DESCRIPTOR);
		addDescriptor(objBuilder, buildJson(eitEvent.getShortSmoothingBufferDescriptor()),
				KEY_SHORT_SMOOTHING_BUFFER_DESCRIPTOR);
		addDescriptor(objBuilder, buildJson(eitEvent.getPrivateDescriptor()), KEY_PRIVATE_DESCRIPTOR);

		List<AbstractComponentDescriptor> listComponentDescriptors = eitEvent.getListComponentDescriptors();

		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		boolean hasItems = false;
		if (listComponentDescriptors != null) {
			for (int i = 0; i < listComponentDescriptors.size(); i++) {
				if (addDescriptor(arrayBuilder, buildJson(listComponentDescriptors.get(i)))) {
					hasItems = true;
				}
			}
		}
		if (hasItems) {
			objBuilder.add(KEY_COMPONENT_DESCRIPTORS, arrayBuilder.build());
		}

		return objBuilder.build();
	}

	private static boolean addDescriptor(JsonObjectBuilder objBuilder, JsonObject jsonObject, String key) {
		if (jsonObject != null) {
			objBuilder.add(key, jsonObject);
			return true;
		}
		return false;
	}

	private static boolean addDescriptor(JsonObjectBuilder objBuilder, JsonValue jsonValue, String key) {
		if (jsonValue != null) {
			objBuilder.add(key, jsonValue);
			return true;
		}
		return false;
	}

	private static boolean addDescriptor(JsonArrayBuilder objBuilder, JsonObject jsonObject) {
		if (jsonObject != null) {
			objBuilder.add(jsonObject);
			return true;
		}
		return false;
	}

	private static EitEvent parseEitEvent(JsonValue jsonValue) {
		JsonObject jsonObj = transformJsonValueToJsonObject(jsonValue);

		EitEvent eitEvent = new EitEvent();

		ShortEventDescriptor shortEventDescriptor = parseShortEventDescriptor(jsonObj.get(KEY_SHORT_EVENT_DESCRIPTOR));
		if (shortEventDescriptor.isEnabled()) {
			eitEvent.setShortEventDescriptor(shortEventDescriptor);
		}

		ExtendedEventDescriptor extendedEventDescriptor = parseExtendedEventDescriptor(jsonObj
				.get(KEY_EXTENDED_EVENT_DESCRIPTOR));
		if (extendedEventDescriptor.isEnabled()) {
			eitEvent.setExtendedEventDescriptor(extendedEventDescriptor);
		}

		ContentDescriptor contentDescriptor = parseContentDescriptor(jsonObj.get(KEY_CONTENT_DESCRIPTOR));
		if (contentDescriptor.isEnabled()) {
			eitEvent.setContentDescriptor(contentDescriptor);
		}

		ParentalRatingDescriptor parentalRatingDescriptor = parseParentalRatingDescriptor(jsonObj
				.get(KEY_PARENTAL_RATING_DESCRIPTOR));
		if (parentalRatingDescriptor.isEnabled()) {
			eitEvent.setParentalRatingDescriptor(parentalRatingDescriptor);
		}

		CAIdentifierDescriptor caIdentifierDescriptor = parseCAIdentifierDescriptor(jsonObj
				.get(KEY_CAIDENTIFIER_DESCRIPTOR));
		if (caIdentifierDescriptor.isEnabled()) {
			eitEvent.setCAIdentifierDescriptor(caIdentifierDescriptor);
		}

		ShortSmoothingBufferDescriptor shortSmoothingBufferDescriptor = parseShortSmoothingBufferDescriptor(jsonObj
				.get(KEY_SHORT_SMOOTHING_BUFFER_DESCRIPTOR));
		if (shortSmoothingBufferDescriptor.isEnabled()) {
			eitEvent.setShortSmoothingBufferDescriptor(shortSmoothingBufferDescriptor);
		}

		PrivateDescriptor privateDescriptor = parsePrivateDescriptor(jsonObj.get(KEY_PRIVATE_DESCRIPTOR));
		if (privateDescriptor.isEnabled()) {
			eitEvent.setPrivateDescriptor(privateDescriptor);
		}

		JsonValue value = jsonObj.get(KEY_COMPONENT_DESCRIPTORS);
		JsonArray array = transformJsonValueToJsonArray(value);

		if (array != null) {

			for (int i = 0; i < array.size(); i++) {

				AbstractComponentDescriptor descriptor = parseComponentDescriptor(array.get(i));
				if (descriptor != null) {
					eitEvent.addComponentDescriptor(descriptor);
				}
			}
		}

		return eitEvent;

	}

	// -----------------------------
	// --- LabelAndDescription ---
	// -----------------------------
	private static JsonObject buildJson(LabelAndDescription labelAndDescription) {
		return Json.createObjectBuilder()
				.add(KEY_TEXT, (labelAndDescription == null) ? "" : labelAndDescription.getText())
				.add(KEY_DESC, (labelAndDescription == null) ? "" : labelAndDescription.getDescription()).build();
	}

	private static LabelAndDescription parseLabelAndDescription(JsonValue jsonValue) {

		JsonObject jsonObj = transformJsonValueToJsonObject(jsonValue);
		return new LabelAndDescription(getStringValue(jsonObj, KEY_TEXT), getStringValue(jsonObj, KEY_DESC));
	}

	// -----------------------------
	// --- ShortEventDescriptor ---
	// -----------------------------
	private static JsonObject buildJson(ShortEventDescriptor shortEventDescriptor) {

		if (shortEventDescriptor == null || !shortEventDescriptor.isEnabled()) {
			return null;
		}
		return Json.createObjectBuilder().add(KEY_EVENT_NAME, shortEventDescriptor.getName())
				.add(KEY_EVENT_TEXT, shortEventDescriptor.getText())
				.add(KEY_EVENT_LANG, shortEventDescriptor.getLang().getCode())

				.build();
	}

	private static ShortEventDescriptor parseShortEventDescriptor(JsonValue jsonValue) {

		JsonObject jsonObj = transformJsonValueToJsonObject(jsonValue);
		return new ShortEventDescriptor(

		getStringValue(jsonObj, KEY_EVENT_NAME), getStringValue(jsonObj, KEY_EVENT_TEXT), getStringValue(jsonObj,
				KEY_EVENT_LANG));

	}

	// --------------------------------
	// --- ExtendedEventDescriptor ---
	// --------------------------------
	private static JsonObject buildJson(ExtendedEventDescriptor extendedEventDescriptor) {

		if (extendedEventDescriptor == null || !extendedEventDescriptor.isEnabled()) {
			return null;
		}
		JsonObjectBuilder objBuilder = Json.createObjectBuilder().add(KEY_TEXT, extendedEventDescriptor.getText())
				.add(KEY_LANG, extendedEventDescriptor.getLang().getCode());

		JsonArrayBuilder arrayItems = Json.createArrayBuilder();

		// Json.createArrayBuilder().add(buildJson(extendedEventDescriptor.getDirector()))
		// .add(buildJson(extendedEventDescriptor.getYear())).add(buildJson(extendedEventDescriptor.getRating()))
		// .add(buildJson(extendedEventDescriptor.getWriters()))
		// .add(buildJson(extendedEventDescriptor.getStars()));

		objBuilder.add(KEY_ITEMS, arrayItems.build());
		return objBuilder.build();
	}

	private static ExtendedEventDescriptor parseExtendedEventDescriptor(JsonValue jsonValue) {

		JsonObject jsonObj = transformJsonValueToJsonObject(jsonValue);
		ExtendedEventDescriptor extendedEventDescriptor = new ExtendedEventDescriptor(
				getStringValue(jsonObj, KEY_TEXT), getStringValue(jsonObj, KEY_LANG));

		// JsonValue valueItems = (jsonObj == null) ? null :
		// jsonObj.get(KEY_ITEMS);
		// JsonArray arrayItems = transformJsonValueToJsonArray(valueItems);
		//
		// if (arrayItems != null) {
		//
		// for (int i = 0; i < arrayItems.size(); i++) {
		//
		// JsonValue item = arrayItems.get(i);
		// LabelAndDescription labelAndDescription =
		// parseLabelAndDescription(item);
		// String text = labelAndDescription.getText();
		// if (ValueHelper.isStringEmptyOrNull(text)) {
		// continue;
		// }
		//
		// try {
		// LABEL label = LABEL.valueOf(text);
		// switch (label) {
		// case director:
		// extendedEventDescriptor.setDirector(labelAndDescription);
		// break;
		// case rating:
		// extendedEventDescriptor.setRating(labelAndDescription);
		// break;
		// case stars:
		// extendedEventDescriptor.setStars(labelAndDescription);
		// break;
		// case writers:
		// extendedEventDescriptor.setWriters(labelAndDescription);
		// break;
		// case year:
		// extendedEventDescriptor.setYear(labelAndDescription);
		// break;
		// }
		// } finally {
		// // do nothing
		// }
		//
		// }
		// }

		return extendedEventDescriptor;
	}

	// --------------------------------
	// --- ContentDescriptor ---
	// --------------------------------
	private static JsonValue buildJson(ContentDescriptor contentDescriptor) {

		if (contentDescriptor == null || !contentDescriptor.isEnabled()) {
			return null;
		}
		List<ContentNibbleLevel2> listCategories = contentDescriptor.getistCategories();
		JsonArrayBuilder arrayCategories = Json.createArrayBuilder();

		if (listCategories != null) {
			for (int i = 0; i < listCategories.size(); i++) {
				arrayCategories.add(buildJson(listCategories.get(i)));
			}
		}

		return arrayCategories.build();
	}

	private static JsonObject buildJson(ContentNibbleLevel2 category) {

		return Json.createObjectBuilder()

		.add(KEY_CONTENT_CAT_LEVEL1, category.getParent().getCode()).add(KEY_CONTENT_CAT_LEVEL2, category.getCode())
				.build();
	}

	private static ContentDescriptor parseContentDescriptor(JsonValue jsonValue) {
		JsonArray arrayCategories = transformJsonValueToJsonArray(jsonValue);

		ContentDescriptor contentDescriptor = new ContentDescriptor();

		if (arrayCategories != null) {

			for (int i = 0; i < arrayCategories.size(); i++) {

				JsonValue vCat = arrayCategories.get(i);
				JsonObject vObj = transformJsonValueToJsonObject(vCat);
				if (vObj != null) {
					String parentCode = getStringValue(vObj, KEY_CONTENT_CAT_LEVEL1);
					String categoryCode = getStringValue(vObj, KEY_CONTENT_CAT_LEVEL2);
					contentDescriptor.addCategory(parentCode, categoryCode);
				}
			}
		}

		return contentDescriptor;

	}

	// --------------------------------
	// --- ComponentDescriptor ---
	// --------------------------------
	private static JsonObject buildJson(AbstractComponentDescriptor componentDescriptor) {

		if (componentDescriptor == null || !componentDescriptor.isEnabled()) {
			return null;
		}
		return Json.createObjectBuilder().add(KEY_STREAM_CONTENT, componentDescriptor.getStreamContent().getCode())
				.add(KEY_COMPONENT_TYPE, componentDescriptor.getComponentType().getCode()).add(KEY_COMPONENT_TAG, 46)
				.add(KEY_TEXT, componentDescriptor.getText()).add(KEY_LANG, componentDescriptor.getLang().getCode())

				.build();
	}

	private static AbstractComponentDescriptor parseComponentDescriptor(JsonValue jsonValue) {

		JsonObject jsonObj = transformJsonValueToJsonObject(jsonValue);
		String langCode = getStringValue(jsonObj, KEY_LANG);
		String streamContentCode = getStringValue(jsonObj, KEY_STREAM_CONTENT);
		String componentTypeCode = getStringValue(jsonObj, KEY_COMPONENT_TYPE);

		StreamContent streamContent = StreamContent.get(streamContentCode);

		Type type = streamContent.getType();

		AbstractComponentDescriptor componentDescriptor = null;
		switch (type) {
		case AUDIO:
			boolean dolby = streamContent == StreamContent.AUDIO_AC3;
			boolean dts = streamContent == StreamContent.AUDIO_DTS;
			componentDescriptor = new ComponentDescriptorAudio(langCode, componentTypeCode, dolby, dts);
			break;
		case SUBTITLE:
			componentDescriptor = new ComponentDescriptorSubtitle(langCode, componentTypeCode);
			break;
		case ASPECT_RATIO:
			log.fine("parseComponentDescriptor() - ASPECT_RATIO - StreamContent: " + streamContent);
			componentDescriptor = new ComponentDescriptorAspectRatio(langCode, streamContent, componentTypeCode);
			break;

		}

		return componentDescriptor;
	}

	// ---------------------------------
	// --- ParentalRatingDescriptor ---
	// ---------------------------------
	private static JsonObject buildJson(ParentalRatingDescriptor parentalRatingDescriptor) {

		if (parentalRatingDescriptor == null || !parentalRatingDescriptor.isEnabled()) {
			return null;
		}

		return Json.createObjectBuilder().add(KEY_COUNTRY_CODE, parentalRatingDescriptor.getCountry().getCode())
				.add(KEY_PARENTAL_RATING, parentalRatingDescriptor.getParentalRating().getRating())

				.build();
	}

	private static ParentalRatingDescriptor parseParentalRatingDescriptor(JsonValue jsonValue) {

		JsonObject jsonObj = transformJsonValueToJsonObject(jsonValue);
		String countryCode = getStringValue(jsonObj, KEY_COUNTRY_CODE);
		int parentalRatingValue = getIntValue(jsonObj, KEY_PARENTAL_RATING);

		return new ParentalRatingDescriptor(new ParentalRating(parentalRatingValue), countryCode);

	}

	// ---------------------------------
	// --- CAIdentifierDescriptor --
	// ---------------------------------
	private static JsonValue buildJson(CAIdentifierDescriptor caIdentifierDescriptor) {

		if (caIdentifierDescriptor == null || !caIdentifierDescriptor.isEnabled()) {
			return null;
		}

		List<CASystemId> list = caIdentifierDescriptor.getListSystemIds();
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		if (list != null) {

			for (int i = 0; i < list.size(); i++) {
				arrayBuilder.add(list.get(i).getId());
			}

		}

		return arrayBuilder.build();
	}

	private static CAIdentifierDescriptor parseCAIdentifierDescriptor(JsonValue jsonValue) {
		JsonArray array = transformJsonValueToJsonArray(jsonValue);

		CAIdentifierDescriptor caIdentifierDescriptor = new CAIdentifierDescriptor();

		if (array != null) {
			for (int i = 0; i < array.size(); i++) {

				caIdentifierDescriptor.addSystemId(new CASystemId(getIntValue(array.get(i))));
			}
		}

		return caIdentifierDescriptor;
	}

	// ---------------------------------
	// --- PrivateDescriptor --
	// ---------------------------------
	private static JsonValue buildJson(PrivateDescriptor privateDescriptor) {

		if (privateDescriptor == null || !privateDescriptor.isEnabled()) {
			return null;
		}

		List<PrivateToken> list = privateDescriptor.getListPrivateTokens();
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		if (list != null) {

			for (int i = 0; i < list.size(); i++) {
				PrivateToken privateToken = list.get(i);
				arrayBuilder.add(buildJson(privateToken));
			}

		}

		return arrayBuilder.build();
	}

	private static PrivateDescriptor parsePrivateDescriptor(JsonValue jsonValue) {
		JsonArray array = transformJsonValueToJsonArray(jsonValue);

		PrivateDescriptor privateDescriptor = new PrivateDescriptor();

		if (array != null) {
			for (int i = 0; i < array.size(); i++) {

				PrivateToken privateToken = parsePrivateToken(array.get(i));
				privateDescriptor.addPrivateToken(privateToken);
			}
		}

		return privateDescriptor;
	}

	private static PrivateToken parsePrivateToken(JsonValue jsonValue) {

		JsonObject jsonObj = transformJsonValueToJsonObject(jsonValue);
		return new PrivateToken(getStringValue(jsonObj, KEY_PRIVATE_TAG), getStringValue(jsonObj, KEY_PRIVATE_TOKEN));
	}

	private static JsonObject buildJson(PrivateToken privateToken) {
		return Json.createObjectBuilder().add(KEY_PRIVATE_TAG, privateToken.getTag())
				.add(KEY_PRIVATE_TOKEN, privateToken.getToken()).build();
	}

	// ---------------------------------
	// --- ShortSmoothingBufferDescriptor
	// ---------------------------------
	private static JsonObject buildJson(ShortSmoothingBufferDescriptor shortSmoothingBufferDescriptor) {

		if (shortSmoothingBufferDescriptor == null || !shortSmoothingBufferDescriptor.isEnabled()) {
			return null;
		}

		return Json.createObjectBuilder()
				.add(KEY_SB_LEAK_RATING, shortSmoothingBufferDescriptor.getSbLeakRate().getCode())

				.build();
	}

	private static ShortSmoothingBufferDescriptor parseShortSmoothingBufferDescriptor(JsonValue jsonValue) {

		JsonObject jsonObj = transformJsonValueToJsonObject(jsonValue);
		JsonValue value = (jsonObj == null) ? null : jsonObj.get(KEY_SB_LEAK_RATING);

		String sbLeakRateCode = getStringValue(value);
		return new ShortSmoothingBufferDescriptor(sbLeakRateCode);
	}

	// -----------------------------------------------------------------
	private static JsonObject transformJsonValueToJsonObject(JsonValue jsonValue) {
		if ((jsonValue != null) && jsonValue.getValueType() == ValueType.OBJECT) {
			return (JsonObject) jsonValue;
		}
		return null;
	}

	private static JsonArray transformJsonValueToJsonArray(JsonValue jsonValue) {
		if ((jsonValue != null) && jsonValue.getValueType() == ValueType.ARRAY) {
			return (JsonArray) jsonValue;
		}
		return null;
	}

	private static boolean getBooleanValue(JsonValue jsonValue) {
		if (jsonValue != null) {

			if (jsonValue.getValueType() == ValueType.TRUE) {
				return true;
			}
		}
		return false;
	}

	private static boolean getBooleanValue(JsonObject jsonObj, String key) {

		if (jsonObj != null) {

			JsonValue jsonVal = jsonObj.get(key);
			return getBooleanValue(jsonVal);
		}

		return false;
	}

	private static int getIntValue(JsonValue jsonValue) {
		if ((jsonValue != null) && jsonValue.getValueType() == ValueType.NUMBER) {
			JsonNumber jsonNumber = (JsonNumber) jsonValue;
			return jsonNumber.intValue();
		}
		return -1;
	}

	private static int getIntValue(JsonObject jsonObj, String key) {

		if (jsonObj != null) {

			JsonValue jsonValue = jsonObj.get(key);
			return getIntValue(jsonValue);
		}

		return -1;
	}

	private static String getStringValue(JsonValue jsonValue) {
		if ((jsonValue != null) && jsonValue.getValueType() == ValueType.STRING) {
			JsonString jsonString = (JsonString) jsonValue;
			return jsonString.getString();
		}
		return "";
	}

	private static String getStringValue(JsonObject jsonObj, String key) {

		if (jsonObj != null) {

			JsonValue jsonValue = jsonObj.get(key);
			return getStringValue(jsonValue);
		}

		return "";
	}

}
