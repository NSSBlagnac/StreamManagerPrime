package com.francetelecom.orangetv.streammanager.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import javax.json.JsonObject;

import org.junit.Test;

import com.francetelecom.orangetv.streammanager.server.util.EitJsonParser;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel.EitSection;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractComponentDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractDescriptor.Country;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractDescriptor.Language;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.CAIdentifierDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentNibbleLevel2;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ExtendedEventDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ParentalRatingDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ParentalRatingDescriptor.ParentalRating;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortEventDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortSmoothingBufferDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortSmoothingBufferDescriptor.SbLeakRate;

public class EitJsonParserTest implements IEitJsonParserTest {

	private static final Logger log = Logger.getLogger(EitJsonParserTest.class.getName());

	@Test
	public void testParse() {

		EitInfoModel eitInfo = EitJsonParser.parse(json);
		this.assertResult(eitInfo);

		JsonObject jsonObject = EitJsonParser.buildJson(eitInfo);
		assertNotNull(jsonObject);

		log.info("json: " + jsonObject.toString());
	}

	// @Test
	// public void testParseIncompleteJson() {
	//
	// EitInfoModel eitInfo = EitJsonParser.parse(test);
	// assertNotNull("eitInfo cannot be null!", eitInfo);
	// // this.assertResult(eitInfo);
	// }

	private void assertResult(EitInfoModel eitInfo) {

		assertNotNull("eitInfo cannot be null!!", eitInfo);
		assertNotNull("eitGeneral cannot be null!", eitInfo.getEitGeneral());

		assertNotNull("present section cannot be null!", eitInfo.getPresentSection());
		this.assertResult(eitInfo.getPresentSection(), true);
		assertNotNull("following section cannot be null!", eitInfo.getFollowingSection());
		this.assertResult(eitInfo.getFollowingSection(), false);

	}

	private void assertResult(EitSection eitSection, boolean presentSection) {

		List<EitEvent> listEvents = eitSection.getListEvents();
		assertNotNull("listEvents cannot be null!", listEvents);
		assertEquals("Wrong size!", 1, listEvents.size());
		this.assertResult(listEvents.get(0), presentSection);
	}

	private void assertResult(EitEvent eitEvent, boolean presentSection) {

		assertNotNull("ShortEventDescriptor cannot be null!!", eitEvent.getShortEventDescriptor());
		this.assertResult(eitEvent.getShortEventDescriptor(), presentSection);

		assertNotNull("ExtendedEventDescriptor cannot be null!!", eitEvent.getExtendedEventDescriptor());
		this.assertResult(eitEvent.getExtendedEventDescriptor(), presentSection);

		assertNotNull("ParentalRatingDescriptor cannot be null!!", eitEvent.getParentalRatingDescriptor());
		this.assertResult(eitEvent.getParentalRatingDescriptor(), presentSection);

		assertNull("CAIdentifierDescriptor must be null!!", eitEvent.getCAIdentifierDescriptor());

		assertNull("ShortSmoothingBufferDescriptor must be null!!", eitEvent.getShortSmoothingBufferDescriptor());

		assertNotNull("ContentDescriptor cannot be null!!", eitEvent.getContentDescriptor());
		this.assertResult(eitEvent.getContentDescriptor(), presentSection);

		// assertNotNull("ComponentDescriptor cannot be null!!",
		// eitEvent.getComponentDescriptors());
		// this.assertResult(eitEvent.getComponentDescriptor(), doAssertion);

	}

	private void assertResult(ShortEventDescriptor shortEventDescriptor, boolean doAssertion) {
		if (doAssertion) {
			assertNotNullAndNotEmpty("ShortEventDescriptor.text", shortEventDescriptor.getText());
			assertNotNullAndNotEmpty("ShortEventDescriptor.name", shortEventDescriptor.getName());
			assertNotNullAndNotEmpty("ShortEventDescriptor.lang", shortEventDescriptor.getLang());
		}
	}

