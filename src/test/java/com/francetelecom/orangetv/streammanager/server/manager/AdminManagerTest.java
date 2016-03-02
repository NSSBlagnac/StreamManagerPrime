package com.francetelecom.orangetv.streammanager.server.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.streammanager.server.util.SSHAgent;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdRequest;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.francetelecom.orangetv.streammanager.shared.dto.SupervisorStatus;

public class AdminManagerTest extends AbstractManagerTest {

	private static final Logger log = Logger.getLogger(AdminManagerTest.class.getName());

	private static final AdminManager manager = AdminManager.get();

	// @Test
	// public void testExecuteDosCommand() throws Exception {
	//
	// CmdResponse response = manager.executeCommand(new
	// CmdRequest("dir /s c:\\tmp", false));
	// assertNotNull("response cannot be null!", response);
	// log.info("exit value: " + response.getExitValue());
	//
	// assertNotNull("response lines cannot be null!",
	// response.getResponseLines());
	//
	// assertTrue("response must be in success!", response.isSuccess());
	// for (String line : response.getResponseLines()) {
	// log.info(line);
	// }
	//
	// }

	@Test
	public void testExtractPmtTableFromVideofile() {

		this.initMulticatServerInfo();
		CmdResponse response = manager.extractPmtTableFromVideofile("/usr/local/multicat-tools/tests", "france2.ts");
		this.assertAndLogCmdResponse(response);
		this.buildAndLogPMTTable("france2.ts", response);
	}

	@Test
	public void testDeleteVideoFileAndAux() throws Exception {

		this.initMulticatServerInfo();
		CmdResponse response = manager.deleteVideoFileAndAux("/usr/local/multicat-tools/tests", "france2.ts");

		this.assertAndLogCmdResponse(response);
	}

	@Test
	public void testCreateAuxVideoFileWithIngest() throws Exception {

		this.initMulticatServerInfo();
		CmdResponse response = manager.createAuxVideoFileWithIngest("/usr/local/multicat-tools/tests", "france2.ts");
		this.assertAndLogCmdResponse(response);
	}

	@Test
	public void testExecuteLinuxSshCommand() {

		this.initMulticatServerInfo();
		CmdRequest cmdRequest = new CmdRequest("ls /home/qualif/", true);

		CmdResponse response = manager.executeRemoteCommand(cmdRequest);
		this.assertAndLogCmdResponse(response);
	}

	@Test
	public void testGetSupervisorStatus() {

		this.initMulticatServerInfo();
		SupervisorStatus status = manager.getSupervisorStatus();
		assertNotNull("supervisor status cannot be null", status);

		log.info("status: " + status);
		assertTrue("status must be started or stopped", status == SupervisorStatus.started
				|| status == SupervisorStatus.stopped);
	}

	@Test
	public void testExecuteTailCommand() {

		this.initMulticatServerInfo();
		CmdRequest cmdRequest = new CmdRequest("tail /usr/local/multicat-tools/logs/multicat-supervisor.log", true);

		CmdResponse response = manager.executeRemoteCommand(cmdRequest);
		this.assertAndLogCmdResponse(response);
	}

	@Test
	public void testExecuteTailFileNotExistCommand() {
		this.initMulticatServerInfo();

		CmdRequest cmdRequest = new CmdRequest("tail /usr/local/multicat-tools/logs/toto.log", true);

		CmdResponse response = manager.executeRemoteCommand(cmdRequest);

		assertNotNull("response cannot be null!", response);
		log.info("exit value: " + response.getExitValue());

		assertNotNull("response lines cannot be null!", response.getResponseLines());

		for (String line : response.getResponseLines()) {
			log.info(line);
		}

		for (String line : response.getErrorLines()) {
			log.info("ERROR: " + line);
		}

		assertTrue("exit value must be != 0", response.getExitValue() != 0);
	}

	@Test
	public void testExecuteTailWrongFileCommand() {
		this.initMulticatServerInfo();

		CmdRequest cmdRequest = new CmdRequest("tail /usr/local/multicat-tools/logs/multicat-eit.log", true);

		CmdResponse response = manager.executeRemoteCommand(cmdRequest);

		assertNotNull("response cannot be null!", response);
		log.info("exit value: " + response.getExitValue());

		assertNotNull("response lines cannot be null!", response.getResponseLines());

		for (String line : response.getResponseLines()) {
			log.info(line);
		}

		for (String line : response.getErrorLines()) {
			log.info("ERROR: " + line);
		}

	}

	@Test
	public void testSSHAgent() throws Exception {

		try {
			SSHAgent sshAgent = new SSHAgent("10.185.111.249", "qualif", "qualif");
			if (sshAgent.connect()) {
				CmdResponse response = sshAgent.executeCommand("ls /home/qualif/");

				// Logout
				sshAgent.logout();

				assertNotNull("response cannot be null!", response);
				log.info("exit value: " + response.getExitValue());

				assertNotNull("response lines cannot be null!", response.getResponseLines());

				assertTrue("response must be in success!", response.isSuccess());
				for (String line : response.getResponseLines()) {
					log.info(line);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// --------------------------------------------------- private methods
}
