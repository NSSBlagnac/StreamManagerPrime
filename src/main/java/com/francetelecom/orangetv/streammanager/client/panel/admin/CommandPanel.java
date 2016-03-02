package com.francetelecom.orangetv.streammanager.client.panel.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.AbstractCmdDatas;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;

/**
 * Contient juste bouton executer
 * 
 * @author ndmz2720
 *
 */
public class CommandPanel extends AbstractCmdPanel {

	private final static Logger log = Logger.getLogger("CommandPanel");

	public CommandPanel() {
		super.btExecute.setText("Execute remote command on multicat server...");
		this.initWidget(super.btExecute);
	}

	@Override
	AbstractCmdDatas getDataFromWidget() {
		return new CommandDatas();
	}

	@Override
	void setCmdResponse(CmdResponse cmdResponse) {

	}

	@Override
	Logger getLog() {
		return log;
	}

	// =============================================== INNER CLASS
	static class CommandDatas extends AbstractCmdDatas {

		CommandDatas() {
			super.setSshCommand(true);
			super.setConfirmCommand(true);
		}

	}

}
