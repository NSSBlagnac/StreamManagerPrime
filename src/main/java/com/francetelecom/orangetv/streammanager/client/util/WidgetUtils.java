package com.francetelecom.orangetv.streammanager.client.util;

import com.francetelecom.orangetv.streammanager.client.service.IActionCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WidgetUtils {

	private final static String STYLE = "style";
	public final static String TOP = "top";
	public final static String LEFT = "left";

	// focus sur bouton OK, close auto si OK
	public final static MyDialogBox buildDialogBoxWithOkFocused(final String title, final String[] messages,
			final Widget widget, final boolean withCancel, final IActionCallback actionCallback) {
		return buildDialogBox(title, messages, widget, withCancel, true, true, actionCallback);
	}

	public final static MyDialogBox buildDialogBox(final String title, final String[] messages, final Widget widget,
			final boolean withCancel, final boolean hideAfterOK, boolean focusButtonOk,
			final IActionCallback actionCallback) {

		return new MyDialogBox(title, messages, widget, withCancel, hideAfterOK, focusButtonOk, actionCallback);

	}

	public static void centerDialogAndShow(final DialogBox dialogBox) {

		dialogBox.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				dialogBox.setPopupPosition(left, top);
			}
		});

		dialogBox.show();
	}

	public static void addStyleAttributeWithPrefix(final Element element, final String attributePrefix,
			final String attributeValue) {

		if (attributeValue == null || attributeValue.trim().length() == 0) {
			_removeStyleAttributFromPrefix(element, attributePrefix);
			return;
		}

		final String styleAttributes = element.getAttribute(STYLE);

		if (styleAttributes == null || styleAttributes.length() == 0) {
			element.setAttribute(STYLE, attributePrefix + ": " + attributeValue + "; ");
			return;
		}

		boolean findAttribute = false;
		final StringBuilder sb = new StringBuilder();
		final String[] tokens = styleAttributes.split(";");
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i].trim();

			if (token.equals(";") || token.equals("")) {
				continue;
			}
			if (token.startsWith(attributePrefix)) {
				// replace
				findAttribute = true;
				sb.append(attributePrefix + ": " + attributeValue + "; ");
			} else {
				sb.append(token + "; ");
			}
		}

		// new attribute
		if (!findAttribute) {
			sb.append(attributePrefix + ": " + attributeValue + "; ");
		}

		element.setAttribute(STYLE, sb.toString());

	}

	private static void _removeStyleAttributFromPrefix(final Element element, String attributePrefix) {

		final String styleAttributes = element.getAttribute(STYLE);
		if (styleAttributes == null || styleAttributes.length() == 0) {
			return;
		}
		final StringBuilder sb = new StringBuilder();
		final String[] tokens = styleAttributes.split(";");
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i].trim();
			if (token.equals(";") || token.equals("")) {
				continue;
			}
			if (!token.startsWith(attributePrefix)) {
				sb.append(token + "; ");
			}
		}
		element.setAttribute("style", sb.toString());
	}

	public static Panel buildLabelAndWidgetPanel(String labelText, Widget widget, int labelWith) {

		Label label = new Label(labelText);
		label.setWidth(labelWith + "px");

		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(CssConstants.PANEL_SPACING);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setStyleName(CssConstants.PANEL_INPUT);

		panel.add(label);
		if (widget != null) {
			panel.add(widget);
		}

		return panel;
	}

	// ============================== INNER CLASS
	public static interface IMyDialogBox {

		public void setReadOnly(boolean readOnly);

		public void lock();

		public void unlock();

	}

	public static class MyDialogBox extends DialogBox implements IMyDialogBox {

		private final Button closeButton;
		private Button cancelButton;
		private IActionCallback actionCallback;
		protected ChangeHandler changeHandler;

		private final boolean focusButtonOk;

		// -------------------------- accessor
		public void setActionCallback(IActionCallback actionCallback) {
			this.actionCallback = actionCallback;
		}

		// -------------------------------------------- overriding Widget
		@Override
		protected void onLoad() {
			super.onLoad();

			if (this.focusButtonOk) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						closeButton.setFocus(true);
					}
				});
			}

		}

		// -------------------------------------------- constructor
		public MyDialogBox(final String title, final String[] messages, final Widget widget, final boolean withCancel,
				final boolean hideAfterOK) {
			this(title, messages, widget, withCancel, hideAfterOK, true, null);
		}

		public MyDialogBox(final String title, final String[] messages, final Widget widget, final boolean withCancel,
				final boolean hideAfterOK, boolean focusButtonOk, final IActionCallback actionCallback) {

			this.focusButtonOk = focusButtonOk;
			this.actionCallback = actionCallback;
			this.setText(title);
			this.setAnimationEnabled(true);

			final VerticalPanel vPanelInfo = new VerticalPanel();
			vPanelInfo.setSpacing(CssConstants.PANEL_SPACING);

			if (messages != null) {
				for (int i = 0; i < messages.length; i++) {
					vPanelInfo.add(new Label(messages[i]));
				}
			}

			if (widget != null) {
				vPanelInfo.add(widget);
			}

			final HorizontalPanel panelButton = new HorizontalPanel();
			panelButton.setSpacing(CssConstants.PANEL_SPACING);
			panelButton.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

			this.closeButton = new Button("ok");
			panelButton.add(closeButton);

			if (withCancel) {
				this.cancelButton = new Button("annuler");
				panelButton.add(cancelButton);
			}

			vPanelInfo.add(panelButton);
			vPanelInfo.setCellHorizontalAlignment(panelButton, HasHorizontalAlignment.ALIGN_RIGHT);

			this.initHandlers(hideAfterOK);

			this.setWidget(vPanelInfo);

		}

		private void initHandlers(final boolean hideAfterOK) {

			this.closeButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (hideAfterOK) {
						MyDialogBox.this.hide();
					}
					if (actionCallback != null) {
						actionCallback.onOk();
					}
				}
			});

			if (this.cancelButton != null) {
				this.cancelButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						MyDialogBox.this.hide();
						if (actionCallback != null) {
							actionCallback.onCancel();
						}

					}
				});

			}
		}

		// ------------------------------ overriding IMyDialogBox
		@Override
		public void setReadOnly(boolean readOnly) {
			this.closeButton.setEnabled(!readOnly);
		}

		@Override
		public void lock() {
			this.closeButton.setEnabled(false);
			if (this.cancelButton != null) {
				this.cancelButton.setEnabled(false);
			}
		}

		@Override
		public void unlock() {
			this.closeButton.setEnabled(true);
			if (this.cancelButton != null) {
				this.cancelButton.setEnabled(true);
			}
		}

	}

}
