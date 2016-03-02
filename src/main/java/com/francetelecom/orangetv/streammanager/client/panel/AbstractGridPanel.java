package com.francetelecom.orangetv.streammanager.client.panel;

import java.util.logging.Logger;

import org.gwt.advanced.client.datamodel.EditableGridDataModel;
import org.gwt.advanced.client.ui.PagerListener;
import org.gwt.advanced.client.ui.widget.EditableGrid;
import org.gwt.advanced.client.ui.widget.GridPanel;
import org.gwt.advanced.client.ui.widget.Pager;
import org.gwt.advanced.client.ui.widget.cell.AbstractCell;
import org.gwt.advanced.client.ui.widget.cell.DefaultGridCellFactory;
import org.gwt.advanced.client.ui.widget.cell.LabelCell;

import com.francetelecom.orangetv.streammanager.client.controller.AppController.Action;
import com.francetelecom.orangetv.streammanager.client.controller.AppController.ActionClickEvent;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoActionProtection;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoActionProtection.Rules;
import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.util.ValueHelper;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Attention, la premiere colonne doit etre la col id!
 * 
 * @author ndmz2720
 *
 */
public abstract class AbstractGridPanel extends AbstractPanel {

	protected final static String HTML_BR = "<br/>";

	protected final VerticalPanel main = new VerticalPanel();

	protected MyEditableGrid grid;
	protected final EditableGridDataModel model = new EditableGridDataModel(null);

	// create a new grid panel
	protected final MyGridPanel gridPanel = new MyGridPanel();
	protected ClickHandler actionClickHandler;

	// ----------------------------- abstract methods
	protected abstract int[] getColSizes();

	protected abstract Logger getLog();

	// --------------------------------------- constructor
	protected AbstractGridPanel() {
		this.initHandlers();
	}

	// ---------------------------------------- public methods
	public void bindHandlers(ClickHandler actionClickHandler) {
		this.actionClickHandler = actionClickHandler;
	}

	public void unlock() {
		this.gridPanel.unlock();
		this.grid.removeStyleName(STYLE_CURSOR_WAIT);
	}

	public void lock() {
		this.gridPanel.lock();
		this.grid.addStyleName(STYLE_CURSOR_WAIT);
	}

	// ---------------------------------- protected methods
	protected void doAction(Action action, int rowIndex) {

		// int absoluteGridIndex = this.getAbsoluteGridIndex(rowIndex);
		int id = this.getId(rowIndex);
		this.getLog().config("doAction(): " + action + " row: " + rowIndex + " - id: " + id);

		if (id != IDto.ID_UNDEFINED) {
			ActionClickEvent event = new ActionClickEvent(action, id);
			if (this.actionClickHandler != null) {
				this.actionClickHandler.onClick(event);
			}
		}

	}

	protected void defineColSize() {
		int i = 0;
		for (int size : this.getColSizes()) {
			this.grid.setColumnWidth(i++, size);
		}
	}

	protected void initColumns() {
		int i = 0;
		for (int size : this.getColSizes()) {
			this.grid.setReadOnly(i++, true);
			this.grid.setSortable(i, false);
		}
	}

	// grid row >> model row >> id (premiere valeur des datas)
	protected int getId(int gridRow) {

		int modelRow = grid.getModelRow(gridRow);
		getLog().config("grid row: " + gridRow + " - model row: " + modelRow);
		Object[] datas = this.model.getRowData(modelRow);

		if (datas != null && datas.length > 0) {
			Object obj = datas[0];
			if (obj != null) {
				return ValueHelper.getIntValue(obj.toString(), IDto.ID_UNDEFINED);
			}
		}
		return IDto.ID_UNDEFINED;
	}

	// -------------------------------------- private methods
	private void initHandlers() {

		this.gridPanel.addPagerListener(new PagerListener() {

			@Override
			public void onPageChange(Pager sender, int page) {
				getLog().config("Current page: " + page);

			}
		});
	}

	// +============================================= INNER CLASSES
	protected static class IdCell extends LabelCell {

		@Override
		public void setValue(Object value) {

			super.setValue(value);
			this.setVisible(false);

		}
	}

	protected static abstract class AbstractStatusCell extends AbstractCell {

		private Label widget = new Label();

		protected abstract Label buildWidget();

		@Override
		public void setFocus(boolean focus) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object getNewValue() {
			return this.widget;
		}

		@Override
		protected Widget createActive() {
			return createInactive();
		}

		@Override
		protected Widget createInactive() {
			this.widget.addStyleName("labelStatus");
			return this.widget;
		}

		@Override
		public void setValue(Object value) {

			super.setValue(value);
			this.widget = this.buildWidget();

		}

	}

	protected class ButtonForAbstractButtonCell extends Button {

		protected final AbstractButtonCell cell;

		protected ButtonForAbstractButtonCell(AbstractButtonCell cell) {
			this.cell = cell;
		}
	}

	protected abstract class AbstractButtonCell extends AbstractCell {

		private final ButtonForAbstractButtonCell widget;
		protected int rowIndex;
		protected String title;

		protected abstract ClickHandler buildClickHandler(final int rowIndex);

