/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.rowset.CachedRowSet;

import jdbc.wrapper.mongo.MongoCachedRowSetImpl;
import jdbc.wrapper.mongo.MongoConnection;
import jdbc.wrapper.mongo.MongoResultSet;

import org.bson.types.ObjectId;

import com.dbm.common.error.BaseExceptionWrapper;
import com.dbm.common.error.WarningException;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * MongoDb数据库操作
 *
 * @author JiangJusheng
 */
public class DbClient4MongoImpl extends DbClient {

	private DB dbObj = null;
	protected Statement stmt = null;
	protected ResultSet rs = null;

	@Override
	public void start(String[] args) {
		_dbArgs = args;
		String dbUrl = _dbArgs[1];

		try {
			Class.forName(_dbArgs[0]);
			_dbConn = DriverManager.getConnection(dbUrl, _dbArgs[2], _dbArgs[3]);
			_isConnected = true;
		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
		dbObj = ((MongoConnection) _dbConn).getMongoDb();
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
	public ResultSet directQuery(String sqlStr, int pageNum) {
		// 查询数据，此处只需考虑分页，不需考虑更新
		int dot_1st = sqlStr.indexOf(".");
		int dot_2nd = sqlStr.indexOf(".", dot_1st + 1);
		String tblName = sqlStr.substring(dot_1st + 1, dot_2nd);

		if (sqlStr.indexOf(".find(") > 0) {
			// 查询
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException exp) {
					logger.error(exp);
				}
			}
			rs = new MongoCachedRowSetImpl(_dbConn, tblName, 1, 500, null);

		} else if (sqlStr.indexOf(".findOne(") > 0) {
			
			
		} else if (sqlStr.indexOf(".count(") > 0) {
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

	CachedRowSet allRowSet = null;

	private ObjectId lastId = null;

	@Override
	public ResultSet defaultQuery(int pageNum) {
		currPage = pageNum;

		try {
			// 先取得该表的数据总件数
			if (pageNum == 1) {
				allRowSet = new MongoCachedRowSetImpl(_dbConn, _tblName, 0, 0, null);
				_size = allRowSet.size();
				logger.debug("TBL: " + _tblName + " size: " + _size);
			}

			allRowSet = new MongoCachedRowSetImpl(_dbConn, _tblName, pageNum, 500, lastId);
			allRowSet.beforeFirst();
			lastId = ((MongoCachedRowSetImpl) allRowSet).getLastIdxObj();
			return allRowSet;

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	// 当前页数
	protected int currPage = 0;

	@Override
	public int getCurrPageNum() {
		return currPage;
	}

	@Override
	public String procCellData(Object obj) {
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	@Override
	public String getTableDataAt(int rowNum, int colNum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void defaultUpdate(HashMap<Integer, HashMap<Integer, String>> params,
			ArrayList<HashMap<Integer, String>> addParams, ArrayList<Integer> delParams) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTableName(String tblName) {
		this._tblName = tblName;
	}
}
