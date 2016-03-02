package com.francetelecom.orangetv.streammanager.shared.dto;

import java.io.Serializable;

import com.francetelecom.orangetv.streammanager.shared.util.ValueHelper;

public class TripletDvb implements Serializable {

	private static final long serialVersionUID = 1L;

	private int tsid;
	private int sid;
	private int onid;

	// -------------------------------- constructor
	public TripletDvb() {

	}

	public TripletDvb(int tsid, int sid, int onid) {
		this.tsid = tsid;
		this.sid = sid;
		this.onid = onid;
	}

	public TripletDvb(String tsid_sid_onid) {
		if (tsid_sid_onid != null) {
			String[] triplets = tsid_sid_onid.split(":");
			if (triplets != null && triplets.length == 3) {
				this.tsid = ValueHelper.getIntValue(triplets[0], 0);
				this.sid = ValueHelper.getIntValue(triplets[1], 0);
				this.onid = ValueHelper.getIntValue(triplets[2], 0);
			}
		}
	}

	// ------------------------------- public methods
	public int[] getItems() {

		return new int[] { this.tsid, this.sid, this.onid };
	}

	// -------------------------------- accessors

	public int getTsid() {
		return tsid;
	}

	public void setTsid(int tsid) {
		this.tsid = tsid;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public int getOnid() {
		return onid;
	}

	public void setOnid(int onid) {
		this.onid = onid;
	}

	// ------------------------- overriding Object
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TripletDvb tripletDvb = (TripletDvb) o;

		return this.getTsid() == tripletDvb.getTsid() && this.getSid() == tripletDvb.getSid()
				&& this.getOnid() == tripletDvb.getOnid();
	}

	@Override
	public int hashCode() {
		int result = 0;
		result = 31 * result + this.tsid;
		result = 31 * result + this.sid;
		result = 31 * result + this.onid;

		return result;
	}

	@Override
	public String toString() {
		return this.getTsid() + ":" + this.getSid() + ":" + this.getOnid();
	}

}
