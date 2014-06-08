/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;


/**
 * Sqlite数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4SqliteImpl extends DbClient4DefaultImpl {

	@Override
	protected String getLimitString(String tblName, int pageNum) {
		String sql = "select tbl.* from " + tblName;
		return sql + " tbl limit " + _pageSize + " offset " + ( pageNum - 1) * _pageSize;
	}

}
