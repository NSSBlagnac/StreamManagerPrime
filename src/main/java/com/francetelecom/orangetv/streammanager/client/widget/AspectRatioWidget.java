package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.client.util.EnumListManager;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractDescriptor.Language;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ComponentDescriptorAspectRatio;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ComponentDescriptorAspectRatio.AspectRatio;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * Display list of Aspect ratio
 * 
 * @author ndmz2720
 * 
 */
public class AspectRatioWidget extends Composite implements CssConstants {

	private final HorizontalPanel main = new HorizontalPanel();
	private LabelAndListWidget aspectRatioList;
	private final Label labelDescription = new Label();

	// ------------------------------------- constructor
	public AspectRatioWidget() {
		this.initWidget(this.buildMainPanel());
	}

	// -------------------------------------- public methods
	public void setValue(ComponentDescriptorAspectRatio aspectRatioDescriptor) {

		if (aspectRatioDescriptor != null) {

			this.aspectRatioList.setValue(aspectRatioDescriptor.getAspectRatio().getCode());
			this.refreshDescription(aspectRatioDescriptor);
		}
	}

	public ComponentDescriptorAspectRatio getComponentDescriptorAspectRatio() {
		return this.buildDescriptor();
	}

	// -------------------------------------- private methods
	private Panel buildMainPanel() {

		this.main.setSpacing(PANEL_SPACING);
		this.main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		final ListBox listBox = EnumListManager.get().buildListBoxAspectRatio();
		aspectRatioList = new LabelAndListWidget("aspect ratio", 100, 150, listBox, 1);
		this.initHandlers(listBox);

		this.main.setSpacing(PANEL_SPACING);
		this.main.add(aspectRatioList);
		this.main.add(labelDescription);

		return this.main;
	}

	private void initHandlers(ListBox listBox) {

		listBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refreshDescription(buildDescriptor());
			}
		});
	}

	private ComponentDescriptorAspectRatio buildDescriptor() {
		String aspectRatioCode = aspectRatioList.getListUserInput();
		return new ComponentDescriptorAspectRatio(Language.FRE, AspectRatio.get(aspectRatioCode));
	}

	private void refreshDescription(ComponentDescriptorAspectRatio descriptor) {

		this.labelDescription.setText(descriptor.getComponentType().getDescription());

	}

}
