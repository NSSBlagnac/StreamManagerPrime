package com.francetelecom.orangetv.streammanager.shared.dto;

import java.io.Serializable;

/**
 * Encapsule les informations nécessaire au lancement d'une ligne de commande,
 * soit en localhost (pour test) soit sur un serveur linux distant (qualif)
 * 
 * @author ndmz2720
 *
 */
public class CmdRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean linux = true;
	private String cmd;

	// ------------------------------- constructor
	public CmdRequest() {
	}

	public CmdRequest(String cmd, boolean linux) {
		this.cmd = cmd;
		this.linux = linux;
	}

	// --------------------------------------- accessors

	public boolean isLinux() {
		return linux;
	}

	public void setLinux(boolean linux) {
		this.linux = linux;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

}
