package com.francetelecom.orangetv.streammanager.client.panel;

import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.controller.AppController;
import com.francetelecom.orangetv.streammanager.client.panel.admin.AdminPanel;
import com.francetelecom.orangetv.streammanager.client.panel.eit.EitJsonPanel;
import com.francetelecom.orangetv.streammanager.client.panel.eit.EitPanel;
import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;

public class MainTabPanel extends Composite implements CssConstants {

	private final static Logger log = Logger.getLogger("MainTabPanel");

	private final static int INDEX_TAB_STREAM = 0;
	private final static int INDEX_TAB_EIT = 1;
	private final static int INDEX_TAB_VIDEO = 2;
	private final static int INDEX_TAB_ADMIN = 3;

	private final SimplePanel main = new SimplePanel();
	private final TabPanel tabPanel = new TabPanel();

	private final StreamPanel streamPanel = new StreamPanel();
	private final VideoPanel videoPanel = new VideoPanel();
	private final EitPanel eitPanel;
	private final AdminPanel adminPanel = new AdminPanel();

	// ------------------------------------------ constructor
	public MainTabPanel(EitJsonPanel jsonPanel) {
		this.eitPanel = new EitPanel(jsonPanel);
		this.buildMainPanel();
		this.initComposants();
		this.initWidget(this.main);
	}

	// --------------------------------------------- public methods
	public EitPanel getEitPanel() {
		return this.eitPanel;
	}

	/**
	 * @see AppController.TAB_MAIN_XXX
	 * @return
	 */
	public int getSelectedTab() {
		int tabIndex = this.tabPanel.getTabBar().getSelectedTab();
		switch (tabIndex) {
		case INDEX_TAB_STREAM:
			return AppController.TAB_MAIN_STREAM;
		case INDEX_TAB_EIT:
			return AppController.TAB_MAIN_EIT;
		case INDEX_TAB_VIDEO:
			return AppController.TAB_MAIN_VIDEO;
		case INDEX_TAB_ADMIN:
			return AppController.TAB_MAIN_ADMIN;
		}
		return -1;
	}

	public StreamPanel getStreamPanel() {
		return this.streamPanel;
	}

	public AdminPanel getAdminPanel() {
		return this.adminPanel;
	}

	public VideoPanel getVideoPanel() {
		return this.videoPanel;
	}

	public void setAdminPanelVisible(boolean visible) {
		if (visible) {
			log.config("tab panel count widget: " + this.tabPanel.getWidgetCount());
			if (this.tabPanel.getWidgetCount() == 3) {
				this.tabPanel.add(this.adminPanel, new LabelTitle("admin"));
			}

		} else {
			this.tabPanel.remove(this.adminPanel);
		}
	}

	public void displayStreamPanel() {
		this.tabPanel.selectTab(INDEX_TAB_STREAM);
	}

	public void displayAdminPanel() {
		this.tabPanel.selectTab(INDEX_TAB_ADMIN);
	}

	public void displayVideoPanel() {
		this.tabPanel.selectTab(INDEX_TAB_VIDEO);
	}

	public void bindHandlers(SelectionHandler<Integer> selectionHandler, ClickHandler actionClickHandler) {
		this.tabPanel.addSelectionHandler(selectionHandler);
		this.streamPanel.bindHandlers(actionClickHandler);
		this.videoPanel.bindHandlers(actionClickHandler);
		this.adminPanel.bindHandlers(actionClickHandler);
	}

	public void displayEitSectionPanel() {
		this.tabPanel.selectTab(INDEX_TAB_EIT);
	}

	// --------------------------------------------- private methods

	private void initComposants() {
		this.adminPanel.setVisible(false);
	}

	private void buildMainPanel() {

		this.main.setWidth(MAX_WIDTH);
		this.tabPanel.setWidth(MAX_WIDTH);
		this.tabPanel.add(this.streamPanel, new LabelTitle("Table stream"));
		this.tabPanel.add(this.eitPanel, new LabelTitle("Eit"));
		this.tabPanel.add(this.videoPanel, new LabelTitle("videos"));
		// this.tabPanel.add(this.adminPanel, new LabelTitle("admin"));
		this.tabPanel.getTabBar().selectTab(INDEX_TAB_STREAM);

		this.main.setWidget(this.tabPanel);
	}

	// ================================= INNER CLASS
	public static class LabelTitle extends Label {

		public LabelTitle(String label) {
			this(label, PANEL_DETAIL_TITLE);
		}

		public LabelTitle(String label, String stylename) {
			super(label);
			this.setStyleName(stylename);
		}
	}

}
