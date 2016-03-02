package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.shared.util.EitException;

public class StringValidator implements IValidator<String> {

	private final int minLength;
	private final int maxLength;
	private final String comment;

	public StringValidator(int maxLength, String comment) {
		this(0, maxLength, comment);
	}

	public StringValidator(int minLength, int maxLength, String comment) {
		this.maxLength = maxLength;
		this.minLength = minLength;
		this.comment = comment;
	}

	@Override
	public void validate(String value) throws EitException {

		if (value == null) {
			throw new EitException(comment + " cannot be null!");
		}

		if (value.length() < this.minLength || value.length() > this.maxLength) {
			throw new EitException(this.comment + "'s length must be between " + this.minLength + " and "
					+ this.maxLength + "!");
		}
	}

	@Override
	public String getComment() {
		return this.comment;
	}

	// FIXME non adapte a minValue > 0
	@Override
	public String getCorrectedValue(String value) {

		if (value == null) {
			return "";
		}

		if (value.length() < this.minLength || value.length() > this.maxLength) {
			return value.substring(0, maxLength);
		}
		return value;

	}

}
