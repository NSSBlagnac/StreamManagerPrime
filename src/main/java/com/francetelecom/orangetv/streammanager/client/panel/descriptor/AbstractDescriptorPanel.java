package com.francetelecom.orangetv.streammanager.client.panel.descriptor;

import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.IDescriptor;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class AbstractDescriptorPanel extends SimplePanel implements CssConstants, IDescriptorPanel {

	protected boolean display(IDescriptor descriptor) {

		boolean display = true;
		if (descriptor == null || !descriptor.isEnabled()) {

			display = false;
		}
		this.setVisible(display);
		return display;
	}

}
