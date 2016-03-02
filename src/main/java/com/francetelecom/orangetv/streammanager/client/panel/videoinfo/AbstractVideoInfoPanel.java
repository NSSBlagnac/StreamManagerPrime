package com.francetelecom.orangetv.streammanager.client.panel.videoinfo;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.controller.VideoController.VideoUploadChangeEvent;
import com.francetelecom.orangetv.streammanager.client.controller.VideoController.VideoUploadState;
import com.francetelecom.orangetv.streammanager.client.panel.AbstractPanel;
import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.client.util.StatusUtils;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils.IMyDialogBox;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndCheckWidget;
import com.francetelecom.orangetv.streammanager.client.widget.MyUploader;
import com.francetelecom.orangetv.streammanager.client.widget.MyUploader.UploadHandler;
import com.francetelecom.orangetv.streammanager.client.widget.MyUploader.UploadInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoVideoProtection;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoStatus;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractVideoInfoPanel extends AbstractPanel implements CssConstants {

	protected final VerticalPanel main = new VerticalPanel();
	protected Panel uploadPanel;

	protected final CheckBox cbEntryProtected = new CheckBox("data protected");
	protected final LabelAndBoxWidget tbName = new LabelAndBoxWidget("name:", 80, 300);
	protected final LabelAndBoxWidget tbDescription = new LabelAndBoxWidget("description:", 80, 300);
	protected final LabelAndBoxWidget tbResolution = new LabelAndBoxWidget("resolution:", 80, 80);
	protected final LabelAndBoxWidget tbFormat = new LabelAndBoxWidget("format:", 80, 80);

	protected final LabelAndCheckWidget wEnabledCheckbox = new LabelAndCheckWidget("enabled:", 50);
	protected final LabelAndCheckWidget wColorCheckbox = new LabelAndCheckWidget("color:", 50);
	protected final LabelAndCheckWidget wCryptedCheckbox = new LabelAndCheckWidget("OCS:", 50);
	protected final LabelAndCheckWidget wCSA5Checkbox = new LabelAndCheckWidget("CSA5:", 50);

	// -------------- UPLOAD -----------------------------
	private final MyUploader uploader;

	protected final Label labelErrorMessage = new Label();
	protected final Panel pStatus = new SimplePanel();

	protected FullVideoInfo videoInfo;
	protected IMyDialogBox container;
	private ChangeHandler changeHandler;
	private FullVideoStatus initialStatus = null;

	// ---------------------------- abstract methods
	protected abstract Logger logger();

	public abstract void setData(FullVideoInfo videoInfo);

	// ---------------------------- constructor
	protected AbstractVideoInfoPanel(int maxUploadSizeMo) {
		this.uploader = new MyUploader(maxUploadSizeMo);
		this.initComposants();
		this.initHandlers();
	}

	// ------------------------------- public methods
	public void addChangeHandler(ChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
	}

	public void setContainer(IMyDialogBox container) {
		this.container = container;
	}

	public void setErrorMessage(String errorMessage) {
		this.labelErrorMessage.setText(errorMessage);
		this.labelErrorMessage.setVisible(true);
	}

	public FullVideoInfo getDataFromWidget() {
		this.videoInfo.setName(this.tbName.getBoxUserInput());
		this.videoInfo.setDescription(this.tbDescription.getBoxUserInput());
		this.videoInfo.setFormat(this.tbFormat.getBoxUserInput());
		this.videoInfo.setResolution(this.tbResolution.getBoxUserInput());
		this.videoInfo.setEntryProtected(this.cbEntryProtected.getValue());
		this.videoInfo.setColor(this.wColorCheckbox.getValue());
		this.videoInfo.setOcs(this.wCryptedCheckbox.getValue());
		this.videoInfo.setCsa5(this.wCSA5Checkbox.getValue());
		this.videoInfo.setEnabled(this.wEnabledCheckbox.getValue());

		return this.videoInfo;
	}

	// ----------------------------------------- protected methods
	protected void refreshVideoInfo(FullVideoInfo videoInfo) {

		logger().config("refreshVideoInfo() - " + videoInfo.getId());

		this.setProtection(videoInfo.getDtoProtection());

		this.videoInfo = videoInfo;

		this.tbName.setValue(videoInfo.getName());
		this.tbDescription.setValue(videoInfo.getDescription());
		this.tbFormat.setValue(videoInfo.getFormat());
		this.tbResolution.setValue(videoInfo.getResolution());

		this.wColorCheckbox.setValue(videoInfo.isColor());
		this.wCryptedCheckbox.setValue(videoInfo.isOcs());
		this.wCSA5Checkbox.setValue(videoInfo.isCsa5());
		this.wEnabledCheckbox.setValue(videoInfo.isEnabled());
		this.cbEntryProtected.setValue(videoInfo.isEntryProtected());

	}

	protected void refreshStatus(FullVideoStatus status) {

		this.pStatus.clear();
		this.pStatus.add(StatusUtils.buildLabelStatus(status));

		// status initial avant upload ou modification
		if (this.initialStatus == null) {
			this.initialStatus = status;
		}

	}

	protected Widget buildMainPanel() {

		this.main.setSpacing(PANEL_SPACING);

		HorizontalPanel hStatusAndCbPanel = new HorizontalPanel();
		hStatusAndCbPanel.setWidth(MAX_WIDTH);
		hStatusAndCbPanel.add(this.pStatus);
		hStatusAndCbPanel.add(this.cbEntryProtected);
		hStatusAndCbPanel.setCellHorizontalAlignment(this.cbEntryProtected, HasHorizontalAlignment.ALIGN_RIGHT);
		this.main.add(hStatusAndCbPanel);

		this.main.add(this.tbName);
		this.main.add(this.tbDescription);
		this.main.add(this.wColorCheckbox);

		Panel hpResFor = new HorizontalPanel();
		hpResFor.add(this.tbResolution);
		hpResFor.add(this.tbFormat);
		this.main.add(hpResFor);

		Panel hpCheckbox = new HorizontalPanel();
		hpCheckbox.add(this.wCryptedCheckbox);
		hpCheckbox.add(this.wCSA5Checkbox);
		hpCheckbox.add(this.wEnabledCheckbox);

		this.main.add(hpCheckbox);

		// Panel upload file
		this.main.add(this.buildUploadPanel());

		this.main.add(this.labelErrorMessage);

		return this.main;
	}

	// ----------------------------- private methods
	private void setProtection(DtoVideoProtection protection) {

		boolean canUpdate = protection.getActionUpdate().isActive();

		this.tbDescription.setEnabled(canUpdate);
		this.tbFormat.setEnabled(canUpdate);
		this.tbResolution.setEnabled(canUpdate);
		this.wColorCheckbox.setEnabled(canUpdate);
		this.wCryptedCheckbox.setEnabled(canUpdate);
		this.wCSA5Checkbox.setEnabled(canUpdate);
		this.wEnabledCheckbox.setEnabled(canUpdate);

		// attribut entry protected
		this.cbEntryProtected.setEnabled(protection.getActionModifProtectedAttribut().isActive());
		this.cbEntryProtected.setTitle(protection.getActionModifProtectedAttribut().getMessage());

		// case à cocher enabled
		this.wEnabledCheckbox.setEnabled(protection.getActionEnableDisable().isActive());
		this.wEnabledCheckbox.setTitle(protection.getActionEnableDisable().getMessage());

		// upload
		this.uploadPanel.setTitle(protection.getActionUpload().getMessage());
		this.uploader.setEnabled(protection.getActionUpload().isActive());

	}

	private void initHandlers() {

		this.uploader.setUploadHandler(new UploadHandler() {

			@Override
			public void onOpenDialog() {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeStarting(int countOfFile) {
				logger().config("onStart()");
				refreshStatus(FullVideoStatus.PENDING);
				videoInfo.setUploadPending(true);
				publishChangeEvent(VideoUploadState.start);
			}

			@Override
			public void onQueue(String filename) {

				if (tbName.getBoxUserInput() != null) {
					if (!tbName.getBoxUserInput().equals(filename)) {
						// changement de fichier
						refreshStatus(FullVideoStatus.NEW);
					}
				}
				tbName.setValue(filename);
			}

			@Override
			public void onCancel(String filename) {
				logger().config("onCancel()");
				publishChangeEvent(VideoUploadState.canceled);
				tbName.setValue("");
				videoInfo.setUploadPending(false);
				refreshStatus(initialStatus);
			}

			@Override
			public void onFinish(UploadInfo uploadInfo) {
				logger().config("onFinish()");
				if (videoInfo.isUploadPending()) {
					uploadPanel.setVisible(false);
					publishChangeEvent(VideoUploadState.finish);
				}

			}

			@Override
			public void onError(String filename, String messageError) {
				logger().config("onError()");
				Window.alert(messageError);
				publishChangeEvent(VideoUploadState.error);
				videoInfo.setUploadPending(false);
				refreshStatus(initialStatus);

			}

		});

	}

	private void publishChangeEvent(VideoUploadState state) {
		if (this.changeHandler != null) {
			this.changeHandler.onChange(new VideoUploadChangeEvent(state));
		}
	}

	private Panel buildUploadPanel() {

		this.uploadPanel = new SimplePanel();
		this.uploadPanel.getElement().setId(PANEL_UPLOAD_VIDEO);

		this.uploadPanel.add(this.uploader.getUploadPanel());

		return this.uploadPanel;
	}

	private void initComposants() {
		this.labelErrorMessage.setVisible(false);
		this.labelErrorMessage.setStyleName(TEXT_ERROR);
		this.cbEntryProtected.setValue(false);
		this.wColorCheckbox.setValue(true);

		this.tbName.setEnabled(false);
	}

}
