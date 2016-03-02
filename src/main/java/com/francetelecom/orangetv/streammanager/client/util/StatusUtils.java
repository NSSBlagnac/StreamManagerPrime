package com.francetelecom.orangetv.streammanager.client.util;

import com.francetelecom.orangetv.streammanager.client.panel.StreamPanel.ResultType;
import com.francetelecom.orangetv.streammanager.client.widget.MyUploader.UploadState;
import com.francetelecom.orangetv.streammanager.shared.dto.FullVideoStatus;
import com.francetelecom.orangetv.streammanager.shared.dto.StreamStatus;
import com.francetelecom.orangetv.streammanager.shared.dto.SupervisorStatus;
import com.google.gwt.user.client.ui.Label;

public class StatusUtils {

	public static void buildLabelStatus(Label label, UploadState state) {

		String text = state.getText();

		String styleName = CssConstants.STYLE_LABEL_STATUS + " ";
		switch (state) {
		case started:
			styleName += CssConstants.UPLOAD_INPROGRESS;
			break;
		case done:
			styleName += CssConstants.UPLOAD_DONE;
			break;
		case canceled:
			styleName += CssConstants.UPLOAD_CANCELED;
			break;
		case error:
			styleName += CssConstants.UPLOAD_ERROR;
			break;
		case none:
			styleName += "";
			break;

		}
		label.setText(text);
		label.setTitle("upload state: " + text);
		label.setStyleName(styleName);

	}

	public static void buildLogLabel(Label label, ResultType state) {

		if (state == null) {
			return;
		}
		String styleName = null;
		switch (state) {
		case success:
			styleName = CssConstants.STYLE_LOG_SUCCESS;
			break;
		case warn:
			styleName = CssConstants.STYLE_LOG_WARN;
			break;
		case error:
			styleName = CssConstants.STYLE_LOG_ERROR;
			break;

		}
		label.setTitle("log status: " + state.name());
		label.setStyleName(styleName);

	}

	public static Label buildLabelStatus(FullVideoStatus status) {
		final Label label = new Label();
		String text = status.name();

		String styleName = CssConstants.STYLE_LABEL_STATUS + " ";
		switch (status) {
		case RUNNING:
			styleName += CssConstants.STYLE_STATUS_STARTED;
			break;
		case USED:
			styleName += CssConstants.STYLE_STATUS_USED;
			break;
		case UPLOADED:
			styleName += CssConstants.STYLE_STATUS_UPLOADED;
			break;
		case NEW:
			styleName += CssConstants.STYLE_STATUS_NEW;
			break;
		case ERROR:
			styleName += CssConstants.STYLE_STATUS_FAILURE;
			break;
		case PENDING:
			styleName += CssConstants.STYLE_STATUS_PROCESSING;
			break;

		default:
			break;
		}
		label.setText(text);
		label.setTitle(status.getDescription());
		label.setStyleName(styleName);

		return label;

	}

	public static Label buildLabelStatus(SupervisorStatus status) {
		final Label label = new Label();
		String text = status.name();

		String styleName = CssConstants.STYLE_LABEL_STATUS + " ";
		switch (status) {
		case started:
			styleName += CssConstants.STYLE_STATUS_STARTED;
			break;
		case stopped:
			styleName += CssConstants.STYLE_STATUS_STOPPED;
			break;
		case starting:
		case stopping:
			styleName += CssConstants.STYLE_STATUS_PROCESSING;
			break;
		case unknown:
			styleName += CssConstants.STYLE_STATUS_NEW;
			break;
		}

		label.setText(text);
		label.setTitle("status: " + status.name());
		label.setStyleName(styleName);

		return label;

	}

	public static Label buildLabelStatus(StreamStatus status) {

		final Label label = new Label();
		String text = status.name();

		String styleName = CssConstants.STYLE_LABEL_STATUS + " ";
		switch (status) {
		case STARTED:
			styleName += CssConstants.STYLE_STATUS_STARTED;
			break;
		case STOPPED:
			styleName += CssConstants.STYLE_STATUS_STOPPED;
			break;
		case NEW:
			styleName += CssConstants.STYLE_STATUS_NEW;
			break;
		case STARTING:
		case STOPPING:
			styleName += CssConstants.STYLE_STATUS_PROCESSING;
			break;

		default:
			if (status.name().contains("FAILURE")) {
				text = "FAILURE";
				styleName += CssConstants.STYLE_STATUS_FAILURE;
				break;
			}
			break;
		}
		label.setText(text);
		label.setTitle("status: " + status.name());
		label.setStyleName(styleName);

		return label;

	}

}
