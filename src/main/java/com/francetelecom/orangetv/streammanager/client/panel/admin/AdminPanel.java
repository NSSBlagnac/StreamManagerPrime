package com.francetelecom.orangetv.streammanager.client.panel.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.controller.AppController.Action;
import com.francetelecom.orangetv.streammanager.client.controller.AppController.ActionClickEvent;
import com.francetelecom.orangetv.streammanager.client.panel.AbstractPanel;
import com.francetelecom.orangetv.streammanager.client.panel.admin.GwtUploadPanel.GwtUpdloadData2;
import com.francetelecom.orangetv.streammanager.client.panel.admin.LogReaderCmdPanel.LogCmdDatas;
import com.francetelecom.orangetv.streammanager.client.panel.admin.ProcessCmdPanel.ProcessCmdDatas;
import com.francetelecom.orangetv.streammanager.client.panel.admin.SupervisorCmdPanel.ActionSupervisor;
import com.francetelecom.orangetv.streammanager.client.panel.admin.SupervisorCmdPanel.SupervisorCmdDatas;
import com.francetelecom.orangetv.streammanager.client.panel.admin.UploadPanel.UploadCmdDatas;
import com.francetelecom.orangetv.streammanager.client.panel.admin.VideoCmdPanel.ActionVideo;
import com.francetelecom.orangetv.streammanager.client.panel.admin.VideoCmdPanel.VideoCmdDatas;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdRequest;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoAdminInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoLogFilename;
import com.francetelecom.orangetv.streammanager.shared.dto.SupervisorStatus;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel d'administration pour user de profile admin uniquement
 * 
 * @author ndmz2720
 *
 */
public class AdminPanel extends AbstractPanel {

	private final static Logger log = Logger.getLogger("AdminPanel");

	public enum AdminAction {
		console, logs, supervisor, process, upload, gwtupload, videos
	}

	private final HorizontalPanel main = new HorizontalPanel();
	private final SimplePanel cmdContainerPanel = new SimplePanel();

	private AbstractCmdPanel cmdPanel;
	private final ConsolePanel consolePanel = new ConsolePanel();
	private final CmdActionPanel actionPanel = new CmdActionPanel();

	private ClickHandler actionClickHandler;
	private ChangeHandler argumentsChangeHandler;
	private ClickHandler executeClickHandler;
	private DtoAdminInfo adminInfo;

	private AdminAction currentAction;
	private boolean confirmCommand = true;
	private AbstractCmdDatas nextCmdCommand = null;
	private SupervisorStatus supervisorStatus = SupervisorStatus.unknown;

	private final Map<AdminAction, AbstractCmdPanel> mapCmdPanel = new HashMap<>();

	private boolean cmdRunning = false;

	// -------------------------------------- constructor
	public AdminPanel() {
		this.initComposants();
		this.initWidget(this.buildMainPanel());
		this.initHandlers();

		this.onSelectAction(AdminAction.console);
	}

	// ---------------------------------------- public methods

	public AdminAction getCurrentAction() {
		return this.currentAction;
	}

	public boolean confirmExecuteCommand() {
		return this.confirmCommand;
	}

	public void setSupervisorStatus(SupervisorStatus supervisorStatus) {
		this.supervisorStatus = supervisorStatus;

		if (this.currentAction == AdminAction.supervisor) {
			if (this.cmdPanel instanceof SupervisorCmdPanel) {
				((SupervisorCmdPanel) this.cmdPanel).setSupervisorStatus(supervisorStatus);
			}
		}
	}

	public void setAdminInfo(DtoAdminInfo adminInfo) {
		this.adminInfo = adminInfo;
	}

	public void bindHandlers(ClickHandler actionClickHandler) {
		this.actionClickHandler = actionClickHandler;

	}

	public void setCmdResponse(CmdResponse cmdResponse) {
		this.consolePanel.setCmdResponse(cmdResponse);
		this.cmdPanel.setCmdResponse(cmdResponse);

		this.waiting(false);
		if (this.nextCmdCommand != null) {
			if (cmdResponse != null && cmdResponse.isSuccess()) {
				this.refreshCommandLine(this.nextCmdCommand);
				this.doExecute(this.nextCmdCommand);
			}
			this.nextCmdCommand = null;
		}
	}

