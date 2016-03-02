package com.francetelecom.orangetv.streammanager.shared.model.divers;

import java.io.Serializable;

public class CodeAndDescription implements Serializable {

	private static final long serialVersionUID = 1L;
	private String code;
	private String description;

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public CodeAndDescription() {
		this(null, null);
	}

	public CodeAndDescription(String code, String description) {
		this.code = code;
		this.description = description;
	}

}
