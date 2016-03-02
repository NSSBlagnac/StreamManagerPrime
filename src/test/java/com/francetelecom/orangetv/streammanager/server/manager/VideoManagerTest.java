package com.francetelecom.orangetv.streammanager.server.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.streammanager.server.dao.FullVideoDao;
import com.francetelecom.orangetv.streammanager.server.model.DbFullVideoFile;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdRequest;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;

public class VideoManagerTest extends AbstractManagerTest {

	private static final Logger log = Logger.getLogger(VideoManagerTest.class.getName());

	private static final VideoManager manager = VideoManager.get();
	private static final String VIDEO_PATH = "/usr/local/multicat-tools/videos";

	@Test
	public void testGeneratePmtTableAllVideo() throws Exception {

		this.initMulticatServerInfo();
		List<DbFullVideoFile> list = FullVideoDao.get().listVideoFile();
		assertNotNull("list of videos cannot be null!", list);

		for (DbFullVideoFile dbFullVideoFile : list) {

			CmdResponse response = AdminManager.get().extractPmtTableFromVideofile(VIDEO_PATH,
					dbFullVideoFile.getName());

			// this.assertAndLogCmdResponse(response, false);
			manager.updateVideoWithPmtTable(dbFullVideoFile.getId(), response);

		}
	}

	@Test
	public void testListAllVideoFiles() {

		this.initMulticatServerInfo();
		this.getAndAssertListOfVideoFile(VIDEO_PATH);
	}

	@Test
	public void testShowPMTTableForAllVideoFiles() {

		String debugVideoPath = "/usr/local/multicat-tools/videos/";
		this.initMulticatServerInfo();
		List<String> listFiles = this.getAndAssertListOfVideoFile(debugVideoPath);
		for (String filename : listFiles) {

			if (filename.endsWith(".ts")) {

				CmdResponse response = AdminManager.get().extractPmtTableFromVideofile(VIDEO_PATH, filename);
				this.buildAndLogPMTTable(filename, response);
			}
		}
	}

	private List<String> getAndAssertListOfVideoFile(String path) {

		CmdResponse response = AdminManager.get().executeRemoteCommand(new CmdRequest("ls " + path, true));
		assertNotNull("response cannot be null!");
		assertTrue("response must be in success", response.isSuccess());

		assertNotNull("list of lines cannot be null!!", response.getResponseLines());
		for (String line : response.getResponseLines()) {
			log.info(line);
		}
		return response.getResponseLines();
	}
}
