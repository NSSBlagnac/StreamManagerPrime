package com.francetelecom.orangetv.streammanager.shared.dto;

/**
 * Status de la video stocké en base
 * (independant des streams)
 * 
 * @author ndmz2720
 *
 */
public enum VideoStatus {
	NEW(false), // video cree en base mais fichier non uploade
	READY(true), // video cree et fichier uploaded, utilisée ou non
	PENDING(false), // upload en cours
	ERROR(false); // upload error

	// utilisable dans un flux
	private final boolean available;

	public boolean isAvailable() {
		return this.available;
	}

	private VideoStatus(boolean available) {
		this.available = available;
	}
}