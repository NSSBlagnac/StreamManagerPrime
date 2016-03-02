package com.francetelecom.orangetv.streammanager.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.francetelecom.orangetv.streammanager.server.dao.DatabaseManager;

public class DatabaseManagerTest {

	@Test
	public void testGetConnection() {

		Connection con = null;
		try {
			con = DatabaseManager.get().getConnection();
			assertNotNull("con cannot be null!!", con);
		} catch (SQLException e) {
			fail(e.getMessage());
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException ignored) {
					ignored.printStackTrace();
				}
			}
		}
	}

}
