package com.francetelecom.orangetv.streammanager.client.panel.eit;

import com.francetelecom.orangetv.streammanager.client.panel.AbstractPanel;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EitJsonPanel extends AbstractPanel {

	private final Button btImportJson = new AppButton("import json");
	private final Button btShowJson = new AppButton("show json");

	private final TextArea taJson = new TextArea();

	// ------------------------------------------ constructor
	public EitJsonPanel() {

		this.initWidget(this.buildMainPanel());
	}

	// ----------------------------------------------- public methods
	public void setJson(String json) {
		this.taJson.setText(json);
	}

	public String getJson() {
		return this.taJson.getText();
	}

	public void bindHandlers(ClickHandler showJsonClickHandler, ClickHandler importJsonClickHandler) {
		this.btShowJson.addClickHandler(showJsonClickHandler);
		this.btImportJson.addClickHandler(importJsonClickHandler);

	}

	// ---------------------------------------private methods
	private Widget buildMainPanel() {

		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(PANEL_SHOW_ACTION);
		panel.setSpacing(PANEL_SPACING);
		panel.setWidth(MAX_WIDTH);

		final HorizontalPanel panelButton = new HorizontalPanel();
		panelButton.setSpacing(PANEL_SPACING);
		this.btShowJson.setTitle("Show current eit for control before sending...");
		panelButton.add(this.btShowJson);
		this.btImportJson.setTitle("Replace your eit with current json...");
		panelButton.add(this.btImportJson);

		panel.add(panelButton);

		this.taJson.setWidth(MAX_WIDTH);
		this.taJson.setHeight("400px");
		panel.add(this.taJson);

		return panel;
	}

}
