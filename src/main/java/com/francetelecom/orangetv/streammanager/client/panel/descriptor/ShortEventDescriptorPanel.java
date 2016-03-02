package com.francetelecom.orangetv.streammanager.client.panel.descriptor;

import com.francetelecom.orangetv.streammanager.client.util.EnumListManager;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.IDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortEventDescriptor;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ShortEventDescriptorPanel extends AbstractDescriptorPanel {

	private final LabelAndBoxWidget wShortEventName = new LabelAndBoxWidget("name", 150, 300);
	private final LabelAndBoxWidget wShortEventText = new LabelAndBoxWidget("description", 150, 300);
	private LabelAndListWidget wShortEventLang;

	public ShortEventDescriptorPanel() {

		this.initComposants();
		this.setWidget(this.buildContentPanel());
	}

	// --------------------------------------- implements IDescriptorPanel
	@Override
	public void populateWidgetFromData(EitEvent eitEvent) {
		this.clean();
		ShortEventDescriptor descriptor = (eitEvent == null) ? null : eitEvent.getShortEventDescriptor();
		if (super.display(descriptor)) {
			this.wShortEventLang.setValue(descriptor.getLang().getCode());
			this.wShortEventName.setValue(descriptor.getName());
			this.wShortEventText.setValue(descriptor.getText());
		}
	}

	@Override
	public IDescriptor getDataFromWidget() {

		String name = wShortEventName.getBoxUserInput();
		String text = wShortEventText.getBoxUserInput();
		String langCode = wShortEventLang.getListUserInput();
		ShortEventDescriptor descriptor = new ShortEventDescriptor(name, text, langCode);
		return descriptor;
	}

	// ----------------------------------------------- private methods
	private void clean() {
		if (this.wShortEventLang != null) {
			this.wShortEventLang.setValue(null);
		}
		this.wShortEventName.clear();
		this.wShortEventText.clear();
	}

	private Panel buildContentPanel() {

		VerticalPanel content = new VerticalPanel();
		content.setStyleName(PANEL_CONTENT_DESCRIPTOR);
		content.setSpacing(PANEL_SPACING);

		content.add(this.wShortEventName);
		content.add(this.wShortEventText);
		content.add(this.wShortEventLang);
		return content;
	}

	private void initComposants() {
		this.setStyleName(PANEL_DESCRIPTOR);

		this.wShortEventLang = new LabelAndListWidget("lang:", 150, 150, EnumListManager.get().buildListBoxLanguage(),
				LANGUAGE_DEFAULT_CODE);

		this.wShortEventName.setMaxLength(20);
		this.wShortEventText.setMaxLength(20);
	}

}
