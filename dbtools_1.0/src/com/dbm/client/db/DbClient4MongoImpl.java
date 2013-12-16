/*
 * Created on 2007/03/13
 */
package com.dbm.client.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jdbc.wrapper.mongo.MongoCachedRowSetImpl;


import com.dbm.client.error.BaseExceptionWrapper;
import com.dbm.client.error.WarningException;
import com.dbm.client.util.StringUtil;
import com.dbm.client.util.TableUtil;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

/**
 * MongoDb数据库操作
 *
 * @author JiangJusheng
 */
public class DbClient4MongoImpl extends DbClient4DefaultImpl {

	private DB dbObj = null;
	private Connection dbConn = null;

	@Override
	public Connection getConnection() {
		return dbConn;
	}

	@Override
	public void start(String[] args) {
		dbArgs = args;
		// connect to db 
		try {
			String dbUrl = dbArgs[1];
			String[] dbType = dbUrl.split("//");
			String dbArr[] = dbType[1].split("/");
			String urlArr[] = dbArr[0].split(":");
			MongoClient mongoClient = new MongoClient(urlArr[0], StringUtil.parseInt(urlArr[1]));
			dbObj = mongoClient.getDB(dbArr[1]);

			try {
				Class.forName(dbArgs[0]);
				dbConn = DriverManager.getConnection(dbUrl);
				isConnected = true;
			} catch (Exception exp) {
				throw new BaseExceptionWrapper(exp);
			}

		} catch (IOException exp) {
			throw new BaseExceptionWrapper(exp);
		}
		if (dbObj == null) {
			throw new WarningException("数据库不存在");
		}
		if (dbArgs[2] != null && !dbArgs[2].isEmpty()) {
			if (!dbObj.authenticate(dbArgs[2], dbArgs[3].toCharArray())) {
				throw new WarningException("没有权限访问该数据库");
			}
		}

		isConnected = true;
	}

	/**
	 * 执行MongoDb数据库操作
	 */
	@Override
	public Object execute(int sqlType, String action) {

		if (sqlType == 9) {
			// query sql
			allRowSet = new MongoCachedRowSetImpl(dbObj.getCollection(action));
			TableUtil.setTableData(allRowSet, true);
		}
		return null;
	}

	/**
	 * MongoDb目前支持的操作：<br>
	 * db.createCollection(), db.collection.drop(),
	 * db.collection.find/findOne(), db.collection.insert(), db.collection.update(), db.collection.remove(),
	 */
	@Override
	public boolean executeScript(String action) {

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
			if (allRowSet != null) {
				try {
					allRowSet.close();
				} catch (SQLException exp) {
					logger.error(exp);
				}
			}
			int dot_1st = action.indexOf(".");
			int dot_2nd = action.indexOf(".", dot_1st + 1);
			String tblName = action.substring(dot_1st + 1, dot_2nd);
			
			allRowSet = new MongoCachedRowSetImpl(dbObj.getCollection(tblName).find());
			TableUtil.setTableData(allRowSet, true);
			
		} else if (action.indexOf(".findOne(") > 0) {
			
			
		}
		
		

		return false;
	}

	/**
	 * 取得DB对象信息，如：表、视图等等
	 */
	@Override
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
	@Override
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
