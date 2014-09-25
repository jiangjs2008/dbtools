/*
 * Created on 2007/03/13
 */
package com.dbm.common.db;

import java.sql.ResultSet;

import jdbc.wrapper.mongo.MongoCachedRowSetImpl;
import jdbc.wrapper.mongo.MongoConnection;
import jdbc.wrapper.mongo.MongoResultSet;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;


/**
 * MongoDb数据库操作
 *
 * @author JiangJusheng
 */
public class DbClient4MongoImpl extends DbClient4DefaultImpl {

	private DB dbObj = null;

	@Override
	public boolean start(String[] args) {
		boolean rslt = super.start(args);
		dbObj = ((MongoConnection) _dbConn).getMongoDb();
		return rslt;
	}

	/**
	 * MongoDb目前支持的操作：<br>
	 * db.collection.find/findOne(), db.collection.count()
	 */
	@Override
	public ResultSet directQuery(String sqlStr, int pageNum) {
		// 查询数据，此处只需考虑分页，不需考虑更新
		String tblName = null;
		// 先判断是否是"fs.files"
		if (sqlStr.indexOf(".fs.files.") > 0) {
			tblName = "fs.files";
		} else {
			int dot_1st = sqlStr.indexOf('.');
			int dot_2nd = sqlStr.indexOf('.', dot_1st + 1);
			tblName = sqlStr.substring(dot_1st + 1, dot_2nd);
		}

		int findIdx = sqlStr.indexOf(".find(");
		int findOneIdx = sqlStr.indexOf(".findOne(");
		int countIdx = sqlStr.indexOf(".count(");

		if (findIdx > 0) {
			// 查询
			BasicDBList reqObj = getReqDbObj(sqlStr, findIdx + 6);
			if (reqObj == null) {
				return null;
			}

			// 先取得该查询的数据总件数
			if (pageNum == 1) {
				_size = 0;
				allRowSet = new MongoCachedRowSetImpl(dbObj, tblName, reqObj, 0, 0);
				_size = allRowSet.size();
				logger.debug("TBL: " + _tblName + " size: " + _size);
			}
			allRowSet = new MongoCachedRowSetImpl(dbObj, tblName, reqObj, pageNum, _pageSize);
			try {
				allRowSet.beforeFirst();
			} catch (Exception exp) {
				logger.error(exp);
			}
			return allRowSet;

		} else if (findOneIdx > 0) {
			_size = 0;
			BasicDBList reqObj = getReqDbObj(sqlStr, findOneIdx + 9);
			if (reqObj == null) {
				return null;
			}

			BasicDBObject rsltObj = null;
			if (reqObj.size() == 1) {
				rsltObj = (BasicDBObject) dbObj.getCollection(tblName).findOne((DBObject) reqObj.get(0));
			} else if (reqObj.size() == 2) {
				rsltObj = (BasicDBObject) dbObj.getCollection(tblName).findOne((DBObject) reqObj.get(0), (DBObject) reqObj.get(1));
			} else {
				rsltObj = (BasicDBObject) dbObj.getCollection(tblName).findOne();
			}
			if (rsltObj != null) {
				_size = rsltObj.size();
			}
			if (_size == 0) {
				return new MongoResultSet(null, null);
			}
			return new MongoResultSet(rsltObj);

		} else if (countIdx > 0) {
			BasicDBList reqObj = getReqDbObj(sqlStr, countIdx + 7);
			if (reqObj == null) {
				return null;
			}

			long size = 0;
			if (reqObj.size() == 1) { 
				size = dbObj.getCollection(tblName).count((DBObject) reqObj.get(0));
			} else {
				size = dbObj.getCollection(tblName).count();
			}
			_size = 1;
			return new MongoResultSet(new String[] {"count"}, new String[][] { new String[]{ Long.toString(size) } });
		}
		return null;
	}

	/**
	 * 解析SQL参数
	 *
	 * @param sqlStr SQL文
	 * @param begIdx SQL参数索引开始位置
	 *
	 * @return BasicDBList SQL参数
	 */
	private BasicDBList getReqDbObj(String sqlStr, int begIdx) {
		try {
			int endIdx = sqlStr.indexOf(')', begIdx);
			String scripts = sqlStr.substring(begIdx, endIdx);
			BasicDBList ja = (BasicDBList) JSON.parse("[" + scripts + "]");

			if (ja == null) {
				logger.error("解析SQL参数时出错: " + sqlStr);
				return null;
			}
			if (ja.size() > 2) {
				logger.error("不支持的操作，参数过多: " + sqlStr);
				return null;
			}
			return ja;

		} catch (Exception exp) {
			logger.error("解析SQL语句时出错: " + sqlStr);
			logger.error(exp);
			return null;
		}
	}

	/**
	 * MongoDb目前支持的操作：<br>
	 * db.createCollection(), db.collection.drop(),
	 * db.collection.insert(), db.collection.update(), db.collection.remove(),
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

	/**
	 * 分页方案1.<br>
	 * 	对"_id"排序，记住关键点的_id，此方案弊端是只能前后翻页，不能跳转到指定页面<br>
	 * 	DBObject orderObj = new BasicDBObject("_id", 1);<br>
	 * 	DBObject values.put("$gt", lastId);<br>
	 * 	DBObject reqObj.put("_id", values);<br>
	 * 	DBCursor _cur = _tblObj.find(reqObj).sort(orderObj).limit(limit);<br>
	 * <br><br>
	 * 分页方案2.<br>
	 * 使用skip() 加limit(), 此方案弊端是如果数据量超大，例如到了上亿条数据，翻转到靠后的页面时，费时较多<br>
	 * DBCursor _cur = _tblObj.find().skip((pageNum - 1) * limit).limit(limit);<br>
	 * <br><br>
	 * 目前采用方案2分页<br>
	 */
	@Override
	public ResultSet defaultQuery(int pageNum) {
		currPage = pageNum;

		// 先取得该表的数据总件数
		if (pageNum == 1) {
			allRowSet = new MongoCachedRowSetImpl(dbObj, _tblName, null, 0, 0);
			_size = allRowSet.size();
			logger.debug("TBL: " + _tblName + " size: " + _size);
		}

		allRowSet = new MongoCachedRowSetImpl(dbObj, _tblName, null, pageNum, _pageSize);
		try {
			allRowSet.beforeFirst();
		} catch (Exception exp) {
			logger.error(exp);
		}
		return allRowSet;
	}

}
