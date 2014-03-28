/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;

import com.dbm.common.error.BaseExceptionWrapper;
import com.sun.rowset.CachedRowSetImpl;

/**
 * 缺省数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4OracleImpl extends DbClient {

	protected Statement stmt = null;
	protected ResultSet rs = null;
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
			_isConnected = true;
	           
		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	void close() {
		_isConnected = false;
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (_dbConn != null) {
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
	public ResultSet directQuery(String action) {
		// 查询数据，此处只需考虑分页，不需考虑更新
		try {
			stmt = _dbConn.createStatement();
			rs = stmt.executeQuery(action);
			return rs;
		} catch (SQLException exp) {
			logger.error(exp);
		}
		return null;
	}

	@Override
	public boolean directExec(String action) {
		// single update sql
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException exp) {
			logger.error(exp);
		}
		try {
			stmt = _dbConn.createStatement();
			int rslt = stmt.executeUpdate(action);
			if (rslt == 0) {
				// TODO msg
			}

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
		return true;
	}

	// 当前页数
	protected int currPage = 0;

	public int supportsPageScroll() {
		return 2;
	}

	/**
	 * 取得当前页数
	 *
	 * @return int
	 */
	public int getCurrPageNum() {
		return currPage;
	}
	
	public ResultSet getPreviousPage() {
		try {
			if (allRowSet.previousPage()) {
				return allRowSet;
			}
			return null;
		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	
	public ResultSet getNextPage() {
		try {
			if (allRowSet.nextPage()) {
				return allRowSet;
			}
			return null;
		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}
	
	@Override
	public ResultSet getPage(int pageNum, int rowIdx, int pageSize) {
		return null;
	}

	@Override
	public ResultSet executeQuery(String tblName) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
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
					_size = irs.getInt(1);
					logger.debug("TBL: " + tblName + " size: " + _size);
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
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(action);

			allRowSet = new CachedRowSetImpl();
			//allRowSet.setMaxRows(500);
			allRowSet.setPageSize(500);
			allRowSet.populate(rs);
			return allRowSet;

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	public void executeUpdate(String tblName, HashMap<Integer, HashMap<Integer, String>> params,
			ArrayList<HashMap<Integer, String>> addParams, ArrayList<Integer> delParams) {
		int colNum = 0;
		String colValue = null;
		try {
			// 更新
			if (params != null && params.size() > 0) {
				allRowSet.setTableName(tblName);
				int rowNum = 0;
				for (Iterator<Entry<Integer, HashMap<Integer, String>>> iter = params.entrySet().iterator(); iter.hasNext(); ) {

					Entry<Integer, HashMap<Integer, String>> entry = iter.next();
					rowNum = entry.getKey();

					// update data
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

			// 追加
			if (addParams != null && addParams.size() > 0) {
				allRowSet.setTableName(tblName);

				for (HashMap<Integer, String> addLine : addParams) {
					// insert data
					allRowSet.moveToInsertRow();

					for (Iterator<Entry<Integer, String>> iter2 = addLine.entrySet().iterator(); iter2.hasNext(); ) {
						
						Entry<Integer, String> entry2 = iter2.next();
						colNum = entry2.getKey();
						colValue = entry2.getValue();

						allRowSet.updateString(colNum, colValue);
					}

					allRowSet.insertRow();
					allRowSet.moveToCurrentRow();
				}
			}

			// 删除
			if (delParams != null && delParams.size() > 0) {
				for (Integer rowNum : delParams) {
					allRowSet.absolute(rowNum);
					allRowSet.deleteRow();
				}
			}

			getConnection().setAutoCommit(false);
			allRowSet.acceptChanges(_dbConn);

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

}
