/*
 * Created on 2007/03/13
 */
package com.dbm.client.db;

import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;

import com.dbm.client.action.data.PageJumpActionListener;
import com.dbm.client.error.BaseExceptionWrapper;
import com.dbm.client.error.WarningException;
import com.dbm.client.ui.AppUIAdapter;
import com.sun.rowset.CachedRowSetImpl;

/**
 * 缺省数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4DefaultImpl extends DbClient {

	protected Statement stmt = null;
	protected ResultSet rs = null;
	protected CachedRowSet allRowSet = null;
	protected PageJumpActionListener pageAction = null;
	private int rowCnt = 0;

	@Override
	public String getTableDataAt(int rowNum, int colNum) {
		if (allRowSet != null) {
			try {
				allRowSet.absolute(rowNum);
				return allRowSet.getString(colNum);
			} catch (Exception exp) {
				throw new BaseExceptionWrapper(exp);
			}
		}
		return null;
	}

	public void setCachedRowSetImpl(CachedRowSet crs) {
		allRowSet = crs;
	}

	@Override
	public void start(String[] args) {
		_dbArgs = args;
		// connect to db 
		try {
			Class.forName(_dbArgs[0]);
			DriverManager.setLoginTimeout(1800);
			if (_dbArgs[2] == null || _dbArgs[2].isEmpty()) {
				_dbConn = DriverManager.getConnection(_dbArgs[1]);
			} else {
				_dbConn = DriverManager.getConnection(_dbArgs[1], _dbArgs[2], _dbArgs[3]);
			}

			pageAction = (PageJumpActionListener) AppUIAdapter.getUIObj(AppUIAdapter.PageAction);
			isConnected = true;

			logger.debug("驱动名: " + _dbArgs[0]);
			DatabaseMetaData dmd = _dbConn.getMetaData();
			if (dmd.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY)) {
				logger.debug("该结果集 光标只能向前移动");
			} else if (dmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
				logger.debug("该结果集 光标可滚动但通常受底层数据更改影响");
			} else if (dmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
				logger.debug("该结果集 光标可滚动但通常不受底层数据更改影响");
			}

			if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
				logger.debug("该结果集 可更新 光标只能向前移动");
			} else if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
				logger.debug("该结果集 可更新 光标可滚动但通常受底层数据更改影响");
			} else if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
				logger.debug("该结果集 可更新 光标可滚动但通常不受底层数据更改影响");
			} else if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
				logger.debug("该结果集 不可更新 光标只能向前移动");
			} else if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
				logger.debug("该结果集 不可更新 光标可滚动但通常受底层数据更改影响");
			} else if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
				logger.debug("该结果集 不可更新 光标可滚动但通常不受底层数据更改影响");
			}

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
		createStatement();
	}

	protected void createStatement() {
		try {
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	void close() {
		isConnected = false;
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
			if (stmt != null && !stmt.isClosed()) {
				stmt.close();
			}
			if (_dbConn != null && !_dbConn.isClosed()) {
				_dbConn.close();
			}
			if (allRowSet != null) {
				allRowSet.close();
			}
		} catch (SQLException exp) {
			logger.error(exp);
		}
	}

	@Override
	public boolean executeScript(String action) {
		int sqlType = -1;
		if (action.length() <= 6) {
			throw new WarningException(20002);
		}
		String typeStr = action.substring(0, 6);
		// 判断SQL类型
		if ("select".equalsIgnoreCase(typeStr)) {
			sqlType = 1;
		} else if ("create".equalsIgnoreCase(typeStr) || "update".equalsIgnoreCase(typeStr)
				|| "insert".equalsIgnoreCase(typeStr) || "delete".equalsIgnoreCase(typeStr)
				|| "drop".equalsIgnoreCase(typeStr.substring(0, 4)) ) {
			sqlType = 2;
		} else {
			throw new WarningException(20001);
		}

		if (sqlType == 1) {
			// 查询数据，此处只需考虑分页，不需考虑更新


		} else if (sqlType == 2) {
			// single update sql
			try {
				if (rs != null && !rs.isClosed()) {
					rs.close();
				}
			} catch (SQLException exp) {
				logger.error(exp);
			}
			try {
				int rslt = stmt.executeUpdate(action);
				if (rslt == 0) {
					// TODO msg
				}

			} catch (Exception exp) {
				throw new BaseExceptionWrapper(exp);
			}
		}
		return true;
	}

	@Override
	public void executeQuery(String tblName) {
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
		} catch (SQLException exp) {
			logger.error(exp);
		}
		try {
			// 先取得该表的数据总件数
			Statement istmt = null;
			ResultSet irs = null;
			try {
				istmt = _dbConn.createStatement();
				irs = istmt.executeQuery("select count(1) from " + tblName);
				if (irs.next()) {
					rowCnt = irs.getInt(1);
					pageAction.setAllRowSize(rowCnt);
					logger.debug("TBL: " + tblName + " size: " + rowCnt);
				}
			} catch (SQLException exp) {
				throw new BaseExceptionWrapper(exp);
			} finally {
				try {
					if (irs != null) {
						irs.close();
					}
					if (istmt != null) {
						istmt.close();
					}
				} catch (SQLException exp) {
					logger.error(exp);
				}
			}

			// 查询表数据
			String action = "select * from " + tblName;
			rs = stmt.executeQuery(action);

			allRowSet = new CachedRowSetImpl();
			allRowSet.setMaxRows(500);
			allRowSet.setPageSize(500);
			allRowSet.populate(rs, 1);

			// 无论分页与否，全都交由PageJumpActionListener处理
			pageAction.displayTableData(allRowSet, rs, false);

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
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

			for (Iterator<Entry<Integer, HashMap<Integer, String>>> iter = params.entrySet().iterator(); iter.hasNext(); ) {

				Entry<Integer, HashMap<Integer, String>> entry = iter.next();
				rowNum = entry.getKey();
				if (rowCnt < rowNum) {
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
			allRowSet.acceptChanges(_dbConn);
			params.clear();
			return true;
		} catch (SyncProviderException exp) {
			SyncResolver resolver = exp.getSyncResolver();
			try {
				// value in crs
				Object crsValue = null;
				// value in the SyncResolver object
				Object resolverValue = null;
				// value to be persistent
				Object resolvedValue = null;

				while (resolver.nextConflict()) {
					int status = resolver.getStatus();
					logger.debug("resolver status: " + status);
					if (status == SyncResolver.UPDATE_ROW_CONFLICT) {
						int row = resolver.getRow();
						allRowSet.absolute(row);
						int colCount = allRowSet.getMetaData().getColumnCount();
						for (int j = 1; j <= colCount; j++) {
							if (resolver.getConflictValue(j) != null) {
								// value in crs
								crsValue = allRowSet.getObject(j);
								logger.debug("value in crs: " + crsValue);
								// value in the SyncResolver object
								resolverValue = resolver.getConflictValue(j);
								logger.debug("value in the SyncResolver object: " + resolverValue);
								// ...
								// compare crsValue and resolverValue
								// to determine the value to be persistent
								//resolvedValue = crsValue;
								//resolver.setResolvedValue(j, resolvedValue);
							}
						}
					}
				}
			} catch (SQLException exp2) {
				logger.error(exp2);
			}

			throw new BaseExceptionWrapper(exp);
		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	public boolean executeDelete(String tblName, int... rows) {
		if (rows.length == 0) {
			return false;
		}
		try {
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
