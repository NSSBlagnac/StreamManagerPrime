package com.francetelecom.orangetv.streammanager.client.controller;

import java.util.logging.Logger;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;

public class TimerController {

	private final static Logger log = Logger.getLogger("TimerController");

	// -------------------- singleton
	private static TimerController instance;

	static TimerController get() {
		if (instance == null) {
			instance = new TimerController();
		}
		return instance;
	}

	private TimerController() {
	}

	// -------------------------------

	private RefreshTimer refreshListStreamTimer;
	private RefreshTimer refreshListVideoTimer;
	private RefreshTimer refreshEditStreamTimer;
	private RefreshTimer refreshSupervisorStatusTimer;

	// ---------------------------------------- package methods
	void createAndScheduleRefreshProfileTimer(int refreshIntervall, final Command action) {

		RefreshTimer timer = new RefreshTimer(true, refreshIntervall, action);
		timer.scheduleRepeating(refreshIntervall);
	}

	void createRefreshListStreamTimer(int refreshIntervall, final Command action) {
		this.stop(this.refreshListStreamTimer);
		this.refreshListStreamTimer = new RefreshTimer(true, refreshIntervall, action);
	}

	void createRefreshListVideoTimer(int refreshIntervall, final Command action) {
		this.stop(this.refreshListVideoTimer);
		this.refreshListVideoTimer = new RefreshTimer(true, refreshIntervall, action);
	}

	void createRefreshSupervisorStatusTimer(int refreshIntervall, final Command action) {
		this.stop(this.refreshSupervisorStatusTimer);
		this.refreshSupervisorStatusTimer = new RefreshTimer(true, refreshIntervall, action);
	}

	void createRefreshEditStreamTimer(final Command action) {

		this.refreshEditStreamTimer = new RefreshTimer(true, 5000, action);
	}

	/*
	 * retourne le timer pour le rafraichissement de la liste des
	 * stream
	 * Retablit la valeur enableRefresh par defaut
	 * 
	 * @return not null timer
	 */
	RefreshTimer getRefreshListStreamTimer() {

		return this.refreshListStreamTimer;
	}

	RefreshTimer getRefreshListVideoTimer() {

		return this.refreshListVideoTimer;
	}

	RefreshTimer getRefreshSupervisorStatusTimer() {
		return this.refreshSupervisorStatusTimer;
	}

	RefreshTimer getRefreshEditStreamTimer() {
		return this.refreshEditStreamTimer;
	}

	void resume(RefreshTimer refreshTimer) {
		if (refreshTimer == null) {
			return;
		}
		refreshTimer.setSuspended(false);
	}

	void resumeAndSchedule(RefreshTimer refreshTimer) {
		if (refreshTimer == null) {
			return;
		}
		refreshTimer.setSuspended(false);
		refreshTimer.doSchedule();
	}

	void resumeAndSchedule(RefreshTimer refreshTimer, int intervall) {
		if (refreshTimer == null) {
			return;
		}
		refreshTimer.setSuspended(false);
		refreshTimer.doSchedule(intervall);
	}

	void resumeAndForceSchedule(RefreshTimer refreshTimer, int intervall) {
		if (refreshTimer == null) {
			return;
		}
		refreshTimer.setSuspended(false);
		refreshTimer.forceSchedule(intervall);
	}

	/*
	 * Recupere le timer en cours.
	 *  l'arrete et le suspend
	 */
	void suspends(RefreshTimer refreshTimer) {
		if (refreshTimer == null) {
			return;
		}
		refreshTimer.cancel();
		refreshTimer.setSuspended(true);
	}

	void active(RefreshTimer refreshTimer, boolean active) {
		if (refreshTimer == null) {
			return;
		}
		refreshTimer.setActif(active);

	}

	void stop(RefreshTimer refreshTimer) {
		if (refreshTimer == null) {
			return;
		}
		refreshTimer.cancel();
		refreshTimer.setActif(false);
		refreshTimer = null;
	}

	void schedule(RefreshTimer refreshTimer) {
		if (refreshTimer == null) {
			return;
		}
		refreshTimer.doSchedule();
	}

	void forceSchedule(RefreshTimer refreshTimer, int delaiMs) {
		if (refreshTimer == null) {
			return;
		}
		refreshTimer.forceSchedule(delaiMs);
	}

	// ======================================= INNER CLASS
	class RefreshTimer extends Timer {

		// actif: dépend du contexte (onglet en cours)
		private boolean actifTimer;

		// on peut suspendre temporairement le timer pendant un process de
		// courte duree
		private boolean suspended = false;

		private final int refreshIntervall;

		private final Command action;

		public void run() {
			if (this.action != null) {
				this.action.execute();
			}
		}

		private RefreshTimer(boolean actifTimer, int refreshIntervall, Command action) {
			this.actifTimer = actifTimer;
			this.refreshIntervall = refreshIntervall;
			this.action = action;
		}

		private void setSuspended(boolean suspended) {
			log.finest("RefreshTimer.suspended(" + suspended + ")");
			this.suspended = suspended;
		}

		private void setActif(boolean actifTimer) {
			log.finest("RefreshTimer.actif(" + actifTimer + ")");
			this.actifTimer = actifTimer;
		}

		// Excecution de la tache si timer actif et !suspended
		private void doSchedule() {

			this.doSchedule(refreshIntervall);
		}

		private void doSchedule(int intervall) {

			if (this.actifTimer && !this.suspended && intervall > 0) {
				this.forceSchedule(intervall);
			}
		}

		// // execution de la tache dans tous les cas
		// void forceSchedule(int delai, Command nextStep) {
		// this.nextStep = nextStep;
		// this._forceSchedule(delai);
		// }

		private void forceSchedule(int delai) {
			log.finest("forceSchedule(" + ")");
			// this.nextStep = null;
			this._forceSchedule(delai);
		}

		private void _forceSchedule(int delai) {
			this.cancel();
			super.schedule(delai);
		}
	}

}
