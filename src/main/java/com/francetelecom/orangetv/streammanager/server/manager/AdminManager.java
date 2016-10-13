package com.francetelecom.orangetv.streammanager.server.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.server.service.IMyServices;
import com.francetelecom.orangetv.streammanager.server.util.SSHAgent;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdRequest;
import com.francetelecom.orangetv.streammanager.shared.dto.CmdResponse;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoAdminInfo;
import com.francetelecom.orangetv.streammanager.shared.dto.DtoLogFilename;
import com.francetelecom.orangetv.streammanager.shared.dto.SupervisorStatus;

/**
 * Manager pour toutes les fonctions d'administration
 * 
 * @author ndmz2720
 *
 */
public class AdminManager implements IMyServices {

	private static final Logger log = Logger.getLogger(AdminManager.class.getName());
	private static AdminManager instance;

	public static AdminManager get() {
		if (instance == null) {
			instance = new AdminManager();
		}
		return instance;
	}

	private AdminManager() {
	}

	// --------------------------------------
	private MulticatServerInfo multicatServerInfo;
	private DtoAdminInfo adminInfo;

	// ------------------------------------- public methods
	void init(MulticatServerInfo multicatServerInfo) {
		this.multicatServerInfo = multicatServerInfo;
	}

	public CmdResponse executeRemoteCommand(CmdRequest cmdRequest) {

		return this.executeLinuxSshCommand(cmdRequest, this.multicatServerInfo);
	}