		// on ne doit pas pouvoir unlocker un bouton
		// inactif
		protected void setLocked(boolean locked) {

			if (!this.isButtonEnabled()) {
				return;
			}

			this.widget.setEnabled(!locked);
			if (locked) {
				this.widget.addStyleName(STYLE_CURSOR_WAIT);
			} else {
				this.widget.removeStyleName(STYLE_CURSOR_WAIT);
			}

		}

		private boolean isButtonEnabled() {
			return (Boolean) super.getValue();
		}

		protected void addButtonStyleName(String stylename) {
			this.widget.addStyleName(STYLE_IMG_ACTION + " " + stylename);
		}

		protected void removeButtonStyleName(String stylename) {
			this.widget.removeStyleName(STYLE_IMG_ACTION + " " + stylename);
		}

		protected void enableButton(boolean enabled) {
			this.widget.setEnabled(enabled);
			this.widget.setVisible(enabled);
		}

		protected AbstractButtonCell(String title, final int rowIndex, String stylename) {
			this.removeStyleName("active-cell");
			this.removeStyleName("passive-cell");

			this.rowIndex = rowIndex;
			this.title = title;
			this.widget = new ButtonForAbstractButtonCell(this);
			this.widget.setTitle(title);
			this.addButtonStyleName(stylename);
			this.widget.addClickHandler(this.buildClickHandler(rowIndex));
		}

		@Override
		public void setFocus(boolean focus) {
		}

		@Override
		public Object getNewValue() {
			return true;
		}

		@Override
		protected Widget createActive() {
			return this.widget;
		}

		@Override
		protected Widget createInactive() {
			return this.widget;
		}

		public void setActionProtection(DtoActionProtection actionProtection) {
			super.setValue(actionProtection.isActive());
			this.widget.setEnabled(actionProtection.isActive());

			// action non possible
			if (!actionProtection.isActive()) {

				// cas où la règle est fonctionnelle
				if (actionProtection.getRules() == Rules.functionnal) {
					this.widget.setVisible(true);
					this.widget.setTitle(actionProtection.getMessage());
				} else {
					// cas où la règle vient du profile utilisateur
					this.widget.setVisible(false);
				}
			} else {
				// action possible
				this.widget.setTitle(this.title);
			}

		}

		// FIXME A REMPLACER PAR setActionProtection
		@Override
		public void setValue(Object value) {

			Boolean enabled = (Boolean) value;
			super.setValue(enabled);
			this.enableButton(enabled);
		}

	}

	protected abstract class AbstractDeleteCell extends AbstractButtonCell {

		public AbstractDeleteCell(final int rowIndex, String title) {

			super(title, rowIndex, STYLE_IMG_DELETE);
		}

		@Override
		protected void enableButton(boolean enabled) {
			// a supprimer
		}

		@Override
		public void setValue(Object value) {

			DtoActionProtection actionProtection = (DtoActionProtection) value;
			super.setActionProtection(actionProtection);

			if (actionProtection.isActive()) {
				super.removeButtonStyleName(STYLE_IMG_DELETEOFF);
				super.addButtonStyleName(STYLE_IMG_DELETE);
			} else {
				super.removeButtonStyleName(STYLE_IMG_DELETE);
				super.addButtonStyleName(STYLE_IMG_DELETEOFF);

			}
		}

	}

	protected abstract class AbstractEditCell extends AbstractButtonCell {

		public AbstractEditCell(final int rowIndex, String title) {

			super(title, rowIndex, STYLE_IMG_EDIT);
		}

		@Override
		protected void enableButton(boolean enabled) {
			// a supprimer
		}

		@Override
		public void setValue(Object value) {

			DtoActionProtection actionProtection = (DtoActionProtection) value;
			super.setActionProtection(actionProtection);

			if (actionProtection.isActive()) {
				super.removeButtonStyleName(STYLE_IMG_EDITOFF);
				super.addButtonStyleName(STYLE_IMG_EDIT);
			} else {
				super.removeButtonStyleName(STYLE_IMG_EDIT);
				super.addButtonStyleName(STYLE_IMG_EDITOFF);

			}

		}

	}

	protected static abstract class AbstractEnabledCell extends AbstractCell {

		private Panel widget = new SimplePanel();
		private boolean enabled;

		public AbstractEnabledCell(final int rowIndex) {
			this.removeStyleName("active-cell");
			this.removeStyleName("passive-cell");

		}

		@Override
		public void setFocus(boolean focus) {
		}

		@Override
		public Object getNewValue() {
			return Boolean.valueOf(this.enabled);
		}

		@Override
		protected Widget createActive() {
			return createInactive();
		}

		@Override
		protected Widget createInactive() {
			return this.widget;
		}

		@Override
		public void setValue(Object value) {
			this.enabled = (Boolean) value;

			String styleName = this.getStyleName(this.enabled);

			this.widget.addStyleName(styleName);
			super.setValue(value);
		}

		protected abstract String getStyleName(boolean enabled);

	}

	protected static class StyleLabelCell extends AbstractCell {

		private SimplePanel panel = new SimplePanel();
		private Element widget = panel.getElement();

