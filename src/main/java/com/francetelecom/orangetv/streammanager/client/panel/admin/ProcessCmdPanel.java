package com.francetelecom.orangetv.streammanager.client.panel.admin;

import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.AbstractCmdDatas;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Voir les processus multicat demarres
 * 
 * @author ndmz2720
 *
 */
public class ProcessCmdPanel extends AbstractCmdPanel {

	private final static Logger log = Logger.getLogger("ProcessCmdPanel");

	private final VerticalPanel main = new VerticalPanel();

	private final ListBox lbProcessNames = new ListBox();
	private final LabelAndListWidget wListProcessNames = new LabelAndListWidget("", 10, 200, lbProcessNames, 1);

	// ------------------------------------- constructor
	ProcessCmdPanel(final List<String> logFilenames) {

		this.iniComposants(logFilenames);
		this.initWidget(this.buildMainPanel());
		this.initHandlers();

	}

	// ---------------------------------------- package methods
	@Override
	AbstractCmdDatas getDataFromWidget() {

		ProcessCmdDatas datas = new ProcessCmdDatas();
		datas.setProcessName(this.wListProcessNames.getListUserInput());

		return datas;

	}

	@Override
	void setCmdResponse(CmdResponse cmdResponse) {

	}

	@Override
	Logger getLog() {
		return log;
	}

	// --------------------------------------- private methods
	private Widget buildMainPanel() {
		this.main.setSpacing(PANEL_SPACING);

		HorizontalPanel hpPanel = new HorizontalPanel();
		hpPanel.setWidth(MAX_WIDTH);
		hpPanel.setSpacing(PANEL_SPACING);
		hpPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpPanel.add(new Label("Processus: "));
		hpPanel.add(this.wListProcessNames);
		hpPanel.add(this.btExecute);

		this.main.add(hpPanel);

		return this.main;
	}

	private void initHandlers() {
		ChangeHandler commonHandler = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (argumentsChangeHandler != null) {
					argumentsChangeHandler.onChange(event);
				}
			}
		};
		this.lbProcessNames.addChangeHandler(commonHandler);
	}

	private void iniComposants(final List<String> processNames) {

		if (processNames != null) {
			for (String process : processNames) {
				this.lbProcessNames.addItem(process);
			}
		}
		super.btExecute.setText("List of processus...");

	}

	// ========================================== INNER CLASS
	static class ProcessCmdDatas extends AbstractCmdDatas {

		private String processName;

		public String getProcessName() {
			return processName;
		}

		public void setProcessName(String processName) {
			this.processName = processName;
		}

	}

}