	/*
	 * Extraction de la table PMT d'un fichier video
	 * cd /usr/local/multicat-tools/bin;
	 * ./dvb_print_si -x xml -T PMT <
	 * /usr/local/multicat-tools/videos/canal+crypt.ts | grep -v ERROR
	 * 
	 * @param videoDirPath
	 * @param videoFilename
	 * @return
	 */
	public CmdResponse extractPmtTableFromVideofile(String videoDirPath, String videoFilename) {
		log.info("extractPmtTableFromVideofile() videoDirPath: " + videoDirPath + " - videoFilename: " + videoFilename);
		if (this.multicatServerInfo == null) {
			return this.buildFailureResponse("Error server info unavailable!");
		}

		if (!videoDirPath.endsWith("/")) {
			videoDirPath += "/";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("cd ");
		sb.append(this.getAdminInfo().getMulticatBinPath());
		sb.append("; ");

		sb.append("./dvb_print_si -x xml -T PMT  < ");
		sb.append(videoDirPath);
		sb.append(videoFilename);
		sb.append(" | grep -v ERROR");

		CmdRequest request = new CmdRequest(sb.toString(), true);

		return this.executeLinuxSshCommand(request, this.multicatServerInfo);

	}

	/**
	 * Suppression physique du fichier video et du fichier aux
	 * 
	 * @param videoDirPath
	 * @param videoFilename
	 * @return
	 */
	public CmdResponse deleteVideoFileAndAux(String videoDirPath, String videoFilename) {

		log.info("deleteVideoFileAndAux() videoDirPath: " + videoDirPath + " - videoFilename: " + videoFilename);
		if (this.multicatServerInfo == null) {
			return this.buildFailureResponse("Error server info unavailable!");
		}

		if (!videoDirPath.endsWith("/")) {
			videoDirPath += "/";
		}

		String filePath = videoDirPath + videoFilename;

		int pos = videoFilename.lastIndexOf(".");
		String rootName = videoFilename.substring(0, pos);
		String auxFilePath = videoDirPath + rootName + ".aux";

		CmdResponse response1 = this.deleteFile(filePath);
		CmdResponse response2 = this.deleteFile(auxFilePath);

		return response1.concat(response2);
	}

	// cd /usr/local/multicat-tools/videos; PID=$(../bin/mpeg_print_pcr <
	// france2.ts) ; ../bin/ingests -p $PID france2.ts
	public CmdResponse createAuxVideoFileWithIngest(String videoDirPath, String videoFilename) {

		log.info("createAuxVideoFileWithIngest() videoDirPath: " + videoDirPath + " - videoFilename: " + videoFilename);
		if (this.multicatServerInfo == null) {
			return this.buildFailureResponse("Error server info unavailable!");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("cd ");
		sb.append(videoDirPath);
		sb.append("; ");

		sb.append("PID=$(../bin/mpeg_print_pcr < ");
		sb.append(videoFilename);
		sb.append("); ");

		sb.append("../bin/ingests -p $PID ");
		sb.append(videoFilename);

		CmdRequest request = new CmdRequest(sb.toString(), true);

		CmdResponse response = this.executeLinuxSshCommand(request, this.multicatServerInfo);
		return response;

	}

	// scp <file> <username>@<IP address or hostname>:<Destination>
	public CmdResponse copyFileToRemoteWithLinuxScp(String filepath, String destination) {

		log.info("copyFileToRemoteWithLinuxScp() filepath: " + filepath + " - target: " + destination);
		if (this.multicatServerInfo == null) {
			return this.buildFailureResponse("Error server info unavailable!");
		}

		StringBuilder sb = new StringBuilder();
		sb.append("scp -o StrictHostKeyChecking=no ");
		sb.append(filepath);
		sb.append(" ");
		sb.append(this.multicatServerInfo.getUser());
		sb.append("@");
		sb.append(this.multicatServerInfo.getIp());
		sb.append(":");
		sb.append(destination);

		return this.executeLocalCommand(sb.toString());
	}

	public SupervisorStatus getSupervisorStatus() {

		if (multicatServerInfo != null && !multicatServerInfo.getProcessList().isEmpty()) {
			String cmd = "ps u -A | grep " + multicatServerInfo.getProcessList().get(0);
			CmdResponse response = this.executeLinuxSshCommand(new CmdRequest(cmd, true), this.multicatServerInfo);
			if (response != null) {

				if (response.getResponseLines() != null) {
					for (String line : response.getResponseLines()) {
						if (line.contains("multicat.ini")) {
							return SupervisorStatus.started;
						}
					}
					return SupervisorStatus.stopped;
				}
			}

		}
		return SupervisorStatus.unknown;
	}

	public DtoAdminInfo getAdminInfo() {

		if (this.adminInfo == null) {

			if (this.multicatServerInfo != null) {

				log.config("getAdminInfo()");
				String path = this.multicatServerInfo.getPath();
				this.adminInfo = new DtoAdminInfo();

				for (String logFilename : this.multicatServerInfo.getLogList()) {
					this.adminInfo.getListLogFilenames().add(this.buildLogFilename(path, logFilename));
				}
				for (String processName : this.multicatServerInfo.getProcessList()) {
					this.adminInfo.getListProcessNames().add(processName);
				}

				this.adminInfo.setSupervisorScriptPathname(path + this.multicatServerInfo.getSupervisorScript());

				this.adminInfo.setUploadSrcPath(this.multicatServerInfo.getUploadTomcatPath());
				this.adminInfo.setUploadTargetPath(this.multicatServerInfo.getUploadMulticatTempPath());

				this.adminInfo.setMulticatVideoPath(this.multicatServerInfo.getMulticatVideoPath());
				this.adminInfo.setMulticatBinPath(this.multicatServerInfo.getPath() + "/bin");

				this.adminInfo.setMaxUploadSizeMo(this.multicatServerInfo.getMaxUploadSizeMo());
			}
		}

		return this.adminInfo;
	}

	// -------------------------------------------- private methods
	// rm /usr/local/multicat-tools/videos/filename.ts
	// si fichier absent exitCode 1 (failure)
	private CmdResponse deleteFile(String filePathname) {

		if (!this.fileExists(filePathname)) {

			CmdResponse response = new CmdResponse();
			String errorMessage = "file " + filePathname + " doesn't exists!";
			response.addErrorLine(errorMessage);
			log.warning(errorMessage);
			response.setSuccess(false);
			return response;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("rm ");
		sb.append(filePathname);

		CmdRequest request = new CmdRequest(sb.toString(), true);
		CmdResponse response = this.executeLinuxSshCommand(request, this.multicatServerInfo);
		if (!response.isSuccess()) {
			log.severe("Error deleting file: " + filePathname + "!");
			if (response.getErrorLines() != null) {

				for (String error : response.getErrorLines()) {
					log.severe(error);
				}
			}
		}
		return response;
	}

	// ls /usr/local/multicat-tools/videos/filename.ts
	// si fichier absent exitCode 2 (failure)
	private boolean fileExists(String filePathname) {

		StringBuilder sb = new StringBuilder();
		sb.append("ls ");
		sb.append(filePathname);

		CmdRequest request = new CmdRequest(sb.toString(), true);
		CmdResponse response = this.executeLinuxSshCommand(request, this.multicatServerInfo);

		return (response.isSuccess());
	}

	private DtoLogFilename buildLogFilename(String path, String logPath) {

		String pathname = path + logPath;
		File file = new File(pathname);
		String filename = file.getName();

		return new DtoLogFilename(filename, pathname);
	}

	private CmdResponse executeLinuxSshCommand(CmdRequest request, MulticatServerInfo serverInfo) {

		CmdResponse response;
		SSHAgent sshAgent = new SSHAgent(serverInfo.getIp(), serverInfo.getUser(), serverInfo.getPwd());
		try {
			if (sshAgent.connect()) {
				log.info(("executeLinuxSshCommand: " + request.getCmd() + " on " + serverInfo.getIp()));
				response = sshAgent.executeCommand(request.getCmd());
			} else {
				log.severe("Failure when connecting to SSH session!");
				response = new CmdResponse();
				response.setSuccess(false);
				response = this.buildFailureResponse("Failure when connecting to SSH session!");
			}
		} catch (Exception e) {
			log.severe("Error while executing command : " + e.getMessage());
			response = this.buildFailureResponse("Error while executing command : " + e.getMessage());
		} finally {
			try {
				sshAgent.logout();
			} catch (Exception ignored) {
			}
		}

		return response;
	}

	private CmdResponse buildFailureResponse(String errorMessage) {
		CmdResponse response = new CmdResponse();
		response.setSuccess(false);
		response.setErrorMessage(errorMessage);
		return response;
	}

	private CmdResponse executeLocalCommand(String cmd) {

		log.info("executeLocalCommand(): cmd: " + cmd);
		CmdResponse cmdResponse = null;

		try {

			final Process process = Runtime.getRuntime().exec(cmd);

			final List<String> standardLines = new ArrayList<>();
			LireInputStream lireStandard = new LireInputStream(process.getInputStream(), standardLines);
			final List<String> errorLines = new ArrayList<>();
			LireInputStream lireErrors = new LireInputStream(process.getErrorStream(), errorLines);

			new Thread(lireStandard).start();
			new Thread(lireErrors).start();

			int exitValue = process.waitFor();

			cmdResponse = new CmdResponse();
			cmdResponse.setExitValue(exitValue);

			// cmdResponse.setSuccess(true);
			if (!standardLines.isEmpty()) {
				for (String line : standardLines) {
					log.fine(": " + line);
					cmdResponse.addResponseLine(line);
				}
			}

			if (!errorLines.isEmpty()) {
				for (String line : errorLines) {
					log.warning("line NOK: " + line);
					cmdResponse.addErrorLine(line);
				}
				// cmdResponse.setSuccess(false);
			}

		} catch (Exception e) {

			e.printStackTrace();
			String errorMessage = "Error in executeLocalCommand(): " + e.toString();
			log.severe(errorMessage);
			cmdResponse = this.buildFailureResponse(errorMessage);

		}

		cmdResponse.setUsedCommand(cmd);
		return cmdResponse;
	}

	// =================================== INNER CLASS
	public static class MulticatServerInfo {

		private String ip;
		private String user;
		private String pwd;
		private String path;
		private List<String> logList;
		private List<String> processList;
		private String supervisorScript;

		// chemin sur le server tomcat : video GWT uploaded by user
		private String uploadTomcatPath;

		// chemin sur le server multicat: video scp uploaded
		private String uploadMulticatTempPath;
		// chemin sur le server multicat: videos multicats
		private String multicatVideoPath;

		// max upload size
		private int maxUploadSizeMo;

		// ---------------------------------- accessors

		public String getUploadMulticatTempPath() {
			return uploadMulticatTempPath;
		}

		public void setUploadMulticatTempPath(String uploadMulticatTempPath) {
			this.uploadMulticatTempPath = uploadMulticatTempPath;
		}

		public String getMulticatVideoPath() {
			return multicatVideoPath;
		}

		public void setMulticatVideoPath(String multicatVideoPath) {
			this.multicatVideoPath = multicatVideoPath;
		}

		public String getUploadTomcatPath() {
			return uploadTomcatPath;
		}

		public void setUploadTomcatPath(String uploadSrcPath) {
			this.uploadTomcatPath = uploadSrcPath;
		}

		public String getSupervisorScript() {
			return supervisorScript;
		}

		public void setSupervisorScript(String supervisorScript) {
			this.supervisorScript = supervisorScript;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getPwd() {
			return pwd;
		}

		public void setPwd(String pwd) {
			this.pwd = pwd;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public List<String> getLogList() {
			return logList;
		}

		public void setLogList(List<String> logList) {
			this.logList = logList;
		}

		public List<String> getProcessList() {
			return processList;
		}

		public void setProcessList(List<String> processList) {
			this.processList = processList;
		}

		public MulticatServerInfo() {

		}

		public void setMaxUploadSizeMo(int maxUpload) {
			this.maxUploadSizeMo = maxUpload;
		}

		public int getMaxUploadSizeMo() {
			return this.maxUploadSizeMo;
		}
	}

	private static class LireInputStream implements Runnable {

		private final InputStream inputStream;
		private final List<String> lines;

		private LireInputStream(InputStream in, List<String> lines) {
			this.inputStream = in;
			this.lines = lines;
		}

		@Override
		public void run() {

			BufferedReader bf = new BufferedReader(new InputStreamReader(this.inputStream));

			try {

				String line;
				while ((line = bf.readLine()) != null) {

					this.lines.add(line);
				}

			} catch (Exception e) {
				log.severe(e.getMessage());
			} finally {
				try {
					if (bf != null) {
						bf.close();
					}
				} catch (IOException ignored) {
				}

			}

		}

	}

}
