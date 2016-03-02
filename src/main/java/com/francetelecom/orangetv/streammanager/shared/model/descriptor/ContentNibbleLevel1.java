package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.util.Comparator;

import com.francetelecom.orangetv.streammanager.shared.model.divers.CodeAndDescription;

// ====================== INNER CLASS
public enum ContentNibbleLevel1 {

	UNDEFINED("0x0", "undefined content"), MOVIE("0x1", "Film, mélodrame"), NEWS("0x2", "Journal, Documentaire"), SHOW(
			"0x3", "Variété, débat"), SPORT("0x4", "Sport"), CHILDREN("0x5", "Enfant, Junior"), MUSIC("0x6",
			"Musique, Ballet, Danse"), ART("0x7", "Art Culture (sans musique)"), SOCIAL("0x8",
			"Société, Politique, Economie"), SCIENCE("0x9", "Education, Science, Santé"), LEISURE("0xA", "Loisirs");

	public static ContentNibbleLevel1 get(String code) {

		for (ContentNibbleLevel1 nibble1 : ContentNibbleLevel1.values()) {
			if (nibble1.getCode().equals(code)) {
				return nibble1;
			}
		}

		return UNDEFINED;
	}

	public static Comparator<ContentNibbleLevel1> getDescriptionComparator() {
		return new Comparator<ContentNibbleLevel1>() {

			@Override
			public int compare(ContentNibbleLevel1 o1, ContentNibbleLevel1 o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		};
	}

	private CodeAndDescription codeAndDescription;

	public String getCode() {
		return this.codeAndDescription.getCode();
	}

	public String getDescription() {
		return this.codeAndDescription.getDescription();
	}

	private ContentNibbleLevel1() {
	}

	private ContentNibbleLevel1(String code, String description) {
		this.codeAndDescription = new CodeAndDescription(code, description);
	}
}