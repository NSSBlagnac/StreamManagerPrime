package com.francetelecom.orangetv.streammanager.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.francetelecom.orangetv.streammanager.shared.util.EitException;
import com.francetelecom.orangetv.streammanager.shared.util.ValueHelper;

public abstract class AbstractValidator {

	protected static final int[] ADDRESS_DIGITS = new int[] { 3, 2, 2, 2 };
	protected static final int[] TRIPLET_DIGITS = new int[] { 2, 2, 3 };

	/*
	 * transforme le tableau de int [x, y, z] en une valeur numerique
	 * en fonction des puissances de 10 fournies par le tableau digits
	 * par ex [x, y, z] avec digits [2,2,3] donne (x*10 exp 5) + (y*10 exp 3) + z
	 */
	protected long itemsToLongValue(int[] items, int[] digits, boolean inverse) {
		if (!inverse) {
			return this.itemsToLongValue(items, digits);
		}

		return this.itemsToLongValue(ValueHelper.reverseArray(items), ValueHelper.reverseArray(digits));

	}

	protected long itemsToLongValue(int[] items, int[] digits) {

		int length = items.length;
		long value = 0;

		byte exposant = 0;
		for (int i = 0; i < length; i++) {
			int digit = digits[i];

			value += items[length - (i + 1)] * ((long) Math.pow(10, exposant));
			exposant += digit;
		}

		return value;
	}

	protected String getRange(Number valueInf, Number valueSup) {
		return "[" + valueInf + ", " + valueSup + "]";
	}

	protected void validateInOutRange(boolean inRange, int value, int valueInf, int valueSup, String comment,
			String requiredRange) throws EitException {

		String rangeMessage = this.getInOutRangeMessage(inRange, valueInf, valueSup, comment, requiredRange);

		// value in [valueInf, valueSup]
		if (inRange) {
			if (!(value >= valueInf && value <= valueSup)) {
				throw new EitException(rangeMessage);
			}
		}
		// value out of [valueInf, valueSup]
		else {
			if ((value >= valueInf && value <= valueSup)) {
				throw new EitException(rangeMessage);
			}
		}
	}

	protected void validateInOutRange(boolean inRange, long value, long valueInf, long valueSup, String comment,
			String requiredRange) throws EitException {

		if (valueSup < 0 || valueInf < 0) {
			return;
		}

		requiredRange = (requiredRange == null) ? "[" + valueInf + ", " + valueSup + "]" : requiredRange;
		// value in [valueInf, valueSup]
		if (inRange) {
			if (!(value >= valueInf && value <= valueSup)) {
				throw new EitException(comment + " must be in " + requiredRange + " range!");
			}
		}
		// value out of [valueInf, valueSup]
		else {
			if ((value >= valueInf && value <= valueSup)) {
				throw new EitException(comment + " must be out of " + requiredRange + " range!");
			}
		}
	}

	/**
	 * 
	 * @param toValidate
	 * @param digits
	 *            : count max of digit
	 * @param comment
	 * @return
	 * @throws EitException
	 */
	protected int validateNumber(String toValidate, int digits, String comment) throws EitException {

		int value = this.validateNumber(toValidate, comment);
		if (digits <= 0) {
			return value;
		}

		int maxValue = (int) Math.pow(10, digits);
		if (!(value < (maxValue))) {
			throw new EitException("The " + comment + " must be < " + maxValue + "!");
		}
		return value;
	}

	protected int validateNumber(String toValidate, String comment) throws EitException {

		int value = Integer.MAX_VALUE;
		try {
			value = Integer.parseInt(toValidate);
		} catch (NumberFormatException e) {
			throw new EitException("The " + comment + " is not a number!");
		}
		return value;
	}

	/*
	 * XXX.XX.XX.XX
	 */
	protected void validateUrlAddress(String urlAddress) throws EitException {

		// address url not empty
		validateRequired(urlAddress, "Url Address");
		StringTokenizer st = new StringTokenizer(urlAddress, ".");

		List<String> items = new ArrayList<>();
		while (st.hasMoreTokens()) {
			items.add(st.nextToken());
		}

		if (items == null || items.size() != ADDRESS_DIGITS.length) {
			throw new EitException("Invalid Url Address!");
		}

		for (int i = 0; i < items.size(); i++) {
			validateNumber(items.get(i), ADDRESS_DIGITS[i], "Url Address (" + items.get(i) + ")");
		}
	}

	protected void validateRequired(String toValidate, String comment) throws EitException {
		if (toValidate == null || toValidate.length() == 0) {
			throw new EitException("The " + comment + " is required!");
		}
	}

	protected void validateIntValue(int toValidate, int minValue, String comment) throws EitException {

		if (toValidate <= minValue) {
			String message = (minValue == 0) ? " cannot be null or negative!" : " must been > " + minValue;
			throw new EitException(comment + message);
		}
	}

	protected void validateString(String toValidate, int minLength, String comment) throws EitException {
		if (toValidate == null || toValidate.length() < minLength) {
			throw new EitException("The " + comment + " must have more than " + minLength + " characters!");
		}
	}

	private String getInOutRangeMessage(boolean inRange, Number valueInf, Number valueSup, String comment,
			String requiredRange) {

		if (valueSup == null || valueInf == null) {
			return null;
		}

		requiredRange = (requiredRange == null) ? this.getRange(valueInf, valueSup) : requiredRange;
		String rangeMessage = (inRange) ? comment + " must be in " + requiredRange + " range!" : comment
				+ " must be out of " + requiredRange + " range!";

		return rangeMessage;
	}

}
