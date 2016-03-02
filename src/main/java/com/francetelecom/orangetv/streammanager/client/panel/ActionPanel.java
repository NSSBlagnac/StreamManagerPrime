package com.francetelecom.orangetv.streammanager.client.panel;

import com.francetelecom.orangetv.streammanager.client.controller.AppController.Action;
import com.francetelecom.orangetv.streammanager.client.controller.AppController.ActionClickEvent;
import com.francetelecom.orangetv.streammanager.client.controller.AppController.ButtonActionStates;
import com.francetelecom.orangetv.streammanager.client.controller.AppController.StreamDescription;
import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.shared.dto.UserProfile;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ActionPanel extends AbstractPanel implements CssConstants {

	private final ActionPanelButton btUpdateEit = new ActionPanelButton("Update EIT", Action.sendEit);
	private final ActionPanelButton btCreateStream = new ActionPanelButton("Create stream", Action.createStream);
	private final ActionPanelButton btCreateVideo = new ActionPanelButton("Create video", Action.createVideo);
	private final ActionPanelButton btConnection = new ActionPanelButton("Connection", Action.connectUser);

	private final Label labelProfil = new Label(UserProfile.anybody.name());
	private final Label labelTarget = new Label("Target: ");
	private final Label labelEitToInject = new Label("");

	// ------------------------------------------ constructor
	public ActionPanel() {
		this.initComposants();
		this.initWidget(this.buildMainPanel());
	}

	private void initComposants() {
		this.enableButton(new ButtonActionStates());
		this.btCreateStream.setWidth("200px");
		this.btCreateVideo.setWidth("200px");
		this.labelProfil.getElement().setId(STYLE_ID_PROFIL_USER);
	}

	// ----------------------------------------------- public methods
	public void setUserProfil(UserProfile userProfile) {
		this.labelProfil.setText(userProfile.name());
		this.btConnection.setText((userProfile != UserProfile.anybody) ? "Deconnection" : "Connection");
	}

	public void setTarget(StreamDescription targetDescription) {
		this.labelTarget.setText(targetDescription.getDescription());
		this.updateLabelEitInjection(targetDescription.isEitToInject());
	}

	public void bindHandlers(final ClickHandler actionClickHandler) {

		this.btUpdateEit.setActionHandler(actionClickHandler);
		this.btCreateStream.setActionHandler(actionClickHandler);
		this.btCreateVideo.setActionHandler(actionClickHandler);
		this.btConnection.setActionHandler(actionClickHandler);

	}

	public void enableButton(ButtonActionStates buttonActionStates) {
		this.btCreateStream.setEnabled(buttonActionStates.isEnableCreateStream());
		this.btCreateVideo.setEnabled(buttonActionStates.isEnableCreateVideo());
		this.btUpdateEit.setEnabled(buttonActionStates.isEnableSendEit());
	}

	// ----------------------------------------------- private methods

	private Widget buildMainPanel() {

		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(PANEL_SEND_ACTION);
		panel.setSpacing(PANEL_SPACING);
		panel.setWidth(MAX_WIDTH);

		HorizontalPanel panelTop = new HorizontalPanel();
		panelTop.setSpacing(PANEL_SPACING);
		panelTop.setWidth(MAX_WIDTH);

		HorizontalPanel panelCreate = new HorizontalPanel();
		panelCreate.setSpacing(PANEL_SPACING);
		panelCreate.add(this.btCreateStream);
		panelCreate.add(this.btCreateVideo);
		panelTop.add(panelCreate);

		HorizontalPanel panelTarget = new HorizontalPanel();
		this.labelTarget.getElement().setId(STYLE_ID_TARGET);
		panelTarget.setSpacing(PANEL_SPACING);
		this.btUpdateEit.setTitle("Send your eit to choosen target...");

		panelTarget.add(this.btUpdateEit);
		panelTarget.add(this.labelTarget);
		panelTarget.setCellHorizontalAlignment(this.labelTarget, HasHorizontalAlignment.ALIGN_LEFT);
		panelTarget.add(this.labelEitToInject);
		panelTarget.setCellHorizontalAlignment(this.labelEitToInject, HasHorizontalAlignment.ALIGN_LEFT);
		panelTop.add(panelTarget);

		HorizontalPanel panelConnection = new HorizontalPanel();
		panelConnection.setSpacing(PANEL_SPACING);
		panelConnection.add(this.btConnection);
		panelConnection.add(this.labelProfil);

		panelTop.add(panelConnection);
		panelTop.setCellHorizontalAlignment(panelConnection, HasHorizontalAlignment.ALIGN_RIGHT);

		panel.add(panelTop);

		return panel;
	}

	private void updateLabelEitInjection(boolean eitToInject) {
		this.labelEitToInject.setText(eitToInject ? "Injection Eit active" : "Pas injection Eit");

		this.labelEitToInject.removeStyleName(STYLE_LABEL_EIT_INJECT);
		this.labelEitToInject.removeStyleName(STYLE_EIT_INJECTION);
		this.labelEitToInject.removeStyleName(STYLE_EIT_NO_INJECTION);

		String styleName = STYLE_LABEL_EIT_INJECT + " ";
		styleName += (eitToInject) ? STYLE_EIT_INJECTION : STYLE_EIT_NO_INJECTION;

		this.labelEitToInject.setStyleName(styleName);
	}

	// ====================================== INNER CLASS
	private final class ActionPanelButton extends AppButton {

		private final Action action;

		private ActionPanelButton(String text, Action action) {
			super(text);
			this.action = action;
		}

		private void setActionHandler(final ClickHandler actionClickHandler) {
			super.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					actionClickHandler.onClick(new ActionClickEvent(action));
				}
			});
		}

	}
}
