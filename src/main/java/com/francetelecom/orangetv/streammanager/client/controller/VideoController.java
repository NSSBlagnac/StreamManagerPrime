package com.francetelecom.orangetv.streammanager.client.controller;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.controller.AppController.VideoDescription;
import com.francetelecom.orangetv.streammanager.client.panel.StreamPanel.ResultType;
import com.francetelecom.orangetv.streammanager.client.panel.VideoPanel;
import com.francetelecom.orangetv.streammanager.client.panel.videoinfo.AbstractVideoInfoPanel;
import com.francetelecom.orangetv.streammanager.client.panel.videoinfo.CreateVideoInfoPanel;
import com.francetelecom.orangetv.streammanager.client.panel.videoinfo.EditVideoInfoPanel;
import com.francetelecom.orangetv.streammanager.client.service.IActionCallback;
import com.francetelecom.orangetv.streammanager.client.service.IActionValidator;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils.MyDialogBox;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoAdminInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;

public class VideoController extends AbstractController {

	private final static Logger log = Logger.getLogger("VideoController");

	private static VideoController instance;

	static VideoController get() {
		if (instance == null) {
			instance = new VideoController();
		}
		return instance;
	}

	private VideoController() {
	}

	// --------------------------------------
	private DtoAdminInfo adminInfo;

