package com.francetelecom.orangetv.streammanager.client.panel.descriptor;

import com.francetelecom.orangetv.streammanager.client.widget.ContentCategoryWidget;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentNibbleLevel2;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.IDescriptor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Les SPEC DVB autorisent plusieurs categorie.
 * Ici on n'en gère qu'une seule ou pas du tout
 * 
 * @author NDMZ2720
 *
 */
public class ContentDescriptorPanel extends AbstractDescriptorPanel {

	private final VerticalPanel main = new VerticalPanel();
	private final VerticalPanel content = new VerticalPanel();

	private Button btAddCategoryWidget = new Button("Define category");
	// private List<ContentCategoryWidget> listCategoryWidget = new
	// ArrayList<ContentCategoryWidget>();

	private ContentCategoryWidget contentCategoryWidget;

	// ---------------------------------------------- construtor
	public ContentDescriptorPanel() {
		this.setStyleName(PANEL_DESCRIPTOR);
		this.setWidget(this.buildContentPanel());
		this.buildHandlers();
	}

	// --------------------------------------------- implements IDescriptorPanel
	@Override
	public void populateWidgetFromData(EitEvent eitEvent) {
		this.clean();
		ContentDescriptor descriptor = (eitEvent == null) ? null : eitEvent.getContentDescriptor();
		if (super.display(descriptor)) {

			// WARNING : on ne prend que la premiere categorie
			for (ContentNibbleLevel2 category : descriptor.getistCategories()) {
				ContentCategoryWidget widget = this.setCategoryWidget();
				widget.setValue(category);
				break;
			}
		}
	}

	@Override
	public IDescriptor getDataFromWidget() {
		ContentDescriptor descriptor = new ContentDescriptor();

		// for (ContentCategoryWidget widget : listCategoryWidget) {

		if (this.contentCategoryWidget != null) {
			String categoryCode = this.contentCategoryWidget.getCategoryLevel2Code();
			String parentCode = this.contentCategoryWidget.getCategoryLevel1Code();
			descriptor.addCategory(parentCode, categoryCode);
		}
		// }

		return descriptor;
	}

	// ---------------------------------------------- private methods
	private void clean() {
		// this.listCategoryWidget.clear();
		this.content.clear();
	}

	private Panel buildContentPanel() {

		main.setStyleName(PANEL_CONTENT_DESCRIPTOR);
		main.setSpacing(PANEL_SPACING);

		main.add(this.btAddCategoryWidget);
		main.add(content);
		content.setSpacing(PANEL_SPACING);

		return main;
	}

	private void buildHandlers() {

		this.btAddCategoryWidget.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				setCategoryWidget();
			}
		});
	}

	private ContentCategoryWidget buildCategoryWidget() {

		ContentCategoryWidget widget = new ContentCategoryWidget();
		DeleteClickHandler deleteHandler = new DeleteClickHandler(widget);
		widget.setDeleteClickHandler(deleteHandler);
		this.contentCategoryWidget = widget;
		// listCategoryWidget.add(widget);

		return widget;
	}

	private ContentCategoryWidget setCategoryWidget() {
		ContentCategoryWidget widget = this.buildCategoryWidget();
		content.add(widget);
		this.btAddCategoryWidget.setEnabled(false);
		return widget;
	}

	private void deleteCategoryWidget(ContentCategoryWidget categoryWidget) {
		// this.listCategoryWidget.remove(categoryWidget);
		this.content.remove(categoryWidget);
		this.contentCategoryWidget = null;
		this.btAddCategoryWidget.setEnabled(true);
	}

	// ===================== INNER CLASS ===================
	private class DeleteClickHandler implements ClickHandler {

		private final ContentCategoryWidget categoryWidget;

		private DeleteClickHandler(ContentCategoryWidget categoryWidget) {
			this.categoryWidget = categoryWidget;
		}

		@Override
		public void onClick(ClickEvent event) {
			deleteCategoryWidget(this.categoryWidget);
		}

	}

}
