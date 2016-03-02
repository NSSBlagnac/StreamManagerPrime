package com.francetelecom.orangetv.streammanager.shared.dto;

import com.francetelecom.orangetv.streammanager.shared.dto.DtoActionProtection.Rules;

/**
 * Determine les droits de modification, suppression en fonction du profile
 * 
 * @author ndmz2720
 * 
 */
public class DtoStreamProtection extends DtoProtection {
	private static final long serialVersionUID = 1L;

	private DtoActionProtection actionStartOrStop = new DtoActionProtection(true);

	private DtoActionProtection actionDisplayEit = new DtoActionProtection(true);

	// ------------------------- public methods

	public void setReadOnly(boolean readOnly, String message, Rules rules) {
		super.setReadOnly(readOnly, message, rules);

	}

	// ------------------------------ accessors

	public DtoActionProtection getActionDisplayEit() {
		return this.actionDisplayEit;
	}

	public DtoActionProtection getActionStartOrStop() {
		return this.actionStartOrStop;
	}

}
