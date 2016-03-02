package com.francetelecom.orangetv.streammanager.client.panel.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel.AbstractCmdDatas;
import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;

/**
 * Panel de base pour toutes les taches d'administration
 * 
 * @author ndmz2720
 *
 */
abstract class AbstractCmdPanel extends Composite implements CssConstants {

	protected final Button btExecute = new Button("Execute command...");
	protected ChangeHandler argumentsChangeHandler;

	abstract AbstractCmdDatas getDataFromWidget();

	abstract void setCmdResponse(CmdResponse cmdResponse);

	abstract Logger getLog();

	// on force l'execution
	void autoexecute() {
		getLog().config("autoexecute()");
		this.btExecute.click();
	}

	void unlock() {
		this.btExecute.setEnabled(true);
	}

	void lock() {
		this.btExecute.setEnabled(false);
	}

	void setExecuteClickHandler(ClickHandler clickHandler) {
		this.btExecute.addClickHandler(clickHandler);
	}

	void setChangeClickHandler(ChangeHandler argumentsChangeHandler) {
		this.argumentsChangeHandler = argumentsChangeHandler;
	}

}
