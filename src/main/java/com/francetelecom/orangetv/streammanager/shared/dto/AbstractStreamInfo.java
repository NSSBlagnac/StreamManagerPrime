package com.francetelecom.orangetv.streammanager.shared.dto;

import java.io.Serializable;

public abstract class AbstractStreamInfo implements Serializable, IDto, Comparable<AbstractStreamInfo> {

	private static final long serialVersionUID = 1L;

	private int id = ID_UNDEFINED;
	private int lcn;
	private int usi;
	private String name;
	private String description;
	private String user;
	private boolean enable = false;

	private int videoId;
	private String address;
	private int port;
	private String tripletDvb;

	private StreamStatus status = StreamStatus.NEW;
	private boolean entryProtected = true;
	private boolean eitToInject = true;

	private DtoStreamProtection dtoProtection;

	// issu de la table video
	private String videoFilename;
	private boolean videoEnabled;
	private VideoStatus videoStatus;

	// authorized ranges
	private String rangeLcn;
	private String rangeUSI;
	private String rangeAddress;

	// --------------------------------- accessors

	public boolean isVideoAvailable() {
		return this.videoEnabled && videoStatus == VideoStatus.READY;
	}

	public String getRangeLcn() {
		return rangeLcn;
	}

	public void setRangeLcn(String rangeLcn) {
		this.rangeLcn = rangeLcn;
	}

	public String getRangeUSI() {
		return rangeUSI;
	}

	public void setRangeUSI(String rangeUSI) {
		this.rangeUSI = rangeUSI;
	}

	public String getRangeAddress() {
		return rangeAddress;
	}

	public void setRangeAddress(String rangeAddress) {
		this.rangeAddress = rangeAddress;
	}

	public VideoStatus getVideoStatus() {
		return videoStatus;
	}

	public void setVideoStatus(VideoStatus videoStatus) {
		this.videoStatus = videoStatus;
	}

	public boolean isVideoEnabled() {
		return videoEnabled;
	}

	public void setVideoEnabled(boolean videoEnabled) {
		this.videoEnabled = videoEnabled;
	}

	public boolean isEitToInject() {
		return eitToInject;
	}

	public void setEitToInject(boolean eitToInject) {
		this.eitToInject = eitToInject;
	}

	public DtoStreamProtection getDtoProtection() {
		return dtoProtection;
	}

	public void setDtoProtection(DtoStreamProtection dtoProtection) {
		this.dtoProtection = dtoProtection;
	}

	public boolean isEntryProtected() {
		return entryProtected;
	}

	public void setEntryProtected(boolean entryProtected) {
		this.entryProtected = entryProtected;
	}

	public int getVideoId() {
		return this.videoId;
	}

	public void setVideoId(int videoId) {
		this.videoId = videoId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLcn() {
		return lcn;
	}

	public void setLcn(int lcn) {
		this.lcn = lcn;
	}

	public int getUsi() {
		return usi;
	}

	public void setUsi(int usi) {
		this.usi = usi;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getVideoFilename() {
		return this.videoFilename;
	}

	public void setVideoFilename(String videoFilename) {
		this.videoFilename = videoFilename;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String adress) {
		this.address = adress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public StreamStatus getStatus() {
		return status;
	}

	public void setStatus(String strStatus) {
		if (strStatus != null) {
			strStatus = strStatus.replaceFirst(" ", "_");
		}
		this.setStatus(StreamStatus.valueOf(strStatus));
	}

	public void setStatus(StreamStatus status) {
		this.status = status;
	}

	public String getTripletDvd() {
		return this.tripletDvb;
	}

	public void setTripleDvb(String tripletDvb) {
		this.tripletDvb = tripletDvb;
	}

	// -------------------------------------- overriding Comparable
	@Override
	public int compareTo(AbstractStreamInfo o) {
		if (o == null) {
			return 1;
		}
		if (this.getLcn() == o.getLcn()) {
			return 0;
		}
		return (this.getLcn() < o.getLcn()) ? -1 : 1;
	}

}
