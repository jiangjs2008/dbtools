package com.dbm.client.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.dbm.client.error.BaseExceptionWrapper;

public class DbClient4OracleImpl extends DbClient4DefaultImpl {

	@Override
	protected void createStatement() {
		try {
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	public boolean executeUpdate(String tblName, HashMap<Integer, HashMap<Integer, String>> params) {
		try {
			allRowSet.setTableName(tblName);
		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
		return super.executeUpdate(tblName, params);
	}
}
