/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import oracle.jdbc.rowset.OracleCachedRowSet;
import oracle.sql.Datum;
import oracle.sql.ROWID;
import java.sql.Struct;

import com.dbm.common.error.BaseExceptionWrapper;

/**
 * 缺省数据库操作类
 *
 * @author JiangJusheng
 */
public class DbClient4Oracle9iImpl extends DbClient4DefaultImpl {

	@Override
	protected String getLimitString(String tblName, int pageNum) {
		return "select t.* from " + tblName + " t";
	}

	@Override
	protected CachedRowSet doDirectQueryImpl(String sqlStr, int pageNum) {
		try {
			// 查询表数据
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(sqlStr);

			allRowSet = new OracleCachedRowSet();
			allRowSet.setPageSize(_pageSize);

			allRowSet.populate(rs, (pageNum - 1) * _pageSize);
			return allRowSet;

		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	protected CachedRowSet doDefaultQueryImpl(String tblName, int pageNum) {
		try {
			// 查询表数据
			String action = getLimitString(_tblName, pageNum);
			stmt = _dbConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(action);

			allRowSet = new OracleCachedRowSet();
			allRowSet.setPageSize(_pageSize);

			allRowSet.populate(rs, (pageNum - 1) * _pageSize);
			allRowSet.setTableName(tblName);
			return allRowSet;

		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	@Override
	public String procCellData(Object obj) {
		if (obj == null) {
			return "";
		}
		if (obj instanceof Struct) {
			Struct objValue = (Struct) obj;
			// 如果是空间数据类型
			try {
				Object[] sps = ((Struct) objValue.getAttributes()[2]).getAttributes();
				return sps[0].toString() + ", " + sps[1].toString() ;
			} catch (SQLException ex) {
				logger.error(ex);
				return objValue.toString();
			}

		} else if (obj instanceof ROWID) {
			return ((ROWID) obj).stringValue();
		} else if (obj instanceof Datum) {
			try {
				return ((Datum) obj).stringValue();
			} catch (SQLException ex) {
				logger.error(ex);
				return "";
			}
		} else {
			return obj.toString();
		}
	}

	/**
	 * 取得DB所属对象一览，如表、视图一览
	 *
	 * @param catalog
	 * @param schemaPattern
	 * @param tableNamePattern
	 * @param types
	 *
	 * @return
	 */
	@Override
	public List<String[]> getTableList(String catalog, String schemaPattern, String tableNamePattern, String[] types) {
		String schema = null;
		try {
			schema = getConnection().getMetaData().getUserName();
		} catch (SQLException ex) {
			logger.error(ex);
		}
		return super.getTableList(catalog, schema, tableNamePattern, types);
	}
}
