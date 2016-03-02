package com.francetelecom.orangetv.streammanager.server.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfoForList;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;

public class StreamMulticatServiceTest {

	private static final Log log = LogFactory.getLog(StreamMulticatServiceTest.class);
	private static final StreamMulticatServiceImpl service = new StreamMulticatServiceImpl();

	@Test
	public void testGetListStreamInfo() throws Exception {

		List<StreamInfoForList> result = service.getListStreamInfo();
		assertNotNull("result cannot be null", result);
	}

	@Test
	public void testBuilEitInfoFromDbStreamInfo() throws Exception {

		EitInfoModel result = service.getEitOfStream(2);
		assertNotNull("result cannot be null", result);
	}

	@Test(expected = Exception.class)
	public void testBuilEitInfoFromDbEitEntryWrongEit() throws Exception {

		try {
			EitInfoModel result = service.getEitOfStream(10);
			assertNull("result cannot be null", result);
		} catch (Exception ex) {
			log.error("expected exception: " + ex.getMessage());
			throw ex;
		}
	}

}
