package com.francetelecom.orangetv.streammanager.shared.dto;

import java.io.Serializable;

import com.francetelecom.orangetv.streammanager.shared.dto.DtoActionProtection.Rules;

public class DtoProtection implements Serializable {

	private static final long serialVersionUID = 1L;

	private DtoActionProtection actionUpdate = new DtoActionProtection(true);
	private DtoActionProtection actionDelete = new DtoActionProtection(true);
	private DtoActionProtection actionModifProtectedAttribut = new DtoActionProtection(true);
	private DtoActionProtection actionEnableDisable = new DtoActionProtection(true);

	private boolean canChangeStatus = false;
	private boolean atLeastProfileManager = false;

	// ------------------------------------ public methods

	public void setReadOnly(boolean readOnly, String message, Rules rules) {
		this.getActionDelete().setActive(!readOnly, message, rules);
		this.getActionUpdate().setActive(!readOnly, message, rules);
		this.getActionModifProtectedAttribut().setActive(!readOnly, message, rules);
		this.getActionEnableDisable().setActive(!readOnly, message, rules);

		this.canChangeStatus = false;
	}

	public DtoActionProtection getActionUpdate() {
		return actionUpdate;
	}

	public boolean isCanChangeStatus() {
		return canChangeStatus;
	}

	public void setCanChangeStatus(boolean canChangeStatus) {
		this.canChangeStatus = canChangeStatus;
	}

	public DtoActionProtection getActionEnableDisable() {
		return this.actionEnableDisable;
	}

	public DtoActionProtection getActionDelete() {
		return actionDelete;
	}

	public void setAtLeastProfileManager(boolean atLeastProfileManager) {
		this.atLeastProfileManager = atLeastProfileManager;
	}

	public boolean isAtLeastProfileManager() {
		return this.atLeastProfileManager;
	}

	public DtoActionProtection getActionModifProtectedAttribut() {
		return actionModifProtectedAttribut;
	}

}
