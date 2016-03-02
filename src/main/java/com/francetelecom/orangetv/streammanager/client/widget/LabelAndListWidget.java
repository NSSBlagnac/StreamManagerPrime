package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class LabelAndListWidget extends Composite implements CssConstants {

	private final Label label;;
	private final ListBox listBox;

	// -------------------------------------- accessors
	public Label getLabel() {
		return label;
	}

	public ListBox getListBox() {
		return listBox;
	}

	// ------------------------------------------ public methods
	public String getListUserInput() {

		int index = this.listBox.getSelectedIndex();
		String input = (index >= 0) ? this.listBox.getValue(index) : null;
		return (input == null) ? "" : input;
	}

	/**
	 * Set the code/value of a list item
	 * 
	 * @param value
	 */
	public void setValue(String value) {

		boolean success = false;
		if (value != null) {
			for (int i = 0; i < this.listBox.getItemCount(); i++) {

				if (this.listBox.getValue(i).equals(value)) {
					this.listBox.setSelectedIndex(i);
					success = true;
					break;
				}

			}
		}
		// on a rien trouvé
		if (!success) {
			this.listBox.setSelectedIndex(0);
		}
	}

	// --------------------------------------------- constructor
	public LabelAndListWidget(final String labelText, final int labelWidth, final int listWidth, final String[] items) {
		this(labelText, labelWidth, listWidth, items, 1);
	}

	public LabelAndListWidget(final String labelText, final int labelWidth, final int listWidth, final ListBox listBox,
			String defaultCode) {
		this(labelText, labelWidth, listWidth, listBox, 1);
		this.setValue(defaultCode);
	}

	public LabelAndListWidget(final String labelText, final int labelWidth, final int listWidth, final ListBox listBox,
			int visibleItemCount) {
		this.label = new Label(labelText);
		this.label.setWidth(labelWidth + "px");
		this.listBox = listBox;
		this.listBox.setWidth(listWidth + "px");
		this.listBox.setVisibleItemCount(visibleItemCount);

		final HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(PANEL_SPACING);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setStyleName(CssConstants.PANEL_INPUT);
		panel.add(label);
		panel.add(this.listBox);

		this.initWidget(panel);
	}

	public LabelAndListWidget(final String labelText, final int labelWidth, final int listWidth, final String[] items,
			int visibleItemCount) {
		this(labelText, labelWidth, listWidth, new ListBox(), visibleItemCount);
		this.setItems(items);

	}

	public void addChangeHandler(ChangeHandler handler) {
		this.listBox.addChangeHandler(handler);
	}

	private void setItems(final String[] items) {

		for (String item : items) {
			this.listBox.addItem(item);
		}
	}
}
