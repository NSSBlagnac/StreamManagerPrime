package com.francetelecom.orangetv.streammanager.server.manager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.francetelecom.orangetv.streammanager.server.service.IMyServices;
import com.francetelecom.orangetv.streammanager.shared.dto.UserProfile;

public class ProfileManager implements IMyServices {

	private static ProfileManager instance;

	public static ProfileManager get() {
		if (instance == null) {
			instance = new ProfileManager();
		}
		return instance;
	}

	private ProfileManager() {
	}

	private ProfilCredential qualifCredential;
	private ProfilCredential adminCredential;

	// ---------------------------------- package methods
	void setQualifCredential(ProfilCredential qualifCredential) {
		this.qualifCredential = qualifCredential;
	}

	void setAdminCredential(ProfilCredential adminCredential) {
		this.adminCredential = adminCredential;
	}

	// ---------------------------------- public methods
	/*
	 * Récupère le UserProfile stocké en session si existe sinon null
	 */
	public UserProfile getSessionUserProfile(HttpServletRequest request) {

		HttpSession session = request.getSession();
		if (session != null) {
			return (UserProfile) session.getAttribute(KEY_SESSION_USER_PROFIL);
		}
		return null;
	}

	/*
	 * Verifie si une session existe avec profil de droit >= profil
	 */
	public boolean isSessionAtLeastUserProfile(UserProfile profil, HttpServletRequest request) {

		// HttpServletRequest request = this.getThreadLocalRequest();
		UserProfile UserProfile = this.getSessionUserProfile(request);
		if (UserProfile != null && UserProfile.ordinal() <= profil.ordinal()) {
			return true;
		}
		return false;
	}

	/*	 
	 * Vérifie le credential pour un profil donné
	 */
	private boolean controlCredential(UserProfile UserProfile, String login, String pwd) {

		if (login == null || pwd == null) {
			return false;
		}

		switch (UserProfile) {
		case admin:
			return this.adminCredential.validate(login, pwd);

		case manager:
			return this.qualifCredential.validate(login, pwd);

		default:
			return true;
		}
	}

	public UserProfile getUserProfileFromCredential(String login, String pwd) {

		for (UserProfile userProfile : UserProfile.values()) {
			if (this.controlCredential(userProfile, login, pwd)) {
				return userProfile;
			}
		}
		return UserProfile.anybody;
	}

	// =========================================== INNER CLASS
	public static class ProfilCredential {

		private final String login;
		private final String pwd;

		public ProfilCredential(String login, String pwd) {
			this.login = login;
			this.pwd = pwd;
		}

		public boolean validate(String loginToControl, String pwdToControl) {
			return (this.login.equals(loginToControl) && this.pwd.equals(pwdToControl));
		}
	}

}
