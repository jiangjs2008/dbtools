/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;

import com.dbm.common.error.BaseExceptionWrapper;
import com.sun.rowset.CachedRowSetImpl;


/**
 * Mysql数据库操作类(postgresql 也可使用)
 *
 * @author JiangJusheng
 */
public class DbClient4MysqlImpl extends DbClient4DefaultImpl {

	@Override
	protected CachedRowSet doDirectQueryImpl(String sqlStr, int pageNum) {
		String sql = "select tbl.* from ( " + sqlStr + " ) tbl limit " + _pageSize + " offset " + ( pageNum - 1) * _pageSize;
		try {
			// 查询表数据
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);

			allRowSet = new CachedRowSetImpl();
			allRowSet.setPageSize(_pageSize);

			allRowSet.populate(rs);
			return allRowSet;

		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	protected CachedRowSet doDefaultQueryImpl(String tblName, int pageNum) {
		String sql = "select tbl.* from " + tblName + " tbl limit " + _pageSize + " offset " + ( pageNum - 1) * _pageSize;
		try {
			// 查询表数据
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(sql);

			allRowSet = new CachedRowSetImpl();
			allRowSet.setPageSize(_pageSize);

			allRowSet.populate(rs);
			allRowSet.setTableName(tblName);
			return allRowSet;

		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}
}
