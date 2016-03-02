package com.francetelecom.orangetv.streammanager.client.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.francetelecom.orangetv.streammanager.client.panel.descriptor.ParentalRatingDescriptorPanel.CsaMoralityLevel;
import com.francetelecom.orangetv.streammanager.client.panel.descriptor.PrivateDescriptorPanel.PrivateTag;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractDescriptor.Country;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.AbstractDescriptor.Language;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ComponentDescriptorAspectRatio.AspectRatio;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentNibbleLevel1;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ContentNibbleLevel2;
import com.francetelecom.orangetv.streammanager.shared.model.descriptor.ShortSmoothingBufferDescriptor.SbLeakRate;
import com.google.gwt.user.client.ui.ListBox;

public class EnumListManager {

	private static EnumListManager instance;

	public final static EnumListManager get() {
		if (instance == null) {
			instance = new EnumListManager();
		}
		return instance;
	}

	private EnumListManager() {
	}

	private final Map<ContentNibbleLevel1, List<ContentNibbleLevel2>> mapCategoryLevel12Level2 = new HashMap<ContentNibbleLevel1, List<ContentNibbleLevel2>>();

	private List<Language> sortedLanguage;
	private List<Country> sortedCountry;
	private List<ContentNibbleLevel1> sortedCatLevel1;

	public void updateListBoxCategoryLevel2(ContentNibbleLevel1 catLevel1, ListBox listBox) {

		listBox.clear();
		List<ContentNibbleLevel2> listCatLevel2 = this.mapCategoryLevel12Level2.get(catLevel1);
		if (listCatLevel2 == null) {
			listCatLevel2 = ContentNibbleLevel2.getForLevel1(catLevel1);
			Collections.sort(listCatLevel2, ContentNibbleLevel2.getDescriptionComparator());
			this.mapCategoryLevel12Level2.put(catLevel1, listCatLevel2);
		}

		for (ContentNibbleLevel2 catLevel2 : listCatLevel2) {
			listBox.addItem(catLevel2.getDescription(), catLevel2.getCode());
		}

	}

	public ListBox buildListBoxCategoryLevel1() {

		ListBox lbCatLevel1 = new ListBox();

		if (this.sortedCatLevel1 == null) {
			this.sortedCatLevel1 = Arrays.asList(ContentNibbleLevel1.values());
			Collections.sort(this.sortedCatLevel1, ContentNibbleLevel1.getDescriptionComparator());
		}

		for (ContentNibbleLevel1 catLevel1 : this.sortedCatLevel1) {
			lbCatLevel1.addItem(catLevel1.getDescription(), catLevel1.getCode());
		}
		return lbCatLevel1;
	}

	public ListBox buildListBoxCountry() {

		ListBox lbCountry = new ListBox();

		if (this.sortedCountry == null) {
			this.sortedCountry = Arrays.asList(Country.values());
			Collections.sort(sortedCountry, Country.getDescriptionComparator());
		}
		for (Country country : this.sortedCountry) {
			lbCountry.addItem(country.getDescription(), country.getCode());
		}

		return lbCountry;
	}

	public ListBox buildListBoxLanguage() {

		ListBox lbCountry = new ListBox();

		if (this.sortedLanguage == null) {
			this.sortedLanguage = Arrays.asList(Language.values());
			Collections.sort(sortedLanguage, Language.getDescriptionComparator());
		}

		for (Language language : sortedLanguage) {
			lbCountry.addItem(language.getDescription(), language.getCode());
		}

		return lbCountry;
	}

	public ListBox buildListBoxCsaMoralityLevel(boolean orange) {
		ListBox lbCsa = new ListBox();

		for (CsaMoralityLevel csaMoralityLevel : CsaMoralityLevel.values()) {
			lbCsa.addItem(csaMoralityLevel.getDescription(orange), csaMoralityLevel.getCode(orange));
		}

		return lbCsa;
	}

	public ListBox buildListBoxPrivateTag() {

		ListBox lbTags = new ListBox();
		for (PrivateTag privateTag : PrivateTag.values()) {
			String code = privateTag.getTag();
			lbTags.addItem(code + " - " + privateTag.getDescription(), code);
		}

		return lbTags;
	}

	public ListBox buildListBoxAspectRatio() {

		ListBox lbCountry = new ListBox();

		for (AspectRatio aspectRatio : AspectRatio.values()) {
			lbCountry.addItem(aspectRatio.getDescription(), aspectRatio.getCode());
		}

		return lbCountry;
	}

	public ListBox buildListBoxSbLeakRate() {

		ListBox lbCountry = new ListBox();

		for (SbLeakRate sbLeakRate : SbLeakRate.values()) {
			lbCountry.addItem(sbLeakRate.getDescription(), sbLeakRate.getCode());
		}

		return lbCountry;
	}

}
