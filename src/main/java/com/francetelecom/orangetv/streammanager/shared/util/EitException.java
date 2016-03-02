package com.francetelecom.orangetv.streammanager.shared.util;

import java.io.Serializable;

public class EitException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String errorMessage;
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	public EitException() {}
	
	public EitException(String message) {
		super(message);
		this.errorMessage = message;
	}
	public EitException(Exception exception) {
		
		super(exception.getMessage());
        this.buildErrorMessage(exception);		
	}
	

private void buildErrorMessage(Exception exception) {
		
		this.errorMessage = exception.getMessage();
		
		if (exception.getCause() != null) {
			this.errorMessage += " (" +exception.getCause().getMessage() + ")"; 
		}
	}

}
