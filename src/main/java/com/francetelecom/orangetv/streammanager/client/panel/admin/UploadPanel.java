package com.francetelecom.orangetv.streammanager.client.panel.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.AbstractCmdDatas;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.streammanager.client.widget.LabelWidget;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UploadPanel extends AbstractCmdPanel {

	private final static Logger log = Logger.getLogger("UploadPanel");

	private final VerticalPanel main = new VerticalPanel();

	private final LabelAndBoxWidget tbSourceFilepath = new LabelAndBoxWidget("tomcat server", 100, 400);
	private final LabelAndBoxWidget tbTargetFilepath = new LabelAndBoxWidget("multicat server", 100, 300);

	// ------------------------------------- constructor
	UploadPanel(String srcPath, String targetPath) {

		this.initWidget(this.buildMainPanel());
		this.initComposants(srcPath, targetPath);
	}

	// -------------------------------------- overriding AbstractCmdPanel
	@Override
	Logger getLog() {
		return log;
	}

	@Override
	AbstractCmdDatas getDataFromWidget() {

		String srcFilepath = this.tbSourceFilepath.getBoxUserInput();
		String targetFilepath = this.tbTargetFilepath.getBoxUserInput();

		return new UploadCmdDatas(srcFilepath, targetFilepath);
	}

	@Override
	void setCmdResponse(CmdResponse cmdResponse) {

	}

	// -------------------------------------------- private methods
	private void initComposants(String srcPath, String targetPath) {
		this.tbTargetFilepath.setValue(targetPath);
		this.tbSourceFilepath.setValue(srcPath);
		super.btExecute.setText("Upload file to multicat server...");
	}

	private Widget buildMainPanel() {

		this.main.setSpacing(PANEL_SPACING);

		HorizontalPanel hpPanel = new HorizontalPanel();
		hpPanel.setWidth(MAX_WIDTH);
		hpPanel.setSpacing(PANEL_SPACING);
		hpPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpPanel.add(this.tbSourceFilepath);
		hpPanel.add(new LabelWidget("to", 30));
		hpPanel.add(this.tbTargetFilepath);

		this.main.add(hpPanel);
		this.main.add(this.btExecute);

		return this.main;
	}

	// ========================================== INNER CLASS
	public static class UploadCmdDatas extends AbstractCmdDatas {

		private final String srcFilepath;
		private final String targetFilePath;

		public String getScrFilePath() {
			return this.srcFilepath;
		}

		public String getTargetFilepath() {
			return this.targetFilePath;
		}

		private UploadCmdDatas(String srcFilePath, String targetFilepath) {
			this.srcFilepath = srcFilePath;
			this.targetFilePath = targetFilepath;
			super.setSshCommand(false);
		}
	}

}
