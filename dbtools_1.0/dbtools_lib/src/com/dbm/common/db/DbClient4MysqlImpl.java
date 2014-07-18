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
	protected String getLimitString(String tblName, int pageNum) {
		String sql = "select tbl.* from " + tblName;
		return sql + " tbl limit " + _pageSize + " offset " + ( pageNum - 1) * _pageSize;
	}

	@Override
	protected CachedRowSet getCachedRowSetImpl(String tblName, int pageNum) {
		try {
			// 查询表数据
			String action = getLimitString(_tblName, pageNum);
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(action);

			allRowSet = new CachedRowSetImpl();
			allRowSet.setPageSize(500);

			allRowSet.populate(rs, (pageNum - 1) * 500 + 1);
			return allRowSet;

		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}
}
