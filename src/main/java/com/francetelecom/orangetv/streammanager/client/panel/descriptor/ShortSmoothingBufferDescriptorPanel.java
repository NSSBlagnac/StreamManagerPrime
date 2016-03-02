package com.francetelecom.orangetv.streammanager.client.panel.descriptor;

import com.francetelecom.orangetv.streammanager.client.util.EnumListManager;
import com.francetelecom.orangetv.streammanager.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.IDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortSmoothingBufferDescriptor;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ShortSmoothingBufferDescriptorPanel extends AbstractDescriptorPanel {

	private LabelAndListWidget sbLeakRateWidget;

	// ------------------------------------------ constructor
	public ShortSmoothingBufferDescriptorPanel() {
		this.setStyleName(PANEL_DESCRIPTOR);
		this.setWidget(this.buildContentPanel());
	}

	// --------------------------------------- implements IDescriptorPanel
	@Override
	public void populateWidgetFromData(EitEvent eitEvent) {

		this.clean();
		ShortSmoothingBufferDescriptor descriptor = (eitEvent == null) ? null : eitEvent
				.getShortSmoothingBufferDescriptor();
		if (super.display(descriptor)) {
			this.sbLeakRateWidget.setValue(descriptor.getSbLeakRate().getCode());
		}
	}

	@Override
	public IDescriptor getDataFromWidget() {

		final String rateCode = sbLeakRateWidget.getListUserInput();
		ShortSmoothingBufferDescriptor descriptor = new ShortSmoothingBufferDescriptor(rateCode);
		return descriptor;
	}

	// -------------------------------------- private methods
	private void clean() {
		this.sbLeakRateWidget.setValue("");
	}

	private Panel buildContentPanel() {

		sbLeakRateWidget = new LabelAndListWidget("sb leak rate", 150, 150, EnumListManager.get()
				.buildListBoxSbLeakRate(), 1);
		VerticalPanel content = new VerticalPanel();
		content.setStyleName(PANEL_CONTENT_DESCRIPTOR);
		content.setSpacing(PANEL_SPACING);
		content.add(sbLeakRateWidget);
		return content;
	}

}
