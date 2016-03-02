package com.francetelecom.orangetv.streammanager.client.panel.eit;

import java.util.ArrayList;
import java.util.List;

import org.gwt.advanced.client.ui.widget.AdvancedTabPanel;
import org.gwt.advanced.client.ui.widget.tab.TabPosition;

import com.francetelecom.orangetv.streammanager.client.panel.MainTabPanel.LabelTitle;
import com.francetelecom.orangetv.streammanager.client.panel.descriptor.CAIdentifierDescriptorPanel;
import com.francetelecom.orangetv.streammanager.client.panel.descriptor.ComponentDescriptorsPanel;
import com.francetelecom.orangetv.streammanager.client.panel.descriptor.ContentDescriptorPanel;
import com.francetelecom.orangetv.streammanager.client.panel.descriptor.ExtendedEventDescriptorPanel;
import com.francetelecom.orangetv.streammanager.client.panel.descriptor.IDescriptorPanel;
import com.francetelecom.orangetv.streammanager.client.panel.descriptor.ParentalRatingDescriptorPanel;
import com.francetelecom.orangetv.streammanager.client.panel.descriptor.PrivateDescriptorPanel;
import com.francetelecom.orangetv.streammanager.client.panel.descriptor.ShortEventDescriptorPanel;
import com.francetelecom.orangetv.streammanager.client.panel.descriptor.ShortSmoothingBufferDescriptorPanel;
import com.francetelecom.orangetv.streammanager.client.util.CssConstants;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractComponentDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.CAIdentifierDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ExtendedEventDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ListComponentDescriptors;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ParentalRatingDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.PrivateDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortEventDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortSmoothingBufferDescriptor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Presentation dans un seul panel des eits present & following section
 * 
 * @author ndmz2720
 *
 */
public class EitAllEventPanel extends Composite implements CssConstants {

	private final static String[] SECTION_TITLES = new String[] { "Present section", "Following section" };

	private final SimplePanel main = new SimplePanel();
	private final AdvancedTabPanel tabPanel = new AdvancedTabPanel(TabPosition.LEFT);

	private DoubleDescriptorPanel<ShortEventDescriptorPanel> shortEventDescriptorPanels = new DoubleDescriptorPanel<>();
	private DoubleDescriptorPanel<ExtendedEventDescriptorPanel> extendedEventDescriptorPanels = new DoubleDescriptorPanel<>();
	private DoubleDescriptorPanel<ContentDescriptorPanel> contentDescriptorPanels = new DoubleDescriptorPanel<>();
	private DoubleDescriptorPanel<CAIdentifierDescriptorPanel> caIdentifierDescriptorPanels = new DoubleDescriptorPanel<>();
	private DoubleDescriptorPanel<ShortSmoothingBufferDescriptorPanel> shortSmoothingBufferDescriptorPanels = new DoubleDescriptorPanel<>();
	private DoubleDescriptorPanel<ParentalRatingDescriptorPanel> parentalRatingDescriptorPanels = new DoubleDescriptorPanel<>();

	private DoubleDescriptorPanel<ComponentDescriptorsPanel> componentDescriptorsPanels = new DoubleDescriptorPanel<>();
	private DoubleDescriptorPanel<PrivateDescriptorPanel> privateDescriptorPanels = new DoubleDescriptorPanel<>();

	// ------------------------------------ constructor
	public EitAllEventPanel() {

		this.buildMainPanel();
		this.initWidget(this.main);
	}

	// --------------------------------- public method
	public void populateWidgetFromData(EitEvent[] eitEvents) {

		this.populateDescriptorPanel(this.caIdentifierDescriptorPanels, eitEvents);
		this.populateDescriptorPanel(this.componentDescriptorsPanels, eitEvents);
		this.populateDescriptorPanel(this.contentDescriptorPanels, eitEvents);
		this.populateDescriptorPanel(this.extendedEventDescriptorPanels, eitEvents);
		this.populateDescriptorPanel(this.parentalRatingDescriptorPanels, eitEvents);
		this.populateDescriptorPanel(this.privateDescriptorPanels, eitEvents);
		this.populateDescriptorPanel(this.shortEventDescriptorPanels, eitEvents);
		this.populateDescriptorPanel(this.shortSmoothingBufferDescriptorPanels, eitEvents);

	}

