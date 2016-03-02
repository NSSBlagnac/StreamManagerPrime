package com.francetelecom.orangetv.streammanager.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SIAudioTrack;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SISubtitleTrack;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SITablePMT;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SIVideoTrack;
import com.francetelecom.orangetv.streammanager.shared.util.EitException;

/**
 * JUnit tests for SIStreamManager
 * 
 * @author ndmz2720
 *
 */
public class SIStreamManagerTest {

	private static final Logger log = Logger.getLogger(SIStreamManagerTest.class.getName());

	private static final SIStreamManager manager = SIStreamManager.get();

	private static final String FILE_PMT_XML_1 = "SI_PMT1.xml";

	@Test
	public void testReadFilePMT1() {

		String listLines = this.readFile(FILE_PMT_XML_1);
		assertNotNull("listLines cannot be null!", listLines);
		assertTrue("list lines cannot be empty!", !listLines.isEmpty());
		log.info(listLines);
	}

	@Test
	public void testReadTablePMT1() throws EitException {

		String listLines = this.readFile(FILE_PMT_XML_1);
		assertTrue("list lines cannot be empty!", !listLines.isEmpty());

		SITablePMT tablePMT = manager.readTablePMT(listLines);
		assertNotNull("table PMT cannot be null!!", tablePMT);

		assertEquals("Wrong programId", 9910, tablePMT.getProgramId());
		assertEquals("Wrong pcrpid", 210, tablePMT.getPcrpid());

		// video tracks
		List<SIVideoTrack> listVideoTracks = tablePMT.getListVideoTracks();
		assertNotNull("listVideoTracks cannot be null!", listVideoTracks);
		assertEquals("Wrong count of video tracks", 1, listVideoTracks.size());
		SIVideoTrack videoTrack = listVideoTracks.get(0);
		assertNotNull("videoTrack cannot be null!", videoTrack);
		assertEquals("Wrong video streamType", "0x1b", videoTrack.getStreamType());
		assertEquals("Wrong video streamType_txt", "H.264/14496-10 video (MPEG-4/AVC)", videoTrack.getStreamTypeTxt());

		// audio tracks
		List<SIAudioTrack> listAudioTracks = tablePMT.getListAudioTracks();
		assertNotNull("listAudioTracks cannot be null!", listAudioTracks);
		assertEquals("Wrong count of audio tracks", 2, listAudioTracks.size());

		for (SIAudioTrack siAudioTrack : listAudioTracks) {
			assertEquals("Wrong audio stream type!", "0x11", siAudioTrack.getStreamType());
			assertEquals("Wrong audio stream type_txt!", "14496-3 Audio with LATM transport syntax (14496-3/AMD 1)",
					siAudioTrack.getStreamTypeTxt());
		}
		assertEquals("Wrong audio language", "fra", listAudioTracks.get(0).getLanguage());
		assertEquals("Wrong audio language", "eng", listAudioTracks.get(1).getLanguage());

		// subtitle tracks
		List<SISubtitleTrack> listSubtitleTracks = tablePMT.getListSubtitleTracks();
		assertNotNull("listSubtitleTracks cannot be null!", listSubtitleTracks);
		assertEquals("Wrong count of subtitle tracks", 3, listSubtitleTracks.size());

		for (SISubtitleTrack siSubtitleTrack : listSubtitleTracks) {
			assertEquals("Wrong subtitle language!", "fra", siSubtitleTrack.getLanguage());
		}
		assertEquals("Wrong subtitle hoh!", true, listSubtitleTracks.get(1).isHardOfHearing());

	}

	private String readFile(String filename) {

		StringBuilder sb = new StringBuilder();
		ClassLoader classLoader = getClass().getClassLoader();
		URL url = classLoader.getResource(filename);
		assertNotNull("url cannot be null!", url);
		File file = new File(url.getFile());

		try (Scanner scanner = new Scanner(file)) {

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				sb.append(line).append("\n");
			}

			scanner.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();

	}
}
