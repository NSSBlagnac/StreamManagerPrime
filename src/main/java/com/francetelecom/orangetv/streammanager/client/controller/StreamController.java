package com.francetelecom.orangetv.streammanager.client.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.controller.AppController.StreamDescription;
import com.francetelecom.orangetv.streammanager.client.panel.StreamPanel;
import com.francetelecom.orangetv.streammanager.client.panel.StreamPanel.ResultType;
import com.francetelecom.orangetv.streammanager.client.panel.streaminfo.AbstractStreamInfoPanel;
import com.francetelecom.orangetv.streammanager.client.panel.streaminfo.CreateStreamInfoPanel;
import com.francetelecom.orangetv.streammanager.client.panel.streaminfo.EditStreamInfoPanel;
import com.francetelecom.orangetv.streammanager.client.service.IActionCallback;
import com.francetelecom.orangetv.streammanager.client.service.IActionValidator;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils.MyDialogBox;
import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfoForList;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;

public class StreamController extends AbstractController {

	private final static Logger log = Logger.getLogger("StreamController");

	private final static Comparator<StreamInfoForList> STREAM_COMPARATOR = new Comparator<StreamInfoForList>() {

		@Override
		public int compare(StreamInfoForList o1, StreamInfoForList o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}
			if (o1 == null) {
				return -1;
			} else if (o2 == null) {
				return 1;
			}
			// les stream proteges en premier
			if (o1.isEntryProtected() != o2.isEntryProtected()) {
				return o1.isEntryProtected() ? -1 : 1;
			}
			// sinon tri sur le lcn
			return o1.compareTo(o2);
		}
	};

	private static StreamController instance;

	static StreamController get() {
		if (instance == null) {
			instance = new StreamController();
		}
		return instance;
	}

	private StreamController() {
	}

	// ----------------------------------- overriding AbstractController
	protected void go() {
		log.info("go()");
		this.doRefreshListStreamInfo(false);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ----------------------------------------------- package methods
	void doRefreshListStreamInfo(final boolean update) {
		log.fine("doRefreshDbEitEntries - update: " + update + " - currentTarget: "
				+ this.appController.getCurrentTarget());
		this.appController.clearAllMessages(true);

		streamPanelLock();
		this.rpcService.getListStreamInfo(new AsyncCallback<List<StreamInfoForList>>() {

			@Override
			public void onFailure(Throwable caught) {

				showPanelStreamResult("Error when refresh stream list! " + buildOnFailureMessage(caught), false);
				streamPanelUnlock();
				TimerController.get()
						.resumeAndSchedule(TimerController.get().getRefreshListStreamTimer(), SLOW_REFRESH);
			}

			@Override
			public void onSuccess(List<StreamInfoForList> listEntries) {
				showPanelStreamResult("Success refreshing stream list...", true);
				int currentEit = (appController.getCurrentTarget() == null) ? -1 : Integer.parseInt(appController
						.getCurrentTarget());
				Collections.sort(listEntries, STREAM_COMPARATOR);
				getStreamPanel().refresh(listEntries, update, currentEit);
				streamPanelUnlock();
				appController.enableButtonInActionPanel(-1);
				appController.displayCurrentTarget();
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListStreamTimer());
			}
		});
	}

	private void doChangeStatus(int streamId) {

		streamPanelLock();
		TimerController.get().suspends(TimerController.get().getRefreshListStreamTimer());
		this.rpcService.updateStreamStatus(streamId, StreamStatus.STOPPED, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				showPanelStreamResult("Error when starting stream! " + buildOnFailureMessage(caught), false);
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListStreamTimer());
				streamPanelUnlock();
			}

			@Override
			public void onSuccess(Boolean result) {
				showPanelStreamResult("change status succeded!", true);
				TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListStreamTimer(),
						FAST_REFRESH);
			}
		});

	}

	void streamPanelUnlock() {
		this.getStreamPanel().unlock();
	}

	void streamPanelLock() {
		this.getStreamPanel().lock();
	}

	void showPanelStreamResult(String message, boolean success) {
		this.showPanelStreamResult(message, success ? ResultType.success : ResultType.error);
	}

	void showPanelStreamResult(String message, ResultType type) {
		this.getStreamPanel().setActionResult(message, type);
	}

	void beforeChangeStreamStatus(final int streamId) {

		StreamDescription targetDescription = this.appController.createStreamDescription(streamId);
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused(
				"Change status",
				new String[] { "Confirmer la réinitialisation du status à STOPPED", "pour le stream",
						targetDescription.getDescription() }, null, true, new IActionCallback() {

					@Override
					public void onCancel() {
						showPanelStreamResult("Change status cancelled! ", false);
					}

					@Override
					public void onOk() {

						doChangeStatus(streamId);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	void beforeDeleteStream(final int eitId) {

		StreamDescription targetDescription = this.appController.createStreamDescription(eitId);
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Delete stream", new String[] {
				"Confirmer la suppression du stream ", targetDescription.getDescription(), " et de ses eit ?" }, null,
				true, new IActionCallback() {

					@Override
					public void onCancel() {
						showPanelStreamResult("Delete stream cancelled! ", false);
					}

					@Override
					public void onOk() {
						doDeleteStream(eitId);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	void doCreateStreamInfo(StreamInfo streamInfo) {

		final EitInfoModel eitInfo = EitController.get().getCurrentEitInfo();

		streamPanelLock();
		this.rpcService.createStreamInfo(streamInfo, eitInfo, new AsyncCallback<StreamInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				showPanelStreamResult("Error when creating stream info! " + buildOnFailureMessage(caught), false);
				streamPanelUnlock();
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListStreamTimer());
			}

			@Override
			public void onSuccess(final StreamInfo result) {
				if (result != null) {
					showPanelStreamResult("Success when creating stream info! ", true);
					TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListStreamTimer(),
							FAST_REFRESH);
				} else {
					showPanelStreamResult("Failure when creating stream infos! wrong datas ", false);
					TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListStreamTimer(),
							FAST_REFRESH);
				}
				streamPanelUnlock();
			}
		});
	}

	void beforeShowDialogCreateNewStream() {

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Edit stream",
				new String[] { "Confirmer la création d'un nouveau stream  ?" }, null, true, new IActionCallback() {

					@Override
					public void onCancel() {
						showPanelStreamResult("Edit stream cancelled! ", false);
					}

					@Override
					public void onOk() {
						streamPanelLock();
						showDialogCreateNewStream();
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	void beforeStartStream(final int streamId) {

		StreamDescription targetDescription = this.appController.createStreamDescription(streamId);
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Start stream", new String[] {
				"Confirmer le démarrage du stream ", targetDescription.getDescription() }, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						showPanelStreamResult("Start stream cancelled! ", false);
					}

					@Override
					public void onOk() {
						doStartStream(streamId);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	void beforeStopStream(final int streamId) {

		StreamDescription targetDescription = this.appController.createStreamDescription(streamId);
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Stop stream", new String[] {
				"Confirmer l'arrêt du stream ", targetDescription.getDescription() }, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						showPanelStreamResult("Stop stream cancelled! ", false);
					}

					@Override
					public void onOk() {
						doStopStream(streamId);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	void beforeShowDialogEditStream(final int eitId) {

		StreamDescription targetDescription = this.appController.createStreamDescription(eitId);
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Edit stream", new String[] {
				"Confirmer l'édition des données du stream ", targetDescription.getDescription(), " ?" }, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						showPanelStreamResult("Edit stream cancelled! ", false);
					}

					@Override
					public void onOk() {
						showDialogEditStream(eitId);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	// -------------------------------- private methods

	private void doStartStream(int eitId) {

		streamPanelLock();
		TimerController.get().suspends(TimerController.get().getRefreshListStreamTimer());
		this.rpcService.startStream(eitId, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				showPanelStreamResult("Error when starting stream! " + buildOnFailureMessage(caught), false);
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListStreamTimer());
				streamPanelUnlock();
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					showPanelStreamResult("action start succeded!", true);
					TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListStreamTimer(),
							FAST_REFRESH);
				} else {
					showPanelStreamResult("The stream cannot be started!", false);
					streamPanelUnlock();
				}
			}
		});
	}

	private void doStopStream(int eitId) {

		streamPanelLock();
		TimerController.get().suspends(TimerController.get().getRefreshListStreamTimer());
		this.rpcService.stopStream(eitId, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				showPanelStreamResult("Error when stopping stream! " + buildOnFailureMessage(caught), false);
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListStreamTimer());
				streamPanelUnlock();
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					showPanelStreamResult("action stop succeded!", true);

				} else {
					showPanelStreamResult("The stream cannot be stopped!", false);
					streamPanelUnlock();
				}
				TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListStreamTimer(),
						FAST_REFRESH);
			}
		});
	}

	private void beforeUpdateStreamInfo(final StreamInfo streamInfo) {

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("update stream info", new String[] {
				"Confirmer la modification des infos du stream", streamInfo.getName() + " ?" }, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						showPanelStreamResult("Update stream info cancelled! ", false);
					}

					@Override
					public void onOk() {
						doUpdateStreamInfo(streamInfo);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	private void doUpdateStreamInfo(final StreamInfo streamInfo) {

		streamPanelLock();
		this.rpcService.updateStreamInfo(streamInfo, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				showPanelStreamResult("Error when updating stream info! " + buildOnFailureMessage(caught), false);
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListStreamTimer());
				streamPanelUnlock();
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					showPanelStreamResult("Success when updating stream info! ", true);
					appController.displayCurrentTarget(streamInfo.getId());
					EitController.get().doImportEitFromStream(streamInfo.getId(), false);
				} else {
					showPanelStreamResult("Failure when updating stream infos! wrong datas ", false);
				}
				TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListStreamTimer(),
						FAST_REFRESH);
				streamPanelUnlock();
			}
		});
	}

	private void doValidStreamInfo(final StreamInfo streamInfo, final IActionValidator<StreamInfo> validator) {

		this.rpcService.validStreamInfo(streamInfo, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				validator.onValidationError(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				validator.onValidOK(streamInfo);
			}
		});
	}

	private StreamPanel getStreamPanel() {
		return this.appController.getMainTabPanel().getStreamPanel();
	}

	/**
	 * Edition des infos annexes d'un nouveau stream
	 * <ul>
	 * <li>showDialogCreateNewStream
	 * <li>doValidStreamInfo
	 * <li>beforeCreateStreamInfo
	 * <li>doCreateStreamInfo
	 * </ul>
	 * 
	 * @param eitId
	 */
	private void showDialogCreateNewStream() {
		this.appController.clearAllMessages(false);
		this.appController.getMainTabPanel().displayStreamPanel();
		TimerController.get().suspends(TimerController.get().getRefreshListStreamTimer());

		final CreateStreamInfoPanel createStreamInfoPanel = new CreateStreamInfoPanel();
		final MyDialogBox dialogBox = WidgetUtils.buildDialogBox("Create new stream", null, createStreamInfoPanel,
				true, false, false, null);

		final IActionValidator<StreamInfo> validator = new IActionValidator<StreamInfo>() {

			@Override
			public void onValidationError(String errorMessage) {
				showPanelStreamResult("Create new stream validation error! ", false);
				createStreamInfoPanel.setErrorMessage(errorMessage);
			}

			@Override
			public void onValidOK(StreamInfo result) {

				dialogBox.hide();
				showPanelStreamResult("Create new stream success! ", true);
				// confirmation & update
				beforeCreateStreamInfo(result);
			}
		};

		IActionCallback actionCallback = new IActionCallback() {

			// User press OK...
			@Override
			public void onOk() {

				StreamInfo streamInfo = createStreamInfoPanel.getDataFromWidget();
				// validation of stream info
				doValidStreamInfo(streamInfo, validator);

			}

			@Override
			public void onCancel() {
				showPanelStreamResult("Create new stream canceled! ", false);
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListStreamTimer());
			}
		};
		dialogBox.setActionCallback(actionCallback);
		createStreamInfoPanel.setContainer(dialogBox);

		WidgetUtils.centerDialogAndShow(dialogBox);
		doRefreshStreamInfo(createStreamInfoPanel, IDto.ID_UNDEFINED);

	}

	/*
	 * Suppression du stream et de ses eit
	 */
	private void doDeleteStream(final int eitId) {
		streamPanelLock();
		TimerController.get().suspends(TimerController.get().getRefreshListStreamTimer());
		this.rpcService.deleteStream(eitId, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				showPanelStreamResult("Error when deleting stream! " + buildOnFailureMessage(caught), false);
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListStreamTimer());
				streamPanelUnlock();
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					showPanelStreamResult("action delete stream succeded!", true);
					TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListStreamTimer(),
							FAST_REFRESH);
				} else {
					showPanelStreamResult("The stream cannot be deleted!", false);

				}
				streamPanelUnlock();
			}
		});

	}

	private void doRefreshStreamInfo(final AbstractStreamInfoPanel panel, final int eitId) {
		log.config("doRefreshStream - eitId: " + eitId);
		this.rpcService.getStreamInfo(eitId, new AsyncCallback<StreamInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				TimerController.get().stop(TimerController.get().getRefreshEditStreamTimer());
				showPanelStreamResult("Error when refresh stream! " + buildOnFailureMessage(caught), false);
			}

			@Override
			public void onSuccess(StreamInfo streamInfo) {
				log.config("onSuccess(): streamInfo");
				panel.setData(streamInfo);
				TimerController.get().schedule(TimerController.get().getRefreshEditStreamTimer());
			}
		});
	}

	private void beforeCreateStreamInfo(final StreamInfo streamInfo) {

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Create stream info", new String[] {
				"Confirmer la creation  du stream", streamInfo.getName() + " ?" }, null, true, new IActionCallback() {

			@Override
			public void onCancel() {
				showPanelStreamResult("Create stream info cancelled! ", false);
			}

			@Override
			public void onOk() {
				doCreateStreamInfo(streamInfo);
			}

		});
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	/**
	 * Edition des infos annexes du stream
	 * <ul>
	 * <li>showDialogEditStream
	 * <li>doValidStreamInfo
	 * <li>beforeUpdateStreamInfo
	 * <li>doUpdateStreamInfo
	 * </ul>
	 * 
	 * @param eitId
	 */
	private void showDialogEditStream(final int eitId) {
		this.appController.clearAllMessages(false);
		TimerController.get().suspends(TimerController.get().getRefreshListStreamTimer());

		final EditStreamInfoPanel editStreamPanel = new EditStreamInfoPanel();
		final MyDialogBox dialogBox = WidgetUtils.buildDialogBox("Edit stream",
				new String[] { "Stream eitId: " + eitId }, editStreamPanel, true, false, false, null);

		final IActionValidator<StreamInfo> validator = new IActionValidator<StreamInfo>() {

			@Override
			public void onValidationError(String errorMessage) {
				showPanelStreamResult("Edit stream validation error! ", false);
				editStreamPanel.setErrorMessage(errorMessage);
			}

			@Override
			public void onValidOK(StreamInfo result) {

				dialogBox.hide();
				showPanelStreamResult("Edit stream success! ", true);
				TimerController.get().stop(TimerController.get().getRefreshEditStreamTimer());
				// confirmation & update
				beforeUpdateStreamInfo(result);
			}
		};

		IActionCallback actionCallback = new IActionCallback() {

			// User press OK...
			@Override
			public void onOk() {

				StreamInfo streamInfo = editStreamPanel.getDataFromWidget();
				// validation of stream info
				doValidStreamInfo(streamInfo, validator);

			}

			@Override
			public void onCancel() {
				showPanelStreamResult("Edit stream canceled! ", false);
				TimerController.get().stop(TimerController.get().getRefreshEditStreamTimer());
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListStreamTimer());
			}
		};
		dialogBox.setActionCallback(actionCallback);
		editStreamPanel.setContainer(dialogBox);

		TimerController.get().createRefreshEditStreamTimer(new Command() {

			@Override
			public void execute() {
				doRefreshStreamInfo(editStreamPanel, eitId);
			}
		});

		WidgetUtils.centerDialogAndShow(dialogBox);
		TimerController.get().forceSchedule(TimerController.get().getRefreshEditStreamTimer(), 10);

	}

}
