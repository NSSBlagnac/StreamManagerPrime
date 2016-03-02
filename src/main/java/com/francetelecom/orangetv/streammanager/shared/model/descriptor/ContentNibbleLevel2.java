package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.francetelecom.orangetv.streammanager.shared.model.divers.CodeAndDescription;

public enum ContentNibbleLevel2 {

	C0(ContentNibbleLevel1.UNDEFINED, "0x0", "undefined content"),

	C1_5(ContentNibbleLevel1.MOVIE, "0x5", "movie/mélodrame"),

	C2_2(ContentNibbleLevel1.NEWS, "0x02", "journal"), C2_3(ContentNibbleLevel1.NEWS, "0x03", "documentaire"),

	C3_2(ContentNibbleLevel1.SHOW, "0x02", "variétés"), C3_3(ContentNibbleLevel1.SHOW, "0x03", "débat"),

	C4_8(ContentNibbleLevel1.SPORT, "0x08", "sports nautiques"), C4_9(ContentNibbleLevel1.SPORT, "0x09",
			"sports d'hiver"),

	C5_1(ContentNibbleLevel1.CHILDREN, "0x01", "Enfant"), C5_2(ContentNibbleLevel1.CHILDREN, "0x02", "Junior"), C5_3(
			ContentNibbleLevel1.CHILDREN, "0x03", "Ado"),

	C6_3(ContentNibbleLevel1.MUSIC, "0x03", "musique traditionnelle"),

	C7_0(ContentNibbleLevel1.ART, "0x00", "arts/culture (sans musique, général)"), C7_1(ContentNibbleLevel1.ART,
			"0x01", "théatre"), C7_2(ContentNibbleLevel1.ART, "0x02", "Beaux-Arts"), C7_3(ContentNibbleLevel1.ART,
			"0x03", "religion"), C7_5(ContentNibbleLevel1.ART, "0x05", "lettres"), C7_7(ContentNibbleLevel1.ART,
			"0x07", "film experimental"), C7_8(ContentNibbleLevel1.ART, "0x08", "media"), C7_9(ContentNibbleLevel1.ART,
			"0x09", "nouvelles technologies"),

	C8_0(ContentNibbleLevel1.SOCIAL, "0x00", "société (général)"), C8_1(ContentNibbleLevel1.SOCIAL, "0x01",
			"documentaire"),

	C9_3(ContentNibbleLevel1.SCIENCE, "0x03", "santé"), C9_4(ContentNibbleLevel1.SCIENCE, "0x04", "découverte"), C9_5(
			ContentNibbleLevel1.SCIENCE, "0x05", "sciences sociales"), C9_6(ContentNibbleLevel1.SCIENCE, "0x06",
			"education"), C9_7(ContentNibbleLevel1.SCIENCE, "0x07", "langues"),

	CA_0(ContentNibbleLevel1.LEISURE, "0x00", "loisirs (general)"), CA_2(ContentNibbleLevel1.LEISURE, "0x02",
			"artisanat"), CA_3(ContentNibbleLevel1.LEISURE, "0x03", "sports mécaniques"), CA_4(
			ContentNibbleLevel1.LEISURE, "0x04", "bien être"), CA_5(ContentNibbleLevel1.LEISURE, "0x05",
			"arts de la table"), CA_7(ContentNibbleLevel1.LEISURE, "0x07", "jardinage");

	public static ContentNibbleLevel2 get(String parentCode, String code) {

		List<ContentNibbleLevel2> listCategories = ContentNibbleLevel2
				.getForLevel1(ContentNibbleLevel1.get(parentCode));

		for (ContentNibbleLevel2 nibble2 : listCategories) {
			if (nibble2.getCode().equals(code)) {
				return nibble2;
			}
		}
		return C0;
	}

	public static Comparator<ContentNibbleLevel2> getDescriptionComparator() {
		return new Comparator<ContentNibbleLevel2>() {

			@Override
			public int compare(ContentNibbleLevel2 o1, ContentNibbleLevel2 o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		};
	}

	public static List<ContentNibbleLevel2> getForLevel1(ContentNibbleLevel1 level1) {

		List<ContentNibbleLevel2> list = new ArrayList<ContentNibbleLevel2>();

		for (ContentNibbleLevel2 level2 : ContentNibbleLevel2.values()) {
			if (level2.getParent() == level1) {
				list.add(level2);
			}
		}

		return list;
	}

	private ContentNibbleLevel1 parent;

	public ContentNibbleLevel1 getParent() {
		return this.parent;
	}

	private CodeAndDescription codeAndDescription;

	public String getCode() {
		return this.codeAndDescription.getCode();
	}

	public String getDescription() {
		return this.codeAndDescription.getDescription();
	}

	private ContentNibbleLevel2() {
	}

	private ContentNibbleLevel2(ContentNibbleLevel1 parent, String code, String description) {
		this.parent = parent;
		this.codeAndDescription = new CodeAndDescription(code, description);
	}
}