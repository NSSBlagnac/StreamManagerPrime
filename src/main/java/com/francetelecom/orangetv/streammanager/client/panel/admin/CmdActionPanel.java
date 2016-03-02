package com.francetelecom.orangetv.streammanager.client.panel.admin;

import java.util.ArrayList;
import java.util.List;

import com.francetelecom.orangetv.streammanager.client.panel.AbstractPanel;
import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.AdminAction;
import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.CmdClickEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel de bouton pour l'AdminPanel
 * 
 * @author ndmz2720
 *
 */
class CmdActionPanel extends AbstractPanel {

	private final Panel main = new SimplePanel();

	private final ActionButton btConsole = new ActionButton("Console", AdminAction.console);
	private final ActionButton btLogs = new ActionButton("show logs", AdminAction.logs);
	private final ActionButton btProcess = new ActionButton("show process", AdminAction.process);
	private final ActionButton btSupervisor = new ActionButton("Supervisor", AdminAction.supervisor);
	private final ActionButton btVideos = new ActionButton("Videos", AdminAction.videos);
	private final ActionButton btUpload = new ActionButton("Upload", AdminAction.upload);
	// private final ActionButton btGwtUploadOld = new
	// ActionButton("Gwt Upload", AdminAction.gwtuploadOld);
	private final ActionButton btNewGwtUpload = new ActionButton("Gwt Upload", AdminAction.gwtupload);

	private final List<ActionButton> listActionButton = new ArrayList<>();

	private ClickHandler cmdClickHandler;

	CmdActionPanel() {
		this.initWidget(this.buildMainPanel());
		this.btConsole.setSelected(true);

	}

	// ---------------------------------------- package methods
	void setCmdActionClickHandler(ClickHandler cmdHandler) {
		this.cmdClickHandler = cmdHandler;
	}

	void lock() {
		this.setEnabled(false);
	}

	void unlock() {
		this.setEnabled(true);
	}

	// ---------------------------------------- private methods
	private void setEnabled(boolean enabled) {
		if (this.listActionButton == null) {
			return;
		}
		for (ActionButton actionButton : listActionButton) {
			actionButton.setEnabled(enabled);
		}
	}

	private Widget buildMainPanel() {

		this.listActionButton.add(this.btConsole);
		this.listActionButton.add(this.btLogs);
		this.listActionButton.add(this.btProcess);
		this.listActionButton.add(this.btSupervisor);
		this.listActionButton.add(this.btVideos);
		this.listActionButton.add(this.btUpload);
		// this.listActionButton.add(this.btGwtUploadOld);
		this.listActionButton.add(this.btNewGwtUpload);

		this.main.addStyleName(PANEL_SHOW_ACTION);

		VerticalPanel vpButton = new VerticalPanel();
		vpButton.setSpacing(PANEL_SPACING);

		for (ActionButton actionButton : this.listActionButton) {
			vpButton.add(actionButton);
		}

		this.main.add(vpButton);
		return this.main;
	}

	private void onSelectAction(AdminAction action) {

		for (ActionButton actionButton : this.listActionButton) {
			actionButton.setSelected(action == actionButton.action);
		}

		this.cmdClickHandler.onClick(new CmdClickEvent(action));
	}

	// ====================================== INNER CLASS
	private class ActionButton extends Button {

		private final AdminAction action;

		private void setSelected(boolean selected) {

			if (selected) {
				this.addStyleName(STYLE_SELECTED);
			} else {
				this.removeStyleName(STYLE_SELECTED);
			}
		}

		private ActionButton(String text, final AdminAction action) {
			super(text);
			this.setStyleName(BUTTON_CMD_STYLE);
			this.action = action;

			this.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					onSelectAction(action);

				}
			});

		}
	}

}
