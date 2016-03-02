package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class LabelWidget extends Composite {

	private final Label label;

	// -------------------------------------- accessors

	// ------------------------------------------ public methods
	public void setValue(String value) {
		this.label.setText(value);
	}

	public String getValue() {
		return this.label.getText();
	}

	// --------------------------------------------- constructor
	public LabelWidget(final String labelText, final int labelWidth) {
		this.label = new Label();
		Panel panel = WidgetUtils.buildLabelAndWidgetPanel(labelText, null, labelWidth);
		this.initWidget(panel);
	}

}
