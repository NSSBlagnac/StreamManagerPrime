package com.francetelecom.orangetv.streammanager.client.panel.descriptor;

import com.francetelecom.orangetv.streammanager.client.util.EnumListManager;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ExtendedEventDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.IDescriptor;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dans l'AR les descriptors complementaires ne sont pas utilisés.
 * Aussi le panel a-t-il été allégé en conséquence
 * 
 * @author ndmz2720
 *
 */
public class ExtendedEventDescriptorPanel extends AbstractDescriptorPanel {

	private final LabelAndBoxWidget wExtEventText = new LabelAndBoxWidget("name", 150, 300);
	private LabelAndListWidget wExtEventLang;

	// ------------------------------------ constructor
	public ExtendedEventDescriptorPanel() {
		this.setStyleName(PANEL_DESCRIPTOR);
		this.initComposants();
		this.setWidget(this.buildContentPanel());
	}

	// ---------------------------------------- implements IDescriptorPanel
	@Override
	public void populateWidgetFromData(EitEvent eitEvent) {
		this.clean();
		ExtendedEventDescriptor descriptor = (eitEvent == null) ? null : eitEvent.getExtendedEventDescriptor();
		if (super.display(descriptor)) {

			this.wExtEventText.setValue(descriptor.getText());
			if (this.wExtEventLang != null) {
				this.wExtEventLang.setValue(descriptor.getLang().getCode());
			}
		}

	}

	@Override
	public IDescriptor getDataFromWidget() {

		final String text = wExtEventText.getBoxUserInput();
		final String langCode = wExtEventLang.getListUserInput();
		ExtendedEventDescriptor descriptor = new ExtendedEventDescriptor(text, langCode);

		return descriptor;
	}

	// ------------------------------------------ private methods
	private void clean() {
		this.wExtEventText.clear();
		if (this.wExtEventLang != null) {
			this.wExtEventLang.setValue(null);
		}
	}

	private Panel buildContentPanel() {

		wExtEventLang = new LabelAndListWidget("lang:", 150, 150, EnumListManager.get().buildListBoxLanguage(),
				LANGUAGE_DEFAULT_CODE);

		VerticalPanel content = new VerticalPanel();
		content.setStyleName(PANEL_CONTENT_DESCRIPTOR);
		content.setSpacing(PANEL_SPACING);

		content.add(this.wExtEventText);
		content.add(this.wExtEventLang);
		return content;
	}

	private void initComposants() {
		this.wExtEventText.setMaxLength(20);
	}

}
