package com.francetelecom.orangetv.streammanager.client.panel.streaminfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.AbstractPanel;
import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils.IMyDialogBox;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndCheckWidget;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoStreamProtection;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoInfo;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractStreamInfoPanel extends AbstractPanel implements CssConstants {

	protected final VerticalPanel main = new VerticalPanel();

	protected final CheckBox cbEitToInject = new CheckBox("injection eit");
	protected final CheckBox cbEntryProtected = new CheckBox("data protected");
	protected final LabelAndBoxWidget tbLcn = new LabelAndBoxWidget("lcn:", 80, 100);
	protected final LabelAndBoxWidget tbUsi = new LabelAndBoxWidget("usi:", 40, 120);
	protected final LabelAndBoxWidget tbName = new LabelAndBoxWidget("name:", 80, 300);
	protected final LabelAndBoxWidget tbDescription = new LabelAndBoxWidget("description:", 80, 300);
	protected final LabelAndBoxWidget tbUser = new LabelAndBoxWidget("user:", 80, 200);
	protected final LabelAndBoxWidget tbTripletDvb = new LabelAndBoxWidget("tsid:sid:onid:", 80, 80);
	protected final LabelAndBoxWidget tbUrlAdress = new LabelAndBoxWidget("url adress:", 80, 200);
	protected final LabelAndBoxWidget tbUrlPort = new LabelAndBoxWidget("url port:", 50, 80);

	protected final ListBox lbVideoFileList = new ListBox();
	protected final LabelAndListWidget wVideoFiles = new LabelAndListWidget("video file:", 80, 250, lbVideoFileList, 1);

	protected final LabelAndCheckWidget wEnabledCheckBox = new LabelAndCheckWidget("enabled:", 50);
	protected final Label labelErrorMessage = new Label();
	protected final Panel pStatus = new SimplePanel();

	protected final Button btServicePlan = new Button("Service plan...");
	private ServicePlanPanel servicePlanPanel;

	protected StreamInfo streamInfo;
	protected IMyDialogBox container;

	protected Map<Integer, VideoInfo> mapVideoFile = new HashMap<Integer, VideoInfo>();

	// ---------------------------- abstract methods
	protected abstract Logger logger();

	protected abstract void _setData(StreamInfo streamInfo);

	// ---------------------------- constructor
	protected AbstractStreamInfoPanel() {
		this.initComposants();
		this.initHandlers();
	}

	// --------------------------------- Overriding Widget

	@Override
	protected void onLoad() {
		super.onLoad();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				tbName.setFocus(true);

			}
		});
	}

	// ------------------------------- public methods
	public void setData(StreamInfo streamInfo) {
		this.btServicePlan.setVisible(streamInfo != null);
		this._setData(streamInfo);
	}

	public void setContainer(IMyDialogBox container) {
		this.container = container;
	}

	public StreamInfo getDataFromWidget() {

		this.streamInfo.setName(this.tbName.getBoxUserInput());
		this.streamInfo.setDescription(this.tbDescription.getBoxUserInput());
		this.streamInfo.setUser(this.tbUser.getBoxUserInput());

		this.streamInfo.setAddress(this.tbUrlAdress.getBoxUserInput());
		this.streamInfo.setTripleDvb(this.tbTripletDvb.getBoxUserInput());
		this.streamInfo.setEnable(this.wEnabledCheckBox.getValue());
		this.streamInfo.setEntryProtected(this.cbEntryProtected.getValue());
		this.streamInfo.setEitToInject(this.cbEitToInject.getValue());

		try {

			int videoId = Integer.parseInt(this.wVideoFiles.getListUserInput());
			this.streamInfo.setVideoId(videoId);
			this.streamInfo.setVideoFilename(this.mapVideoFile.get(videoId).getName());

			int lcn = Integer.parseInt(this.tbLcn.getBoxUserInput());
			this.streamInfo.setLcn(lcn);

			int port = Integer.parseInt(this.tbUrlPort.getBoxUserInput());
			this.streamInfo.setPort(port);

			int usi = Integer.parseInt(this.tbUsi.getBoxUserInput());
			this.streamInfo.setUsi(usi);
		} catch (NumberFormatException e) {
			logger().warning("Error in getDataFromWidget()" + e.getMessage());
		}

		return this.streamInfo;
	}

	public void setErrorMessage(String errorMessage) {
		this.labelErrorMessage.setText(errorMessage);
		this.labelErrorMessage.setVisible(true);
	}

	// -------------------------------- protected methods
	protected void refreshStreamInfo(StreamInfo streamInfo) {

		logger().config("refreshStreamInfo() - " + streamInfo.getId());
		this.streamInfo = streamInfo;
		this.mapVideoFile.clear();

		this.setNumericValue(this.tbLcn, this.streamInfo.getLcn(),
				this.getPlaceHolder(streamInfo.isEntryProtected(), streamInfo.getRangeLcn()));

		this.setNumericValue(this.tbUsi, this.streamInfo.getUsi(),
				this.getPlaceHolder(streamInfo.isEntryProtected(), streamInfo.getRangeUSI()));

		this.tbName.setValue(this.streamInfo.getName());
		this.tbDescription.setValue(this.streamInfo.getDescription());

		this.tbUser.setValue(this.streamInfo.getUser());
		this.tbUser.setPlaceHolderAndTitle(streamInfo.isEntryProtected() ? "Banc qualif" : "user");

		this.tbTripletDvb.setValue(this.streamInfo.getTripletDvd());
		this.tbTripletDvb.setPlaceHolderAndTitle("xx:xx:167");

		this.tbUrlAdress.setValue(this.streamInfo.getAddress());
		this.tbUrlAdress.setPlaceHolderAndTitle(this.getPlaceHolder(streamInfo.isEntryProtected(),
				streamInfo.getRangeAddress()));

		// FIXME remplacer 8200 par parametre
		this.setNumericValue(this.tbUrlPort, this.streamInfo.getPort(), "8200");

		this.wEnabledCheckBox.setValue(this.streamInfo.isEnable());

		this.cbEntryProtected.setValue(this.streamInfo.isEntryProtected());

		this.cbEitToInject.setValue(this.streamInfo.isEitToInject());

		List<VideoInfo> listVideoFile = streamInfo.getVideoFileList();
		if (listVideoFile != null) {

			for (VideoInfo videoFile : listVideoFile) {
				logger().config("videofile: " + videoFile.getName());
				this.mapVideoFile.put(videoFile.getId(), videoFile);
				this.lbVideoFileList.addItem(this.getVideoFileDescription(videoFile), videoFile.getId() + "");
			}

			this.wVideoFiles.setValue(streamInfo.getVideoId() + "");
		}

		this.setProtection(streamInfo.getDtoProtection());

	}

	private void setProtection(DtoStreamProtection protection) {

		boolean canUpdate = protection.getActionUpdate().isActive();

		this.tbLcn.setEnabled(canUpdate);
		this.tbUsi.setEnabled(canUpdate);
		this.tbName.setEnabled(canUpdate);
		this.tbDescription.setEnabled(canUpdate);
		this.tbUser.setEnabled(canUpdate);
		this.tbTripletDvb.setEnabled(canUpdate);
		this.tbUrlAdress.setEnabled(canUpdate);
		this.tbUrlPort.setEnabled(canUpdate);
		this.lbVideoFileList.setEnabled(canUpdate);

		// attribut entry protected
		this.cbEntryProtected.setEnabled(protection.getActionModifProtectedAttribut().isActive());
		this.cbEntryProtected.setTitle(protection.getActionModifProtectedAttribut().getMessage());

		// case à cocher enabled
		this.wEnabledCheckBox.setEnabled(protection.getActionEnableDisable().isActive());
		this.wEnabledCheckBox.setTitle(protection.getActionEnableDisable().getMessage());
	}

	private void setNumericValue(LabelAndBoxWidget widget, int value, String placeHolder) {
		if (widget == null) {
			return;
		}
		widget.setValue((value <= 0) ? null : value + "");
		widget.setPlaceHolderAndTitle(placeHolder);
	}

	private String getPlaceHolder(boolean entryProtected, String range) {
		String inOrOutRange = (streamInfo.isEntryProtected()) ? "in " : "out of ";
		return inOrOutRange + range;
	}

	protected Widget buildMainPanel() {

		this.main.setSpacing(PANEL_SPACING);

		HorizontalPanel hStatusAndCbPanel = new HorizontalPanel();
		hStatusAndCbPanel.setWidth(MAX_WIDTH);
		hStatusAndCbPanel.add(this.pStatus);
		hStatusAndCbPanel.add(this.cbEitToInject);
		hStatusAndCbPanel.setCellHorizontalAlignment(this.cbEitToInject, HasHorizontalAlignment.ALIGN_RIGHT);
		hStatusAndCbPanel.add(this.cbEntryProtected);
		hStatusAndCbPanel.setCellHorizontalAlignment(this.cbEntryProtected, HasHorizontalAlignment.ALIGN_RIGHT);
		this.main.add(hStatusAndCbPanel);

		final Panel hLcnAndUsiPanel = new HorizontalPanel();
		hLcnAndUsiPanel.add(this.tbLcn);
		hLcnAndUsiPanel.add(this.tbUsi);
		this.main.add(hLcnAndUsiPanel);

		this.main.add(this.tbName);
		this.main.add(this.tbDescription);
		this.main.add(this.tbUser);

		this.main.add(wVideoFiles);

		final Panel tripletAndEnabled = new HorizontalPanel();
		tripletAndEnabled.add(this.tbTripletDvb);
		tripletAndEnabled.add(this.wEnabledCheckBox);
		this.main.add(tripletAndEnabled);

		final Panel hUrlPanel = new HorizontalPanel();
		hUrlPanel.add(this.tbUrlAdress);
		hUrlPanel.add(this.tbUrlPort);
		this.main.add(hUrlPanel);

		this.main.add(this.labelErrorMessage);
		this.main.add(this.btServicePlan);

		return this.main;
	}

	protected String getVideoFileDescription(VideoInfo videoFile) {
		return videoFile.getName() + " " + videoFile.getResolution() + " " + videoFile.getFormat();
	}

	// ----------------------------- private methods
	private void initComposants() {
		this.labelErrorMessage.setVisible(false);
		this.labelErrorMessage.setStyleName(TEXT_ERROR);
		this.cbEntryProtected.setValue(false);

		this.tbName.setPlaceHolderAndTitle("HD/SD CSAx name");
		this.tbDescription.setPlaceHolderAndTitle("description");

		this.btServicePlan.setVisible(false);
	}

	private void showServicePlanDialog() {

		if (this.servicePlanPanel == null) {
			this.servicePlanPanel = new ServicePlanPanel();
		}
		this.servicePlanPanel.setDatas(this.getDataFromWidget());

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Service plan part IP", null,
				this.servicePlanPanel, false, null);
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	private void initHandlers() {

		this.btServicePlan.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showServicePlanDialog();
			}
		});
	}

}
