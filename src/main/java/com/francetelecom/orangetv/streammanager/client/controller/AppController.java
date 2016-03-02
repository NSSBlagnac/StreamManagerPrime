package com.francetelecom.orangetv.streammanager.client.controller;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.ActionPanel;
import com.francetelecom.orangetv.streammanager.client.panel.ConnectionPanel;
import com.francetelecom.orangetv.streammanager.client.panel.ConnectionPanel.Credential;
import com.francetelecom.orangetv.streammanager.client.panel.MainTabPanel;
import com.francetelecom.orangetv.streammanager.client.panel.StreamPanel.ResultType;
import com.francetelecom.orangetv.streammanager.client.panel.eit.EitJsonPanel;
import com.francetelecom.orangetv.streammanager.client.service.IActionCallback;
import com.francetelecom.orangetv.streammanager.client.service.IStreamMulticatServiceAsync;
import com.francetelecom.orangetv.streammanager.client.util.LocalStorageManager;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils.MyDialogBox;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfoForList;
import com.francetelecom.orangetv.streammanager.shared.dto.UserProfile;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;
import com.francetelecom.orangetv.streammanager.shared.model.ServerInfoBean;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AppController extends AbstractController {

	private final static Logger log = Logger.getLogger("AppController");

	public enum Action {
		start, stop, startOff, stopOff, showEit, sendEit, editStream, createStream, deleteStream, createVideo, editVideo, deleteVideo, executeCmd, changeStreamStatus, refreshSupervisorStatus, uploadFile, connectUser
	}

	public final static int TAB_MAIN_STREAM = 0;
	public final static int TAB_MAIN_EIT = 1;
	public final static int TAB_MAIN_VIDEO = 2;
	public final static int TAB_MAIN_ADMIN = 3;

	private VerticalPanel main = new VerticalPanel();

	private ActionPanel actionPanel;
	private EitJsonPanel jsonPanel;
	private MainTabPanel mainTabPanel;

	private String currentTarget = null;

	private ServerInfoBean serverInfo = new ServerInfoBean();

	private UserProfile currentUserProfile;

	// ---------------------------------------------------constructor
	public AppController(IStreamMulticatServiceAsync rpcService) {
		log.info("new AppController()");
		this.rpcService = rpcService;

		StreamController.get().init(this, rpcService);
		VideoController.get().init(this, rpcService);
		EitController.get().init(this, rpcService);
		AdminController.get().init(this, rpcService);

	}

	// ----------------------------------- overriding AbstractController
	@Override
	protected void go() {
		log.info("go()");
		this.bind();
		this.doGetRefreshDataInterval();
		this.restoreParamsFromLocalStorage();
		this.doGetCurrentUserProfil();

		StreamController.get().go();
		VideoController.get().go();
		EitController.get().go();
		AdminController.get().go();

		TimerController.get().createAndScheduleRefreshProfileTimer(PROFILE_REFRESH, new Command() {

			@Override
			public void execute() {
				doGetCurrentUserProfil();
			}
		});
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ---------------------------------------------------- public methods
	public void go(HasWidgets eitContainer) {

		log.info("go(eitContainer)");
		this.main.setSpacing(PANEL_SPACING);

		this.actionPanel = new ActionPanel();
		this.jsonPanel = new EitJsonPanel();
		this.mainTabPanel = new MainTabPanel(this.jsonPanel);

		this.main.add(actionPanel);
		this.main.add(mainTabPanel);

		eitContainer.add(this.main);
		this.go();

	}

	// -------------------------------------------- package methods
	String getCurrentTarget() {
		return this.currentTarget;
	}

	VideoDescription createVideoDescription(int videoId) {

		FullVideoInfo entry = this.mainTabPanel.getVideoPanel().getVideoInfo(videoId);
		String text = (entry == null) ? "Wrong entry!! " + videoId : entry.getName() + "(" + entry.getFormat() + " - "
				+ entry.getResolution() + ")";

		return new VideoDescription(text);
	}

	StreamDescription createStreamDescription(int eitId) {

		StreamInfoForList entry = this.mainTabPanel.getStreamPanel().getDbStreamInfo(eitId);
		String text = (entry == null) ? "Wrong entry!! " + eitId : ("LCN: " + entry.getLcn() + " - User: " + entry
				.getUser());

		return new StreamDescription(text, (entry == null) ? false : entry.isEitToInject());
	}

	EitJsonPanel getJsonPanel() {
		return this.jsonPanel;
	}

	MainTabPanel getMainTabPanel() {
		return this.mainTabPanel;
	}

	ActionPanel getActionPanel() {
		return this.actionPanel;
	}

	void clearAllMessages(boolean waiting) {
		final int tabMainIndex = this.mainTabPanel.getSelectedTab();

		String message = waiting ? "waiting..." : "";
		switch (tabMainIndex) {
		case TAB_MAIN_STREAM:
			StreamController.get().showPanelStreamResult(message, ResultType.warn);
			break;
		case TAB_MAIN_VIDEO:
			VideoController.get().showPanelVideoResult(message, ResultType.warn);
			break;
		}

	}

	void enableButtonInActionPanel(int tabMainIndex) {

		tabMainIndex = (tabMainIndex < 0) ? this.mainTabPanel.getSelectedTab() : tabMainIndex;

		boolean tabStream = tabMainIndex == TAB_MAIN_STREAM;
		boolean tabVideo = tabMainIndex == TAB_MAIN_VIDEO;
		boolean tabEit = tabMainIndex == TAB_MAIN_EIT;

		// condition d'activation du send button
		boolean activeSendButton = tabEit && this.getCurrentTarget() != null;
		if (activeSendButton) {
			StreamInfoForList streamInfo = mainTabPanel.getStreamPanel().getDbStreamInfo(
					Integer.parseInt(this.getCurrentTarget()));
			log.config("enableButtonInActionPanel() streamId: " + streamInfo.getId() + " - canUpdate: "
					+ streamInfo.getDtoProtection().getActionUpdate().isActive());
			activeSendButton = streamInfo.getDtoProtection().getActionUpdate().isActive();
		}

		ButtonActionStates buttonActionStates = new ButtonActionStates();
		buttonActionStates.setEnableCreateStream(tabStream);
		buttonActionStates.setEnableCreateVideo(tabVideo);
		buttonActionStates.setEnableSendEit(activeSendButton);
		actionPanel.enableButton(buttonActionStates);

	}

	void displayCurrentTarget() {
		if (this.getCurrentTarget() != null) {
			int eitId = Integer.parseInt(this.getCurrentTarget());
			actionPanel.setTarget(this.createStreamDescription(eitId));
		}
	}

	void displayCurrentTarget(int eitId) {
		this.currentTarget = eitId + "";
		this.displayCurrentTarget();
	}

	// ---------------------------------------------------- private methods
	private void bind() {

		ClickHandler actionClickHandler = this.buildActionClickHandler();
		this.mainTabPanel.bindHandlers(this.buildTabSelectionHandler(), actionClickHandler);
		this.actionPanel.bindHandlers(actionClickHandler);
		this.jsonPanel.bindHandlers(this.buildShowJsonClickHandler(), this.buildImportJsonClickHandler());

	}

	private SelectionHandler<Integer> buildTabSelectionHandler() {
		return new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int selectedIndex = event.getSelectedItem();
				onTabPanelSelected(selectedIndex);
			}
		};
	}

	private void onTabPanelSelected(int selectedIndex) {

		// index du tabPanel principal
		int tabMainIndex = this.mainTabPanel.getSelectedTab();

		boolean tabStream = tabMainIndex == TAB_MAIN_STREAM;
		boolean tabVideo = tabMainIndex == TAB_MAIN_VIDEO;
		boolean tabEit = tabMainIndex == TAB_MAIN_EIT;
		boolean tabAdmin = tabMainIndex == TAB_MAIN_ADMIN;
		enableButtonInActionPanel(tabMainIndex);

		TimerController.get().active(TimerController.get().getRefreshListStreamTimer(), tabStream);
		TimerController.get().active(TimerController.get().getRefreshListVideoTimer(), tabVideo);

		if (tabStream) {
			TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListStreamTimer(),
					FAST_REFRESH);
		} else if (tabVideo) {
			TimerController.get()
					.resumeAndForceSchedule(TimerController.get().getRefreshListVideoTimer(), FAST_REFRESH);
		}

	}

	private ClickHandler buildShowJsonClickHandler() {

		return new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doShowJson();
			}
		};
	}

	private ClickHandler buildActionClickHandler() {

		return new ClickHandler() {

			@SuppressWarnings("incomplete-switch")
			@Override
			public void onClick(ClickEvent event) {
				if (event instanceof ActionClickEvent) {
					ActionClickEvent actionClickEvent = (ActionClickEvent) event;

					log.config("Action: " + actionClickEvent.action + " id: " + actionClickEvent.id);
					// lancer action
					switch (actionClickEvent.action) {
					case start:
						StreamController.get().beforeStartStream(actionClickEvent.id);
						break;
					case stop:
						StreamController.get().beforeStopStream(actionClickEvent.id);
						break;
					case showEit:
						beforeImportEit(actionClickEvent.id);
						break;
					case sendEit:
						beforeSendEitToServer();
						break;
					case editStream:
						StreamController.get().beforeShowDialogEditStream(actionClickEvent.id);
						break;
					case createStream:
						StreamController.get().beforeShowDialogCreateNewStream();
						break;
					case deleteStream:
						StreamController.get().beforeDeleteStream(actionClickEvent.id);
						break;
					case editVideo:
						VideoController.get().beforeShowDialogEditVideo(actionClickEvent.id);
						break;
					case createVideo:
						VideoController.get().beforeShowDialogCreateNewVideo();
						break;
					case deleteVideo:
						VideoController.get().beforeDeleteVideo(actionClickEvent.id);
						break;
					case executeCmd:
						AdminController.get().beforeExecuteCommand();
						break;
					case changeStreamStatus:
						StreamController.get().beforeChangeStreamStatus(actionClickEvent.id);
						break;
					case refreshSupervisorStatus:
						AdminController.get().refreshSupervisorStatus();
						break;
					case uploadFile:
						AdminController.get().doUploadFile();
						break;
					case connectUser:
						showDialogConnection();
						break;
					}

				} else {
					log.severe("bad event");
				}
			}

		};
	}

	private ClickHandler buildImportJsonClickHandler() {
		return new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				beforeImportJson();
			}

		};
	}

	private void doAuthenticateUser(final Credential credential) {

		// si credential null, authentification renverra une connection anonyme
		// equivalente à une deconnection
		String login = (credential == null) ? "" : credential.getLogin();
		String pwd = (credential == null) ? "" : credential.getPwd();

		this.rpcService.authenticateUserProfile(login, pwd, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				StreamController.get().showPanelStreamResult(
						"Error when authentication process! " + buildOnFailureMessage(caught), false);
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListStreamTimer());
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListVideoTimer());
			}

			@Override
			public void onSuccess(String userProfile) {

				UserProfile profile = UserProfile.valueOf(userProfile);
				StreamController.get().showPanelStreamResult("authentication succeded: " + userProfile + "!", true);
				actionPanel.setUserProfil(setCurrentUserProfile(profile));
				VideoController.get().doRefreshListVideoFiles(true);
				TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListStreamTimer(),
						FAST_REFRESH);

				// manage profile
				manageUserProfil(profile);
				// manage action button
				enableButtonInActionPanel(-1);

				if (profile == UserProfile.admin) {
					mainTabPanel.displayAdminPanel();
				} else {
					mainTabPanel.displayStreamPanel();
				}
			}
		});
	}

	private void beforeImportEit(final int eitId) {

		StreamDescription targetDescription = this.createStreamDescription(eitId);
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Import eit", new String[] {
				"Confirmer l'importation des eit depuis", targetDescription.getDescription(),
				"et l'écrasement des eit existantes ?" }, null, true, new IActionCallback() {

			@Override
			public void onCancel() {
				StreamController.get().showPanelStreamResult("Import eit cancelled! ", false);
			}

			@Override
			public void onOk() {
				StreamController.get().streamPanelLock();
				EitController.get().doImportEitFromStream(eitId, true);
			}

		});
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	private void showDialogConnection() {
		this.clearAllMessages(false);

		if (this.currentUserProfile != UserProfile.anybody) {
			this.doAuthenticateUser(null);
			this.mainTabPanel.setAdminPanelVisible(false);
		} else { // connection
			TimerController.get().suspends(TimerController.get().getRefreshListStreamTimer());
			TimerController.get().suspends(TimerController.get().getRefreshListVideoTimer());
			final ConnectionPanel connectionPanel = new ConnectionPanel();
			final MyDialogBox dialogBox = WidgetUtils.buildDialogBox("Identification", null, connectionPanel, true,
					true, false, new IActionCallback() {

						@Override
						public void onOk() {
							doAuthenticateUser(connectionPanel.getDataFromWidget());
						}

						@Override
						public void onCancel() {
							StreamController.get().showPanelStreamResult("Identification canceled! ", false);
							TimerController.get().resume(TimerController.get().getRefreshListStreamTimer());
							TimerController.get().resume(TimerController.get().getRefreshListVideoTimer());
						}
					});

			WidgetUtils.centerDialogAndShow(dialogBox);
		}
	}

	private void beforeImportJson() {

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Import json", new String[] {
				"Confirmer l'importation du json", "et l'écrasement des eit existantes ?" }, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						StreamController.get().showPanelStreamResult("Import json cancelled! ", false);
					}

					@Override
					public void onOk() {
						EitController.get().doImportEitFromJson(getJsonPanel().getJson(), true);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	// private void doClearInfoServer() {
	// log.config("doClearInfoServer()");
	// LocalStorageManager.get().removeInfoServer();
	// this.actionPanel.setServerInfo(new ServerInfoBean("", ""));
	// this.actionPanel.clear();
	// }

	private void doShowJson() {
		log.config("doShowJson()");
		this.clearAllMessages(false);

		EitInfoModel eitInfo = EitController.get().getCurrentEitInfo();
		EitController.get().doShowAndSaveEitInfo(eitInfo);

	}

	private void beforeSendEitToServer() {

		log.config("beforeSendEitToServer()");

		if (this.getCurrentTarget() == null) {
			showError("Aucune cible n'a été choisie!");
			return;
		}
		final int currentStreamId = (this.getCurrentTarget() == null) ? -1 : Integer.parseInt(this.getCurrentTarget());
		StreamDescription targetDescription = this.createStreamDescription(currentStreamId);
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Send eit", new String[] {
				"Confirmer l'envoi des eit vers", targetDescription.getDescription() }, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						StreamController.get().showPanelStreamResult("Send eit cancelled! ", false);
					}

					@Override
					public void onOk() {
						doSaveAndSendEit(currentStreamId);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	private void doSaveAndSendEit(int streamId) {
		log.config("doSaveAndSendEit()");
		this.clearAllMessages(false);

		EitInfoModel eitInfo = EitController.get().getCurrentEitInfo();
		EitController.get().doSendEit(streamId, eitInfo, true);

		LocalStorageManager.get().storeServerInfo(this.serverInfo.getInfoServer());
	}

	private void doGetRefreshDataInterval() {

		log.config("doGetRefreshDataInterval()");
		this.clearAllMessages(false);
		this.rpcService.getRefreshDbEntryInterval(new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				// nothing
			}

			@Override
			public void onSuccess(Integer result) {
				STANDARD_REFRESH = result;
				TimerController.get().createRefreshListStreamTimer(STANDARD_REFRESH, new Command() {

					@Override
					public void execute() {
						StreamController.get().doRefreshListStreamInfo(true);
					}
				});
				TimerController.get().createRefreshListVideoTimer(2 * STANDARD_REFRESH, new Command() {

					@Override
					public void execute() {
						VideoController.get().doRefreshListVideoFiles(true);
					}
				});
			}
		});
	}

	private void doGetCurrentUserProfil() {
		this.clearAllMessages(false);
		this.rpcService.getCurrentUserProfile(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				StreamController.get().showPanelStreamResult("Error getting user profile!", false);
			}

			@Override
			public void onSuccess(String userProfile) {
				manageUserProfil(UserProfile.valueOf(userProfile));
			}
		});
	}

	private void manageUserProfil(UserProfile userProfile) {
		actionPanel.setUserProfil(setCurrentUserProfile(userProfile));

		boolean admin = UserProfile.admin == userProfile;
		mainTabPanel.setAdminPanelVisible(admin);

	}

	private UserProfile setCurrentUserProfile(UserProfile userProfile) {
		this.currentUserProfile = userProfile;
		return this.currentUserProfile;
	}

	private void restoreParamsFromLocalStorage() {
		log.config("restoreParamsFromLocalStorage()");
		this.clearAllMessages(false);
		final String json = LocalStorageManager.get().retrieveEitInfo();

		if (json != null) {
			EitController.get().doImportEitFromJson(json, false);
		}

		final String infoServer = LocalStorageManager.get().retrieveServerInfo();
		if (infoServer != null) {
			log.config("InfoServer from Local storage");
			this.serverInfo = new ServerInfoBean(infoServer);
		} else {
			log.config("Get InfoServer from RPC server");
			this.rpcService.getServerInfo(new AsyncCallback<ServerInfoBean>() {

				@Override
				public void onFailure(Throwable caught) {
					buildOnFailureMessage(caught);
					serverInfo = new ServerInfoBean(null);
				}

				@Override
				public void onSuccess(ServerInfoBean result) {
					log.config("onSuccess! : " + result.toString());
					serverInfo = result;
				}
			});
		}

	}

	// private void sendDatasWithRequestBuilder(ServerInfoBean serverInfo,
	// String eitInfoJson) {
	//
	// final String url = GWT.getModuleBaseURL() + "sendeit";
	// // "http://" + serverInfo.getInfoServer() + "/" + moduleName +
	// // "/sendeit";
	// RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST,
	// url);
	// log.config("sendDatasWithRequestBuilder() - url: " + url);
	// try {
	// requestBuilder.sendRequest(eitInfoJson, new RequestCallback() {
	//
	// @Override
	// public void onResponseReceived(Request request, Response response) {
	//
	// if (response.getStatusCode() == 200) {
	// StreamController.get().showPanelStreamResult("Success sending eit!",
	// true);
	// actionPanel.setServerResult(url, "SUCCESS: status " +
	// response.getStatusCode(), true);
	// } else {
	// StreamController.get().showPanelStreamResult("Failure sending eit!",
	// false);
	// actionPanel.setServerResult(url, "FAILURE: status " +
	// response.getStatusCode(), false);
	// }
	// }
	//
	// @Override
	// public void onError(Request request, Throwable exception) {
	// StreamController.get().showPanelStreamResult("Error sending eit!",
	// false);
	// actionPanel.setServerResult(url, "ERROR: " + exception.getMessage(),
	// false);
	//
	// }
	// });
	// } catch (RequestException e) {
	// e.printStackTrace();
	// actionPanel.setServerResult(url, "Request Exception: " + SERVER_ERROR,
	// false);
	// }
	// }

	private void showError(String errorMessage) {
		this.showError(errorMessage, null, null);
	}

	// ============================================== INNER CLASS
	public static class ButtonActionStates {

		private boolean enableCreateStream = false;
		private boolean enableCreateVideo = false;
		private boolean enableSendEit = false;
		private boolean enableConnection = true;

		public boolean isEnableCreateVideo() {
			return enableCreateVideo;
		}

		public void setEnableCreateVideo(boolean enableCreateVideo) {
			this.enableCreateVideo = enableCreateVideo;
		}

		public boolean isEnableCreateStream() {
			return enableCreateStream;
		}

		public void setEnableCreateStream(boolean enableCreateStream) {
			this.enableCreateStream = enableCreateStream;
		}

		public boolean isEnableSendEit() {
			return enableSendEit;
		}

		public void setEnableSendEit(boolean enableSendEit) {
			this.enableSendEit = enableSendEit;
		}

		public boolean isEnableConnection() {
			return enableConnection;
		}

		public void setEnableConnection(boolean enableConnection) {
			this.enableConnection = enableConnection;
		}

	}

	public static abstract class AbstractDescription {
		private final String description;

		public String getDescription() {
			return description;
		}

		public AbstractDescription(String description) {
			this.description = description;
		}

	}

	public static class VideoDescription extends AbstractDescription {

		public VideoDescription(String description) {
			super(description);
		}
	}

	public static class StreamDescription extends AbstractDescription {

		private final boolean eitToInject;

		public StreamDescription(String description, boolean eitToInject) {
			super(description);
			this.eitToInject = eitToInject;
		}

		public boolean isEitToInject() {
			return eitToInject;
		}
	}

	public static class ActionClickEvent extends ClickEvent {
		private final Action action;
		private final int id;

		public ActionClickEvent(Action action) {
			this(action, IDto.ID_UNDEFINED);
		}

		public ActionClickEvent(Action action, int id) {
			this.action = action;
			this.id = id;
		}
	}

}
