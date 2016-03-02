package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.CAIdentifierDescriptor.CASystemId;
import com.francetelecom.orangetv.streammanager.shared.util.ValueHelper;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

public class CASystemIdWidget extends Composite implements CssConstants {

	private final HorizontalPanel main = new HorizontalPanel();
	private LabelAndBoxWidget wCaSystemIdBox = new LabelAndBoxWidget("CA system id:", 100, 150);

	private final Button btDelete = new Button();

	// ---------------------------------------------- constructor
	public CASystemIdWidget() {
		this.initWidget(this.buildMainPanel());

	}

	// ------------------------------------------- public methods
	public void setDeleteClickHandler(ClickHandler clickHandler) {
		this.btDelete.addClickHandler(clickHandler);
	}

	public CASystemId getCASystemId() {

		int id = ValueHelper.getIntValue(wCaSystemIdBox.getBoxUserInput(), 0);

		return new CASystemId(id);
	}

	public void setValue(CASystemId caSystemId) {
		if (caSystemId != null) {
			this.wCaSystemIdBox.setValue(caSystemId.getId() + "");
		}
	}

	// ---------------------------------------------- private methods
	private Panel buildMainPanel() {

		this.btDelete.setStyleName(STYLE_IMG_ERASE);

		this.main.setSpacing(PANEL_SPACING);
		this.main.setStyleName(PANEL_INPUT);
		this.main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this.main.add(this.wCaSystemIdBox);

		this.main.add(this.btDelete);

		return this.main;
	}
}
