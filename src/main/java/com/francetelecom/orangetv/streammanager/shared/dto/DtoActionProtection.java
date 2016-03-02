package com.francetelecom.orangetv.streammanager.shared.dto;

/**
 * Protection d'une action utilisateur
 * soit pour une raison de profile soit pour une raison fonctionnelle
 * 
 * @author ndmz2720
 *
 */
public class DtoActionProtection implements IDto {

	private static final long serialVersionUID = 1L;

	public enum Rules {
		profile, functionnal
	}

	private boolean active;
	private String unactiveMessage;
	private Rules rules;

	// ----------------------------- constructor
	public DtoActionProtection() {
		this(false);
	}

	public DtoActionProtection(boolean active) {
		this.active = active;
	}

	// ---------------------------- accessors
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active, String unactiveMessage, Rules rules) {
		this.active = active;
		this.unactiveMessage = active ? null : unactiveMessage;
		this.rules = rules;
	}

	public String getMessage() {
		return unactiveMessage == null ? "" : unactiveMessage;
	}

	public Rules getRules() {
		return rules;
	}

}
