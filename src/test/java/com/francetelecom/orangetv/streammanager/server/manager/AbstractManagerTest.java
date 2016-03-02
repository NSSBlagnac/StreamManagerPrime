package com.francetelecom.orangetv.streammanager.server.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.server.manager.AdminManager.MulticatServerInfo;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SIAudioTrack;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SISubtitleTrack;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SITablePMT;
import com.francetelecom.orangetv.streammanager.server.manager.SIStreamManager.SIVideoTrack;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.francetelecom.orangetv.streammanager.shared.util.EitException;

public abstract class AbstractManagerTest {

	private static final Logger log = Logger.getLogger(AbstractManagerTest.class.getName());

	protected void initMulticatServerInfo() {
		MulticatServerInfo multicatServerInfo = new MulticatServerInfo();
		multicatServerInfo.setIp("10.185.111.249");
		multicatServerInfo.setUser("qualif");
		multicatServerInfo.setPwd("qualif");

		multicatServerInfo.setPath("/usr/local/multicat-tools");
		multicatServerInfo.setSupervisorScript("/sh/multicat-supervisor.sh");

		List<String> processList = new ArrayList<>();
		processList.add("multicat-supervisor");
		processList.add("multicat-eit");
		multicatServerInfo.setProcessList(processList);

		List<String> logList = new ArrayList<>();
		logList.add("/logs/multicat-supervisor.log");
		logList.add("/logs/multicat-eit.log");
		multicatServerInfo.setLogList(logList);

		AdminManager.get().init(multicatServerInfo);

	}

	protected void assertAndLogCmdResponse(CmdResponse response) {
		this.assertAndLogCmdResponse(response, true);
	}

	protected void assertAndLogCmdResponse(CmdResponse response, boolean forceSucces) {

		assertNotNull("response cannot be null!", response);
		log.info("exit value: " + response.getExitValue());

		if (response.getResponseLines() != null) {
			for (String line : response.getResponseLines()) {
				log.info(line);
			}
		}

		if (response.getErrorLines() != null) {
			for (String line : response.getErrorLines()) {
				log.info(line);
			}
		}
		if (forceSucces) {
			assertTrue("response must be in success!", response.isSuccess());
		}

	}

	protected void buildAndLogPMTTable(String filename, CmdResponse response) {

		log.info("buildAndLogPMTTable(): " + filename);
		StringBuilder sb = new StringBuilder();
		// construire la table PMT
		for (String line : response.getResponseLines()) {
			sb.append(line).append("\n");
		}

		try {
			SITablePMT tablePMT = SIStreamManager.get().readTablePMT(sb.toString());
			assertNotNull("PMT table cannot be null!", tablePMT);

			List<SIVideoTrack> listVideoTracks = tablePMT.getListVideoTracks();
			assertNotNull("listVideoTracks cannot be null!", listVideoTracks);
			for (SIVideoTrack siVideoTrack : listVideoTracks) {
				log.info("video: " + siVideoTrack);
			}
			List<SIAudioTrack> listAudioTracks = tablePMT.getListAudioTracks();
			assertNotNull("listAudioTracks cannot be null!", listAudioTracks);
			for (SIAudioTrack siAudioTrack : listAudioTracks) {
				log.info("audio: " + siAudioTrack);
			}

			List<SISubtitleTrack> listSubtitleTracks = tablePMT.getListSubtitleTracks();
			assertNotNull("listSubtitleTracks cannot be null!", listSubtitleTracks);
			for (SISubtitleTrack siSubtitleTrack : listSubtitleTracks) {
				log.info("subtitle: " + siSubtitleTrack);
			}

		} catch (EitException e) {
			// TODO Auto-generated catch block
			fail(e.toString());
		}
	}

}