	public CmdRequest getDataFromWidget() {
		return consolePanel.getDataFromWidget();
	}

	public void unlock() {
		this.consolePanel.unlock();
		this.cmdPanel.unlock();
		this.actionPanel.unlock();
		this.waiting(false);

	}

	public void lock() {
		this.consolePanel.lock();
		this.cmdPanel.lock();
		this.actionPanel.lock();
	}

	public UploadCmdDatas getUploadDatas() {
		if (this.currentAction == AdminAction.upload) {
			return (UploadCmdDatas) this.cmdPanel.getDataFromWidget();
		}
		return null;
	}

	// ------------------------------------------- private methods

	private AbstractCmdPanel getCmdPanel(AdminAction action) {

		AbstractCmdPanel panel = this.mapCmdPanel.get(action);
		if (panel == null) {

			switch (action) {
			case console:
				panel = new CommandPanel();
				break;
			case logs:
				panel = new LogReaderCmdPanel(this.getListLogFilenames());
				break;
			case supervisor:
				panel = new SupervisorCmdPanel(this.supervisorStatus);
				break;
			case process:
				panel = new ProcessCmdPanel(this.getListProcessNames());
				break;

			case upload:
				panel = new UploadPanel(this.adminInfo.getUploadSrcPath(), this.adminInfo.getUploadTargetPath());
				break;

			case gwtupload:
				panel = new GwtUploadPanel(this.adminInfo.getMaxUploadSizeMo());
				break;

			case videos:
				panel = new VideoCmdPanel();
				break;
			}
			this.mapCmdPanel.put(action, panel);

		}
		AbstractCmdDatas datas = panel.getDataFromWidget();
		refreshCommandLine(datas);
		return panel;
	}

	@SuppressWarnings("incomplete-switch")
	private void onSelectAction(AdminAction action) {

		if (action == this.currentAction) {
			return;
		}

		this.currentAction = action;

		this.consolePanel.protectCmdManualEntry(action != AdminAction.console);
		this.consolePanel.clean(action != AdminAction.console);

		this.cmdPanel = this.getCmdPanel(action);
		this.cmdPanel.setExecuteClickHandler(this.getExecuteClickHandler());
		this.cmdPanel.setChangeClickHandler(this.getArgumentsChangeHandler());

		this.cmdContainerPanel.clear();
		this.cmdContainerPanel.add(this.cmdPanel);

		switch (action) {
		case supervisor:
			this.refreshSupervisorStatus();
			break;

		case upload:
		case gwtupload:
			this.consolePanel.hideCommandLine();
			break;

		}

	}

	private void waiting(boolean waiting) {
		cmdRunning = waiting;
		if (waiting) {
			this.main.addStyleName(STYLE_CURSOR_WAIT);
		} else {
			this.main.removeStyleName(STYLE_CURSOR_WAIT);
		}
	}

