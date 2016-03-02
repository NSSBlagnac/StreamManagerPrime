package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.io.Serializable;
import java.util.Comparator;

import com.francetelecom.orangetv.streammanager.shared.model.divers.CodeAndDescription;

public abstract class AbstractDescriptor implements IDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * http://fr.wikipedia.org/wiki/Liste_des_codes_ISO_639-2
	 * 
	 * @author sylvie
	 * 
	 */
	public static enum Language {

		ARA("ara", "arabe"), BRE("bre", "breton"), CAT("cat", "catalan"), CHI("chi", "chinois"), DUT("dut",
				"néerlandais"), FRE("fre", "francais"), GRE("gre", "grec moderne"), HIN("hin", "hindi"), ITA("ita",
				"italien"), JPN("jpn", "japonais"), RUS("rus", "russe"), GER("ger", "allemand"), ENG("eng", "anglais");

		public static Comparator<Language> getDescriptionComparator() {
			return new Comparator<Language>() {

				@Override
				public int compare(Language o1, Language o2) {
					return o1.getDescription().compareTo(o2.getDescription());
				}
			};
		}

		public static Language get(String code) {
			for (Language language : Language.values()) {
				if (language.getCode().equals(code)) {
					return language;
				}
			}
			return FRE;
		}

		private CodeAndDescription codeAndDescription;

		public String getCode() {
			return this.codeAndDescription.getCode();
		}

		public String getDescription() {
			return this.codeAndDescription.getDescription();
		}

		private Language() {
		}

		private Language(String code, String description) {
			this.codeAndDescription = new CodeAndDescription(code, description);
		}

	}

	/**
	 * http://fr.wikipedia.org/wiki/ISO_3166-1
	 * 
	 * @author sylvie
	 * 
	 */
	public static enum Country {

		DZA("DZA", "Algérie"), DEU("DEU", "Allemagne"), ARG("ARG", "Argentine"), BEL("BEL", "Belgique"), CAN("CAN",
				"Canada"), CHN("CHN", "Chine"), ESP("ESP", "Espagne"), FRA("FRA", "france"), GBR("GBR", "Royaume-Uni"), USA(
				"USA", "Etats-Unis"), ITA("ITA", "Italie"), LBN("LBN", "Liban"), MAR("MAR", "Maroc"), POL("POL",
				"Pologne"), TUN("TUN", "Tunisie");

		public static Country get(String code) {
			for (Country country : Country.values()) {
				if (country.getCode().equals(code)) {
					return country;
				}
			}
			return FRA;
		}

		public static Comparator<Country> getDescriptionComparator() {
			return new Comparator<Country>() {

				@Override
				public int compare(Country o1, Country o2) {
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

		private Country() {
		}

		private Country(String code, String description) {
			this.codeAndDescription = new CodeAndDescription(code, description);
		}

	}
	
	//------------------------------------ overriding IDescriptor
	@Override
	public boolean isEnabled() {
		return true;
	}

}
