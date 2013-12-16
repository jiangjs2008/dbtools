package com.dbm.client.db;

import java.util.HashMap;

import com.dbm.client.error.BaseExceptionWrapper;

public class DbClient4OracleImpl extends DbClient4DefaultImpl {

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
