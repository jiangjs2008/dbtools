/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

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

import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbm.common.error.BaseExceptionWrapper;
import com.dbm.common.util.HttpUtil;
import com.sun.rowset.CachedRowSetImpl;

/**
 * 缺省数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4HttpWrapperImpl extends DbClient {

	protected Statement stmt = null;
	protected ResultSet rs = null;
	CachedRowSet allRowSet = null;

	@Override
	public String getTableDataAt(int rowNum, int colNum) {
		if (allRowSet != null && rowNum < size()) {
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
	public boolean start(String[] args) {
		_dbArgs = args;
		// connect to db 
		try {
			String rs = HttpUtil.getBody4Get(args[4]);
			if (rs == null) {
				
				return false;
			}
			JSONObject rsObj = JSON.parseObject(rs);
			if (rsObj.getIntValue("ecd") == 0) {
				_isConnected = true;
			} else {
				
			}

			logger.info(rsObj.getString("procInfo"));
			logger.info(rsObj.getString("driverInfo"));
			logger.info(rsObj.getString("jdbcInfo"));

			int scrollType = NumberUtils.toInt(rsObj.getString("scrollType"));
			if (scrollType == ResultSet.TYPE_FORWARD_ONLY) {
				logger.debug("ResultSet.TYPE_FORWARD_ONLY");
			} else if (scrollType == ResultSet.TYPE_SCROLL_INSENSITIVE) {
				logger.debug("ResultSet.TYPE_SCROLL_INSENSITIVE");
			} else if (scrollType == ResultSet.TYPE_SCROLL_SENSITIVE) {
				logger.debug("ResultSet.TYPE_SCROLL_SENSITIVE");
			}

			int updateType = NumberUtils.toInt(rsObj.getString("updateType"));
			if (updateType == 1) {
				logger.debug("ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY");
			} else if (updateType == 2) {
				logger.debug("ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY");
			} else if (updateType == 3) {
				logger.debug("ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY");
			} else if (updateType == 4) {
				logger.debug("ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE");
			} else if (updateType == 5) {
				logger.debug("ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE");
			} else if (updateType == 6) {
				logger.debug("ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE");
			}

			int commitType = NumberUtils.toInt(rsObj.getString("commitType"));
			if (commitType == ResultSet.HOLD_CURSORS_OVER_COMMIT) {
				// 在事务commit 或rollback 后，ResultSet 仍然可用。
				logger.debug("ResultSet.HOLD_CURSORS_OVER_COMMIT");
			} else if (commitType == ResultSet.CLOSE_CURSORS_AT_COMMIT) {
				// 在事务commit 或rollback 后，ResultSet 被关闭
				logger.debug("ResultSet.CLOSE_CURSORS_AT_COMMIT");
			}

		} catch (Exception exp) {
			logger.error(exp);
			return false;
		}
		return true;
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
		} catch (SQLException exp) {
			logger.error(exp);
		}
	}

	@Override
	public void setTableName(String tblName) {
		this._tblName = tblName;
	}

	@Override
	public ResultSet directQuery(String sqlStr, int pageNum) {
		currPage = pageNum;
		if (pageNum == 1) {
			// 先取得该查询的数据总件数
			Statement istmt = null;
			ResultSet irs = null;
			try {
				istmt = _dbConn.createStatement();
				irs = istmt.executeQuery("select count(1) as c1 from ( " + sqlStr + " )  t1 ");
				if (irs.next()) {
					_size = irs.getInt(1);
					logger.debug("该查询的数据总件数: size= " + _size);
				}
			} catch (SQLException exp) {
				logger.error(exp);
				return null;
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
		}

		// 查询数据，此处只需考虑分页，不需考虑更新
		return doDirectQueryImpl(sqlStr, pageNum);
	}

	protected CachedRowSet doDirectQueryImpl(String sqlStr, int pageNum) {
		try {
			// 查询表数据
			String action = getLimitString(sqlStr, pageNum);
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(action);

			allRowSet = new CachedRowSetImpl();
			allRowSet.setPageSize(_pageSize);

			allRowSet.populate(rs, (pageNum - 1) * _pageSize);
			return allRowSet;

		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	public int directExec(String action) {
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
			return rslt;

		} catch (Exception exp) {
			logger.error("directExec更新不成功: " + action);
			throw new BaseExceptionWrapper(exp);
		}
	}

	// 当前页数
	protected int currPage = 0;

	/**
	 * 取得当前页数
	 *
	 * @return int
	 */
	public int getCurrPageNum() {
		return currPage;
	}

	@Override
	public String procCellData(Object obj) {
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	@Override
	public ResultSet defaultQuery(int pageNum) {
		currPage = pageNum;
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException exp) {
			logger.error(exp);
		}

		// 先取得该表的数据总件数
		if (pageNum == 1) {
			Statement istmt = null;
			ResultSet irs = null;
			try {
				istmt = _dbConn.createStatement();
				irs = istmt.executeQuery("select count(1) from " + _tblName);
				if (irs.next()) {
					_size = irs.getInt(1);
					logger.debug("该表的数据总件数 TBL: " + _tblName + " size= " + _size);
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
		}

		return doDefaultQueryImpl(_tblName, pageNum);
	}

	protected CachedRowSet doDefaultQueryImpl(String tblName, int pageNum) {
		try {
			// 查询表数据
			String action = getLimitString(_tblName, pageNum);
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(action);

			allRowSet = new CachedRowSetImpl();
			allRowSet.setPageSize(_pageSize);

			allRowSet.populate(rs, (pageNum - 1) * _pageSize + 1);
			return allRowSet;

		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	protected String getLimitString(String tblName, int pageNum) {
		return "select * from " + tblName;
	}

	@Override
	public void defaultUpdate(HashMap<Integer, HashMap<Integer, String>> params,
			ArrayList<HashMap<Integer, String>> addParams, ArrayList<Integer> delParams) {
		int colNum = 0;
		String colValue = null;
		try {
			getConnection().setAutoCommit(false);
			// 更新
			if (params != null && params.size() > 0) {
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

			allRowSet.acceptChanges(_dbConn);

		} catch (SyncProviderException exp) {
			SyncResolver resolver = exp.getSyncResolver();
			try {
				// value in crs
				Object crsValue = null;
				// value in the SyncResolver object
				Object resolverValue = null;
				// value to be persistent
				//Object resolvedValue = null;

				while (resolver.nextConflict()) {
					int status = resolver.getStatus();
					logger.debug("resolver status: " + status);
					if (status == SyncResolver.UPDATE_ROW_CONFLICT) {
						int row = resolver.getRow();
						rs.absolute(row);
						int colCount = rs.getMetaData().getColumnCount();
						for (int j = 1; j <= colCount; j++) {
							if (resolver.getConflictValue(j) != null) {
								// value in crs
								crsValue = rs.getObject(j);
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