	private void assertResult(ExtendedEventDescriptor extendedEventDescriptor, boolean presentSection) {
		this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.text", extendedEventDescriptor.getText());
		this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.lang", extendedEventDescriptor.getLang());

		// assertNotNull("ExtendedEventDescriptor.Director cannot be null!",
		// extendedEventDescriptor.getDirector());
		// this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.Director.text",
		// extendedEventDescriptor.getDirector()
		// .getText());
		// this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.Director.description",
		// extendedEventDescriptor
		// .getDirector().getDescription());
		//
		// assertNotNull("ExtendedEventDescriptor.Year cannot be null!",
		// extendedEventDescriptor.getYear());
		// this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.Year.text",
		// extendedEventDescriptor.getYear().getText());
		// this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.Year.description",
		// extendedEventDescriptor.getYear()
		// .getDescription());
		//
		// assertNotNull("ExtendedEventDescriptor.Rating cannot be null!",
		// extendedEventDescriptor.getRating());
		// this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.Rating.text",
		// extendedEventDescriptor.getRating()
		// .getText());
		// this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.Rating.description",
		// extendedEventDescriptor.getRating()
		// .getDescription());
		//
		// assertNotNull("ExtendedEventDescriptor.Writers cannot be null!",
		// extendedEventDescriptor.getWriters());
		// this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.Writers.text",
		// extendedEventDescriptor.getWriters()
		// .getText());
		// this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.Writers.description",
		// extendedEventDescriptor
		// .getWriters().getDescription());
		//
		// assertNotNull("ExtendedEventDescriptor.Stars cannot be null!",
		// extendedEventDescriptor.getStars());
		// this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.Stars.text",
		// extendedEventDescriptor.getStars()
		// .getText());
		// this.assertNotNullAndNotEmpty("ExtendedEventDescriptor.Stars.description",
		// extendedEventDescriptor.getStars()
		// .getDescription());

	}

	private void assertResult(ParentalRatingDescriptor parentalRatingDescriptor, boolean presentSection) {

		assertNotNullAndNotEmpty("ParentalDescriptor.country", parentalRatingDescriptor.getCountry());
		assertEquals("Wrong country!", Country.FRA, parentalRatingDescriptor.getCountry());

		ParentalRating parentalRating = parentalRatingDescriptor.getParentalRating();
		assertNotNull("ParentalRatingDescriptor.parentalRating cannot be null!", parentalRating);
		assertEquals("Wrong rating!", (presentSection) ? 9 : 0, parentalRating.getRating());
	}

	private String getStrSection(boolean presentSection) {
		return (presentSection) ? "presentSection" : "followingSection";
	}

	private void assertResult(CAIdentifierDescriptor caIdentifierDescriptor, boolean presentSection) {

		assertNull("caIdentifierDescriptor must be null!", caIdentifierDescriptor);
		// List<CASystemId> listCaIdentifier =
		// caIdentifierDescriptor.getListSystemIds();
		// assertNotNull("CAIdentifierDescriptor.listSystemIds cannot be null!",
		// listCaIdentifier);
		// assertEquals("Wrong size for " + getStrSection(presentSection),
		// (presentSection) ? 2 : 1,
		// listCaIdentifier.size());
		//
		// CASystemId caSystemId = listCaIdentifier.get(0);
		// assertNotNull("caSystemId cannot be null!", caSystemId);
		// assertEquals("Wrong value!", (presentSection) ? 5555 : 2222,
		// caSystemId.getId());
		//
		// if (presentSection) {
		// caSystemId = listCaIdentifier.get(1);
		// assertNotNull("caSystemId cannot be null!", caSystemId);
		// assertEquals("Wrong value!", 9999, caSystemId.getId());
		// }
	}

	private void assertResult(ShortSmoothingBufferDescriptor shortSmoothingBufferDescriptor, boolean presentSection) {
		SbLeakRate sbLeakRate = shortSmoothingBufferDescriptor.getSbLeakRate();
		assertNotNull("ShortSmoothingBufferDescriptor.sbLeakRate cannot be null!", sbLeakRate);

		assertEquals("Wrong value!", (presentSection) ? "27" : "10", sbLeakRate.getCode());
	}

	private void assertResult(ContentDescriptor contentDescriptor, boolean presentSection) {
		List<ContentNibbleLevel2> listCategories = contentDescriptor.getistCategories();
		assertNotNull("listCategories cannot be nu!!", listCategories);
		assertEquals("Wrong size", (presentSection) ? 1 : 1, listCategories.size());

		ContentNibbleLevel2 category = listCategories.get(0);
		assertEquals("Wrong code!", (presentSection) ? "0x03" : "0x03", category.getCode());

	}

	private void assertResult(AbstractComponentDescriptor componentDescriptor, boolean doAssertion) {
		if (doAssertion) {
			assertNotNullAndNotEmpty("ComponentDescriptor.text", componentDescriptor.getText());
			assertNotNullAndNotEmpty("ComponentDescriptor.lang", componentDescriptor.getLang());
		}
	}

	private void assertNotNullAndNotEmpty(String message, Country country) {

		assertNotNull(message + " cannot be null!", country);
		this.assertNotNullAndNotEmpty(message, country.getCode());
	}

	private void assertNotNullAndNotEmpty(String message, Language language) {

		assertNotNull(message + " cannot be null!", language);
		this.assertNotNullAndNotEmpty(message, language.getCode());
	}

	private void assertNotNullAndNotEmpty(String message, String value) {
		assertNotNull(message + " cannot be null!", value);
		assertTrue(message + " cannot be empty!", value.length() > 0);
		log.info(message + ": " + value);
	}

}
