/**
 * Copyright (c) 2013 JiangJusheng. All rights reserved.
 */
package com.dbm.common.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dbm.common.error.BaseExceptionWrapper;
import com.dbm.common.error.WarningException;
import com.dbm.common.log.LoggerWrapper;

/**
 * [class]<br>
 * database client interface<br><br>
 * [function]<br>
 * database client interface<br><br>
 * [history]<br>
 * 2013/05/10 first edition  JiangJusheng<br>
 *
 * @version 1.00
 */
public abstract class DbClient {

	/**
	 * instances of the log class
	 */
	protected static LoggerWrapper logger = new LoggerWrapper(DbClient.class); 
	/**
	 * 数据库连接状态标志位<br>
	 * true: 已连接  false:未连接
	 */
	protected static boolean _isConnected = false;
	/**
	 * 数据库连接参数
	 * [0]: 数据库Jdbc驱动类名
	 * [1]: 数据库URL
	 * [2]: 用户名
	 * [3]: 密码
	 */
	protected String[] _dbArgs = null;
	/**
	 * 数据库连接对象
	 */
	protected Connection _dbConn = null;

	/**
	 * 指定表(当前查询)的数据件数
	 */
	protected int _size = 0;

	/**
	 * 每页显示的数据件数
	 */
	protected int _pageSize = 0;

	/**
	 * @param _pageSize the _pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this._pageSize = pageSize;
	}

	/**
	 * 每页显示的数据件数
	 */
	protected String _tblName = null;

	/**
	 * @param _pageSize the _pageSize to set
	 */
	public abstract void setTableName(String tblName);

	public abstract int getCurrPageNum();

	protected boolean _hasSchema = false;

	public final boolean hasSchema() {
		return _hasSchema;
	}

	/**
	 * 判断数据库是否已连接
	 *
	 * @return boolean 数据库连接状态 true: 已连接  false:未连接
	 */
	public final boolean isConnected() {
		return _isConnected;
	}

	/**
	 * 取得数据库连接对象
	 *
	 * @return Connection 数据库连接对象
	 */
	public final Connection getConnection() {
		return _dbConn;
	}

	/**
	 * 取得指定表(当前查询)的数据件数(总件数)
	 *
	 * @return int 查询结果件数
	 */
	public final int size() {
		return _size;
	}

	public abstract String procCellData(Object obj);

	/*
	 * 取得指定位置的数据
	 *
	 * The first row is row 1, the second is row 2, and so on.
	 * The first column is col 1, the second is col 2, and so on.
	 */
	public abstract String getTableDataAt(int rowNum, int colNum);

	/**
	 * 判断执行数据库脚本的类型<br>
	 * crud
	 * 0:unkown 1:select 2:create/update/delete/insert/drop...
	 *
	 * @param action 数据库脚本
	 *
	 * @return int 脚本的类型
	 */
	public int getExecScriptType(String action) {
		int sqlType = 0;
		if (action != null) {
			action = action.trim();
		}
		if (action.length() <= 6) {
			throw new WarningException(20001);
		}
		String typeStr = action.substring(0, 6);
		// 判断SQL类型
		if ("select".equalsIgnoreCase(typeStr)) {
			sqlType = 1;
		} else if ("create".equalsIgnoreCase(typeStr) || "update".equalsIgnoreCase(typeStr)
				|| "insert".equalsIgnoreCase(typeStr) || "delete".equalsIgnoreCase(typeStr)
				|| "alter".equalsIgnoreCase(typeStr.substring(0, 5))
				|| "drop".equalsIgnoreCase(typeStr.substring(0, 4)) ) {
			sqlType = 2;
		} else {
			throw new WarningException(20001);
		}
		return sqlType;
	}

	/**
	 * 执行数据库脚本-查询
	 *
	 * @param action 数据库脚本
	 *
	 * @return ResultSet 执行结果
	 */
	public abstract ResultSet directQuery(String sqlStr, int pageNum);

	/**
	 * 执行数据库脚本-更新<br>
	 * 目前只支持基本的更新操作，而且只支持单条SQL文逐个执行，不支持批量SQL文执行
	 *
	 * @param action 数据库脚本
	 *
	 * @return boolean 执行结果
	 */
	public abstract boolean directExec(String sqlStr);


	protected String getLimitString(String sql, int pageNum) {
		return sql;
	}

	/**
	 * 取得指定表的所有数据
	 *
	 * @param tblName 数据库表名
	 *
	 * @return ResultSet 查询结果数据
	 */
	public abstract ResultSet defaultQuery(int pageNum);

	/**
	 * 执行数据更新
	 *
	 * @param tblName 数据库表名
	 * @param params  要更新的数据
	 * @param addParams 要追加的数据
	 * @param delParams  要删除的数据
	 *
	 * @return boolean 执行结果，成功返回true,否则false
	 */
	public abstract void defaultUpdate(HashMap<Integer, HashMap<Integer, String>> params,
			ArrayList<HashMap<Integer, String>> addParams, ArrayList<Integer> delParams);

	/**
	 * 连接数据库
	 *
	 * @param args [0]: driver class
	 *				[1]: db url
	 *				[2]: user name
	 *				[3]: password
	 */
	public abstract void start(String[] args);

	/**
	 * 关闭数据库连接
	 */
	abstract void close();

	/**
	 * 取得DB对象分类信息，如：表、视图等等
	 */
	public List<String> getDbMetaData() {
		List<String> rslt = new ArrayList<String>();
		ResultSet rs = null;
		try {
			Connection conn = getConnection();
			DatabaseMetaData dm = conn.getMetaData();

			rs = dm.getTableTypes();
			while (rs.next()) {
				rslt.add(rs.getString(1));
			}

		} catch (SQLException ex) {
			throw new BaseExceptionWrapper(ex);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlexp) {
					logger.error(sqlexp);
				}
			}
		}
		return rslt;
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
	public List<String> getDbObjList(String catalog, String schemaPattern, String tableNamePattern, String[] types) {
		ArrayList<String> list = new ArrayList<String>();
		ResultSet rs = null;
		try {
			Connection conn = getConnection();
			DatabaseMetaData dm = conn.getMetaData();

			rs = dm.getTables(catalog, schemaPattern, tableNamePattern, types);
			String schemaName = null;
			while (rs.next()) {
				schemaName = rs.getString(2);
				if (schemaName == null || schemaName.isEmpty()) {
					list.add(rs.getString(3));
				} else {
					list.add(rs.getString(2) + "." + rs.getString(3));
				}
			}
		} catch (SQLException ex) {
			throw new BaseExceptionWrapper(ex);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlexp) {
					logger.error(sqlexp);
				}
			}
		}
		return list;
	}
}

