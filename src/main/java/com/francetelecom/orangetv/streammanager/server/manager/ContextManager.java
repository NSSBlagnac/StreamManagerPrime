package com.francetelecom.orangetv.streammanager.server.manager;

import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.francetelecom.orangetv.streammanager.server.dao.DatabaseManager;
import com.francetelecom.orangetv.streammanager.server.manager.AdminManager.MulticatServerInfo;
import com.francetelecom.orangetv.streammanager.server.manager.ProfileManager.ProfilCredential;
import com.francetelecom.orangetv.streammanager.server.manager.ProtectionManager.ProtectedStreamDatas;
import com.francetelecom.orangetv.streammanager.server.service.IMyServices;

/**
 * Manage load and unload of context
 * 
 * @author ndmz2720
 *
 */
public class ContextManager implements ServletContextListener, IMyServices {

	private static final Logger log = Logger.getLogger(ContextManager.class.getName());

	private static final String KEY_JDBC_DRIVER = "jdbcDriver";
	private static final String KEY_DB_URL = "bddUrl";
	private static final String KEY_DB_USER = "bddUser";
	private static final String KEY_DB_PWD = "bddPwd";

	private static final String KEY_LOGIN_MANAGER = "login_manager";
	private static final String KEY_PWD_MANAGER = "pwd_manager";

	private static final String KEY_LOGIN_ADMIN = "login_admin";
	private static final String KEY_PWD_ADMIN = "pwd_admin";

	private static final String KEY_MODE_TEST = "modeTest";

	@Override
	public void contextDestroyed(ServletContextEvent event) {

		log.info("===========================================");
		log.info(" ..........CONTEXT StreamManager DESTROYED");
		log.info("===========================================");

		try {
			DatabaseManager.get().unregister();
		} catch (SQLException e) {
			log.severe("Database.unregister in error: " + e.getMessage());
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {

		log.info("===========================================");
		log.info(" CONTEXT INITIALIZED  StreamManager ......");
		log.info("===========================================");

		ServletContext servletContext = event.getServletContext();

		// ============================
		// CREDENTIALS
		// ============================
		ProfileManager.get().setAdminCredential(
				new ProfilCredential(servletContext.getInitParameter(KEY_LOGIN_ADMIN), servletContext
						.getInitParameter(KEY_PWD_ADMIN)));
		ProfileManager.get().setQualifCredential(
				new ProfilCredential(servletContext.getInitParameter(KEY_LOGIN_MANAGER), servletContext
						.getInitParameter(KEY_PWD_MANAGER)));

		// ============================
		// BDD
		// ============================
		String jdbcDriver = servletContext.getInitParameter(KEY_JDBC_DRIVER);
		String dbUrl = servletContext.getInitParameter(KEY_DB_URL);
		String dbUser = servletContext.getInitParameter(KEY_DB_USER);
		String dbPwd = servletContext.getInitParameter(KEY_DB_PWD);

		log.info("init(): " + jdbcDriver + " - " + dbUrl);
		if (jdbcDriver != null) {
			DatabaseManager.get().init(jdbcDriver, dbUrl, dbUser, dbPwd);
		}

		// ========================
		// Multicat server info
		// ========================
		String test = servletContext.getInitParameter(KEY_MODE_TEST);
		boolean modeTest = (test == null) ? false : Boolean.parseBoolean(test);

		MulticatServerInfo multicatServerInfo = ServletManager.get().buildMulticatServerInfo(servletContext);
		ProtectedStreamDatas protectedStreamDatas = ServletManager.get().buildProtectedStreamDatas(servletContext);
		AdminManager.get().init(multicatServerInfo);
		VideoManager.get().init(multicatServerInfo);
		StreamManager.get().init(multicatServerInfo, protectedStreamDatas, modeTest);

	}

}
