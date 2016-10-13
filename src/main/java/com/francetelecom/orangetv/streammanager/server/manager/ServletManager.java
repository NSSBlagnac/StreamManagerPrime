package com.francetelecom.orangetv.streammanager.server.manager;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.francetelecom.orangetv.streammanager.server.manager.AdminManager.MulticatServerInfo;
import com.francetelecom.orangetv.streammanager.server.manager.ProtectionManager.AddressStream;
import com.francetelecom.orangetv.streammanager.server.manager.ProtectionManager.ProtectedStreamDatas;
import com.francetelecom.orangetv.streammanager.shared.dto.TripletDvb;
import com.francetelecom.orangetv.streammanager.shared.util.ValueHelper;

public class ServletManager {

	private static final Logger log = Logger.getLogger(ServletManager.class.getName());

	// server multicat
	private static final String KEY_MULTICAT_IP = "multicat_ip";
	private static final String KEY_MULTICAT_USER = "multicat_user";
	private static final String KEY_MULTICAT_PWD = "multicat_pwd";
	private static final String KEY_MULTICAT_PATH = "multicat_path";
	private static final String KEY_MULTICAT_LOGS = "multicat_logs";
	private static final String KEY_MULTICAT_PROCESS = "multicat_process";
	private static final String KEY_MULTICAT_SCRIPT = "multicat_script";
	private static final String KEY_MULTICAT_UPLOAD_PATHS = "multicat_upload_paths";

	private static final String KEY_PROTECTED_STREAM_LCN = "protected_stream_lcn";
	private static final String KEY_PROTECTED_STREAM_USI = "protected_stream_usi";
	private static final String KEY_PROTECTED_STREAM_TRIPLET = "protected_stream_triplet";
	private static final String KEY_PROTECTED_STREAM_ADDRESS = "protected_stream_address";

	private static final String KEY_MAX_LENGTH_UPLOAD_MO = "maxLenghtUploadMo";

	private static ServletManager instance;

	public static ServletManager get() {
		if (instance == null) {
			instance = new ServletManager();
		}
		return instance;
	}

	private ServletManager() {
	}

	// ----------------------------------------------------- public methods
	public ProtectedStreamDatas buildProtectedStreamDatas(ServletContext servletContext) {

		ProtectedStreamDatas protectedStreamDatas = new ProtectedStreamDatas();

		// plage de valeurs LCN pour protected stream
		String lcns = servletContext.getInitParameter(KEY_PROTECTED_STREAM_LCN);
		List<String> listLcns = ValueHelper.buildListItems(lcns);
		if (listLcns != null && listLcns.size() == 2) {
			protectedStreamDatas.setLcnInf(ValueHelper.getIntValue(listLcns.get(0), -1));
			protectedStreamDatas.setLcnSup(ValueHelper.getIntValue(listLcns.get(1), -1));
		}

		// plage de valeurs USI pour protected stream
		String usis = servletContext.getInitParameter(KEY_PROTECTED_STREAM_USI);
		List<String> listUsis = ValueHelper.buildListItems(usis);
		if (listUsis != null && listUsis.size() == 2) {
			protectedStreamDatas.setUsiInf(ValueHelper.getIntValue(listUsis.get(0), -1));
			protectedStreamDatas.setUsiSup(ValueHelper.getIntValue(listUsis.get(1), -1));
		}

		// plage de valeurs TripletDVB pour protected stream
		String triplets = servletContext.getInitParameter(KEY_PROTECTED_STREAM_TRIPLET);
		List<String> listTriplets = ValueHelper.buildListItems(triplets);
		if (listTriplets != null && listTriplets.size() == 2) {
			protectedStreamDatas.setTripletInf(new TripletDvb(listTriplets.get(0)));
			protectedStreamDatas.setTripletSup(new TripletDvb(listTriplets.get(1)));
		}

		// plage de valeurs Address pour protected stream
		String addresses = servletContext.getInitParameter(KEY_PROTECTED_STREAM_ADDRESS);
		List<String> listAdresses = ValueHelper.buildListItems(addresses);
		if (listAdresses != null && listAdresses.size() == 2) {
			protectedStreamDatas.setAddressInf(new AddressStream(listAdresses.get(0)));
			protectedStreamDatas.setAddressSup(new AddressStream(listAdresses.get(1)));
		}

		return protectedStreamDatas;
	}

	public MulticatServerInfo buildMulticatServerInfo(ServletContext servletContext) {

		MulticatServerInfo multicatServerInfo = new MulticatServerInfo();
		multicatServerInfo.setIp(servletContext.getInitParameter(KEY_MULTICAT_IP));
		multicatServerInfo.setUser(servletContext.getInitParameter(KEY_MULTICAT_USER));
		multicatServerInfo.setPwd(servletContext.getInitParameter(KEY_MULTICAT_PWD));
		multicatServerInfo.setPath(servletContext.getInitParameter(KEY_MULTICAT_PATH));
		multicatServerInfo.setSupervisorScript(servletContext.getInitParameter(KEY_MULTICAT_SCRIPT));

		String logs = servletContext.getInitParameter(KEY_MULTICAT_LOGS);
		multicatServerInfo.setLogList(ValueHelper.buildListItems(logs));

		String process = servletContext.getInitParameter(KEY_MULTICAT_PROCESS);
		multicatServerInfo.setProcessList(ValueHelper.buildListItems(process));

		multicatServerInfo.setMulticatVideoPath(servletContext.getInitParameter(KEY_MULTICAT_UPLOAD_PATHS));

		Object directory = servletContext.getAttribute("javax.servlet.context.tempdir");

		final File tempDir = (directory != null && directory instanceof File) ? (File) directory : new File("/tempo");
		if (!tempDir.exists()) {
			tempDir.mkdir();
		}
		multicatServerInfo.setUploadTomcatPath(tempDir.getAbsolutePath() + File.separator);

		String maxUploadLengthMo = servletContext.getInitParameter(KEY_MAX_LENGTH_UPLOAD_MO);
		multicatServerInfo.setMaxUploadSizeMo(ValueHelper.getIntValue(maxUploadLengthMo, 100));

		log.info("buildMulticatServerInfo(): " + multicatServerInfo.getIp() + " - " + multicatServerInfo.getPath()
				+ " - " + logs + " - tomcat temp dir: " + multicatServerInfo.getUploadTomcatPath());
		return multicatServerInfo;
	}

}
