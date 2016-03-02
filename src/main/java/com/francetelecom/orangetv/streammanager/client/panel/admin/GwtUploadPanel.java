package com.francetelecom.orangetv.streammanager.client.panel.admin;

import java.util.Date;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.AbstractCmdDatas;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.streammanager.client.widget.LabelWidget;
import com.francetelecom.orangetv.streammanager.client.widget.MyUploader;
import com.francetelecom.orangetv.streammanager.client.widget.MyUploader.UploadHandler;
import com.francetelecom.orangetv.streammanager.client.widget.MyUploader.UploadInfo;
import com.francetelecom.orangetv.streammanager.client.widget.MyUploader.UploadState;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.francetelecom.orangetv.streammanager.shared.util.ValueHelper;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page de test de l'API GwtUpload new version
 * 
 * @author ndmz2720
 *
 */
public class GwtUploadPanel extends AbstractCmdPanel {

	private final static Logger log = Logger.getLogger("New GwtUploadPanel");
	private final int maxSizeServerMo;

	private final VerticalPanel main = new VerticalPanel();

	private final LabelAndBoxWidget tbMaxSizeMo = new LabelAndBoxWidget("Max size Mo", 80, 80);

	// -------------- UPLOAD -----------------------------

	private final SimplePanel uploadPanel = new SimplePanel();
	private final MyUploader uploader;

	private CmdResponse currentResponse;

	// ------------------------------------- constructor
	GwtUploadPanel(int maxSizeServerMo) {

		this.maxSizeServerMo = maxSizeServerMo;
		this.uploader = new MyUploader(this.maxSizeServerMo);

		this.initWidget(this.buildMainPanel());
		this.initComposants();
		this.iniHandlers();
	}

	// --------------------------------------- overriding AbstractCmdPanel
	@Override
	AbstractCmdDatas getDataFromWidget() {
		if (this.currentResponse == null) {
			return null;
		}
		return new GwtUpdloadData2(this.currentResponse);
	}

	@Override
	void setCmdResponse(CmdResponse cmdResponse) {

	}

	@Override
	Logger getLog() {
		return log;
	}

	// -------------------------------------- private methods

	private Widget buildMainPanel() {

		this.main.setWidth(MAX_WIDTH);

		VerticalPanel vpContainer = new VerticalPanel();
		vpContainer.setSpacing(PANEL_SPACING);
		vpContainer.add(new LabelWidget("Page de test de la library moxieapps", 500));
		vpContainer.add(this.tbMaxSizeMo);
		vpContainer.add(this.uploadPanel);

		vpContainer.add(this.btExecute);

		this.main.add(vpContainer);
		return this.main;
	}

	private int getUserMaxSize() {

		String userMaxSize = this.tbMaxSizeMo.getBoxUserInput();
		return ValueHelper.getIntValue(userMaxSize, this.maxSizeServerMo);
	}

	private void setUploadState(UploadState uploadState) {
		// TODO
	}

	private UploadHandler uploadHandler = new UploadHandler() {

		private long starttime;

		@Override
		public void onOpenDialog() {
			log.config("onOpenDialog");
			setUploadState(UploadState.none);
		}

		@Override
		public void beforeStarting(int countOfFile) {
			log.config("beforeStarting(" + countOfFile + ")");
			this.starttime = new Date().getTime();
		}

		@Override
		public void onQueue(String name) {
			log.config("onQueue(" + name + ")");
			setUploadState(UploadState.started);
			if (argumentsChangeHandler != null) {
				argumentsChangeHandler.onChange(null);
			}
		}

		@Override
		public void onCancel(String name) {
			log.config("onCancel(" + name + ")");
			setUploadState(UploadState.canceled);
			if (GwtUploadPanel.this.argumentsChangeHandler != null) {
				currentResponse = new CmdResponse();

				currentResponse.addErrorLine("upload canceled by user!");
				// currentResponse.setExitValue(-1);
				currentResponse.setSuccess(false);

				GwtUploadPanel.super.autoexecute();
			}

		}

		@Override
		public void onError(String name, String errorMessage) {
			log.config("onError(" + name + ")");
			setUploadState(UploadState.error);

			currentResponse = new CmdResponse();
			currentResponse.addErrorLine("upload file ERROR!");
			currentResponse.addErrorLine(errorMessage);
			currentResponse.setSuccess(false);
			GwtUploadPanel.super.autoexecute();

		}

		@Override
		public void onFinish(UploadInfo uploadInfo) {
			log.config("onFinish(" + uploadInfo.getFilename() + ")");
			setUploadState(UploadState.done);

			long durationInSec = (new Date().getTime() - this.starttime) / 1000;
			long sizeInMo = uploadInfo.getSize() / (1024 * 1024);

			if (GwtUploadPanel.this.argumentsChangeHandler != null) {
				currentResponse = new CmdResponse();
				currentResponse.addResponseLine("filename: " + uploadInfo.getFilename());
				currentResponse.addResponseLine("upload duration: " + durationInSec + " seconde(s)");
				currentResponse.addResponseLine("size: " + sizeInMo + " Mo");
				currentResponse.setSuccess(true);

				GwtUploadPanel.super.autoexecute();
			}

		}

	};

	private void initComposants() {
		// Add a finish handler which will load the image once the upload
		// finishes
		this.btExecute.setVisible(false);
		this.btExecute.setEnabled(true);

		this.tbMaxSizeMo.setValue(this.maxSizeServerMo + "");

		this.uploadPanel.clear();
		this.uploadPanel.add(this.uploader.getUploadPanel());
		// add handlers
		this.uploader.setUploadHandler(this.uploadHandler);

	}

	private void iniHandlers() {
		this.tbMaxSizeMo.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				uploader.setMaxSizeMo(getUserMaxSize());
			}
		});
	}

	// =========================================== INNER CLASS
	static class GwtUpdloadData2 extends AbstractCmdDatas {
		private final CmdResponse response;

		CmdResponse getCmdResponse() {
			return this.response;
		}

		private GwtUpdloadData2(CmdResponse response) {
			this.response = response;
			super.setSshCommand(false);
		}
	}
}
