package com.francetelecom.orangetv.streammanager.server.model;

import java.io.Serializable;

import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;
import com.francetelecom.orangetv.streammanager.shared.dto.TripletDvb;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoStatus;
import com.google.gwt.user.client.rpc.IsSerializable;

public class DbFullStreamEntry implements Serializable, IsSerializable, IDto, IDbEntry, Comparable<DbFullStreamEntry> {

	private static final long serialVersionUID = 1L;

	private int id = ID_UNDEFINED;
	private int lcn;
	private int usi;
	private String name;
	private String description;
	private String user;
	private String eitPresentEvent;
	private String eitFollowingEvent;

	// si un stream est enabled, alors
	// le supervisor tentera de le redemarrer automatiquement
	private boolean enable;

	// private String videoFile;
	private int videoId;
	private String address;
	private int port;
	private TripletDvb tripletDvb;

	private StreamStatus status;

	private boolean entryProtected = true;
	private boolean eitToInject = true;

	// issu de la table video

	// video enabled (utilisable)
	private boolean videoEnabled;
	// nom du fichier video
	private String videoFileName;
	// status de la video
	private VideoStatus videoStatus;

	// --------------------------------- accessors

	public VideoStatus getVideoStatus() {
		return videoStatus;
	}

	public void setVideoStatus(String videoStatus) {
		this.videoStatus = (videoStatus == null) ? VideoStatus.ERROR : VideoStatus.valueOf(videoStatus);
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

	public boolean isEntryProtected() {
		return entryProtected;
	}

	public void setEntryProtected(boolean entryProtected) {
		this.entryProtected = entryProtected;
	}

	// public DtoStreamProtection getDtoProtection() {
	// return dtoProtection;
	// }
	//
	// public void setDtoProtection(DtoStreamProtection dtoProtection) {
	// this.dtoProtection = dtoProtection;
	// }

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

	public String getPresentEvent() {
		return eitPresentEvent;
	}

	public void setPresentEvent(String presentEvent) {
		this.eitPresentEvent = presentEvent;
	}

	public String getFollowingEvent() {
		return eitFollowingEvent;
	}

	public void setFollowingEvent(String followingEvent) {
		this.eitFollowingEvent = followingEvent;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getVideoFilename() {
		return this.videoFileName;
	}

	public void setVideoFilename(String videoFileName) {
		this.videoFileName = videoFileName;
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

	public void setTripletDvb(TripletDvb tripletDvb) {
		this.tripletDvb = tripletDvb;
	}

	public TripletDvb getTripletDvd() {
		return (this.tripletDvb == null) ? null : this.tripletDvb;
	}

	// -------------------------------------- overriding Comparable
	@Override
	public int compareTo(DbFullStreamEntry o) {
		if (o == null) {
			return 1;
		}
		if (this.getLcn() == o.getLcn()) {
			return 0;
		}
		return (this.getLcn() < o.getLcn()) ? -1 : 1;
	}

}
