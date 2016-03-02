package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.client.util.WidgetUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;

public class LabelAndCheckWidget extends Composite {

	private final CheckBox checkBox;

	// -------------------------------------- accessors

	public CheckBox getCheckBox() {
		return this.checkBox;
	}

	// ------------------------------------------ public methods
	public void setValue(boolean value) {
		this.checkBox.setValue(value);
	}

	public boolean getValue() {
		return this.checkBox.getValue();
	}

	public void setEnabled(boolean enabled) {
		this.checkBox.setEnabled(enabled);
	}

	// --------------------------------------------- constructor
	public LabelAndCheckWidget(final String labelText, final int labelWidth) {
		this.checkBox = new CheckBox();
		Panel panel = WidgetUtils.buildLabelAndWidgetPanel(labelText, this.checkBox, labelWidth);
		this.initWidget(panel);
	}

}
