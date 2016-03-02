package com.francetelecom.orangetv.streammanager.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;

/**
 * The SSHAgent allows a Java application to execute commands on a remote server
 * via SSH
 * 
 * @author shaines
 *
 */
public class SSHAgent {

	private static final Logger log = Logger.getLogger(SSHAgent.class.getName());

	/**
	 * The hostname (or IP address) of the server to connect to
	 */
	private String hostname;

	/**
	 * The username of the user on that server
	 */
	private String username;

	/**
	 * The password of the user on that server
	 */
	private String password;

	/**
	 * A connection to the server
	 */
	private Connection connection;

	/**
	 * Creates a new SSHAgent
	 * 
	 * @param hostname
	 * @param username
	 * @param password
	 */
	public SSHAgent(String hostname, String username, String password) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}

	/**
	 * Connects to the server
	 * 
	 * @return True if the connection succeeded, false otherwise
	 */
	public boolean connect() throws Exception {
		try {
			// Connect to the server
			connection = new Connection(hostname);
			connection.connect();

			// Authenticate
			boolean result = connection.authenticateWithPassword(username, password);
			return result;
		} catch (Exception e) {
			log.severe("Error in connect() method: " + e.getMessage());
			throw new Exception("An exception occurred while trying to connect to the host: " + hostname
					+ ", Exception=" + e.getMessage(), e);
		}
	}

	/**
	 * Executes the specified command and returns the response from the server
	 * 
	 * @param command
	 *            The command to execute
	 * @return The response that is returned from the server (or null)
	 */
	public CmdResponse executeCommand(String command) throws Exception {

		CmdResponse cmdResponse = new CmdResponse();
		Session session = null;
		try {
			// Open a session
			session = connection.openSession();

			// Execute the command
			session.execCommand(command);

			// Read the results
			InputStream stdout = new StreamGobbler(session.getStdout());
			InputStream stErr = new StreamGobbler(session.getStderr());

			final List<String> standardLines = new ArrayList<>();
			new LireInputStream(stdout, standardLines).run();
			final List<String> errorLines = new ArrayList<>();
			new LireInputStream(stErr, errorLines).run();

			session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EXIT_STATUS, 3000);

			// DEBUG: dump the exit code
			cmdResponse.setExitValue(session.getExitStatus());

			// Return the results to the caller
			// cmdResponse.setSuccess(errorLines.isEmpty());
			cmdResponse.setResponseLines(standardLines);
			cmdResponse.setErrorLines(errorLines);

			return cmdResponse;
		} finally {
			// Close the session
			if (session != null) {
				session.close();
			}
		}

	}

	/**
	 * Logs out from the server
	 * 
	 * @throws Exception
	 */
	public void logout() throws Exception {
		try {
			connection.close();
		} catch (Exception e) {
			throw new Exception("An exception occurred while closing the SSH connection: " + e.getMessage(), e);
		}
	}

	/**
	 * Returns true if the underlying authentication is complete, otherwise
	 * returns false
	 * 
	 * @return
	 */
	public boolean isAuthenticationComplete() {
		return connection.isAuthenticationComplete();
	}

	// ======================================= INNER CLASS
	private static class LireInputStream {

		private final InputStream inputStream;
		private final List<String> lines;

		private LireInputStream(InputStream in, List<String> lines) {
			this.inputStream = in;
			this.lines = lines;
		}

		public void run() {

			if (this.inputStream == null || lines == null) {
				return;
			}
			BufferedReader bf = new BufferedReader(new InputStreamReader(this.inputStream));

			try {

				String line;
				while ((line = bf.readLine()) != null) {

					this.lines.add(line);
				}

			} catch (Exception e) {
				log.severe(e.getMessage());
			} finally {
				try {
					if (bf != null) {
						bf.close();
					}
				} catch (IOException ignored) {
				}

			}

		}

	}

}