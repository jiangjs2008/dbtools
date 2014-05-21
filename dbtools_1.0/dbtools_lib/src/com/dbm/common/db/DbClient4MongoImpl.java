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

import jdbc.wrapper.mongo.MongoCachedRowSetImpl;
import jdbc.wrapper.mongo.MongoResultSet;

import com.dbm.common.error.BaseExceptionWrapper;
import com.dbm.common.error.WarningException;
import com.dbm.common.util.StringUtil;
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
			throw new WarningException(20003);
		}
		if (_dbArgs[2] != null && !_dbArgs[2].isEmpty()) {
			if (!dbObj.authenticate(_dbArgs[2], _dbArgs[3].toCharArray())) {
				throw new WarningException(20004);
			}
		}

		_isConnected = true;
	}

	@Override
	public int getExecScriptType(String action) {
		int sqlType = 0;
		if (action != null) {
			action = action.trim();
		}
		if (action.length() <= 15) {
			throw new WarningException(20001);
		}

		// 判断SQL类型
		if (action.indexOf(".find") > 0 || action.indexOf(".count") > 0) {
			sqlType = 1;
		} else if (action.indexOf("db.createCollection(") == 0 || action.indexOf(".drop(") == 0
				|| action.indexOf(".insert(") == 0 || action.indexOf(".update(") == 0
				|| action.indexOf(".remove(") == 0 ) {
			sqlType = 2;
		} else {
			throw new WarningException(20001);
		}
		return sqlType;
	}

	@Override
	public ResultSet directQuery(String action) {
		// 查询数据，此处只需考虑分页，不需考虑更新
		int dot_1st = action.indexOf(".");
		int dot_2nd = action.indexOf(".", dot_1st + 1);
		String tblName = action.substring(dot_1st + 1, dot_2nd);

		if (action.indexOf(".find(") > 0) {
			// 查询
			if (allRowSet != null) {
				try {
					allRowSet.close();
				} catch (SQLException exp) {
					logger.error(exp);
				}
			}

			allRowSet = new MongoCachedRowSetImpl(dbObj.getCollection(tblName).find());
			//TableUtil.setTableData(allRowSet, true);
			
		} else if (action.indexOf(".findOne(") > 0) {
			
			
		} else if (action.indexOf(".count(") > 0) {
			long size = dbObj.getCollection(tblName).count();
			_size = 1;
			return new MongoResultSet(new String[] {"count"}, new String[][] { new String[]{ Long.toString(size) } });
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
		if (action.startsWith("db.createCollection(")) {
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

		}
		return false;
	}

	@Override
	public ResultSet getPage(int pageNum, int rowIdx, int pageSize) {
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
	public ResultSet executeQuery(String tblName) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
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

	@Override
	public List<String> getDbMetaData() {
		List<String> rslt = new ArrayList<String>();
		rslt.add("Collections");
		rslt.add("Stored JavaScript");
		rslt.add("GridFs");
		return rslt;
	}

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
