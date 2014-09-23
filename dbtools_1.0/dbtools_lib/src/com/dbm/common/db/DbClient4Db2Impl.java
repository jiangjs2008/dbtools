/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

import java.sql.ResultSet;

import javax.sql.rowset.CachedRowSet;

import com.dbm.common.error.BaseExceptionWrapper;
import com.sun.rowset.CachedRowSetImpl;

/**
 * 缺省数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4Db2Impl extends DbClient4DefaultImpl {

	@Override
	public boolean start(String[] args) {
		_hasSchema = false;
		return super.start(args);
	}

	@Override
	protected CachedRowSet doDefaultQueryImpl(String tblName, int pageNum) {
		try {
			// 查询表数据
			String action = "select * from " + _tblName;
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(action);

			allRowSet = new CachedRowSetImpl();
			allRowSet.setPageSize(_pageSize);

			allRowSet.populate(rs, (pageNum - 1) * _pageSize + 1);//奇怪的DB2,行计数从１开始
			String tableName = null;
			if (_tblName.indexOf(".") > 0) {
				tableName = _tblName.substring(_tblName.indexOf(".") + 1);
			} else {
				tableName = _tblName;
			}
			allRowSet.setTableName(tableName);
			return allRowSet;

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	protected CachedRowSet doDirectQueryImpl(String sqlStr, int pageNum) {
		try {
			// 查询表数据
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sqlStr);

			allRowSet = new CachedRowSetImpl();
			allRowSet.setPageSize(_pageSize);

			allRowSet.populate(rs, (pageNum - 1) * _pageSize + 1);
			return allRowSet;

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

}
