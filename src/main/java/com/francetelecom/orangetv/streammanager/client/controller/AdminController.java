package com.francetelecom.orangetv.streammanager.client.controller;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.StreamPanel.ResultType;
import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel;
import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.AbstractCmdDatas;
import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.AdminAction;
import com.francetelecom.orangetv.streammanager.client.panel.admin.UploadPanel.UploadCmdDatas;
import com.francetelecom.orangetv.streammanager.client.service.IActionCallback;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdRequest;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoAdminInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.SupervisorStatus;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;

public class AdminController extends AbstractController {

	private final static Logger log = Logger.getLogger("AdminController");

	private static AdminController instance;

	static AdminController get() {
		if (instance == null) {
			instance = new AdminController();
		}
		return instance;
	}

	private AdminController() {
	}

	// ----------------------------------- overriding AbstractController
	@Override
	protected void go() {
		log.info("go()");
		doGetAdminInfo();
		this.initTimers();
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ------------------------------------- public methods

	// ------------------------------------- protected methods

	void doGetAdminInfo() {
		log.config("doGetAdminInfo()...");
		this.rpcService.getAdminInfo(new AsyncCallback<DtoAdminInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				String errorMessage = "Error when getting admin info! : " + caught.getMessage();
				log.severe(errorMessage);
				getAdminPanel().setActionResult(errorMessage, ResultType.error);
			}

			@Override
			public void onSuccess(DtoAdminInfo adminInfo) {
				log.config("getAdminInfo() on success - " + adminInfo.getListLogFilenames().size() + " logs filenames.");
				getAdminPanel().setAdminInfo(adminInfo);
			}
		});
	}

	void beforeExecuteCommand() {

		final CmdRequest cmdRequest = this.getAdminPanel().getDataFromWidget();
		if (!this.getAdminPanel().confirmExecuteCommand()) {
			doExecuteCommand(cmdRequest);
		}
		// la confirmation est demandée uniquement pour les commandes manuelles
		// (mode console)
		else {

			DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Execute command", new String[] {
					"Confirmer l'execution de la commande ", cmdRequest.getCmd(), " sur le serveur multicat?" }, null,
					true, new IActionCallback() {

						@Override
						public void onCancel() {
							showPanelAdminResult("Execute command cancelled! ", false);
							getAdminPanel().unlock();
						}

						@Override
						public void onOk() {

							Scheduler.get().scheduleDeferred(new ScheduledCommand() {

								@Override
								public void execute() {
									doExecuteCommand(cmdRequest);
								}
							});

						}

					});
			WidgetUtils.centerDialogAndShow(dialogBox);
		}
	}

	void refreshSupervisorStatus() {
		AdminAction adminAction = this.getAdminPanel().getCurrentAction();
		if (adminAction == AdminAction.supervisor) {
			TimerController.get().active(TimerController.get().getRefreshSupervisorStatusTimer(), true);
			TimerController.get().resumeAndSchedule(TimerController.get().getRefreshSupervisorStatusTimer(),
					FAST_REFRESH);
		} else {
			TimerController.get().active(TimerController.get().getRefreshSupervisorStatusTimer(), false);
		}
	}

	void doUploadFile() {
		UploadCmdDatas datas = this.getAdminPanel().getUploadDatas();
		if (datas == null) {
			return;
		}
		adminPanelLock();
		this.rpcService.uploadFileToMulticatServer(datas.getScrFilePath(), datas.getTargetFilepath(),
				new AsyncCallback<CmdResponse>() {

					@Override
					public void onFailure(Throwable caught) {
						log.warning("upload file on Failure: " + caught.getMessage());
						adminPanelUnlock();
						showPanelAdminResult("Failure when uploading file! " + buildOnFailureMessage(caught), false);
					}

					@Override
					public void onSuccess(CmdResponse result) {
						log.config("Upload file on Success: " + result.getUsedCommand());
						adminPanelUnlock();
						getAdminPanel().setCmdResponse(result);
					}
				});
	}

	// ---------------------------------------------- private methods
	public static class AdminChangeEvent extends ChangeEvent {

		private final AbstractCmdDatas datas;

		public AdminChangeEvent(AbstractCmdDatas datas) {
			this.datas = datas;
		}
	}

	private void doGetSupervisorStatus() {

		adminPanelLock();
		this.rpcService.getSupervisorStatus(new AsyncCallback<SupervisorStatus>() {

			@Override
			public void onFailure(Throwable caught) {
				log.warning("get supervisor status on Failure: " + caught.getMessage());
				showPanelAdminResult("Error when getting supervisor status! " + buildOnFailureMessage(caught), false);
				adminPanelUnlock();
			}

			@Override
			public void onSuccess(SupervisorStatus result) {
				log.config("Get supervisor status on Success: " + result.name());
				getAdminPanel().setSupervisorStatus(result);
				adminPanelUnlock();
			}
		});
	}

	private void doExecuteCommand(CmdRequest cmdRequest) {
		log.config("doExecuteCommand(): cmd: " + cmdRequest.getCmd());
		adminPanelLock();
		this.rpcService.executeSSHCommand(cmdRequest, new AsyncCallback<CmdResponse>() {

			@Override
			public void onSuccess(CmdResponse cmdResponse) {
				log.config("execute command on Success: " + cmdResponse.getExitValue());
				getAdminPanel().setCmdResponse(cmdResponse);
				adminPanelUnlock();
				refreshSupervisorStatus();
			}

			@Override
			public void onFailure(Throwable caught) {
				log.config("execute command on Failure: " + caught.getMessage());
				showPanelAdminResult("Error when execute command! " + buildOnFailureMessage(caught), false);
				adminPanelUnlock();
				refreshSupervisorStatus();
			}
		});

	}

	private void initTimers() {
		TimerController.get().createRefreshSupervisorStatusTimer(3000, new Command() {

			@Override
			public void execute() {
				doGetSupervisorStatus();
			}
		});

	}

	private AdminPanel getAdminPanel() {
		return this.appController.getMainTabPanel().getAdminPanel();
	}

	private void adminPanelUnlock() {
		this.getAdminPanel().unlock();
	}

	private void adminPanelLock() {
		this.getAdminPanel().lock();
	}

	private void showPanelAdminResult(String message, boolean success) {
		this.showPanelAdminResult(message, success ? ResultType.success : ResultType.error);
	}

	private void showPanelAdminResult(String message, ResultType type) {
		this.getAdminPanel().setActionResult(message, type);
	}

}
