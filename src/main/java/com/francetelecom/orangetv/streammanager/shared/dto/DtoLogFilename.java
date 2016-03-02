package com.francetelecom.orangetv.streammanager.shared.dto;

import java.io.Serializable;

public class DtoLogFilename implements IDto, Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String pathname;

	public String getName() {
		return name;
	}

	public String getPathname() {
		return pathname;
	}

	public DtoLogFilename() {
		this(null, null);
	}

	public DtoLogFilename(String name, String pathname) {
		// nom du fichier
		this.name = name;
		// chemin complet
		this.pathname = pathname;
	}

}
