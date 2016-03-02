package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.client.panel.descriptor.IDescriptorPanel;
import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.client.util.EnumListManager;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ComponentDescriptorSubtitle;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

public class SubtitleLanguageWidget extends Composite implements CssConstants {

	private final HorizontalPanel main = new HorizontalPanel();
	private LabelAndListWidget languageList;
	private CheckBox cbHardOfHearing = new CheckBox("hoh");
	
	private final Button btDelete = new Button();

	//--------------------------------------------- constructor
	public SubtitleLanguageWidget () {
		this.initWidget(this.buildMainPanel());
		
	}
	
	//------------------------------------------ public methods
	public void setDeleteClickHandler(ClickHandler clickHandler) {
		this.btDelete.addClickHandler(clickHandler);
	}

	public ComponentDescriptorSubtitle getComponentDescriptorSubtitle() {
		final String langCode = languageList.getListUserInput();
		final boolean hardOfHearing = this.cbHardOfHearing.getValue();
		return new ComponentDescriptorSubtitle(langCode, hardOfHearing);
	}
	public void setValue (String languageCode, boolean hardOfHearing) {
		this.languageList.setValue(languageCode);
		this.cbHardOfHearing.setValue(hardOfHearing);
	}
    //------------------------------------------ private methods
	private Panel buildMainPanel() {
		
		languageList = new LabelAndListWidget("lang:", 50, 150, EnumListManager.get().buildListBoxLanguage(),
				IDescriptorPanel.LANGUAGE_DEFAULT_CODE);
		this.btDelete.setStyleName(STYLE_IMG_ERASE);

		this.main.setSpacing(PANEL_SPACING);
		this.main.setStyleName(PANEL_INPUT);
		this.main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		this.main.add(this.languageList);
		this.main.add(this.cbHardOfHearing);
		this.main.add(this.btDelete);
		
		return this.main;
	}
}
