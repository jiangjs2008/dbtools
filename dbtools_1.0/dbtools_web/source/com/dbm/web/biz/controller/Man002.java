package com.dbm.web.biz.controller;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.error.BaseExceptionWrapper;
import com.dbm.common.util.StringUtil;

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
			logger.error(ex);
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

	private String getRealTblName(DbClient dbClient, String tblName) {
		if (dbClient.hasSchema()) {
			String realName = tblName;
			if (tblName.indexOf('.') > 0) {
				String[] arr = StringUtils.split(tblName, '.');
				realName = arr[1];
			}
			return realName;

		} else {
			return tblName;
		}
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

	@RequestMapping("/biz/inf001.do")
	public ModelAndView getCmp0030View(@RequestParam Map<String,String> requestParam){
		logger.debug("/sale/cmp0010.do =>getCmp0010View()");
		return new ModelAndView("inf001").addObject("tblname", requestParam.get("tblname"));
	}

	@RequestMapping("/ajax/tblinfo.do")
	@ResponseBody
	public String getTblInfo(@RequestParam Map<String,String> requestParam) {

		try {
			DbClient dbClient = DbClientFactory.getDbClient();
			DatabaseMetaData dmd = dbClient.getConnection().getMetaData();

			String realName = getRealTblName(dbClient, requestParam.get("tblname"));

			// 取得指定表的主键信息
			ResultSet pkRs = dmd.getPrimaryKeys(null, null, realName);
			ArrayList<String> pkList = new ArrayList<String>();
			if (pkRs != null) {
				while (pkRs.next()) {
					pkList.add(pkRs.getString(4));
				}
			}

			ArrayList<JSONObject> allData = new ArrayList<JSONObject>();
			// 取得指定表的所有列的信息
			ResultSet columnRs = dmd.getColumns(null, null, realName, "%");
			String colName = null;

			while (columnRs.next()) {
				JSONObject columnInfo = new JSONObject(6);

				// 列名
				colName = columnRs.getString(4);
				columnInfo.put("colname", colName);
				// 类型名
				columnInfo.put("type", columnRs.getString(6));
				// 列的大小
				columnInfo.put("size", columnRs.getString(7));

				// 是否为主键
				if (pkList.indexOf(colName) >= 0) {
					// 是主键
					columnInfo.put("pk", "Y");
				} else {
					columnInfo.put("pk", "");
				}

				// 是否可为空
				if (columnRs.getInt(11) == 1) {
					// 可为空
					columnInfo.put("nullable", "Y");
				} else {
					// 不可为空
					columnInfo.put("nullable", "");
				}

				// 列的注释
				columnInfo.put("remark", StringUtil.NVL(columnRs.getString(12)));
				
				allData.add(columnInfo);
			}
			JSONObject params = new JSONObject();
			params.put("total", allData.size());
			params.put("rows", allData);
			return params.toJSONString();

		} catch (Exception exp) {
			throw new BaseExceptionWrapper(exp);
		}

	}
}
