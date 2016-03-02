package com.francetelecom.orangetv.streammanager.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Structure contenant les toutes informations EIT à injecter
 * 
 * @author sylvie
 * 
 */
public class EitInfoModel implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String VERSION = "1.10";

	private EitGeneral eitGeneral;
	private EitSection presentSection;
	private EitSection followingSection;

	public EitGeneral getEitGeneral() {
		return this.eitGeneral;
	}

	public void setEitGeneral(EitGeneral eitGeneral) {
		this.eitGeneral = eitGeneral;
	}

	public EitSection getPresentSection() {
		return this.presentSection;
	}

	public void setPresentSection(EitSection presentSection) {
		this.presentSection = presentSection;
	}

	public EitSection getFollowingSection() {
		return this.followingSection;
	}

	public void setFollowingSection(EitSection followingSection) {
		this.followingSection = followingSection;
	}

	// ======================================= INNER CLASS
	public static class EitSection implements Serializable {

		private static final long serialVersionUID = 1L;
		private List<EitEvent> listEvents = new ArrayList<EitEvent>(1);

		public List<EitEvent> getListEvents() {
			return this.listEvents;
		}

		public void addEitEvent(EitEvent event) {
			this.listEvents.add(event);
		}
	}

	public static class EitGeneral implements Serializable {

		private static final long serialVersionUID = 1L;
		private String version;
		private String target;

		public String getVersion() {
			return version;
		}

		public void setTarget(String target) {
			this.target = target;
		}

		public String getTarget() {
			return target;
		}

		public EitGeneral() {
			this(null, null);
		}

		public EitGeneral(String version, String target) {
			this.version = version;
			this.target = target;
		}

	}

}
