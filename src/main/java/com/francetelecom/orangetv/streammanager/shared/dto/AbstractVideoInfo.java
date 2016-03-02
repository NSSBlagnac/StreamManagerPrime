package com.francetelecom.orangetv.streammanager.shared.dto;

import java.io.Serializable;

public abstract class AbstractVideoInfo implements Serializable, IDto {

	private static final long serialVersionUID = 1L;

	private int id = ID_UNDEFINED;
	private String name;
	private boolean color = true;
	private String resolution = "HD";
	private boolean ocs;
	private boolean csa5;
	private String format = "16/9";
	private String description;
	private boolean enabled;
	private VideoStatus status;

	private boolean entryProtected = true;
	private DtoVideoProtection dtoProtection;

	private boolean isUploadPending = false;

	// ---------------------------- accessors

	public boolean isUploadPending() {
		return isUploadPending;
	}

	public void setUploadPending(boolean isUploadPending) {
		this.isUploadPending = isUploadPending;
	}

	public boolean isCsa5() {
		return csa5;
	}

	public void setCsa5(boolean csa5) {
		this.csa5 = csa5;
	}

	public boolean isEntryProtected() {
		return entryProtected;
	}

	public String getDescription() {
		return (description == null) ? "" : description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public VideoStatus getStatus() {
		return status;
	}

	public void setStatus(VideoStatus status) {
		this.status = status;
	}

	public void setStatus(String strStatus) {
		this.setStatus(VideoStatus.valueOf(strStatus));
	}

	public void setEntryProtected(boolean entryProtected) {
		this.entryProtected = entryProtected;
	}

	public DtoVideoProtection getDtoProtection() {
		return dtoProtection;
	}

	public void setDtoProtection(DtoVideoProtection dtoProtection) {
		this.dtoProtection = dtoProtection;
	}

	public String getName() {
		return this.name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isColor() {
		return color;
	}

	public void setColor(boolean color) {
		this.color = color;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public boolean isOcs() {
		return ocs;
	}

	public void setOcs(boolean ocs) {
		this.ocs = ocs;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setName(String name) {
		this.name = name;
	}

}
