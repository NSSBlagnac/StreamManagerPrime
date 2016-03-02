package com.francetelecom.orangetv.streammanager.client.panel.descriptor;

import java.util.ArrayList;
import java.util.List;

import com.francetelecom.orangetv.streammanager.client.widget.PrivateTokenWidget;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.IDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.PrivateDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.PrivateDescriptor.PrivateToken;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Panel for PrivateDescriptor
 * 
 * @author sylvie
 * 
 */
public class PrivateDescriptorPanel extends AbstractDescriptorPanel {
	private final VerticalPanel main = new VerticalPanel();
	private final VerticalPanel content = new VerticalPanel();

	private Button btAddPrivateToken = new Button("Add private descriptor");
	private List<PrivateTokenWidget> listPrivateTokenWidget = new ArrayList<PrivateTokenWidget>();

	// -------------------------------- constructor
	public PrivateDescriptorPanel() {
		this.setStyleName(PANEL_DESCRIPTOR);
		this.setWidget(this.buildContentPanel());
		this.buildHandlers();
	}

	// --------------------------------------------- implements IDescriptorPanel
	@Override
	public void populateWidgetFromData(EitEvent eitEvent) {
		this.clean();
		PrivateDescriptor descriptor = (eitEvent == null) ? null : eitEvent.getPrivateDescriptor();
		if (super.display(descriptor)) {
			if (descriptor.hasToken()) {
				for (PrivateToken token : descriptor.getListPrivateTokens()) {
					PrivateTokenWidget widget = this.addPrivateTokenWidget();
					widget.setValue(token);
				}
			}
		}
	}

	@Override
	public IDescriptor getDataFromWidget() {
		PrivateDescriptor descriptor = new PrivateDescriptor();

		for (PrivateTokenWidget widget : listPrivateTokenWidget) {

			descriptor.addPrivateToken(widget.getPrivateToken());
		}

		return descriptor;
	}

	// --------------------------------------------- private methods
	private void clean() {
		this.listPrivateTokenWidget.clear();
		this.content.clear();
	}

	private Panel buildContentPanel() {

		main.setStyleName(PANEL_CONTENT_DESCRIPTOR);
		main.setSpacing(PANEL_SPACING);

		main.add(this.btAddPrivateToken);
		main.add(content);
		content.setSpacing(PANEL_SPACING);

		this.addPrivateTokenWidget();
		return main;
	}

	private void buildHandlers() {

		this.btAddPrivateToken.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				addPrivateTokenWidget();
			}
		});
	}

	private PrivateTokenWidget buildPrivateTokenWidget() {

		PrivateTokenWidget widget = new PrivateTokenWidget();
		widget.setDeleteClickHandler(new DeleteClickHandler(widget));
		listPrivateTokenWidget.add(widget);

		return widget;
	}

	private PrivateTokenWidget addPrivateTokenWidget() {
		PrivateTokenWidget widget = this.buildPrivateTokenWidget();
		content.add(widget);
		return widget;
	}

	private void deletePrivateTokenWidgetWidget(PrivateTokenWidget privateTokenWidget) {
		this.listPrivateTokenWidget.remove(privateTokenWidget);
		this.content.remove(privateTokenWidget);
	}

	// ===================== INNER CLASS ===================
	private class DeleteClickHandler implements ClickHandler {

		private final PrivateTokenWidget privateTokenWidget;

		private DeleteClickHandler(PrivateTokenWidget privateTokenWidget) {
			this.privateTokenWidget = privateTokenWidget;
		}

		@Override
		public void onClick(ClickEvent event) {
			deletePrivateTokenWidgetWidget(privateTokenWidget);
		}

	}

	public static enum PrivateTag {
		Tag0xAA("0xAA", "Barker, Start Over, See also"), TagOxAB("0xAB", "Interactive service");

		private final String tag;
		private final String description;

		public static PrivateTag get(String code) {
			for (PrivateTag privateTag : PrivateTag.values()) {
				if (privateTag.getCode().equals(code)) {
					return privateTag;
				}
			}
			return Tag0xAA;
		}

		public String getTag() {
			return this.tag;
		}

		public String getDescription() {
			return this.description;
		}

		public String getCode() {
			return this.tag;
		}

		private PrivateTag(String tag, String description) {
			this.tag = tag;
			this.description = description;
		}
	}

}
