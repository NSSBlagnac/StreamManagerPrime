package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.client.util.EnumListManager;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.PrivateDescriptor.PrivateToken;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

public class PrivateTokenWidget extends Composite implements CssConstants {
	private final HorizontalPanel main = new HorizontalPanel();
	private LabelAndListWidget wPrivateTagListBox;
	private LabelAndBoxWidget wPrivateTokenBox = new LabelAndBoxWidget("token (String):", 100, 80);
	private final Button btDelete = new Button();

	// ---------------------------------------------- constructor
	public PrivateTokenWidget() {
		buildComposants();
		this.initWidget(this.buildMainPanel());

	}

	// ------------------------------------------- public methods
	public void setDeleteClickHandler(ClickHandler clickHandler) {
		this.btDelete.addClickHandler(clickHandler);
	}

	public PrivateToken getPrivateToken() {

		String tag = this.wPrivateTagListBox.getListUserInput();
		String token = this.wPrivateTokenBox.getBoxUserInput();
		return new PrivateToken(tag, token);
	}

	public void setValue(PrivateToken privateToken) {
		this.wPrivateTagListBox.setValue(privateToken.getTag());
		this.wPrivateTokenBox.setValue(privateToken.getToken());
	}

	// ---------------------------------------------- private methods
	private Panel buildMainPanel() {

		this.btDelete.setStyleName(STYLE_IMG_ERASE);

		this.main.setSpacing(PANEL_SPACING);
		this.main.setStyleName(PANEL_INPUT);
		this.main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this.main.add(this.wPrivateTagListBox);
		this.main.add(this.wPrivateTokenBox);
		this.main.add(this.btDelete);

		return this.main;
	}

	private void buildComposants() {

		final ListBox listBox = EnumListManager.get().buildListBoxPrivateTag();
		wPrivateTagListBox = new LabelAndListWidget("Tag:", 50, 250, listBox, 1);
	}
}
