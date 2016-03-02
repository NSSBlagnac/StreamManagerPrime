package com.francetelecom.orangetv.streammanager.client.panel.admin;

import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.AbstractCmdDatas;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LogReaderCmdPanel extends AbstractCmdPanel {

	private final static Logger log = Logger.getLogger("LogReaderCmdPanel");

	private final VerticalPanel main = new VerticalPanel();

	private final static String CMD_TAIL = "tail";
	private final static String CMD_CAT = "cat";

	private final LabelAndListWidget lbCommand = new LabelAndListWidget("", 10, 50, new String[] { CMD_TAIL, CMD_CAT });
	private final LabelAndBoxWidget tbTailCountLine = new LabelAndBoxWidget("-n", 30, 100);
	private final ListBox lbLogNames = new ListBox();
	private final LabelAndListWidget wListLogNames = new LabelAndListWidget("", 10, 200, lbLogNames, 1);
	private final LabelAndBoxWidget tbGrepOption = new LabelAndBoxWidget("grep:", 50, 200);

	// ------------------------------------- constructor
	LogReaderCmdPanel(final List<String> logFilenames) {

		this.iniComposants(logFilenames);
		this.initWidget(this.buildMainPanel());
		this.initHandlers();

		this.onSelectFileCommand();
	}

	// ---------------------------------------- package methods
	@Override
	Logger getLog() {
		return log;
	}

	@Override
	AbstractCmdDatas getDataFromWidget() {

		LogCmdDatas datas = new LogCmdDatas();
		boolean tailCmd = this.isTailCommand();
		datas.setTailCommand(tailCmd);

		datas.setCommand(this.lbCommand.getListUserInput());
		if (tailCmd) {
			String countStr = this.tbTailCountLine.getBoxUserInput();
			int count;
			try {
				count = Integer.parseInt(countStr);
			} catch (NumberFormatException ex) {
				count = 0;
			}
			datas.setCount(count);
		}
		datas.setFilename(this.wListLogNames.getListUserInput());

		if (!this.tbGrepOption.isUserInputEmpty()) {
			datas.setGrep(this.tbGrepOption.getBoxUserInput());
		}

		return datas;

	}

	@Override
	void setCmdResponse(CmdResponse cmdResponse) {

	}

	// --------------------------------------- private methods
	private Widget buildMainPanel() {
		this.main.setSpacing(PANEL_SPACING);

		HorizontalPanel hpPanel = new HorizontalPanel();
		hpPanel.setWidth(MAX_WIDTH);
		hpPanel.setSpacing(PANEL_SPACING);
		hpPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hpPanel.add(this.lbCommand);
		hpPanel.add(this.tbTailCountLine);
		hpPanel.add(this.wListLogNames);
		hpPanel.add(this.tbGrepOption);

		this.main.add(hpPanel);
		this.main.add(this.btExecute);

		return this.main;
	}

	private void initHandlers() {
		this.lbCommand.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				onSelectFileCommand();
				if (argumentsChangeHandler != null) {
					argumentsChangeHandler.onChange(event);
				}
			}
		});
		ChangeHandler commonHandler = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (argumentsChangeHandler != null) {
					argumentsChangeHandler.onChange(event);
				}
			}
		};
		this.lbLogNames.addChangeHandler(commonHandler);
		this.tbTailCountLine.addChangeHandler(commonHandler);
		this.tbGrepOption.addChangeHandler(commonHandler);
	}

	private void onSelectFileCommand() {
		this.tbTailCountLine.setVisible(this.isTailCommand());
	}

	private boolean isTailCommand() {
		return this.lbCommand.getListUserInput().equals(CMD_TAIL);
	}

	private void iniComposants(final List<String> logFilenames) {

		if (logFilenames != null) {
			for (String filename : logFilenames) {
				this.lbLogNames.addItem(filename);
			}
		}

		super.btExecute.setText("Show logs...");
	}

	// ========================================== INNER CLASS
	static class LogCmdDatas extends AbstractCmdDatas {

		private String command;
		private int count;
		private String filename;
		private String grep;
		private boolean tailCommand = true;

		public boolean isTailCommand() {
			return tailCommand;
		}

		public void setTailCommand(boolean tailCommand) {
			this.tailCommand = tailCommand;
		}

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}

		public String getGrep() {
			return grep;
		}

		public void setGrep(String grep) {
			this.grep = grep;
		}
	}

}
