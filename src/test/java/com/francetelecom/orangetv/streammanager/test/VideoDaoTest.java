package com.francetelecom.orangetv.streammanager.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.francetelecom.orangetv.streammanager.server.dao.FullVideoDao;
import com.francetelecom.orangetv.streammanager.server.dao.VideoDao;
import com.francetelecom.orangetv.streammanager.server.model.DbFullVideoFile;
import com.francetelecom.orangetv.streammanager.server.model.DbVideoFile;
import com.francetelecom.orangetv.streammanager.server.util.VideoInfoValidator;
import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoStatus;

public class VideoDaoTest {

	private static final Log log = LogFactory.getLog(VideoDaoTest.class);

	private static final int VIDEO_ID = 99;

	@Test
	public void testListVideoFile() throws Exception {

		List<DbFullVideoFile> result = FullVideoDao.get().listVideoFile();
		assertNotNull("result cannot be null!", result);
		assertTrue("list videoFile size must be >=3!", result.size() >= 3);

		for (DbFullVideoFile videoFile : result) {
			log.info("id: " + videoFile.getId());
			log.info("name: " + videoFile.getName());
		}

	}

	@Test
	public void testListAvailableVideoFile() throws Exception {

		List<DbVideoFile> result = VideoDao.get().listAvailableVideoFile();
		assertNotNull("result cannot be null!", result);
		assertTrue("list videoFile size must be >=3!", result.size() >= 3);

		for (DbVideoFile videoFile : result) {
			log.info("id: " + videoFile.getId());
			log.info("name: " + videoFile.getName());
		}

	}

	@Test
	public void testCreateVideoInfo() throws Exception {
		int videoId = this.createVideoInfo("TU create new ts", false, VIDEO_ID);
		assertTrue("videoId must be > -1", videoId > IDto.ID_UNDEFINED);
	}

	@Test
	public void testGetById() {

		DbFullVideoFile entry = FullVideoDao.get().getById(VIDEO_ID);
		assertNotNull("entry cannot be null", entry);
		assertEquals("Wrond eitId!", VIDEO_ID, entry.getId());
	}

	@Test
	public void testUpdateVideoInfo() throws Exception {
		DbFullVideoFile entry = FullVideoDao.get().getById(VIDEO_ID);
		assertNotNull("entry cannot be null", entry);

		VideoInfo videoInfo = VideoInfoValidator.get().buildVideoInfo(entry);
		this.validate(videoInfo);
		// enregistrer sans modification
		boolean result = VideoDao.get().updateVideoInfo(videoInfo);
		assertTrue("result cannot be false!", result);

		// // enregistrer avec modification
		// streamInfo.setVideoFile("france4.ts");
		// streamInfo.setVideoId(7);
		// streamInfo.setUsi(2504);
		// streamInfo.setEntryProtected(true);
		// result = StreamDao.get().updateStreamInfo(streamInfo);
		// assertTrue("result cannot be false!", result);
	}

	@Test
	public void testUpdatePmtInfos() throws Exception {

		DbFullVideoFile entry = FullVideoDao.get().getById(VIDEO_ID);
		assertNotNull("entry cannot be null", entry);

		String audioTracks = "fra, eng";
		String subtitleTracks = "eng, deu hoh";
		String tablePmt = "video: xxxxxxxxxxxxxx, audio: fra - yyyyyyyyyyyy, audio: eng - uuuuuuuuu, subtitle: eng: tttttttt";
		boolean result = VideoDao.get().updatePmtInfos(VIDEO_ID, audioTracks, subtitleTracks, tablePmt);
		assertTrue("result cannot be false!", result);
	}

	@Test
	public void testDeleteVideo() throws Exception {
		boolean result = VideoDao.get().deleteVideo(VIDEO_ID);
		assertTrue("result cannot be false!", result);
	}

	// ------------------------- private methods

	private int createVideoInfo(String name, boolean color, int id) {

		// int max = VideoDao.get().getMaxVideoId();
		// assertTrue("max eitid must be > 8", max > 8);

		VideoInfo videoInfo = new VideoInfo();
		videoInfo.setId(id);
		videoInfo.setStatus(VideoStatus.NEW);
		videoInfo.setName(name);
		videoInfo.setColor(color);

		VideoDao.get().createVideoInfo(videoInfo);

		return videoInfo.getId();

	}

	private void validate(VideoInfo videoInfo) throws Exception {

		VideoInfoValidator.get().validateVideoInfo(videoInfo);
		List<DbVideoFile> listAllEntries = VideoDao.get().listVideoFile();
		VideoInfoValidator.get().validateUnicityRules(videoInfo, listAllEntries);
	}

}
