/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.rowset.CachedRowSet;

import jdbc.wrapper.mongo.MongoCachedRowSetImpl;

import com.dbm.common.error.BaseExceptionWrapper;
import com.dbm.common.error.WarningException;
import com.dbm.common.util.StringUtil;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import com.sun.rowset.CachedRowSetImpl;

/**
 * MongoDb数据库操作
 *
 * @author JiangJusheng
 */
public class DbClient4MongoImpl extends DbClient4DefaultImpl {

	private DB dbObj = null;

	@Override
	public void start(String[] args) {
		_dbArgs = args;
		// connect to db 
		try {
			String dbUrl = _dbArgs[1];
			String[] dbType = dbUrl.split("//");
			String dbArr[] = dbType[1].split("/");
			String urlArr[] = dbArr[0].split(":");
			MongoClient mongoClient = new MongoClient(urlArr[0], StringUtil.parseInt(urlArr[1]));
			dbObj = mongoClient.getDB(dbArr[1]);

			try {
				Class.forName(_dbArgs[0]);
				_dbConn = DriverManager.getConnection(dbUrl);
				_isConnected = true;
			} catch (Exception exp) {
				throw new BaseExceptionWrapper(exp);
			}

		} catch (IOException exp) {
			throw new BaseExceptionWrapper(exp);
		}
		if (dbObj == null) {
			throw new WarningException("数据库不存在");
		}
		if (_dbArgs[2] != null && !_dbArgs[2].isEmpty()) {
			if (!dbObj.authenticate(_dbArgs[2], _dbArgs[3].toCharArray())) {
				throw new WarningException("没有权限访问该数据库");
			}
		}

		_isConnected = true;
	}

	@Override
	public int getExecScriptType(String action) {
		int sqlType = -1;
		if (action.length() <= 6) {
			throw new WarningException(20002);
		}
		String typeStr = action.substring(0, 6);
		// 判断SQL类型
		if ("select".equalsIgnoreCase(typeStr)) {
			sqlType = 1;
		} else if ("create".equalsIgnoreCase(typeStr) || "update".equalsIgnoreCase(typeStr)
				|| "insert".equalsIgnoreCase(typeStr) || "delete".equalsIgnoreCase(typeStr)
				|| "drop".equalsIgnoreCase(typeStr.substring(0, 4)) ) {
			sqlType = 2;
		} else {
			throw new WarningException(20001);
		}
		return sqlType;
	}

	@Override
	public ResultSet directQuery(String action) {
		// 查询数据，此处只需考虑分页，不需考虑更新
		try {
			stmt = _dbConn.createStatement();
			rs = stmt.executeQuery(action);
			return rs;
		} catch (SQLException exp) {
			logger.error(exp);
		}
		return null;
	}

	/**
	 * MongoDb目前支持的操作：<br>
	 * db.createCollection(), db.collection.drop(),
	 * db.collection.find/findOne(), db.collection.insert(), db.collection.update(), db.collection.remove(),
	 */
	@Override
	public boolean directExec(String action) {

		// 判断SQL类型
		if (action.startsWith("db.createCollection")) {
			// 创建表
			int quote_1st = action.indexOf("\"");
			int quote_2nd = action.indexOf("\"", quote_1st + 1);
			String tblName = action.substring(quote_1st + 1, quote_2nd);

			int braces_1st = action.indexOf("{");
			int braces_2nd = action.indexOf("}", braces_1st);
			if (braces_1st > 0 && braces_2nd > braces_1st) {
				String optStr = action.substring(braces_1st, braces_2nd + 1);
				dbObj.createCollection(tblName, (DBObject) JSON.parse(optStr));
			} else {
				dbObj.createCollection(tblName, null);
			}

		} else if (action.indexOf(".drop()") > 0) {
			// 删除表
			int dot_1st = action.indexOf(".");
			int dot_2nd = action.indexOf(".", dot_1st + 1);
			String tblName = action.substring(dot_1st + 1, dot_2nd);
			dbObj.getCollection(tblName).drop();

		} else if (action.indexOf(".find(") > 0) {
			// 查询
//			if (allRowSet != null) {
//				try {
//					allRowSet.close();
//				} catch (SQLException exp) {
//					logger.error(exp);
//				}
//			}
//			int dot_1st = action.indexOf(".");
//			int dot_2nd = action.indexOf(".", dot_1st + 1);
//			String tblName = action.substring(dot_1st + 1, dot_2nd);
//			
//			allRowSet = new MongoCachedRowSetImpl(dbObj.getCollection(tblName).find());
//			TableUtil.setTableData(allRowSet, true);
			
		} else if (action.indexOf(".findOne(") > 0) {
			
			
		}

		return false;
	}

	@Override
	public CachedRowSet getPage(int pageNum, int rowIdx, int pageSize) {
		currPage = pageNum;

		try {
			if (allRowSet != null) {
				allRowSet.release();
				allRowSet.close();
			}

			allRowSet.populate(null, rowIdx);


		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
		return allRowSet;
	}

	@Override
	public CachedRowSet executeQuery(String tblName) {
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
		} catch (SQLException exp) {
			logger.error(exp);
		}
		try {
			allRowSet = new MongoCachedRowSetImpl(dbObj.getCollection(tblName));
			_size = allRowSet.size();
			return allRowSet;

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	/**
	 * 取得DB对象信息，如：表、视图等等
	 */
	public List<String> getDbMetaData() {
		List<String> rslt = new ArrayList<String>();
		rslt.add("Collections");
		rslt.add("Stored JavaScript");
		rslt.add("GridFs");
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
		if (types == null || types.length == 0) {
			return null;
		}
		ArrayList<String> list = new ArrayList<String>();
		if ("Collections".equals(types[0])) {
			Set<String> colls = dbObj.getCollectionNames();
			for (String tblName : colls) {
				if ("fs.chunks".equals(tblName) || "fs.files".equals(tblName)
						|| "system.indexes".equals(tblName) ||"system.users".equals(tblName)) {
					continue;
				} else {
					list.add(tblName);
				}
			}
		} else if ("GridFs".equals(types[0])) {
			list.add("fs.files");
		}
		return list;
	}
}