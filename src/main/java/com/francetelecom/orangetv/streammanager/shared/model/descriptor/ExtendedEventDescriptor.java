package com.francetelecom.orangetv.streammanager.shared.model.descriptor;

import java.io.Serializable;

/*
 * DVB  Descriptor 0x4e: Extended event descriptor
 * The extended event descriptor provides a detailed text description of an event, which may be used in addition to the
 * short event descriptor
 * A typical application for this structure is to give a cast list
 */
public class ExtendedEventDescriptor extends AbstractDescriptor implements IDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	public enum LABEL {

		director, rating, year, writers, stars

	}

	private String text;
	private String codelang;

	// private LabelAndDescription director;
	// private LabelAndDescription year;
	// private LabelAndDescription rating;
	// private LabelAndDescription writers;
	//
	// private LabelAndDescription stars;

	// public LabelAndDescription getDirector() {
	// return director;
	// }
	//
	// public void setDirector(LabelAndDescription director) {
	// this.director = director;
	// }
	//
	// public LabelAndDescription getYear() {
	// return year;
	// }
	//
	// public void setYear(LabelAndDescription year) {
	// this.year = year;
	// }
	//
	// public LabelAndDescription getRating() {
	// return rating;
	// }
	//
	// public void setRating(LabelAndDescription rating) {
	// this.rating = rating;
	// }

	public String getText() {
		return text;
	}

	public Language getLang() {
		return Language.get(codelang);
	}

	// public LabelAndDescription getWriters() {
	// return writers;
	// }
	//
	// public void setWriters(LabelAndDescription writer) {
	// this.writers = writer;
	// }
	//
	// public LabelAndDescription getStars() {
	// return stars;
	// }
	//
	// public void setStars(LabelAndDescription stars) {
	// this.stars = stars;
	// }

	// ------------------------------------------ constructor
	public ExtendedEventDescriptor() {
		this(null, Language.FRE);
	}

	public ExtendedEventDescriptor(String text, Language language) {
		this.text = text;
		this.codelang = language.getCode();
	}

	public ExtendedEventDescriptor(String text, String langCode) {
		this(text, Language.get(langCode));
	}

}