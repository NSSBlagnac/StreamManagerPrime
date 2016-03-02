package com.francetelecom.orangetv.streammanager.client.controller;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.service.IActionCallback;
import com.francetelecom.orangetv.streammanager.client.service.IStreamMulticatServiceAsync;
import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils;
import com.francetelecom.orangetv.streammanager.shared.util.EitException;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractController implements CssConstants {

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	protected static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	protected static final int FAST_REFRESH = 20; // 200ms
	protected static int STANDARD_REFRESH = 500; // 500ms
	protected static final int SLOW_REFRESH = 1000 * 30; // 30s

	protected static final int PROFILE_REFRESH = 1000 * 60 * 15; // 15mn

	protected IStreamMulticatServiceAsync rpcService;

	protected void init(AppController appController, IStreamMulticatServiceAsync rpcService) {
		this.getLog().info("init()");
		this.rpcService = rpcService;
		this.appController = appController;
	}

	protected AppController appController;

	// ----------------------------- abstract methods
	protected abstract void go();

	protected abstract Logger getLog();

	// ----------------------------- protected methods

	protected void showInformation(String message) {
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Info", new String[] { message }, null, false,
				null);
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	protected void showError(String errorMessage, IActionCallback actionCallback, Widget ankor) {

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Error", new String[] { errorMessage }, null,
				false, actionCallback);
		if (ankor == null) {
			WidgetUtils.centerDialogAndShow(dialogBox);
		} else {
			dialogBox.showRelativeTo(ankor);
		}
	}

	protected String buildOnFailureMessage(Throwable caught) {

		EitException eitException = null;
		if (caught instanceof EitException) {
			eitException = (EitException) caught;
		}

		String errorMessage = (eitException != null) ? eitException.getErrorMessage() : caught.getMessage();

		if (eitException == null && caught.getCause() != null) {
			errorMessage += " (" + caught.getCause().getMessage() + ")";
		}
		// log.warning("onFailure(): " + errorMessage);
		return errorMessage;
	}

}
