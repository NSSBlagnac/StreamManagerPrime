package com.francetelecom.orangetv.streammanager.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.francetelecom.orangetv.streammanager.server.dao.StreamDao;
import com.francetelecom.orangetv.streammanager.server.manager.ProtectionManager.ProtectedStreamDatas;
import com.francetelecom.orangetv.streammanager.server.model.DbFullStreamEntry;
import com.francetelecom.orangetv.streammanager.server.util.EitJsonParser;
import com.francetelecom.orangetv.streammanager.server.util.StreamInfoValidator;
import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;
import com.francetelecom.orangetv.streammanager.shared.dto.TripletDvb;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;

public class StreamDaoTest implements IEitJsonParserTest {

	private static final Log log = LogFactory.getLog(StreamDaoTest.class);

	private static final int STREAM_ID = 99;

	@Test
	public void testGetAllStreamEntry() throws Exception {

		List<DbFullStreamEntry> result = StreamDao.get().listStreamEntry();
		assertNotNull("result cannot be null!", result);
		assertTrue("list size must be > -", result.size() > 6);
	}

	@Test
	public void testGetMaxEitId() throws Exception {

		int max = StreamDao.get().getMaxEitId();
		assertTrue("max eitid must be > 8", max > 8);
	}

	@Test
	public void testCreateStreamInfo() throws Exception {
		TripletDvb tripletDvb = new TripletDvb(4, 4, 4);
		int eitId = this.createStreamInfo(STREAM_ID, "lcn444", 444, 4444, "4.4.4.4", tripletDvb);
		assertTrue("eitId must be > -1", eitId > IDto.ID_UNDEFINED);
	}

	@Test
	public void testGetById() {

		DbFullStreamEntry entry = StreamDao.get().getById(STREAM_ID);
		assertNotNull("entry cannot be null", entry);
		assertEquals("Wrond eitId!", STREAM_ID, entry.getId());
		assertFalse("", entry.isEnable());
	}

	@Test
	public void testUpdateStatus() {

		boolean result = StreamDao.get().updateStatus(STREAM_ID, StreamStatus.STOPPED);
		assertTrue("result cannot be false", result);
	}

	@Test
	public void testEnableEntry() {
		boolean result = StreamDao.get().enableEntry(STREAM_ID, true);
		assertTrue("result cannot be false", result);
	}

	@Test
	public void testUpdateStreamInfo() throws Exception {
		DbFullStreamEntry entry = StreamDao.get().getById(STREAM_ID);
		assertNotNull("entry cannot be null", entry);

		StreamInfo streamInfo = StreamInfoValidator.get().buildStreamInfo(entry);
		this.validate(streamInfo);
		// enregistrer sans modification
		boolean result = StreamDao.get().updateStreamInfo(streamInfo);
		assertTrue("result cannot be false!", result);

		// enregistrer avec modification
		streamInfo.setVideoFilename("france4.ts");
		streamInfo.setVideoId(7);
		streamInfo.setUsi(2504);
		streamInfo.setEntryProtected(true);
		result = StreamDao.get().updateStreamInfo(streamInfo);
		assertTrue("result cannot be false!", result);
	}

	@Test
	public void testUpdateEit() {

		EitInfoModel eitInfo = EitJsonParser.parse(json);
		assertNotNull("eitInfo cannot be null!!", eitInfo);

		boolean result = StreamDao.get().updateEit(eitInfo);
		assertTrue("result cannot be false", result);

	}

	// @Test
	// public void testUpdateEitWithIncompleteJson() {
	//
	// EitInfoModel eitInfo = EitJsonParser.parse(test);
	// assertNotNull("eitInfo cannot be null!!", eitInfo);
	//
	// boolean result = StreamDao.get().updateEit(eitInfo);
	// assertTrue("result cannot be false", result);
	//
	// }

	@Test
	public void testDeleteEntry() throws Exception {
		boolean result = StreamDao.get().deleteStream(STREAM_ID);
		assertTrue("result cannot be false!", result);
	}

	// @Test(expected = Exception.class)
	// public void testCreateStreamInfoSameTriplet() throws Exception {
	// TripletDvb tripletDvb = new TripletDvb(6, 6, 6);
	// int eitId = this.createStreamInfo("lcn666", 666, 6666, "6.6.6.6",
	// tripletDvb);
	// assertTrue("eitId must be > -1", eitId > IDto.ID_UNDEFINED);
	//
	// // meme triplet >> on doit avoir une exception
	// eitId = this.createStreamInfo("lcn777", 777, 7777, "7.7.7.7",
	// tripletDvb);
	// assertTrue("eitId must be > -1", eitId > IDto.ID_UNDEFINED);
	// }

	// ------------------------- private methods
	private void validate(StreamInfo streamInfo) throws Exception {

		StreamInfoValidator.get().validateStreamInfo(streamInfo);
		List<DbFullStreamEntry> listAllEntries = StreamDao.get().listStreamEntry();
		StreamInfoValidator.get().validateUnicityRules(streamInfo, listAllEntries);
	}

	private ProtectedStreamDatas buildProtectedStreamDatas() {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();
		// TODO
		return protectedStreamDatas;
	}

	private int createStreamInfo(int eitId, String name, int lcn, int usi, String url, TripletDvb tripletDvb)
			throws Exception {
		DbFullStreamEntry entry = StreamDao.get().getById(2);
		assertNotNull("entry cannot be null", entry);

		StreamInfo streamInfo = StreamInfoValidator.get().buildStreamInfo(entry);
		streamInfo.setId(eitId);
		streamInfo.setName(name);
		streamInfo.setLcn(lcn);
		streamInfo.setUsi(usi);
		streamInfo.setEnable(false);
		streamInfo.setAddress(url);
		streamInfo.setTripleDvb(tripletDvb.toString());
		this.validate(streamInfo);
		// enregistrer sans modification
		StreamDao.get().createStreamInfo(streamInfo);
		return streamInfo.getId();

	}

}
