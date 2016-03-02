package com.francetelecom.orangetv.streammanager.client.panel.descriptor;

import java.util.ArrayList;
import java.util.List;

import com.francetelecom.orangetv.streammanager.client.widget.CASystemIdWidget;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.CAIdentifierDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.CAIdentifierDescriptor.CASystemId;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.IDescriptor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Panel for CAIdentifierDescriptor
 * 
 * @author sylvie
 *
 */
public class CAIdentifierDescriptorPanel extends AbstractDescriptorPanel {

	private final VerticalPanel main = new VerticalPanel();
	private final VerticalPanel content = new VerticalPanel();

	private Button btAddCASystemId = new Button("Add CA system id");
	private List<CASystemIdWidget> listCASystemIdWidget = new ArrayList<CASystemIdWidget>();

	// -------------------------------- constructor
	public CAIdentifierDescriptorPanel() {
		this.setStyleName(PANEL_DESCRIPTOR);
		this.setWidget(this.buildContentPanel());
		this.buildHandlers();
	}

	// --------------------------------------------- implements IDescriptorPanel
	@Override
	public void populateWidgetFromData(EitEvent eitEvent) {
		this.clean();

		CAIdentifierDescriptor descriptor = (eitEvent == null) ? null : eitEvent.getCaIdentifierDescriptor();

		if (super.display(descriptor)) {
			for (CASystemId caSystemId : descriptor.getListSystemIds()) {
				CASystemIdWidget widget = this.addCASystemIdWidget();
				widget.setValue(caSystemId);
			}

		}
	}

	@Override
	public IDescriptor getDataFromWidget() {
		CAIdentifierDescriptor descriptor = new CAIdentifierDescriptor();

		for (CASystemIdWidget widget : listCASystemIdWidget) {

			descriptor.addSystemId(widget.getCASystemId());
		}

		return descriptor;
	}

	// --------------------------------------------- private methods
	private void clean() {
		this.listCASystemIdWidget.clear();
		this.content.clear();
	}

	private Panel buildContentPanel() {

		main.setStyleName(PANEL_CONTENT_DESCRIPTOR);
		main.setSpacing(PANEL_SPACING);

		main.add(this.btAddCASystemId);
		main.add(content);
		content.setSpacing(PANEL_SPACING);

		this.addCASystemIdWidget();
		return main;
	}

	private void buildHandlers() {

		this.btAddCASystemId.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				addCASystemIdWidget();
			}
		});
	}

	private CASystemIdWidget buildCASystemIdWidget() {

		CASystemIdWidget widget = new CASystemIdWidget();
		widget.setDeleteClickHandler(new DeleteClickHandler(widget));
		listCASystemIdWidget.add(widget);

		return widget;
	}

	private CASystemIdWidget addCASystemIdWidget() {
		CASystemIdWidget widget = this.buildCASystemIdWidget();
		content.add(widget);
		return widget;
	}

	private void deleteCaSystemIdWidgetWidget(CASystemIdWidget caSystemIdWidget) {
		this.listCASystemIdWidget.remove(caSystemIdWidget);
		this.content.remove(caSystemIdWidget);
	}

	// ===================== INNER CLASS ===================
	private class DeleteClickHandler implements ClickHandler {

		private final CASystemIdWidget caSystemIdWidget;

		private DeleteClickHandler(CASystemIdWidget caSystemIdWidget) {
			this.caSystemIdWidget = caSystemIdWidget;
		}

		@Override
		public void onClick(ClickEvent event) {
			deleteCaSystemIdWidgetWidget(this.caSystemIdWidget);
		}

	}
}
