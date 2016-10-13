package com.francetelecom.orangetv.streammanager.shared.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsule les informations necessaires au panel d'admin
 * 
 * @author ndmz2720
 *
 */
public class DtoAdminInfo implements IDto, Serializable {

	private static final long serialVersionUID = 1L;

	// list des fichiers de logs
	private List<DtoLogFilename> listLogFilenames = new ArrayList<>();

	// list des process
	private List<String> listProcessNames = new ArrayList<>();

	// chemin complet du script de start/stop du supervisor
	private String supervisorScriptPathname;

	// chemin sur le server multicat: videos multicats
	private String multicatVideoPath;

	// chemin sur le server multicat: bin multicats
	private String multicatBinPath;

	// chemin source sur server tomcat
	private String uploadSrcPath;

	private int maxUploadSizeMo;

	// ------------------------------------- accessors

	public int getMaxUploadSizeMo() {
		return maxUploadSizeMo;
	}

	public void setMaxUploadSizeMo(int maxUploadSizeMo) {
		this.maxUploadSizeMo = maxUploadSizeMo;
	}

	public String getMulticatBinPath() {
		return multicatBinPath;
	}

	public void setMulticatBinPath(String multicatBinPath) {
		this.multicatBinPath = multicatBinPath;
	}

	public String getMulticatVideoPath() {
		return multicatVideoPath;
	}

	public void setMulticatVideoPath(String multicatVideoPath) {
		this.multicatVideoPath = multicatVideoPath;
	}

	public String getUploadSrcPath() {
		return uploadSrcPath;
	}

	public void setUploadSrcPath(String uploadSrcPath) {
		this.uploadSrcPath = uploadSrcPath;
	}

	public String getSupervisorScriptPathname() {
		return supervisorScriptPathname;
	}

	public void setSupervisorScriptPathname(String supervisorScriptPathname) {
		this.supervisorScriptPathname = supervisorScriptPathname;
	}

	public void setListLogFilenames(List<DtoLogFilename> listLogFilenames) {
		this.listLogFilenames = listLogFilenames;
	}

	public void setListProcessNames(List<String> listProcessNames) {
		this.listProcessNames = listProcessNames;
	}

	public List<DtoLogFilename> getListLogFilenames() {
		return listLogFilenames;
	}

	public List<String> getListProcessNames() {
		return listProcessNames;
	}

}
