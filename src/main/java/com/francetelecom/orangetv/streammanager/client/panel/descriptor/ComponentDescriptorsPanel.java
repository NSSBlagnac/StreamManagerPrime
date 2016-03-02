package com.francetelecom.orangetv.streammanager.client.panel.descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.client.widget.AspectRatioWidget;
import com.francetelecom.orangetv.streammanager.client.widget.AudioLanguageWidget;
import com.francetelecom.orangetv.streammanager.client.widget.SubtitleLanguageWidget;
import com.francetelecom.orangetv.streammanager.shared.model.EitEvent;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractComponentDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ComponentDescriptorAspectRatio;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ComponentDescriptorAudio;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ComponentDescriptorSubtitle;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.IDescriptor;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ListComponentDescriptors;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ComponentDescriptorsPanel extends AbstractDescriptorPanel {

	private final static Logger log = Logger.getLogger("ComponentDescriptorsPanel");

	private final static int MAX_COMPONENT_ITEMS = 3;

	private VerticalPanel mainPanel = new VerticalPanel();
	private List<AudioLanguageWidget> listAudioLanguageWidget = new ArrayList<AudioLanguageWidget>();
	private List<SubtitleLanguageWidget> listSubtitleLanguageWidget = new ArrayList<SubtitleLanguageWidget>();
	private AspectRatioWidget aspectRatioWidget;

	private ComponentDescriptorAspectRatioPanel pAspectRatioPanel;
	private ComponentDescriptorAudioPanel pAudioPanel;
	private ComponentDescriptorSubtitlePanel pSubtitlePanel;

	// ----------------------------------------- constructor
	public ComponentDescriptorsPanel() {
		this.setStyleName(PANEL_DESCRIPTOR);
		this.setWidget(this.buildComponentPanel());
	}

	// --------------------------------------- implements IDescriptorPanel
	@Override
	public void populateWidgetFromData(EitEvent eitEvent) {
		this.clean();

		List<AbstractComponentDescriptor> listComponentDescriptor = (eitEvent == null) ? null : eitEvent
				.getListComponentDescriptors();

		if (listComponentDescriptor == null) {
			return;
		}

		for (int i = 0; i < MAX_COMPONENT_ITEMS; i++) {

			final AbstractComponentDescriptor descriptor = listComponentDescriptor.size() < i + 1 ? null
					: listComponentDescriptor.get(i);
			if (descriptor == null || !descriptor.isEnabled()) {
				continue; // next
			}
			switch (descriptor.getType()) {
			case AUDIO: {
				log.config("AUDIO - language: " + descriptor.getLang() + " - code: " + descriptor.getLang().getCode());
				AudioLanguageWidget widget = this.pAudioPanel.addAudioLanguageWidget();
				ComponentDescriptorAudio audioDescriptor = (ComponentDescriptorAudio) descriptor;
				widget.setValue(audioDescriptor.getLang().getCode(), audioDescriptor.isAudioDescription(),
						audioDescriptor.isDolby(), audioDescriptor.isDts());
			}
				break;
			case SUBTITLE: {
				SubtitleLanguageWidget widget = this.pSubtitlePanel.addSubtitleLanguageWidget();
				ComponentDescriptorSubtitle subTitleDescriptor = (ComponentDescriptorSubtitle) descriptor;
				widget.setValue(subTitleDescriptor.getLang().getCode(), subTitleDescriptor.isHardOfHearing());
			}
				break;
			case ASPECT_RATIO: {
				ComponentDescriptorAspectRatio aspectRatioDescriptor = (ComponentDescriptorAspectRatio) descriptor;
				this.aspectRatioWidget.setValue(aspectRatioDescriptor);
			}
				break;
			}
		}

	}

	@Override
	public IDescriptor getDataFromWidget() {

		ListComponentDescriptors listComponentDescriptors = new ListComponentDescriptors();

		// aspect ratio

		listComponentDescriptors.add(aspectRatioWidget.getComponentDescriptorAspectRatio());

		// audio
		for (AudioLanguageWidget audioWidget : listAudioLanguageWidget) {
			listComponentDescriptors.add(audioWidget.getComponentDescriptorAudio());
		}

		// subtitle
		for (SubtitleLanguageWidget widget : this.listSubtitleLanguageWidget) {
			listComponentDescriptors.add(widget.getComponentDescriptorSubtitle());
		}
		return listComponentDescriptors;
	}

	// --------------------------------------------- private methods
	private void clean() {
		this.listAudioLanguageWidget.clear();
		this.listSubtitleLanguageWidget.clear();

		this.pAspectRatioPanel.clean();
		this.pAudioPanel.clean();
		this.pSubtitlePanel.clean();
	}

	private Panel buildComponentPanel() {

		this.pAspectRatioPanel = new ComponentDescriptorAspectRatioPanel();
		this.pAudioPanel = new ComponentDescriptorAudioPanel();
		this.pSubtitlePanel = new ComponentDescriptorSubtitlePanel();

		mainPanel.setWidth(MAX_WIDTH);
		mainPanel.setSpacing(PANEL_SPACING);
		mainPanel.setBorderWidth(1);
		mainPanel.add(this.pAspectRatioPanel);
		mainPanel.add(this.pAudioPanel);
		mainPanel.add(this.pSubtitlePanel);
		return mainPanel;
	}

	private int getCountComponentItems() {

		int count = 0;
		if (this.aspectRatioWidget != null) {
			count++;
		}
		count += this.listAudioLanguageWidget.size();
		count += this.listSubtitleLanguageWidget.size();

		return count;
	}

	private void addOrRemoveItem() {
		int count = this.getCountComponentItems();

		boolean enableAddButton = count < MAX_COMPONENT_ITEMS;
		if (this.pAudioPanel != null) {
			this.pAudioPanel.enableAddButton(enableAddButton);
		}
		if (this.pSubtitlePanel != null) {
			this.pSubtitlePanel.enableAddButton(enableAddButton);
		}
	}

	// =================================== INNER CLASS
	private class ComponentDescriptorAspectRatioPanel extends SimplePanel {

		private final VerticalPanel main = new VerticalPanel();

		private ComponentDescriptorAspectRatioPanel() {
			this.setWidget(this.buildMainPanel());
		}

		private void clean() {
			aspectRatioWidget.setValue(null);
		}

		private Panel buildMainPanel() {

			aspectRatioWidget = new AspectRatioWidget();
			this.main.setSpacing(PANEL_SPACING);
			this.main.add(aspectRatioWidget);

			return this.main;
		}

	}

	private class ComponentDescriptorAudioPanel extends SimplePanel {

		private final VerticalPanel main = new VerticalPanel();
		private final VerticalPanel content = new VerticalPanel();

		private Button btAdd = new Button("Add audio language");

		private ComponentDescriptorAudioPanel() {
			this.setWidget(this.buildMainPanel());
			this.buildHandlers();

		}

		// ------------------------------------- private methods
		private void clean() {
			this.content.clear();
		}

		private void enableAddButton(boolean enabled) {
			this.btAdd.setEnabled(enabled);
		}

		private void buildHandlers() {
			this.btAdd.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					addAudioLanguageWidget();
				}
			});
		}

		private Panel buildMainPanel() {
			this.main.setSpacing(PANEL_SPACING);
			this.main.add(this.btAdd);
			this.main.add(this.content);

			this.addAudioLanguageWidget();
			return this.main;
		}

		private AudioLanguageWidget buildAudioLanguageWidget() {

			AudioLanguageWidget widget = new AudioLanguageWidget();
			widget.setDeleteClickHandler(new DeleteAudioWidgetClickHandler(widget));
			listAudioLanguageWidget.add(widget);
			return widget;
		}

		private AudioLanguageWidget addAudioLanguageWidget() {
			AudioLanguageWidget widget = this.buildAudioLanguageWidget();
			content.add(widget);
			addOrRemoveItem();
			return widget;
		}

		private void deleteAudioLanguageWidget(AudioLanguageWidget audioLanguageWidget) {
			listAudioLanguageWidget.remove(audioLanguageWidget);
			addOrRemoveItem();
			this.content.remove(audioLanguageWidget);
		}

		private class DeleteAudioWidgetClickHandler implements ClickHandler {

			private final AudioLanguageWidget audioLanguageWidget;

			private DeleteAudioWidgetClickHandler(AudioLanguageWidget audioLanguageWidget) {
				this.audioLanguageWidget = audioLanguageWidget;
			}

			@Override
			public void onClick(ClickEvent event) {
				deleteAudioLanguageWidget(this.audioLanguageWidget);
			}

		}

	}

	private class ComponentDescriptorSubtitlePanel extends SimplePanel {

		private final VerticalPanel main = new VerticalPanel();
		private final VerticalPanel content = new VerticalPanel();

		private Button btAdd = new Button("Add subtitle language");

		private ComponentDescriptorSubtitlePanel() {
			this.setWidget(this.buildMainPanel());
			this.buildHandlers();

		}

		private void clean() {
			this.content.clear();
		}

		private void enableAddButton(boolean enabled) {
			this.btAdd.setEnabled(enabled);
		}

		private void buildHandlers() {
			this.btAdd.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					addSubtitleLanguageWidget();
				}
			});
		}

		private Panel buildMainPanel() {
			this.main.setSpacing(PANEL_SPACING);
			this.main.add(this.btAdd);
			this.main.add(this.content);

			this.addSubtitleLanguageWidget();
			return this.main;
		}

		private SubtitleLanguageWidget buildSubtitleLanguageWidget() {

			SubtitleLanguageWidget widget = new SubtitleLanguageWidget();
			widget.setDeleteClickHandler(new DeleteSubtitleWidgetClickHandler(widget));
			listSubtitleLanguageWidget.add(widget);

			return widget;
		}

		private SubtitleLanguageWidget addSubtitleLanguageWidget() {
			SubtitleLanguageWidget widget = this.buildSubtitleLanguageWidget();
			addOrRemoveItem();
			content.add(widget);
			return widget;
		}

		private void deleteSubtitleLanguageWidget(SubtitleLanguageWidget subtitleLanguageWidget) {
			listSubtitleLanguageWidget.remove(subtitleLanguageWidget);
			addOrRemoveItem();
			this.content.remove(subtitleLanguageWidget);
		}

		private class DeleteSubtitleWidgetClickHandler implements ClickHandler {

			private final SubtitleLanguageWidget subtitleLanguageWidget;

			private DeleteSubtitleWidgetClickHandler(SubtitleLanguageWidget subtitleLanguageWidget) {
				this.subtitleLanguageWidget = subtitleLanguageWidget;
			}

			@Override
			public void onClick(ClickEvent event) {
				deleteSubtitleLanguageWidget(this.subtitleLanguageWidget);
			}

		}

	}

}
