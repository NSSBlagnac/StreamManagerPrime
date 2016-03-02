package com.francetelecom.orangetv.streammanager.shared.dto;

import com.francetelecom.orangetv.streammanager.shared.dto.DtoActionProtection.Rules;

public class DtoVideoProtection extends DtoProtection {

	private static final long serialVersionUID = 1L;

	private DtoActionProtection actionUpload = new DtoActionProtection(false);

	// ------------------------- public methods

	public void setReadOnly(boolean readOnly, String message, Rules rules) {
		super.setReadOnly(readOnly, message, rules);
		this.actionUpload.setActive(!readOnly, message, rules);
	}

	// -------------------------------------------- accessors
	public DtoActionProtection getActionUpload() {
		return this.actionUpload;
	}

}
