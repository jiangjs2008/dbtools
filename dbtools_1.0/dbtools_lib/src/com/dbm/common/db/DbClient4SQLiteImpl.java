/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.dbm.common.error.BaseExceptionWrapper;

/**
 * SQLCipher for Android
 *
 * @author JiangJusheng
 */
public class DbClient4SQLiteImpl extends DbClient4DefaultImpl {

	@Override
	public int supportsPageScroll() {
		return 3;
	}
	
	private String _tblName = null;
	@Override
	public ResultSet getPage(int pageNum, int rowIdx, int pageSize) {
		currPage = pageNum;

		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException exp) {
			logger.error(exp);
		}
		if (pageNum == 1 && rowIdx == 1) {
			rowIdx = 0;
		}
		try {
			// 查询表数据
			String action = "select *,rowid from " + _tblName + " limit " + rowIdx + "," + pageSize;
			stmt = _dbConn.createStatement();
			stmt.execute(action);
			rs = stmt.getResultSet();
			return rs;

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	private ArrayList<String> rawidList = null;
	private ArrayList<String> conNameList = null;

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
		_tblName = tblName;
		Statement istmt = null;
		ResultSet irs = null;

		// 先取得该表的rawid
		rawidList = new ArrayList<String>();
		try {
			istmt = _dbConn.createStatement();
			irs = istmt.executeQuery("select rowid from " + tblName);
			while (irs.next()) {
				rawidList.add(irs.getString(1));
			}
			_size = rawidList.size();
			logger.debug("TBL: " + tblName + " size: " + _size);
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

		try {
			// 查询表数据
			String action = "select * from " + tblName + " limit 0,500";
			stmt = _dbConn.createStatement();
			stmt.execute(action);
			rs = stmt.getResultSet();

			conNameList = new ArrayList<String>();
			_rsmd = rs.getMetaData();
			for (int k = 1, colCount = _rsmd.getColumnCount() + 1; k < colCount; k ++) {
				conNameList.add(_rsmd.getColumnName(k));
			}
			return rs;

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	private ResultSetMetaData _rsmd = null;

	@Override
	public void executeUpdate(String tblName, HashMap<Integer, HashMap<Integer, String>> params,
			ArrayList<HashMap<Integer, String>> addParams, ArrayList<Integer> delParams) {
		int colNum = 0;
		String colValue = null;
		try {
			// 更新
			if (params != null && params.size() > 0) {
				StringBuilder sqlStrsAll = new StringBuilder();

				int rowNum = 0;
				boolean isColEnd = false;
				for (Iterator<Entry<Integer, HashMap<Integer, String>>> iter = params.entrySet().iterator(); iter.hasNext(); ) {
					sqlStrsAll = new StringBuilder();
					sqlStrsAll.append("update ");
					sqlStrsAll.append(tblName);
					sqlStrsAll.append(" set ");

					Entry<Integer, HashMap<Integer, String>> entry = iter.next();
					rowNum = entry.getKey();
					HashMap<Integer, String> rowMap = entry.getValue();

					isColEnd = false;
					for (Iterator<Entry<Integer, String>> iter2 = rowMap.entrySet().iterator(); iter2.hasNext(); ) {
						if (isColEnd) {
							sqlStrsAll.append(", ");
						}
						isColEnd = true;

						Entry<Integer, String> entry2 = iter2.next();
						sqlStrsAll.append(conNameList.get(entry2.getKey() - 1));
						sqlStrsAll.append(" = '");
						sqlStrsAll.append(entry2.getValue());
						sqlStrsAll.append("'");
					}

					sqlStrsAll.append(" where rowid = ");
					sqlStrsAll.append(rawidList.get(rowNum - 1));
					
					stmt.executeUpdate(sqlStrsAll.toString());
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

		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}
}
