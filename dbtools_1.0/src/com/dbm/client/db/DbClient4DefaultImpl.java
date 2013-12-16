/*
 * Created on 2007/03/13
 */
package com.dbm.client.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.sql.rowset.CachedRowSet;

import com.dbm.client.error.BaseExceptionWrapper;
import com.dbm.client.util.TableUtil;
import com.sun.rowset.CachedRowSetImpl;

/**
 * 缺省数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4DefaultImpl extends DbClient {

	protected String[] dbArgs = null;
	
	private Connection dbConn = null;

	@Override
	public Connection getConnection() {
		return dbConn;
	}

	protected CachedRowSet allRowSet = null;

	@Override
	public String getTableDataAt(int rowNum, int colNum) {
		if (allRowSet != null) {
			if (allRowSet.size() < rowNum) {
				return null;
			}
			try {
				allRowSet.absolute(rowNum);
				return allRowSet.getString(colNum);
			} catch (Exception exp) {
				throw new BaseExceptionWrapper(exp);
			}
		}
		return null;
	}

	@Override
	public void start(String[] args) {
		dbArgs = args;
		// connect to db 
		try {
			Class.forName(dbArgs[0]);
			DriverManager.setLoginTimeout(1800);
			if (dbArgs[2] == null || dbArgs[2].isEmpty()) {
				dbConn = DriverManager.getConnection(dbArgs[1]);
			} else {
				dbConn = DriverManager.getConnection(dbArgs[1], dbArgs[2], dbArgs[3]);
			}
			isConnected = true;
		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	void close() {
		isConnected = false;
		if (dbConn != null) {
			try {
				dbConn.close();
			} catch (SQLException exp) {
				logger.error(exp);
			}
		}
		if (allRowSet != null) {
			try {
				allRowSet.close();
			} catch (SQLException exp) {
				logger.error(exp);
			}
		}
	}

	@Override
	public Object execute(int sqlType, String action) {

		if (sqlType == 9) {
			// query sql
			action = "select * from " + action;
			executeScript(action);
		}
		return null;
	}

	@Override
	public boolean executeScript(String action) {
		int sqlType = -1;
		String typeStr = action.substring(0, 6);
		// 判断SQL类型
		if ("select".equalsIgnoreCase(typeStr)) {
			sqlType = 1;
		} else if ("create".equalsIgnoreCase(typeStr) || "update".equalsIgnoreCase(typeStr)
				|| "insert".equalsIgnoreCase(typeStr) || "delete".equalsIgnoreCase(typeStr)
				|| "drop".equalsIgnoreCase(typeStr.substring(0, 4)) ) {
			sqlType = 2;
		} else {
			return false;
		}

		if (sqlType == 1) {
			// query sql
			Statement stmt = null;
			try {
				stmt = dbConn.createStatement();
				ResultSet rs = stmt.executeQuery(action);

				allRowSet = new CachedRowSetImpl();
				allRowSet.populate(rs);

				TableUtil.setTableData(allRowSet, false);

			} catch (Exception exp) {
				throw new BaseExceptionWrapper(exp);
			} finally {
//				if (stmt != null) {
//					try {
//						stmt.close();
//					} catch (SQLException exp) {
//						exp.printStackTrace();
//					}
//				}
			}

		} else if (sqlType == 2) {
			// single update sql
			Statement stmt = null;
			try {
				stmt = dbConn.createStatement();
				int rs = stmt.executeUpdate(action);
				if (rs == 0) {
					// TODO msg
				}

			} catch (Exception exp) {
				throw new BaseExceptionWrapper(exp);
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException exp) {
						logger.error(exp);
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean executeUpdate(String tblName, HashMap<Integer, HashMap<Integer, String>> params) {
		if (params.size() == 0) {
			return false;
		}

		int rowNum = 0;
		int colNum = 0;
		String colValue = null;
		try {
			allRowSet.setTableName(tblName);
			for (Iterator<Entry<Integer, HashMap<Integer, String>>> iter = params.entrySet().iterator(); iter.hasNext(); ) {

				Entry<Integer, HashMap<Integer, String>> entry = iter.next();
				rowNum = entry.getKey();
				if (allRowSet.size() < rowNum) {
					// if is insert data
					allRowSet.moveToInsertRow();

					HashMap<Integer, String> rowMap = entry.getValue();
					for (Iterator<Entry<Integer, String>> iter2 = rowMap.entrySet().iterator(); iter2.hasNext(); ) {

						Entry<Integer, String> entry2 = iter2.next();
						colNum = entry2.getKey();
						colValue = entry2.getValue();

						allRowSet.updateString(colNum, colValue);
					}

					allRowSet.insertRow();
					allRowSet.moveToCurrentRow();

				} else {
					// if is update data
					allRowSet.absolute(rowNum);
	
					HashMap<Integer, String> rowMap = entry.getValue();
					for (Iterator<Entry<Integer, String>> iter2 = rowMap.entrySet().iterator(); iter2.hasNext(); ) {
	
						Entry<Integer, String> entry2 = iter2.next();
						colNum = entry2.getKey();
						colValue = entry2.getValue();
	
						allRowSet.updateString(colNum, colValue);
					}
					
					allRowSet.updateRow();
				}
			}
			getConnection().setAutoCommit(false);
			allRowSet.acceptChanges(getConnection());

			params.clear();
			return true;
		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	public boolean executeDelete(String tblName, int... rows) {

		try {
			allRowSet.setTableName(tblName);
			for (int rowNum : rows) {
				allRowSet.absolute(rowNum);
				allRowSet.deleteRow();
			}
			getConnection().setAutoCommit(false);
			allRowSet.acceptChanges(getConnection());

			return true;
		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}
}
