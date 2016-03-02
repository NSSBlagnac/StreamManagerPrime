package com.francetelecom.orangetv.streammanager.client.widget;

import com.francetelecom.orangetv.streammanager.shared.util.EitException;

public interface IValidator<T> {

	public void validate(T value) throws EitException;

	public String getComment();

	// TODO systeme de validation non satisfaisant. A revoir
	public T getCorrectedValue(T value);
}
