package com.francetelecom.orangetv.streammanager.client.panel.eit;

import com.francetelecom.orangetv.streammanager.client.panel.AbstractPanel;
import com.francetelecom.orangetv.streammanager.client.panel.MainTabPanel.LabelTitle;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel;
import com.francetelecom.orangetv.streammanager.shared.model.EitInfoModel.EitSection;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EitPanel extends AbstractPanel {

	private final static int INDEX_TAB_SECTION_PRESENT = 0;
	private final static int INDEX_TAB_SECTION_FOLLOWING = 1;
	private final static int INDEX_TAB_JSON = 2;

	private final SimplePanel main = new SimplePanel();
	private final TabPanel tabPanel = new TabPanel();

	private final EitAllEventPanel allEventPanel = new EitAllEventPanel();

	public EitPanel(EitJsonPanel jsonPanel) {
		this.buildMainPanel(jsonPanel);
		this.initComposants();
		this.initWidget(this.main);
	}

	// ------------------------------------- public methods

	// /**
	// * @see AppController.TAB_EIT_XXX
	// * @return
	// */
	// public int getSelectedTab() {
	// int tabIndex = this.tabPanel.getTabBar().getSelectedTab();
	// switch (tabIndex) {
	// case INDEX_TAB_SECTION_PRESENT:
	// return AppController.TAB_EIT_PRESENT;
	// case INDEX_TAB_SECTION_FOLLOWING:
	// return AppController.TAB_EIT_FOLLOWING;
	// case INDEX_TAB_JSON:
	// return AppController.TAB_EIT_JSON;
	// }
	// return -1;
	// }

	/**
	 * Récupère la saisie de l'utilisateur dans un objet EitInfoModel
	 * 
	 * @return
	 */
	public EitInfoModel getDataFromWidget(String currentTarget) {

		EitInfoModel eitInfoModel = new EitInfoModel();

		EitInfoModel.EitGeneral eitGeneral = new EitInfoModel.EitGeneral(EitInfoModel.VERSION,
				(currentTarget == null) ? "" : currentTarget);
		eitInfoModel.setEitGeneral(eitGeneral);

		EitSection presentSection = new EitSection();
		EitSection followingSection = new EitSection();

		EitEvent[] eitEvents = this.allEventPanel.getDataFromWidget();
		if (eitEvents == null || eitEvents.length != 2) {
			return null;
		}
		presentSection.addEitEvent(eitEvents[0]);
		followingSection.addEitEvent(eitEvents[1]);

		eitInfoModel.setPresentSection(presentSection);
		eitInfoModel.setFollowingSection(followingSection);

		return eitInfoModel;
	}

	public void populateWidgetFromData(EitInfoModel eitInfo) {

		if (eitInfo == null || eitInfo.getPresentSection() == null || eitInfo.getFollowingSection() == null
				|| eitInfo.getPresentSection().getListEvents().isEmpty()
				|| eitInfo.getFollowingSection().getListEvents().isEmpty()) {
			return;
		}
		EitEvent[] eitEvents = new EitEvent[2];
		eitEvents[0] = eitInfo.getPresentSection().getListEvents().get(0);
		eitEvents[1] = eitInfo.getFollowingSection().getListEvents().get(0);
		this.allEventPanel.populateWidgetFromData(eitEvents);

	}

	// --------------------------------------------- private methods
	private void initComposants() {
	}

	private void buildMainPanel(EitJsonPanel jsonPanel) {

		this.main.setWidth(MAX_WIDTH);

		final VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(PANEL_SPACING);
		vPanel.setWidth(MAX_WIDTH);

		this.tabPanel.setWidth(MAX_WIDTH);
		this.tabPanel.add(this.allEventPanel, new LabelTitle("Eit present & following", PANEL_EIT_TITLE));
		// this.tabPanel.add(this.presentSectionPanel, new
		// LabelTitle("Present section", PANEL_EIT_TITLE));
		// this.tabPanel.add(this.followingSectionPanel, new
		// LabelTitle("Following section", PANEL_EIT_TITLE));
		this.tabPanel.add(jsonPanel, new LabelTitle("json", PANEL_EIT_TITLE));
		this.tabPanel.getTabBar().selectTab(0);
		vPanel.add(this.tabPanel);
		vPanel.add(this.labelResult);

		this.main.setWidget(vPanel);
	}

	public void unLock() {
		// TODO Auto-generated method stub

	}

	public void lock() {
		// TODO Auto-generatesd method stub

	}
}