		@Override
		public void setFocus(boolean focus) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object getNewValue() {
			return this.panel;
		}

		@Override
		protected Widget createActive() {
			return createInactive();
		}

		@Override
		protected Widget createInactive() {
			return this.panel;
		}

		@Override
		public void setValue(Object value) {
			super.setValue(value);
			// this.widget.set
			this.widget.setInnerHTML(value == null ? "" : value.toString());

			if (value != null && value instanceof TextAndStyle) {
				TextAndStyle textAndStyle = (TextAndStyle) value;
				if (textAndStyle.style != null) {
					this.widget.addClassName(textAndStyle.style);
				}
				if (textAndStyle.comment != null) {
					this.panel.setTitle(textAndStyle.comment);
				}
			}
		}
	}

	/*
	 * Ajoute un style à un texte en fonction de son status (enabled/disabled)
	 */
	protected static class ColorStateLabelCell extends AbstractCell {

		private final String styleEnabled;
		private final String styleDisabled;

		private Label widget = new Label();

		protected ColorStateLabelCell(String styleEnabled, String styleDisabled) {
			this.styleEnabled = styleEnabled;
			this.styleDisabled = styleDisabled;
		}

		protected String getStyleName(boolean enabled) {
			return (enabled) ? styleEnabled : styleDisabled;
		}

		@Override
		public void setValue(Object value) {

			this.widget.removeStyleName(styleDisabled);
			this.widget.removeStyleName(styleEnabled);
			if (value instanceof TextAndState) {
				TextAndState textAndState = (TextAndState) value;
				this.widget.addStyleName(this.getStyleName(textAndState.enabled));
				this.widget.setTitle(textAndState.comment);
			}
			this.widget.setText(value.toString());
		}

		/** {@inheritDoc} */
		protected Widget createActive() {
			return this.widget;
		}

		/** {@inheritDoc} */
		protected Widget createInactive() {
			return this.widget;
		}

		/** {@inheritDoc} */
		public void setFocus(boolean focus) {
		}

		/** {@inheritDoc} */
		public Object getNewValue() {
			this.widget.setText(null);
			return this.widget.getText();
		}
	}

	protected static class TextAndState {

		private final String text;
		private final boolean enabled;
		private final String comment;

		protected TextAndState(String text, boolean enabled, String comment) {
			this.text = text;
			this.enabled = enabled;
			this.comment = comment == null ? "" : comment;
		}

		@Override
		public String toString() {
			return this.text;
		}
	}

	protected static class TextAndStyle {

		private final String text;
		private final String style;
		private final String comment;

		protected TextAndStyle(String text, String style, String comment) {
			this.text = text;
			this.style = style;
			this.comment = comment == null ? "" : comment;
		}

		@Override
		public String toString() {
			return this.text;
		}

	}

	/**
	 * Affichage image enabled/disabled
	 * 
	 * @author ndmz2720
	 * 
	 */
	protected static class ActiveCell extends AbstractEnabledCell {

		public ActiveCell(final int rowIndex) {
			super(rowIndex);
		}

		@Override
		protected String getStyleName(boolean enabled) {
			return STYLE_IMG_ACTION + " " + ((enabled) ? STYLE_IMG_ENABLED : STYLE_IMG_DISABLED);
		}

	}

	// ============================================== INNER CLASS
	protected static class MyGridPanel extends GridPanel {
		private final static Logger log = Logger.getLogger("MyGridPanel");

		public MyEditableGrid createMyEditableGrid(String[] headers, Class[] columnWidgetClasses,
				EditableGridDataModel model) {

			MyEditableGrid grid = new MyEditableGrid(headers, columnWidgetClasses);
			grid.setGridPanel(this);
			grid.setModel(model);

			return grid;
		}

	}

	protected static class MyEditableGrid extends EditableGrid<EditableGridDataModel> {

		private final static Logger log = Logger.getLogger("MyEditableGrid");

		public MyEditableGrid(String[] headers, Class[] columnWidgetClasses) {
			super(headers, columnWidgetClasses, true);
		}

		@Override
		public void setModel(EditableGridDataModel model) {
			super.setModel(model);
		}

		@Override
		protected void setGridPanel(GridPanel gridPanel) {
			super.setGridPanel(gridPanel);
		}

		@Override
		protected void setLocked(boolean locked) {
			log.config("setLocked(" + locked + ")");
			// propager à toutes les cellules

			// for each row
			for (int row = 0; row < this.getRowCount(); row++) {

				// for each col
				for (int col = 0; col < this.getHeaders().length; col++) {

					Widget widget = this.getWidget(row, col);
					if (widget != null && widget instanceof AbstractButtonCell) {
						((AbstractButtonCell) widget).setLocked(locked);
					}
				}

			}
		}

	}

	protected abstract class AbstractMyGridCellFactory extends DefaultGridCellFactory {

		public AbstractMyGridCellFactory(EditableGrid<EditableGridDataModel> grid) {
			super(grid);
		}

		protected ActiveCell createActiveCell(boolean enabled, int row) {
			ActiveCell cell = new ActiveCell(row);
			cell.setValue(enabled);
			return cell;
		}

	}

}
