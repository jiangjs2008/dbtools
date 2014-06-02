/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;


/**
 * Mysql数据库操作类(Sqlite， postgresql 也可使用)
 *
 * @author JiangJusheng
 */
public class DbClient4MysqlImpl extends DbClient4DefaultImpl {

	@Override
	protected String getLimitString(String tblName, int pageNum) {
		String sql = "select tbl.* from " + tblName;
		return sql + " tbl limit " + _pageSize + " offset " + ( pageNum - 1) * _pageSize;
	}

}
