package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.shared.util.EitException;
import com.francetelecom.orangetv.streammanager.shared.util.ValueHelper;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LabelAndBoxWidget extends Composite implements CssConstants {

	private final VerticalPanel main = new VerticalPanel();

	private final Label label = new Label();
	private final TextBox textBox;
	private final PasswordTextBox pwdTextBox;
	private final Label errorMessage = new Label();

	private IValidator<String> validator;

	// -------------------------------------- accessors
	public Label getLabel() {
		return label;
	}

	public TextBox getTextBox() {
		return (this.textBox != null) ? this.textBox : this.pwdTextBox;
	}

	public void setMaxLength(int length) {
		this.getTextBox().setMaxLength(length);
	}

	public void setValidator(final IValidator<String> validator) {

		this.validator = validator;
		if (validator != null) {
			this.getTextBox().addKeyUpHandler(new KeyUpHandler() {

				@Override
				public void onKeyUp(KeyUpEvent event) {
					validate();
				}
			});

		}
	}

	private void validate() {
		String message = "";
		this.getTextBox().removeStyleName(STYLE_BOX_ERROR);
		try {
			validator.validate(this.getTextBox().getText());
		} catch (EitException e) {
			message = e.getErrorMessage();
			this.getTextBox().setStyleName(STYLE_BOX_ERROR);
		}
		errorMessage.setText(message);

	}

	// ------------------------------------------ public methods

	public void setPlaceHolderAndTitle(String placeHolderAndTitle) {

		if (placeHolderAndTitle != null) {
			this.getTextBox().getElement().setPropertyString("placeholder", placeHolderAndTitle);
			this.getTextBox().setTitle(placeHolderAndTitle);
		}
	}

	public String getBoxUserInput() {

		String input = this.getTextBox().getText();
		return (input == null) ? "" : (validator != null) ? validator.getCorrectedValue(input) : input;
	}

	public void setValue(String value) {
		this.getTextBox().setText(value);
	}

	public void clear() {
		this.getTextBox().setText("");
	}

	public boolean isUserInputEmpty() {
		return ValueHelper.isStringEmptyOrNull(this.getTextBox().getText());
	}

	public void setEnabled(boolean enabled) {
		this.getTextBox().setEnabled(enabled);
	}

	public void setFocus(boolean focused) {
		this.getTextBox().setFocus(focused);
	}

	public void addChangeHandler(ChangeHandler handler) {
		this.getTextBox().addChangeHandler(handler);
	}

	// --------------------------------------------- constructor
	public LabelAndBoxWidget(final String labelText, final int labelWidth, final int boxWidth) {
		this(labelText, labelWidth, boxWidth, false);
	}

	public LabelAndBoxWidget(final String labelText, final int labelWidth, final int boxWidth, boolean passwordMode) {

		this.textBox = (passwordMode) ? null : new TextBox();
		this.pwdTextBox = (passwordMode) ? new PasswordTextBox() : null;
		this.initComposants(labelText, labelWidth, boxWidth);
		this.initWidget(this.buildMainPanel());
	}

	// ----------------------------------------------- private methods

	private void initComposants(final String labelText, final int labelWidth, final int boxWidth) {
		this.label.setText(labelText);
		this.label.setWidth(labelWidth + "px");
		this.getTextBox().setWidth(boxWidth + "px");
		this.errorMessage.setStyleName(TEXT_ERROR);

	}

	private Panel buildMainPanel() {

		final HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(PANEL_SPACING);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setStyleName(CssConstants.PANEL_INPUT);
		panel.add(label);
		panel.add(this.getTextBox());

		this.main.setSpacing(PANEL_SPACING);
		this.main.add(panel);
		this.main.add(this.errorMessage);

		return this.main;
	}

}