	/**
	 * Récupère la saisie de l'utilisateur dans un objet EitInfoModel
	 * 
	 * @return
	 */
	public EitEvent[] getDataFromWidget() {

		EitEvent[] eitEvents = new EitEvent[2];

		for (int i = 0; i < eitEvents.length; i++) {

			EitEvent eitEvent = new EitEvent();
			eitEvents[i] = eitEvent;

			if (!this.caIdentifierDescriptorPanels.isEmpty()) {
				eitEvent.setCaIdentifierDescriptor((CAIdentifierDescriptor) this.caIdentifierDescriptorPanels.panels
						.get(i).getDataFromWidget());
			}

			if (!this.componentDescriptorsPanels.isEmpty()) {

				ListComponentDescriptors listComponentDescriptors = (ListComponentDescriptors) this.componentDescriptorsPanels.panels
						.get(i).getDataFromWidget();
				if (listComponentDescriptors != null) {
					for (AbstractComponentDescriptor componentDescriptor : listComponentDescriptors) {
						eitEvent.addComponentDescriptor(componentDescriptor);
					}

				}
			}
			if (!this.contentDescriptorPanels.isEmpty()) {
				eitEvent.setContentDescriptor((ContentDescriptor) this.contentDescriptorPanels.panels.get(i)
						.getDataFromWidget());
			}
			if (!this.extendedEventDescriptorPanels.isEmpty()) {
				eitEvent.setExtendedEventDescriptor((ExtendedEventDescriptor) this.extendedEventDescriptorPanels.panels
						.get(i).getDataFromWidget());
			}
			if (!this.parentalRatingDescriptorPanels.isEmpty()) {
				eitEvent.setParentalRatingDescriptor((ParentalRatingDescriptor) this.parentalRatingDescriptorPanels.panels
						.get(i).getDataFromWidget());
			}
			if (!this.privateDescriptorPanels.isEmpty()) {
				eitEvent.setPrivateDescriptor((PrivateDescriptor) this.privateDescriptorPanels.panels.get(i)
						.getDataFromWidget());
			}
			if (!this.shortEventDescriptorPanels.isEmpty()) {
				eitEvent.setShortEventDescriptor((ShortEventDescriptor) this.shortEventDescriptorPanels.panels.get(i)
						.getDataFromWidget());
			}
			if (!this.shortSmoothingBufferDescriptorPanels.isEmpty()) {
				eitEvent.setShortSmoothingBufferDescriptor((ShortSmoothingBufferDescriptor) this.shortSmoothingBufferDescriptorPanels.panels
						.get(i).getDataFromWidget());
			}

		}

		return eitEvents;

	}

	// -------------------------------------------------- private methods
	private void buildMainPanel() {
		this.buildDescriptorPanels();
		this.main.setWidget(this.tabPanel);

	}

	private void populateDescriptorPanel(DoubleDescriptorPanel<?> doublePanels, EitEvent[] eitEvents) {

		if (doublePanels == null) {
			return;
		}
		doublePanels.populateWidgetFromData(eitEvents);
	}

