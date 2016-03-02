package com.francetelecom.orangetv.streammanager.client.panel;

import com.francetelecom.orangetv.streammanager.client.panel.StreamPanel.ResultType;
import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public abstract class AbstractPanel extends Composite implements CssConstants {

	protected Label labelResult = new Label("Result: ");

	// ----------------------------------------- public methods
	public void setActionResult(String message, ResultType resultType) {

		String styleName = TEXT_SUCCESS;
		switch (resultType) {
		case success:
			styleName = TEXT_SUCCESS;
			break;
		case warn:
			styleName = TEXT_WARN;
			break;
		case error:
			styleName = TEXT_ERROR;
			break;
		}

		labelResult.setText(message);
		labelResult.removeStyleName(TEXT_SUCCESS);
		labelResult.removeStyleName(TEXT_WARN);
		labelResult.removeStyleName(TEXT_ERROR);
		labelResult.setStyleName(styleName);
	}

	// =============================== INNER CLASS
	protected static class AppButton extends Button {

		public AppButton(String text) {
			super(text);
			this.setStyleName(BUTTON_STYLE);
		}
	}
}
