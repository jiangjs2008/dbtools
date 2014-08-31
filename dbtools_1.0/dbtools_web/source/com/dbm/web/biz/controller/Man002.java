package com.dbm.web.biz.controller;

import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;

/**
 * [name]<br>
 * Mpc0110 Controller<br><br>
 * [function]<br>
 * 修改车机信息<br><br>
 * [history]<br>
 * 2014/05/05 ver1.00 JiangJusheng<br>
 */
@Controller
public class Man002 extends DefaultController {

	@RequestMapping("/ajax/getcatalog.do")
	@ResponseBody
	public String getCatalog(HttpServletRequest request) {
		try {
			DbClient dbClient = DbClientFactory.getDbClient();

			// 显示数据库内容：表、视图等等
			List<String> objList = dbClient.getDbMetaData();
			JSONArray dbInfo = new JSONArray(objList.size());
			for (String item : objList) {
				HashMap<String, Object> objMap = new HashMap<String, Object>(2);
				objMap.put("hassub", true);
				objMap.put("hasdata", false);
				objMap.put("id", item);
				objMap.put("text", item);
				objMap.put("state", "closed");
				dbInfo.add(objMap);
			}

			return dbInfo.toJSONString();
		} catch (Exception exp) {
			logger.error("查询catalog时发生错误", exp);
			JSONObject params = new JSONObject();
			params.put("ecd", 0);
			return params.toJSONString();
		}
	}

	@RequestMapping("/ajax/gettbllist.do")
	@ResponseBody
	public String getTblList(@RequestParam Map<String,String> requestParam) {
		String tblName = requestParam.get("catalog");

		DbClient dbClient = DbClientFactory.getDbClient();
		String schema = null;
		List<String> tblList = null;
		try {
			if (dbClient.hasSchema()) {
				schema = dbClient.getConnection().getMetaData().getUserName();
			}
			tblList = dbClient.getDbObjList(null, schema, "%", new String[] { tblName });
		} catch (Exception ex) {
			logger.error("取得表一览时发生错误", ex);
		}

		if (tblList == null) {
			JSONObject params = new JSONObject();
			params.put("ecd", "1");
			return params.toJSONString();
		}

		// 显示数据库内容：表、视图等等
		ArrayList<HashMap<String, Object>> dbInfo = new ArrayList<HashMap<String, Object>>(tblList.size());
		for (String item : tblList) {
			HashMap<String, Object> objMap = new HashMap<String, Object>(2);
			objMap.put("text", item);
			objMap.put("hassub", false);
			dbInfo.add(objMap);
		}

		return JSON.toJSONString(dbInfo);
	}

	@RequestMapping("/ajax/griddata.do")
	@ResponseBody
	public String mpc0120dispinfo(@RequestParam Map<String,String> requestParam) {
		logger.debug("/ajax/griddata.do =>mpc0120dispinfo()");
		String _tblName = null;
		try {
			_tblName = URLDecoder.decode(requestParam.get("tblname"), "UTF-8");
		} catch (Exception exp) {
			logger.error("", exp);
		}
		int pageNum = NumberUtils.toInt(requestParam.get("page"));

		JSONObject rsltJObj = new JSONObject();
		rsltJObj.put("total", 0);
		rsltJObj.put("rows", new ArrayList<JSONObject>());

		if (_tblName == null || _tblName.length() == 0 || pageNum == 0) {
			logger.warn("查询参数不正确 表：" + _tblName);
			rsltJObj.put("emsg", "查询参数不正确.");
			return rsltJObj.toJSONString();
		}

		try {
			DbClient dbClient = DbClientFactory.getDbClient();
			if (dbClient == null) {
				logger.warn("数据库联接不正常 表：" + _tblName);
				rsltJObj.put("emsg", "数据库联接不正常.");
				return rsltJObj.toJSONString();
			}

			dbClient.setTableName(_tblName);
			ResultSet rs = dbClient.defaultQuery(pageNum);

			String colName = null;
			ResultSetMetaData rsm = rs.getMetaData();
			if (rsm == null) {
				logger.warn("查询结果不正确 表：" + _tblName);
				rsltJObj.put("emsg", "查询结果不正确.");
				return rsltJObj.toJSONString();
			}

			// 设置header部信息
			ArrayList<JSONObject> columnInfo = new ArrayList<JSONObject>();
			int colCnt = rsm.getColumnCount() + 1;
			for (int i = 1; i < colCnt; i ++) {
				// 列名
				colName = rsm.getColumnName(i);
				JSONObject params = new JSONObject();
				params.put("field", colName);
				params.put("title", colName);
				params.put("type", rsm.getColumnTypeName(i));
				params.put("editor", "text");
				columnInfo.add(params);
			}

			JSONArray rslt = new JSONArray();
			rslt.add(columnInfo);

			// 设置body部信息
			JSONArray dataInfo = new JSONArray();
			while (rs.next()) {
				JSONObject params = new JSONObject();
				for (int i = 1; i < colCnt; i ++) {
					colName = rsm.getColumnName(i);
					params.put(colName, dbClient.procCellData(rs.getObject(i)));
				}
				dataInfo.add(params);
			}

			JSONObject params = new JSONObject();
			params.put("total", dbClient.size());
			params.put("rows", dataInfo);
			params.put("columns", rslt);
			params.put("page", pageNum);
			logger.debug("/ajax/griddata.do =>mpc0120dispinfo() 结束");
			return params.toJSONString();

		} catch (Exception exp) {
			logger.error("查询时发生错误表：" + _tblName, exp);
			rsltJObj.put("emsg", "查询时发生错误.");
			return rsltJObj.toJSONString();
		}
	}

}