	/*
	 * Si les arguments du cmdCommand changent alors on reconstruit la ligne de commande SSH.
	 */
	private ChangeHandler getArgumentsChangeHandler() {

		log.config("getArgumentsChangeHandler()");
		if (this.argumentsChangeHandler == null) {
			this.argumentsChangeHandler = new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					AbstractCmdDatas datas = cmdPanel.getDataFromWidget();
					refreshCommandLine(datas);
				}
			};
		}

		return this.argumentsChangeHandler;
	}

	private void refreshCommandLine(AbstractCmdDatas datas) {

		if (datas != null && datas.isSshCommand()) {
			String cmd = buildLinuxCommandFromDatas(datas);
			if (cmd != null) {
				consolePanel.setCommandLine(cmd);
			} else {
				consolePanel.clean(true);
			}
		}
	}

	/*
	 * Apres activation du boutton execute du cmdPanel (par user ou programmatiquement)
	 * - soit on execute la commande ssh 
	 * - soit on lance un process specifique
	 */
	private ClickHandler getExecuteClickHandler() {

		if (this.executeClickHandler == null) {
			this.executeClickHandler = new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					log.config("onClick(): " + event.getSource());
					event.stopPropagation();
					AbstractCmdDatas datas = cmdPanel.getDataFromWidget();
					doExecute(datas);
				}
			};
		}
		return this.executeClickHandler;
	}

	private void doExecute(AbstractCmdDatas datas) {

		if (datas == null || this.cmdRunning) {
			return;
		}
		consolePanel.clean(false);
		nextCmdCommand = datas.getNext();
		if (datas.isSshCommand()) {
			confirmCommand = datas.isConfirmCommand();
			doExecuteSSHCommand();
		} else {
			consolePanel.hideCommandLine();
			doSpecificProcess(datas);
		}
	}

	private void doExecuteSSHCommand() {

		log.config("doExecuteSSHCommand()");
		if (this.actionClickHandler != null) {
			this.consolePanel.clean(false);
			this.waiting(true);
			ActionClickEvent clickEvent = new ActionClickEvent(Action.executeCmd);
			this.actionClickHandler.onClick(clickEvent);
		}
	}

	private void refreshSupervisorStatus() {
		if (this.actionClickHandler != null) {
			this.actionClickHandler.onClick(new ActionClickEvent(Action.refreshSupervisorStatus));
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void doSpecificProcess(AbstractCmdDatas datas) {

		log.config("doSpecificProcess()");
		if (this.currentAction == null || datas == null) {
			return;
		}
		ClickEvent clickEvent = null;
		switch (this.currentAction) {
		case supervisor:
			if (((SupervisorCmdDatas) datas).getAction() == ActionSupervisor.refresh) {
				// uniquement pour refresh supervisor status
				clickEvent = new ActionClickEvent(Action.refreshSupervisorStatus);
			}
			break;
		case upload:
			clickEvent = new ActionClickEvent(Action.uploadFile);
			break;

		case gwtupload: {
			GwtUpdloadData2 gwtUploadDatas = (GwtUpdloadData2) datas;
			CmdResponse cmdResponse = gwtUploadDatas.getCmdResponse();
			this.consolePanel.setCmdResponse(cmdResponse);
		}
			break;
		}
		if (clickEvent != null) {
			this.actionClickHandler.onClick(clickEvent);
		}
	}

	@SuppressWarnings("incomplete-switch")
	private String buildLinuxCommandFromDatas(AbstractCmdDatas datas) {

		if (this.currentAction == null) {
			return null;
		}
		switch (this.currentAction) {
		case supervisor:
			return this.buildSupervisorActionFromDatas((SupervisorCmdDatas) datas);
		case logs:
			return this.buildReadLogCommandFromDatas((LogCmdDatas) datas);
		case process:
			return this.buildShowRunningProcessFromDatas((ProcessCmdDatas) datas);
		case videos:
			return this.buildVideoCommandFromDatas((VideoCmdDatas) datas);

		}
		return null;
	}

	private String buildVideoCommandFromDatas(VideoCmdDatas datas) {

		if (datas.getActionVideo() == null) {
			return null;
		}
		String videoFilename = datas.getVideoName();
		String binPathname = this.adminInfo.getMulticatBinPath();
		String videoPathname = this.adminInfo.getMulticatVideoPath();
		if (videoPathname == null) {
			return null;
		}
		if (!videoPathname.endsWith("/")) {
			videoPathname += "/";
		}
		if (datas.getActionVideo() != ActionVideo.list && videoFilename == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		switch (datas.getActionVideo()) {
		case list: {

			sb.append("ls ");
			sb.append(videoPathname);
			break;
		}
		case delete: {

			sb.append("sudo rm ");
			sb.append(videoPathname);
			sb.append(videoFilename);
			break;
		}

		case auxfile: {

			sb.append("cd ");
			sb.append(videoPathname);
			sb.append("; ");

			sb.append("PID=$(../bin/mpeg_print_pcr < ");
			sb.append(videoFilename);
			sb.append("); ");

			sb.append("sudo ingests -p $PID ");
			sb.append(videoFilename);

			break;
		}

		// ./dvb_print_si -x xml -T PMT <
		// /usr/local/multicat-tools/videos/canal+crypt.ts | grep -v ERROR
		case showPmt: {

			sb.append("cd ");
			sb.append(binPathname);
			sb.append("; ");

			sb.append("./dvb_print_si -x xml -T PMT  < ");
			sb.append(videoPathname);
			sb.append(videoFilename);
			sb.append(" | grep -v ERROR");

			break;
		}

		}

		return sb.toString();
	}

	// uniquement pour action start | stop supervisor
	private String buildSupervisorActionFromDatas(SupervisorCmdDatas datas) {

		if (datas.getAction() == null || datas.getAction() == ActionSupervisor.refresh) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("sudo ");
		sb.append(this.adminInfo.getSupervisorScriptPathname());
		sb.append(" ");
		sb.append(datas.getAction().name());
		return sb.toString();
	}

	private String buildShowRunningProcessFromDatas(ProcessCmdDatas datas) {

		StringBuilder sb = new StringBuilder();
		sb.append("ps u -A | grep ");
		sb.append(datas.getProcessName());

		return sb.toString();
	}

	private String buildReadLogCommandFromDatas(LogCmdDatas datas) {

		StringBuilder sb = new StringBuilder();
		// tail or cat
		sb.append(datas.getCommand());
		sb.append(" ");
		// si tail alors
		if (datas.isTailCommand() && datas.getCount() > 0) {

			sb.append("-n ");
			sb.append(datas.getCount());
			sb.append(" ");
		}
		sb.append(this.getLogPath(datas.getFilename()));
		if (datas.getGrep() != null) {
			sb.append(" | grep ");
			sb.append(datas.getGrep());
		}

		return sb.toString();
	}

	private String getLogPath(String logfilename) {

		if (this.adminInfo != null) {

			List<DtoLogFilename> listLogFilenames = this.adminInfo.getListLogFilenames();
			for (DtoLogFilename dtoLogFilename : listLogFilenames) {
				if (dtoLogFilename.getName().equals(logfilename)) {
					return dtoLogFilename.getPathname();
				}
			}
		}
		return "";
	}

	private List<String> getListLogFilenames() {
		if (this.adminInfo != null) {

			List<DtoLogFilename> listLogFilenames = this.adminInfo.getListLogFilenames();
			List<String> filenames = new ArrayList<>(listLogFilenames.size());
			for (DtoLogFilename filename : listLogFilenames) {
				filenames.add(filename.getName());
			}
			log.config("getListLogFilenames() filenames: " + filenames.size());
			return filenames;
		} else {
			log.warning("getListLogFilenames() admin info empty!");
			return new ArrayList<>(0);
		}

	}

	private List<String> getListProcessNames() {
		if (this.adminInfo != null) {
			return this.adminInfo.getListProcessNames();
		} else {
			return new ArrayList<>(0);
		}
	}

	private void initHandlers() {
		this.actionPanel.setCmdActionClickHandler(this.buildCmdActionClickHandler());
	}

	private ClickHandler buildCmdActionClickHandler() {

		return new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (event instanceof CmdClickEvent) {
					CmdClickEvent cmdEvent = (CmdClickEvent) event;
					onSelectAction(cmdEvent.action);
				}

			}
		};
	}

	private Widget buildMainPanel() {

		this.main.setSpacing(PANEL_SPACING);
		this.main.setWidth(MAX_WIDTH);
		this.main.add(this.actionPanel);
		this.main.setCellWidth(this.actionPanel, "160px"); // mettre css float

		VerticalPanel vpCommand = new VerticalPanel();
		vpCommand.setWidth(MAX_WIDTH);
		vpCommand.setSpacing(PANEL_SPACING);
		this.cmdContainerPanel.setWidth(MAX_WIDTH);
		vpCommand.add(this.cmdContainerPanel);
		vpCommand.add(this.consolePanel);

		this.main.add(vpCommand);

		return this.main;
	}

	private void initComposants() {

	}

	// ==================================================
	static class CmdClickEvent extends ClickEvent {

		private final AdminAction action;

		CmdClickEvent(AdminAction action) {
			this.action = action;
		}
	}

	public static abstract class AbstractCmdDatas {

		private boolean sshCommand = true;
		private boolean confirmCommand = false;

		// eventuellement next action a enchainer
		private AbstractCmdDatas next;

		public AbstractCmdDatas getNext() {
			return next;
		}

		public void setNext(AbstractCmdDatas next) {
			this.next = next;
		}

		public boolean isConfirmCommand() {
			return this.confirmCommand;
		}

		public void setConfirmCommand(boolean confirmCommand) {
			this.confirmCommand = confirmCommand;
		}

		public boolean isSshCommand() {
			return sshCommand;
		}

		public void setSshCommand(boolean sshCommand) {
			this.sshCommand = sshCommand;
		}

	}
}
