package com.francetelecom.orangetv.streammanager.server.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.streammanager.server.model.IDbEntry;

public abstract class AbstractDao<T extends IDbEntry> {

	// ----------------------------------- abstract methods
	protected abstract T buildDbEntry(ResultSet rs);

	protected abstract Logger getLog();

	// -------------------------------- protected methods

	protected void close(ResultSet rs, Statement stmt, Connection con) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException ignored) {
			// NA
		}

		try {
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException ignored) {
			// NA
		}

		try {
			if (con != null) {
				con.close();
				getLog().finest("...database closed.");
			}

		} catch (SQLException ignored) {
			ignored.printStackTrace();
		}
	}

	protected String formatSqlString(String toFormat) {
		if (toFormat != null) {
			toFormat = toFormat.replaceAll("\'", "\'\'");
		}
		return toFormat;
	}

	protected int count(String sql) {

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection();
			stmt = con.createStatement();

			getLog().fine("sql: " + sql);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(rs, stmt, con);
		}

		return 0;

	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param videoId
	 * @return
	 */
	protected T getById(int id, MessageFormat sqlPattern) {
		if (id < 0) {
			return null;
		}

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection();
			stmt = con.createStatement();

			String sql = sqlPattern.format(new Object[] { id });
			getLog().fine("sql: " + sql);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return this.buildDbEntry(rs);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(rs, stmt, con);
		}

		return null;

	}

	protected boolean deleteEntry(int id, MessageFormat sqlPattern) {

		if (id < 0) {
			return false;
		}

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection();
			stmt = con.createStatement();

			String sql = sqlPattern.format(new Object[] { id });
			getLog().fine("sql: " + sql);
			return stmt.executeUpdate(sql) == 1;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(rs, stmt, con);
		}

		return false;

	}

	protected List<T> listEntry(String sql) throws SQLException {

		List<T> list = new ArrayList<T>();

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection();
			stmt = con.createStatement();

			getLog().fine("sql: " + sql);
			rs = stmt.executeQuery(sql);

			while (rs.next()) {

				T entry = this.buildDbEntry(rs);
				if (entry != null) {
					list.add(entry);
				}
			}

		} finally {
			this.close(rs, stmt, con);
		}

		return list;
	}

	protected int getMaxId(String sql) {

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection();
			stmt = con.createStatement();

			getLog().fine("sql: " + sql);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 10;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(rs, stmt, con);
		}

		return 0;

	}

	protected boolean update(String sql) {

		getLog().fine("update sql: " + sql);

		boolean success = true;

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection();
			stmt = con.createStatement();

			int result = stmt.executeUpdate(sql);
			success = result == 1;

		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		} finally {
			this.close(rs, stmt, con);
		}

		return success;
	}

}