	private void buildDescriptorPanels() {

		if (new ShortEventDescriptor().isEnabled()) {
			this.shortEventDescriptorPanels.add(new ShortEventDescriptorPanel());
			this.shortEventDescriptorPanels.add(new ShortEventDescriptorPanel());

			tabPanel.add(this.aggregate(this.shortEventDescriptorPanels),
					new LabelTitle("0x4d: Short event descriptor"));
		}
		if (new ExtendedEventDescriptor().isEnabled()) {
			this.extendedEventDescriptorPanels.add(new ExtendedEventDescriptorPanel());
			this.extendedEventDescriptorPanels.add(new ExtendedEventDescriptorPanel());
			tabPanel.add(this.aggregate(this.extendedEventDescriptorPanels), new LabelTitle(
					"0x4e: Extended event descriptor"));
		}
		if (new ContentDescriptor().isEnabled()) {
			this.contentDescriptorPanels.add(new ContentDescriptorPanel());
			this.contentDescriptorPanels.add(new ContentDescriptorPanel());
			tabPanel.add(this.aggregate(this.contentDescriptorPanels), new LabelTitle("0x54: Content descriptor"));
		}
		if (new CAIdentifierDescriptor().isEnabled()) {
			this.caIdentifierDescriptorPanels.add(new CAIdentifierDescriptorPanel());
			this.caIdentifierDescriptorPanels.add(new CAIdentifierDescriptorPanel());
			tabPanel.add(this.aggregate(this.caIdentifierDescriptorPanels), new LabelTitle(
					"0x53: CA_identifier_descriptor"));
		}
		if (new ShortSmoothingBufferDescriptor().isEnabled()) {
			this.shortSmoothingBufferDescriptorPanels.add(new ShortSmoothingBufferDescriptorPanel());
			this.shortSmoothingBufferDescriptorPanels.add(new ShortSmoothingBufferDescriptorPanel());
			tabPanel.add(this.aggregate(this.shortSmoothingBufferDescriptorPanels), new LabelTitle(
					"0x61: Short smoothing buffer descriptor"));
		}
		if (new ParentalRatingDescriptor().isEnabled()) {
			this.parentalRatingDescriptorPanels.add(new ParentalRatingDescriptorPanel());
			this.parentalRatingDescriptorPanels.add(new ParentalRatingDescriptorPanel());
			tabPanel.add(this.aggregate(this.parentalRatingDescriptorPanels), new LabelTitle(
					"0x55: Parental rating descriptor"));
		}
		if (new ListComponentDescriptors().isEnabled()) {
			this.componentDescriptorsPanels.add(new ComponentDescriptorsPanel());
			this.componentDescriptorsPanels.add(new ComponentDescriptorsPanel());
			tabPanel.add(this.aggregate(this.componentDescriptorsPanels), new LabelTitle("0x50: Component descriptor"));
		}
		if (new PrivateDescriptor().isEnabled()) {
			this.privateDescriptorPanels.add(new PrivateDescriptorPanel());
			this.privateDescriptorPanels.add(new PrivateDescriptorPanel());
			tabPanel.add(this.aggregate(this.privateDescriptorPanels), new LabelTitle("0xA?: Private descriptors"));
		}

	}

	private Panel aggregate(DoubleDescriptorPanel<?> doublePanels) {

		final HorizontalPanel hPanel = new HorizontalPanel();

		for (int i = 0; i < doublePanels.panels.size(); i++) {
			hPanel.add(this.compose(doublePanels.panels.get(i), i));
		}

		return hPanel;
	}

	private Panel compose(IDescriptorPanel descriptorPanel, int sectionIndex) {

		final VerticalPanel vPanel = new VerticalPanel();
		vPanel.setSpacing(PANEL_SPACING);
		LabelTitle label = new LabelTitle(SECTION_TITLES[sectionIndex]);
		label.setWidth("95%");
		vPanel.add(label);
		vPanel.add((Panel) descriptorPanel);
		return vPanel;
	}

	// ========================================== INNER CLASS
	private static final class DoubleDescriptorPanel<T extends IDescriptorPanel> {

		private List<T> panels;

		void add(T panel) {
			if (panels == null) {
				panels = new ArrayList<>(2);
			}
			panels.add(panel);
		}

		boolean isEmpty() {
			return panels == null || panels.isEmpty();
		}

		void populateWidgetFromData(EitEvent... eitEvents) {

			if (eitEvents != null && !this.isEmpty() && this.panels.size() == eitEvents.length) {

				for (int i = 0; i < eitEvents.length; i++) {
					this.panels.get(i).populateWidgetFromData(eitEvents[i]);
				}
			}
		}
	}

}
