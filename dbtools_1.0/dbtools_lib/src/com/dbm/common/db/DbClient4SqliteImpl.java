/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;

import jdbc.wrapper.sqlite.SQLiteCachedRowSetImpl;

import com.dbm.common.error.BaseExceptionWrapper;


/**
 * Sqlite数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4SqliteImpl extends DbClient4DefaultImpl {

	@Override
	protected String getLimitString(String tblName, int pageNum) {
		String sql = "select rowid, tbl.* from " + tblName;
		return sql + " tbl limit " + _pageSize + " offset " + ( pageNum - 1) * _pageSize;
	}

	protected CachedRowSet getCachedRowSetImpl(String tblName, int pageNum) {
		try {
			// 查询表数据
			String action = getLimitString(_tblName, pageNum);
			stmt = _dbConn.createStatement();
			rs = stmt.executeQuery(action);

			allRowSet = new SQLiteCachedRowSetImpl();
			allRowSet.populate(rs);

			return allRowSet;

		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}
}
