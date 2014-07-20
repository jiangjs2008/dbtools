/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.dbm.common.error.BaseExceptionWrapper;
import com.sun.rowset.CachedRowSetImpl;

/**
 * 缺省数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4Db2Impl extends DbClient4DefaultImpl {

	@Override
	public void start(String[] args) {
		super.start(args);
		_hasSchema = false;
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

		try {
			// 查询表数据
			String action = "select * from " + _tblName;
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(action);

			allRowSet = new CachedRowSetImpl();
			allRowSet.setPageSize(500);

			allRowSet.populate(rs, (pageNum - 1) * _pageSize + 1);
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

}
