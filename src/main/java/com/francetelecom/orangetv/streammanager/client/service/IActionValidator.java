package com.francetelecom.orangetv.streammanager.client.service;

public interface IActionValidator<T> {

	public void onValidOK(T result);

	public void onValidationError(String errorMessage);

}