	// ----------------------------------- overriding AbstractController
	protected void go() {
		this.doGetAdminInfo();
		this.doRefreshListVideoFiles(false);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ------------------------------------- package methods
	void doGetAdminInfo() {
		log.config("doGetAdminInfo()...");
		this.rpcService.getAdminInfo(new AsyncCallback<DtoAdminInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				String errorMessage = "Error when getting admin info! : " + caught.getMessage();
				log.severe(errorMessage);
				getVideoPanel().setActionResult(errorMessage, ResultType.error);
			}

			@Override
			public void onSuccess(DtoAdminInfo adminInfo) {
				log.config("getAdminInfo() on success - " + adminInfo.getListLogFilenames().size() + " logs filenames.");
				VideoController.this.adminInfo = adminInfo;
			}
		});
	}

	void beforeDeleteVideo(final int videoId) {

		VideoDescription targetDescription = this.appController.createVideoDescription(videoId);
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Delete video", new String[] {
				"Confirmer la suppression de la video ", targetDescription.getDescription() }, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						showPanelVideoResult("Delete video cancelled! ", false);
					}

					@Override
					public void onOk() {

						doDeleteVideo(videoId);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	void beforeShowDialogEditVideo(final int videoId) {

		VideoDescription targetDescription = this.appController.createVideoDescription(videoId);
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Edit video", new String[] {
				"Confirmer l'édition des données de la video ", targetDescription.getDescription(), " ?" }, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						showPanelVideoResult("Edit video cancelled! ", false);
					}

					@Override
					public void onOk() {

						showDialogEditVideo(videoId);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	void doRefreshListVideoFiles(final boolean update) {

		log.config("doRefreshVideoFileList - update: " + update);
		videoPanelLock();
		this.appController.clearAllMessages(true);

		this.rpcService.getListFullVideoInfo(new AsyncCallback<List<FullVideoInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				VideoController.get().doShowPanelVideoResult(
						"Error when refresh video list! " + buildOnFailureMessage(caught), false);
				videoPanelUnlock();
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListVideoTimer(), SLOW_REFRESH);
			}

			@Override
			public void onSuccess(List<FullVideoInfo> listVideos) {
				doShowPanelVideoResult("Success refreshing video list!...", true);
				Collections.sort(listVideos);
				getVideoPanel().refresh(listVideos, update);
				videoPanelUnlock();
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListVideoTimer());
			}
		});
	}

	void beforeShowDialogCreateNewVideo() {

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Create video",
				new String[] { "Confirmer la création d'une nouvelle video  ?" }, null, true, new IActionCallback() {

					@Override
					public void onCancel() {
						showPanelVideoResult("Edit video cancelled! ", false);
					}

					@Override
					public void onOk() {
						showDialogCreateNewVideo();
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	// ------------------------------------- private methods
	/*
	 * Suppression de la video
	 */
	private void doDeleteVideo(final int videoId) {
		videoPanelLock();
		this.rpcService.deleteVideo(videoId, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				showPanelVideoResult("Error when deleting video! " + buildOnFailureMessage(caught), false);
				videoPanelUnlock();
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					showPanelVideoResult("action delete video succeded!", true);
					TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListVideoTimer(),
							FAST_REFRESH);
				} else {
					showPanelVideoResult("The video cannot be deleted!", false);

				}
				videoPanelUnlock();
			}
		});

	}

	private void doUpdateVideoStatus(final FullVideoInfo videoInfo, final Command command) {

		log.config("doUpdateVideoStatus() - videoId: " + videoInfo.getId() + " - " + videoInfo.getStatus());

		videoPanelLock();
		this.rpcService.updateVideoStatus(videoInfo.getId(), videoInfo.getStatus(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				showPanelVideoResult("Error when updating video info! " + buildOnFailureMessage(caught), false);
				videoPanelUnlock();
			}

			@Override
			public void onSuccess(Boolean result) {

				if (result) {
					TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListVideoTimer(),
							FAST_REFRESH);
				}
				if (command != null) {
					command.execute();
				} else {
					videoPanelUnlock();
				}
			}
		});
	}

	private void doUpdateVideoInfo(final FullVideoInfo videoInfo) {

		log.config("doUpdateVideoInfo() - upload pending: " + videoInfo.isUploadPending());

		videoPanelLock();
		this.rpcService.updateVideoInfo(videoInfo, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				showPanelVideoResult("Error when updating video info! " + buildOnFailureMessage(caught), false);
				videoPanelUnlock();
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					showPanelVideoResult("Success when updating video info! ", true);
					TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListVideoTimer(),
							FAST_REFRESH);
				} else {
					showPanelVideoResult("Failure when updating video infos! wrong datas ", false);
				}
				videoPanelUnlock();
			}
		});
	}

	private void beforeUpdateVideoInfo(final FullVideoInfo videoInfo) {

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("update video info", new String[] {
				"Confirmer la modification des infos de la video", videoInfo.getName() + " ?" }, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						showPanelVideoResult("Update video info cancelled! ", false);
					}

					@Override
					public void onOk() {

						// si upload en cours on met à jour le status en premier
						if (videoInfo.isUploadPending()) {
							Command command = new Command() {

								@Override
								public void execute() {
									doUpdateVideoInfo(videoInfo);
								}
							};
							doUpdateVideoStatus(videoInfo, command);
						} else {
							doUpdateVideoInfo(videoInfo);
						}

					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	/**
	 * Edition des infos annexes d'une novuelle video
	 * <ul>
	 * <li>showDialogCreateNewVideo
	 * <li>doValidVideoInfo
	 * <li>beforeCreateVideoInfo
	 * <li>doCreateVideoInfo
	 * </ul>
	 * 
	 * @param eitId
	 */
	private void showDialogCreateNewVideo() {

		log.info("showDialogCreateNewVideo()");
		this.appController.clearAllMessages(false);
		this.appController.getMainTabPanel().displayVideoPanel();
		TimerController.get().suspends(TimerController.get().getRefreshListVideoTimer());

		final CreateVideoInfoPanel createVideoInfoPanel = new CreateVideoInfoPanel(this.adminInfo.getMaxUploadSizeMo());
		final MyDialogBox dialogBox = WidgetUtils.buildDialogBox("Create new video", null, createVideoInfoPanel, true,
				false, false, null);
		createVideoInfoPanel.addChangeHandler(new MyVideoUploadChangeChandler(dialogBox));

		final IActionValidator<FullVideoInfo> validator = new IActionValidator<FullVideoInfo>() {

			@Override
			public void onValidationError(String errorMessage) {
				showPanelVideoResult("Validate new video  error! ", false);
				createVideoInfoPanel.setErrorMessage(errorMessage);
			}

			@Override
			public void onValidOK(FullVideoInfo result) {

				dialogBox.hide();
				showPanelVideoResult("validate new video success! ", true);
				// confirmation & update
				beforeCreateVideoInfo(result);
			}
		};

		IActionCallback actionCallback = new IActionCallback() {

			// User press OK...
			@Override
			public void onOk() {

				FullVideoInfo videoInfo = createVideoInfoPanel.getDataFromWidget();
				// validation of stream info
				doValidVideoInfo(videoInfo, validator);

			}

			@Override
			public void onCancel() {
				showPanelVideoResult("Create new video canceled! ", false);
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListVideoTimer());
			}
		};
		dialogBox.setActionCallback(actionCallback);
		createVideoInfoPanel.setContainer(dialogBox);

		WidgetUtils.centerDialogAndShow(dialogBox);
		doRefreshVideoInfo(createVideoInfoPanel, IDto.ID_UNDEFINED);

	}

	public enum VideoUploadState {
		start, canceled, error, finish;
	}

	public static class VideoUploadChangeEvent extends ChangeEvent {

		private final VideoUploadState state;

		public VideoUploadChangeEvent(VideoUploadState state) {
			this.state = state;
		}
	}

	private static class MyVideoUploadChangeChandler implements ChangeHandler {

		private final MyDialogBox myDialogBox;

		private MyVideoUploadChangeChandler(MyDialogBox myDialogBox) {
			this.myDialogBox = myDialogBox;
		}

		@Override
		public void onChange(ChangeEvent event) {

			if (event != null && event instanceof VideoUploadChangeEvent) {

				VideoUploadState state = ((VideoUploadChangeEvent) event).state;
				switch (state) {
				// start upload
				case start:
					myDialogBox.lock();
					break;
				// cancel upload
				case canceled:
					myDialogBox.unlock();
					myDialogBox.setReadOnly(true);
					break;
				case error:
					myDialogBox.unlock();
					myDialogBox.setReadOnly(true);
					break;
				// finish upload
				case finish:
					myDialogBox.unlock();
					break;
				}
			}

		}

	}

	void doRefreshVideoInfo(final AbstractVideoInfoPanel panel, final int videoId) {
		log.info("doRefreshVideoInfo- videoId: " + videoId);

		this.rpcService.getVideoInfo(videoId, new AsyncCallback<FullVideoInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				showPanelVideoResult("Error when refresh video! " + buildOnFailureMessage(caught), false);
			}

			@Override
			public void onSuccess(FullVideoInfo videoInfo) {
				log.config("onSuccess(): videoInfo");
				panel.setData(videoInfo);
			}
		});
	}

	void showPanelVideoResult(String message, ResultType type) {
		this.getVideoPanel().setActionResult(message, type);
	}

	private void doValidVideoInfo(final FullVideoInfo videoInfo, final IActionValidator<FullVideoInfo> validator) {

		log.info("doValidVideoInfo()");
		this.rpcService.validVideoInfo(videoInfo, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				validator.onValidationError(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				validator.onValidOK(videoInfo);
			}
		});
	}

	private void beforeCreateVideoInfo(final FullVideoInfo videoInfo) {

		log.info("beforeCreateVideoInfo()");
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Create video info", new String[] {
				"Confirmer la creation de la video", videoInfo.getName() + " ?" }, null, true, new IActionCallback() {

			@Override
			public void onCancel() {
				showPanelVideoResult("Create video info cancelled! ", false);
			}

			@Override
			public void onOk() {

				doCreateVideoInfo(videoInfo);
			}

		});
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	/**
	 * Edition des infos annexes de la video
	 * <ul>
	 * <li>showDialogEditVideo
	 * <li>doValidVideoInfo
	 * <li>beforeUpdateVideoInfo
	 * <li>doUpdateVideoInfo
	 * </ul>
	 * 
	 * @param videoId
	 */
	private void showDialogEditVideo(final int videoId) {
		this.appController.clearAllMessages(false);

		TimerController.get().suspends(TimerController.get().getRefreshListVideoTimer());

		final EditVideoInfoPanel editVideoPanel = new EditVideoInfoPanel(this.adminInfo.getMaxUploadSizeMo());
		final MyDialogBox dialogBox = WidgetUtils.buildDialogBox("Edit video", new String[] { "Video videoId: "
				+ videoId }, editVideoPanel, true, false, false, null);

		final IActionValidator<FullVideoInfo> validator = new IActionValidator<FullVideoInfo>() {

			@Override
			public void onValidationError(String errorMessage) {
				showPanelVideoResult("Edit video validation error! ", false);
				editVideoPanel.setErrorMessage(errorMessage);
			}

			@Override
			public void onValidOK(FullVideoInfo result) {

				dialogBox.hide();
				showPanelVideoResult("Edit video success! ", true);
				// confirmation & update
				beforeUpdateVideoInfo(result);
			}
		};

		IActionCallback actionCallback = new IActionCallback() {

			// User press OK...
			@Override
			public void onOk() {

				FullVideoInfo videoInfo = editVideoPanel.getDataFromWidget();
				// validation of stream info
				doValidVideoInfo(videoInfo, validator);

			}

			@Override
			public void onCancel() {
				showPanelVideoResult("Edit video canceled! ", false);
				TimerController.get().resumeAndSchedule(TimerController.get().getRefreshListVideoTimer());
			}
		};
		dialogBox.setActionCallback(actionCallback);
		editVideoPanel.setContainer(dialogBox);

		doRefreshVideoInfo(editVideoPanel, videoId);

		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	private void doCreateVideoInfo(final FullVideoInfo videoInfo) {

		videoPanelLock();
		if (videoInfo.isUploadPending()) {
			// schedule un rafraichissement au cas où le process serait long
			TimerController.get().forceSchedule(TimerController.get().getRefreshListVideoTimer(), STANDARD_REFRESH);
		}
		this.rpcService.createVideoFile(videoInfo, new AsyncCallback<FullVideoInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				showPanelVideoResult("Error when creating video info! " + buildOnFailureMessage(caught), false);
				videoPanelUnlock();
			}

			@Override
			public void onSuccess(final FullVideoInfo result) {
				if (result != null) {
					showPanelVideoResult("Success when creating video info! ", true);
					TimerController.get().resumeAndForceSchedule(TimerController.get().getRefreshListVideoTimer(),
							FAST_REFRESH);
				} else {
					showPanelVideoResult("Failure when creating video infos! wrong datas ", false);
				}
				videoPanelUnlock();
			}
		});
	}

	private void doShowPanelVideoResult(String message, boolean success) {
		this.doShowPanelVideoResult(message, success ? ResultType.success : ResultType.error);
	}

	private void doShowPanelVideoResult(String message, ResultType type) {
		this.appController.getMainTabPanel().getVideoPanel().setActionResult(message, type);
	}

	private VideoPanel getVideoPanel() {
		return this.appController.getMainTabPanel().getVideoPanel();
	}

	private void videoPanelUnlock() {
		this.getVideoPanel().unlock();
	}

	private void videoPanelLock() {
		this.getVideoPanel().lock();
	}

	private void showPanelVideoResult(String message, boolean success) {
		this.showPanelVideoResult(message, success ? ResultType.success : ResultType.error);
	}

}
