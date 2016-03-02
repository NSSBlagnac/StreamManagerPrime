package com.francetelecom.orangetv.streammanager.server.dao;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseManager {

	private static final Logger log = Logger.getLogger(DatabaseManager.class.getName());

	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";// "org.hsqldb.jdbc.JDBCDriver";
	private static final String DB_URL = "jdbc:mysql://10.185.111.250/MyTVTools_dev"; // "jdbc:hsqldb:hsql://localhost/testDB";

	// Database credentials (FIXME ne pas laisser en clair!!)
	private static final String USER = "test"; // "SA";
	private static final String PASS = "univers"; // "";

	private String jdbcDriver;
	private String bddUrl;
	private String user;
	private String pwd;

	private boolean init = false;

	private static DatabaseManager instance;

	public static final DatabaseManager get() {
		if (instance == null) {
			instance = new DatabaseManager();
		}
		return instance;
	}

	private DatabaseManager() {

	}

	public void init(String jdbcDriver, String bddUrl, String user, String pwd) {

		this.jdbcDriver = jdbcDriver;
		this.bddUrl = bddUrl;
		this.user = user;
		this.pwd = pwd;

		// STEP 2: Register JDBC driver
		try {
			Class.forName(this.jdbcDriver);
			this.init = true;
		} catch (ClassNotFoundException e) {
			log.severe("init Class.forName( " + this.jdbcDriver + "): " + e.getMessage());
		}

	}

	public void unregister() throws SQLException {

		Driver driver = DriverManager.getDriver(this.bddUrl);
		if (this.init && driver != null) {
			log.info("Deregistering JDBJ driver " + driver.toString());
			this.init = false;
			DriverManager.deregisterDriver(driver);
		}

	}

	private void initDefault() {
		this.init(JDBC_DRIVER, DB_URL, USER, PASS);
	}

	public Connection getConnection() throws SQLException {

		if (!init) {
			this.initDefault();
		}

		// STEP 3: Open a connection
		try {
			log.finest("getConnection()");
			return DriverManager.getConnection(this.bddUrl, this.user, this.pwd);
		} catch (SQLException ex) {
			log.severe("getConnection(): " + ex.getMessage());
			throw ex;
		}
	}
}
