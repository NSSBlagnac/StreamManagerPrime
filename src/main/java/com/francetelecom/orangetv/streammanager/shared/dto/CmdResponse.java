package com.francetelecom.orangetv.streammanager.shared.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsule la reponse du lancement d'une commande en ligne
 * 
 * @author ndmz2720
 *
 */
public class CmdResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int EXIT_SUCCESS = 0;

	private int exitValue = -1;
	private boolean success = false;
	private String errorMessage;

	private String usedCommand;

	private List<String> responseLines;
	private List<String> errorLines;

	// ------------------------ accesseurs

	public String getUsedCommand() {
		return usedCommand;
	}

	public void setUsedCommand(String usedCommand) {
		this.usedCommand = usedCommand;
	}

	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
		this.success = this.exitValue == EXIT_SUCCESS;
	}

	public int getExitValue() {
		return this.exitValue;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public List<String> getResponseLines() {
		return this.responseLines;
	}

	public void setResponseLines(List<String> lines) {
		this.responseLines = lines;
	}

	public List<String> getErrorLines() {
		return this.errorLines;
	}

	public void setErrorLines(List<String> lines) {
		this.errorLines = lines;
	}

	public void addResponseLine(String line) {
		if (this.responseLines == null) {
			this.responseLines = new ArrayList<>();
		}
		this.responseLines.add(line);
	}

	public void addErrorLine(String line) {
		if (this.errorLines == null) {
			this.errorLines = new ArrayList<>();
		}
		this.errorLines.add(line);
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	// ------------------------------------ public methods
	public CmdResponse concat(CmdResponse cmdResponse) {

		if (cmdResponse == null) {
			return this;
		}
		CmdResponse response = new CmdResponse();
		if (this.exitValue != EXIT_SUCCESS) {
			response.setExitValue(this.exitValue);
		}
		if (cmdResponse.getExitValue() != EXIT_SUCCESS) {
			response.setExitValue(cmdResponse.getExitValue());
		}
		cmdResponse.setResponseLines(this.concatLines(this.responseLines, cmdResponse.getResponseLines()));
		cmdResponse.setErrorLines(this.concatLines(this.errorLines, cmdResponse.getErrorLines()));

		return cmdResponse;
	}

	// ----------------------------------- private methods
	private List<String> concatLines(List<String> lines1, List<String> lines2) {

		if (lines1 == null && lines2 == null) {
			return null;
		}
		if (lines1 == null) {
			return lines2;
		}
		if (lines2 == null) {
			return lines1;
		}
		lines1.addAll(lines2);
		return lines1;
	}
}