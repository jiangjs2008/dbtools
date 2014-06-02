/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;


/**
 * 缺省数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4Db2Impl extends DbClient4DefaultImpl {

	@Override
	protected String getLimitString(String sql, int pageNum) {
		return "SELECT * FROM ( SELECT B.*, ROWNUMBER() OVER() AS RN FROM ( " + sql + " ) AS B ) AS A WHERE A.RN BETWEEN " 
			+ ( pageNum - 1) * _pageSize + " AND " + pageNum  * _pageSize;
	}
}
