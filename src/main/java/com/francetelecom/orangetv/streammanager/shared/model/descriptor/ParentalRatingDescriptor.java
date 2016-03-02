package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.io.Serializable;

/**
 * DVB descriptor 0x55: Parental rating descriptor
 * 
 * @author sylvie
 * 
 */
public class ParentalRatingDescriptor extends AbstractDescriptor implements IDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	private ParentalRating parentalRating;
	private String codecountry;

	public ParentalRating getParentalRating() {
		return this.parentalRating;
	}

	public Country getCountry() {
		return Country.get(codecountry);
	}

	// ------------------------------------------ construtor
	public ParentalRatingDescriptor() {
		this(new ParentalRating(-1), Country.FRA);
	}

	public ParentalRatingDescriptor(ParentalRating parentalRating, String countryCode) {
		this(parentalRating, Country.get(countryCode));
	}

	public ParentalRatingDescriptor(ParentalRating parentalRating, Country country) {
		this.parentalRating = parentalRating;
		this.codecountry = country.getDescription();
	}

	// ============================ INNER CLASS
	/*
	 * 0x01 to 0x0F
	 * minimum age = rating + 3 years
	 */
	public static class ParentalRating implements Serializable {

		private static final long serialVersionUID = 1L;
		private int rating;

		public int getRating() {
			return rating;
		}

		public ParentalRating() {
			this(0);
		}

		public ParentalRating(int rating) {
			this.rating = rating;
		}
	}

}