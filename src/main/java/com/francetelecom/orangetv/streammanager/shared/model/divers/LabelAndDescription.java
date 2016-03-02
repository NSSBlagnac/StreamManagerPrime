package com.francetelecom.orangetv.streammanager.shared.model.divers;

import java.io.Serializable;

public class LabelAndDescription implements Serializable {

	private static final long serialVersionUID = 1L;

	private String text;
	private String description;

	public String getText() {
		return text;
	}

	public String getDescription() {
		return description;
	}

	public LabelAndDescription() {
	}

	public LabelAndDescription(String text, String description) {
		this.text = text;
		this.description = description;
	}
}