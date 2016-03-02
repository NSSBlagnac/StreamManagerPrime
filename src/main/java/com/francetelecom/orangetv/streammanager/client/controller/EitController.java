package com.francetelecom.orangetv.streammanager.client.controller;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.StreamPanel.ResultType;
import com.francetelecom.orangetv.streammanager.client.panel.eit.EitPanel;
import com.francetelecom.orangetv.streammanager.client.util.LocalStorageManager;
import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EitController extends AbstractController {

	private final static Logger log = Logger.getLogger("EitController");

	private static EitController instance;

	static EitController get() {
		if (instance == null) {
			instance = new EitController();
		}
		return instance;
	}

	private EitController() {
	}

	// -------------------------------------- overidding AbstractController
	@Override
	protected void go() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// -------------------------------- package methods
	void doSendEit(final int streamId, final EitInfoModel eitInfo, final boolean showInformation) {

		this.appController.clearAllMessages(true);
		EitController.get().eitPanelLock();
		this.rpcService.updateEitInfo(streamId, eitInfo, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				EitController.get().showPanelEitResult("Error when updating eit! " + buildOnFailureMessage(caught),
						false);
				EitController.get().eitPanelUnlock();
			}

			@Override
			public void onSuccess(Boolean result) {

				if (result) {

					EitController.get().showPanelEitResult("Success when updating eit! ", true);

					getEitPanel().populateWidgetFromData(eitInfo);
					if (showInformation) {
						showInformation("Update eit succeded! ");
						appController.enableButtonInActionPanel(-1);
					}

					doShowAndSaveEitInfo(eitInfo);

				} else {
					EitController.get().showPanelEitResult("Failure when updating eit! ", false);
				}
				EitController.get().eitPanelUnlock();
			}
		});
	}

	/**
	 * Get json from JsonPanel and populate EitPanel
	 */
	void doImportEitFromJson(String json, final boolean showInformation) {

		log.config("doImportEitFromJson()");
		this.appController.clearAllMessages(false);

		// transforms json to EitInfoModel then populate EitPanel
		if (json == null) {
			return;
		}

		this.rpcService.buildEitInfoFromJson(json, new AsyncCallback<EitInfoModel>() {

			@Override
			public void onFailure(Throwable caught) {
				EitController.get().showPanelEitResult("Error when getting json from model! " + caught.getMessage(),
						false);
			}

			@Override
			public void onSuccess(EitInfoModel eitInfo) {
				EitController.get().showPanelEitResult("Success getting EitInfo from json!", true);

				if (eitInfo != null) {
					EitController.get().showPanelEitResult("Success when loading eit! ", true);

					getEitPanel().populateWidgetFromData(eitInfo);
					if (showInformation) {
						showInformation("Import eit succeded! ");
						appController.getMainTabPanel().displayEitSectionPanel();
						appController.enableButtonInActionPanel(-1);
					}

				} else {
					EitController.get().showPanelEitResult("Failure when loading eit from json! wrong datas ", false);
				}

			}
		});

	}

	void doImportEitFromStream(final int streamId, final boolean showInformation) {

		log.config("doImportEitFromStream()");
		this.appController.clearAllMessages(true);
		this.appController.getJsonPanel().setJson("");

		StreamController.get().streamPanelLock();
		this.rpcService.getEitOfStream(streamId, new AsyncCallback<EitInfoModel>() {

			@Override
			public void onFailure(Throwable caught) {
				StreamController.get().showPanelStreamResult(
						"Error when loading eit! " + buildOnFailureMessage(caught), false);
				StreamController.get().streamPanelUnlock();
			}

			@Override
			public void onSuccess(EitInfoModel eitInfo) {
				if (eitInfo != null) {
					StreamController.get().showPanelStreamResult("Success when loading eit! ", true);

					doShowAndSaveEitInfo(eitInfo);
					appController.displayCurrentTarget(streamId);

					getEitPanel().populateWidgetFromData(eitInfo);
					if (showInformation) {
						showInformation("Import eit succeded! ");
						appController.getMainTabPanel().displayEitSectionPanel();

					}

				} else {
					StreamController.get().showPanelStreamResult("Failure when loading eit! wrong datas ", false);
				}
				StreamController.get().streamPanelUnlock();
			}
		});

	}

	EitInfoModel getCurrentEitInfo() {
		String target = this.appController.getCurrentTarget();
		if (target == null) {
			target = IDto.ID_UNDEFINED + "";
		}
		return this.getEitPanel().getDataFromWidget(target);
	}

	void doShowAndSaveEitInfo(EitInfoModel eitInfo) {

		if (eitInfo == null) {
			return;
		}
		this.rpcService.getJsonFromEitInfo(eitInfo, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				EitController.get().showPanelEitResult("Error when getting json from model! " + caught.getMessage(),
						false);
			}

			@Override
			public void onSuccess(String jsonEitInfo) {

				appController.getJsonPanel().setJson(jsonEitInfo == null ? "" : jsonEitInfo);
				if (jsonEitInfo != null) {
					LocalStorageManager.get().storeEitInfo(jsonEitInfo);
				}

			}
		});
	}

	// String buildAndSaveEitInfo() {
	//
	// String target = this.appController.getCurrentTarget();
	// if (target == null) {
	// target = IDto.ID_UNDEFINED + "";
	// }
	// EitInfoModel eitInfo = getEitPanel().getDataFromWidget(target);
	//
	// JSONObject jsonObj = GwtEitJsonParser.buildJson(eitInfo);
	// String jsonEitInfo = jsonObj.toString();
	// LocalStorageManager.get().storeEitInfo(jsonEitInfo);
	// return jsonEitInfo;
	//
	// }

	// ------------------------------ private methods
	private EitPanel getEitPanel() {
		return this.appController.getMainTabPanel().getEitPanel();
	}

	private void showPanelEitResult(String message, boolean success) {
		this.showPanelEitResult(message, success ? ResultType.success : ResultType.error);
	}

	void showPanelEitResult(String message, ResultType type) {
		this.getEitPanel().setActionResult(message, type);
	}

	private void eitPanelUnlock() {
		this.getEitPanel().unLock();
	}

	private void eitPanelLock() {
		this.getEitPanel().lock();

	}
}
