package com.francetelecom.orangetv.streammanager.server.model;

import java.io.Serializable;

import com.francetelecom.orangetv.streammanager.shared.dto.IDto;
import com.francetelecom.orangetv.streammanager.shared.dto.VideoStatus;

public class DbVideoFile implements Serializable, IDto, IDbEntry, Comparable<DbVideoFile> {

	private static final long serialVersionUID = 1L;

	private int id = ID_UNDEFINED;
	private String name;
	private boolean color;
	private String resolution;
	private boolean ocs;
	private boolean csa5;
	private String format;
	private String description;

	// permet de désactiver momentanément une video.
	// elle ne sera pas présentée dans la liste des video disponibles.
	// par contre non modifiable si la video est déjà utilisée par un stream
	private boolean enabled;

	// status déterminant la disponibilité du fichier de video
	// NEW : la video a été créé dans la base de donnée mais le fichier n'existe
	// pas encore
	// READY: le fichier a été uploade ou déposé manuellement sur le serveur de
	// multicat
	// ERROR: erreur lors de l'upload
	// seuls les status READY rend la video disponible pour un stream
	private VideoStatus status;

	private boolean entryProtected = true;

	// ---------------------------- accessors

	public boolean isEntryProtected() {
		return entryProtected;
	}

	public boolean isCsa5() {
		return csa5;
	}

	public void setCsa5(boolean csa5) {
		this.csa5 = csa5;
	}

	public String getDescription() {
		return (description == null) ? "" : description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public VideoStatus getStatus() {
		return status;
	}

	public void setStatus(VideoStatus status) {
		this.status = status;
	}

	public void setStatus(String strStatus) {
		this.setStatus(VideoStatus.valueOf(strStatus));
	}

	public void setEntryProtected(boolean entryProtected) {
		this.entryProtected = entryProtected;
	}

	public String getName() {
		return this.name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isColor() {
		return color;
	}

	public void setColor(boolean color) {
		this.color = color;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public boolean isOcs() {
		return ocs;
	}

	public void setOcs(boolean ocs) {
		this.ocs = ocs;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setName(String name) {
		this.name = name;
	}

	// ------------------------------ constructor
	public DbVideoFile() {
		this(-1, null);
	}

	public DbVideoFile(int id, String name) {
		this.id = id;
		this.name = name;
	}

	// ------------------------------------ implementing Comparable

	@Override
	public int compareTo(DbVideoFile o) {
		if (o == null) {
			return 1;
		}
		if (this.getName() == o.getName()) {
			return 0;
		}
		return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
	}

}
