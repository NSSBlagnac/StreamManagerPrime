package com.francetelecom.orangetv.streammanager.client.panel.admin;

import java.util.List;

import com.francetelecom.orangetv.streammanager.client.panel.AbstractPanel;
import com.francetelecom.orangetv.streammanager.client.panel.StreamPanel.ResultType;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdRequest;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

class ConsolePanel extends AbstractPanel {

	private final VerticalPanel main = new VerticalPanel();

	private final LabelAndBoxWidget tbCommand = new LabelAndBoxWidget("Command SSH:", 100, 600);
	private final SimplePanel logContainer = new SimplePanel();
	private Element divArea;

	private final Label labelExitCode = new Label("");
	// -------------------------- protection de la zone de saisie manuelle
	private boolean cmdProtected = false;

	// -------------------------------------- constructor
	ConsolePanel() {

		this.initWidget(this.buildMainPanel());
		this.initComposants();
	}

	// ---------------------------------------- package methods
	void hideCommandLine() {
		this.tbCommand.setVisible(false);
	}

	void setCommandLine(String cmd) {
		this.tbCommand.setValue(cmd);
	}

	void protectCmdManualEntry(boolean cmdProtected) {
		this.cmdProtected = cmdProtected;
		this.tbCommand.setEnabled(!cmdProtected);
	}

	void clean(boolean cleanCmdLine) {
		this.divArea.removeAllChildren();
		this.setActionResult(null, ResultType.success);
		this.labelExitCode.setText(null);
		this.tbCommand.setVisible(true);
		if (cleanCmdLine) {
			this.setCommandLine("");
		}
	}

	void setCmdResponse(CmdResponse cmdResponse) {

		this.writeResponse(cmdResponse.getResponseLines(), false);
		this.writeResponse(cmdResponse.getErrorLines(), true);

		String message = "";
		ResultType type = null;

		if (cmdResponse.isSuccess()) {
			message = "success!";
			type = ResultType.success;
		} else {
			if (cmdResponse.getErrorMessage() != null) {
				message = "error! " + cmdResponse.getErrorMessage();
				type = ResultType.error;
			} else {
				message = "failure! ";
				type = ResultType.warn;
			}
		}
		if (cmdResponse.getUsedCommand() != null) {
			message += cmdResponse.getUsedCommand();
		}
		this.setActionResult(message, type);

		this.labelExitCode.setText("ExitCode: " + cmdResponse.getExitValue());
	}

	CmdRequest getDataFromWidget() {

		CmdRequest cmdRequest = new CmdRequest();
		cmdRequest.setCmd(this.tbCommand.getBoxUserInput());

		return cmdRequest;
	}

	void unlock() {

		if (!this.cmdProtected) {
			this.tbCommand.setEnabled(true);

		}
	}

	void lock() {
		this.tbCommand.setEnabled(false);
	}

	// ------------------------------------------- private methods

	private void writeResponse(List<String> lines, boolean error) {

		if (lines == null) {
			return;
		}
		String style = (error) ? STYLE_LOG_ERROR : STYLE_LOG_DEBUG;
		for (String line : lines) {

			Element child = DOM.createDiv();
			child.addClassName(style);
			child.setInnerText(line);
			this.divArea.appendChild(child);
		}

	}

	private Widget buildMainPanel() {

		this.divArea = this.logContainer.getElement();

		this.main.setSpacing(PANEL_SPACING);
		this.main.setWidth(MAX_WIDTH);

		HorizontalPanel hpCommand = new HorizontalPanel();
		hpCommand.setSpacing(PANEL_SPACING);
		hpCommand.add(this.tbCommand);
		this.main.add(hpCommand);

		this.main.add(this.logContainer);

		HorizontalPanel hpMessages = new HorizontalPanel();
		hpMessages.setSpacing(PANEL_SPACING);
		hpMessages.add(this.labelExitCode);
		hpMessages.add(this.labelResult);
		this.main.add(hpMessages);
		return this.main;
	}

	private void initComposants() {

		this.divArea.setId(PANEL_ADMIN_RESULT);

	}

}
