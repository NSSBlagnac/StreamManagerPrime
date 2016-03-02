package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.client.panel.descriptor.IDescriptorPanel;
import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.client.util.EnumListManager;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ComponentDescriptorAudio;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;

public class AudioLanguageWidget extends Composite implements CssConstants {

	private final HorizontalPanel main = new HorizontalPanel();
	private LabelAndListWidget languageList;
	private CheckBox cbAudiodescription = new CheckBox("audio description");

	private static int compteur = 0;
	private final static String GROUP_AUDIO = "rbGroupAudio";
	private RadioButton rbAudioNormal;
	private RadioButton rbAudioDolby;
	private RadioButton rbAudioDTS;

	private final Button btDelete = new Button();

	// ------------------------------------- constructor
	public AudioLanguageWidget() {
		this.initWidget(this.buildMainPanel());

	}

	// -------------------------------------- public methods
	public void setDeleteClickHandler(ClickHandler clickHandler) {
		this.btDelete.addClickHandler(clickHandler);
	}

	public ComponentDescriptorAudio getComponentDescriptorAudio() {

		final String langCode = languageList.getListUserInput();
		final boolean audioDescription = cbAudiodescription.getValue();

		final boolean dolby = this.rbAudioDolby.getValue();
		final boolean dts = this.rbAudioDTS.getValue();

		return new ComponentDescriptorAudio(langCode, audioDescription, dolby, dts);
	}

	public void setValue(String languageCode, boolean audioDescription, boolean dolby, boolean dts) {
		this.languageList.setValue(languageCode);
		this.cbAudiodescription.setValue(audioDescription);
		if (dolby) {
			this.rbAudioDolby.setValue(true);
		} else if (dts) {
			this.rbAudioDTS.setValue(true);
		} else {
			this.rbAudioNormal.setValue(true);
		}
	}

	// -------------------------------------- private methods
	private Panel buildMainPanel() {

		String groupName = GROUP_AUDIO + compteur++;
		this.rbAudioNormal = new RadioButton(groupName, "AAC (normal)");
		this.rbAudioDolby = new RadioButton(groupName, "AC3 (dolby)");
		this.rbAudioDTS = new RadioButton(groupName, "DTS");
		this.rbAudioNormal.setValue(true);

		languageList = new LabelAndListWidget("lang:", 50, 150, EnumListManager.get().buildListBoxLanguage(),
				IDescriptorPanel.LANGUAGE_DEFAULT_CODE);
		this.btDelete.setStyleName(STYLE_IMG_ERASE);

		this.main.setSpacing(PANEL_SPACING);
		this.main.setStyleName(PANEL_INPUT);
		this.main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this.main.add(this.languageList);
		this.main.add(this.cbAudiodescription);

		this.main.add(this.rbAudioNormal);
		this.main.add(this.rbAudioDolby);
		this.main.add(this.rbAudioDTS);

		this.main.add(this.btDelete);

		return this.main;
	}
}
