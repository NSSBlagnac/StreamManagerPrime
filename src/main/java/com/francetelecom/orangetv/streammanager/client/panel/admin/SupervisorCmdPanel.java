package com.francetelecom.orangetv.streammanager.client.panel.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.AbstractCmdDatas;
import com.francetelecom.orangetv.streammanager.client.util.StatusUtils;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.francetelecom.orangetv.streammanager.shared.dto.SupervisorStatus;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel donnant le status du process multicat-supervisor (started|stopped)
 * Possibilite de le demarrer, de l'arreter ou de rafraichir le status
 * 
 * @author ndmz2720
 *
 */
public class SupervisorCmdPanel extends AbstractCmdPanel {

	private final static Logger log = Logger.getLogger("SupervisorCmdPanel");

	enum ActionSupervisor {
		start(true), stop(true), refresh(false);

		private final boolean sshCommand;

		private ActionSupervisor(boolean sshCommand) {
			this.sshCommand = sshCommand;
		}
	}

	private final VerticalPanel main = new VerticalPanel();

	private SimplePanel panelStatus = new SimplePanel();
	private SupervisorActionButton btStart = new SupervisorActionButton(ActionSupervisor.start);
	private SupervisorActionButton btStop = new SupervisorActionButton(ActionSupervisor.stop);
	private SupervisorActionButton btRefresh = new SupervisorActionButton(ActionSupervisor.refresh);

	private ActionSupervisor pendingAction = null;

	// ------------------------------------- constructor
	SupervisorCmdPanel(SupervisorStatus supervisorStatus) {

		this.initWidget(this.buildMainPanel());
		this.initComposants(supervisorStatus);
		this.initHandlers();
	}

	// -------------------------------- overriding AbstractCmdPanel
	@Override
	AbstractCmdDatas getDataFromWidget() {

		return this.pendingAction == null ? null : new SupervisorCmdDatas(this.pendingAction);
	}

	@Override
	void lock() {
	}

	@Override
	void setCmdResponse(CmdResponse cmdResponse) {

	}

	@Override
	Logger getLog() {
		return log;
	}

	// ----------------------------------------
	void setSupervisorStatus(SupervisorStatus supervisorStatus) {

		// label status
		this.panelStatus.clear();
		Label labelStatus = StatusUtils.buildLabelStatus(supervisorStatus);
		this.panelStatus.add(labelStatus);

		this.btStop.setVisible(false);
		this.btStart.setVisible(false);

		// button start/stop
		if (supervisorStatus == SupervisorStatus.started) {
			this.btStop.setVisible(true);
		} else if (supervisorStatus == SupervisorStatus.stopped) {
			this.btStart.setVisible(true);

		}

	}

	// --------------------------------------- private methods
	private Widget buildMainPanel() {
		this.main.setSpacing(PANEL_SPACING);

		HorizontalPanel hpPanel = new HorizontalPanel();
		hpPanel.setWidth(MAX_WIDTH);
		hpPanel.setSpacing(PANEL_SPACING);
		hpPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpPanel.add(new Label("Supervisor status: "));
		hpPanel.add(this.panelStatus);
		hpPanel.add(this.btStart);
		hpPanel.add(this.btStop);
		hpPanel.add(this.btRefresh);

		this.main.add(hpPanel);
		this.main.add(this.btExecute);

		return this.main;
	}

	private void initHandlers() {

		ClickHandler actionClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Object source = event.getSource();
				if (source != null && source instanceof SupervisorActionButton) {
					SupervisorActionButton button = (SupervisorActionButton) source;
					pendingAction = button.action;

					SupervisorStatus newStatus = null;
					switch (pendingAction) {
					case start:
						newStatus = SupervisorStatus.starting;
						break;
					case stop:
						newStatus = SupervisorStatus.stopping;
						break;
					default:
						newStatus = SupervisorStatus.unknown;
						break;
					}

					setSupervisorStatus(newStatus);
					if (argumentsChangeHandler != null && pendingAction.sshCommand) {
						// pour stop && start action
						argumentsChangeHandler.onChange(null);
					}
					// dans tous les cas on lance l'execution
					SupervisorCmdPanel.this.autoexecute();

				} else {
					pendingAction = null;
				}
			}
		};

		this.btStart.addClickHandler(actionClickHandler);
		this.btStop.addClickHandler(actionClickHandler);
		this.btRefresh.addClickHandler(actionClickHandler);
	}

	private void initComposants(SupervisorStatus status) {
		this.btStart.addStyleName(STYLE_IMG_ACTION + " " + STYLE_IMG_START);
		this.btStart.setTitle("start supervisor...");
		this.btStop.addStyleName(STYLE_IMG_ACTION + " " + STYLE_IMG_BIG_STOP);
		this.btStop.setTitle("stop supervisor...");
		this.btRefresh.addStyleName(STYLE_IMG_ACTION + " " + STYLE_IMG_REFRESH);
		this.btRefresh.setTitle("refresh status ...");

		this.btExecute.setVisible(false);

		this.setSupervisorStatus(status);
	}

	// ====================================== INNER CLASS
	private static class SupervisorActionButton extends Button {

		private ActionSupervisor action;

		private SupervisorActionButton(ActionSupervisor action) {
			this.action = action;
		}

	}

	public static class SupervisorCmdDatas extends AbstractCmdDatas {

		private ActionSupervisor action;

		public ActionSupervisor getAction() {
			return action;
		}

		private SupervisorCmdDatas(ActionSupervisor action) {

			this.action = action;
			super.setSshCommand(this.action.sshCommand);
			super.setConfirmCommand(true);
		}
	}

}
