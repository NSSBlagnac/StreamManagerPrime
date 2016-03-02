package com.francetelecom.orangetv.streammanager.shared.dto;

/**
 * Video status tenant compte de l'utilisation par les stream
 * valeur calculée en fonction VideoStatus et count stream
 * 
 * @author ndmz2720
 *
 */
public enum FullVideoStatus {

	NEW(false, "created but not uploaded!"), // video cree en base mais fichier
												// non uploade
	UPLOADED(true, "uploaded but not used!"), // video cree et fichier uploaded
	USED(true, "used in stream but not running!"), // video utilisée par une
													// video mais non running
	RUNNING(true, "running in a stream!"), // video diffusee dans un stream
											// actif (enabled)
	PENDING(false, "upload pending..."), // upload en cours
	ERROR(false, "upload in error!"); // upload error

	// utilisable dans un flux
	private final boolean available;

	// description
	private final String description;

	public boolean isAvailable() {
		return this.available;
	}

	public String getDescription() {
		return this.description;
	}

	private FullVideoStatus(boolean available, String description) {
		this.available = available;
		this.description = description;
	}

}
