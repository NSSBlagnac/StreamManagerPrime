package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.client.util.EnumListManager;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentNibbleLevel1;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentNibbleLevel2;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
/**
 * Widget encapsulant la catégorie et sa sous-categorie
 * ainsi que le bouton de suppression
 * A utiliser dans un panel ContentDescriptorPanel
 * @author sylvie
 *
 */
public class ContentCategoryWidget extends Composite implements CssConstants {

	private final HorizontalPanel main = new HorizontalPanel();
	
	private final Button btDelete = new Button();
	private LabelAndListWidget listCatetoryLevel1;
	private LabelAndListWidget listCatetoryLevel2;
	
	
	//---------------------------------------- constructor
	public ContentCategoryWidget () {
		this.initWidget(this.buildMainPanel());
        this.initHandlers();		
	}
	
	//-------------------------------------------- public methods
	public void setDeleteClickHandler(ClickHandler clickHandler) {
		this.btDelete.addClickHandler(clickHandler);
	}

	public String getCategoryLevel1Code() {
		return this.listCatetoryLevel1.getListUserInput();
	}
	public String getCategoryLevel2Code() {
		return this.listCatetoryLevel2.getListUserInput();
	}

	public void setValue(ContentNibbleLevel2 category) {
		
		this.listCatetoryLevel1.setValue(category.getParent().getCode());
		this.updateListCategoryLevel2();
		this.listCatetoryLevel2.setValue(category.getCode());
	}
	//------------------------------------------- private methods

	private void initHandlers() {

		ChangeHandler changeHandler = new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				
				updateListCategoryLevel2();
			}
		};
		this.listCatetoryLevel1.addChangeHandler(changeHandler);
		
	}

	private void updateListCategoryLevel2() {
		
		if (listCatetoryLevel2 != null) {
			final String catLevel1Code = listCatetoryLevel1.getListUserInput();
			final ContentNibbleLevel1 catLevel1 = ContentNibbleLevel1.get(catLevel1Code);
			EnumListManager.get().updateListBoxCategoryLevel2(catLevel1, listCatetoryLevel2.getListBox());
		}
	}
	private Panel buildMainPanel() {

		this.btDelete.setStyleName(STYLE_IMG_ERASE);
		
		this.main.setSpacing(PANEL_SPACING);
		this.main.setStyleName(PANEL_INPUT);

		this.main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		
		this.listCatetoryLevel1 = new LabelAndListWidget("level1", 50, 250, EnumListManager.get().buildListBoxCategoryLevel1(), 1);
		this.listCatetoryLevel2 = new LabelAndListWidget("level2", 50, 250, new ListBox(), 1);
		this.updateListCategoryLevel2();
		
		this.main.add(this.listCatetoryLevel1);
		this.main.add(this.listCatetoryLevel2);
		
		this.main.add(this.btDelete);
		
		return this.main;
	}
	
}
