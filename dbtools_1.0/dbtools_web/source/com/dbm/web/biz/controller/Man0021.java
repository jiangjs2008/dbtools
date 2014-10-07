package com.dbm.web.biz.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.property.PropUtil;
import com.dbm.web.util.SQLsSession;

/**
 * [name]<br>
 * Man0021 Controller<br><br>
 * [function]<br>
 * 执行SQL脚本<br><br>
 * [history]<br>
 * 2014/05/05 ver1.00 JiangJusheng<br>
 */
@Controller
public class Man0021 extends DefaultController {


	@RequestMapping("/ajax/sqlscript.do")
	@ResponseBody
	public String mpc0110query(@RequestParam Map<String,String> requestParam) {
		String sqlScript = requestParam.get("sqlscript");
		JSONObject rsltJObj = new JSONObject();
		rsltJObj.put("total", 0);
		rsltJObj.put("rows", "{}");
		rsltJObj.put("colmodel", "[[]]");

		if ("empty".equals(sqlScript)) {
			// 清空画面原有数据
			rsltJObj.put("ecd", "0");
			return rsltJObj.toJSONString();
		}

		try {
			sqlScript = URLDecoder.decode(sqlScript, "UTF-8");
		} catch (UnsupportedEncodingException eop) {
			logger.error(sqlScript, eop);
			rsltJObj.put("ecd", "5");
			return rsltJObj.toJSONString();
		}

		int start = NumberUtils.toInt(requestParam.get("start"), -1);
		int pageNum = 0;
		if (start == -1) {
			pageNum = 1;
		} else {
			int lmt = NumberUtils.toInt(PropUtil.getAppConfig("page.data.count"));
			pageNum = start / lmt + 1;
		}

		if (sqlScript == null || sqlScript.length() == 0 || pageNum == 0) {
			logger.error("执行时错误 参数不对");
			rsltJObj.put("ecd", "5");
			return rsltJObj.toJSONString();
		}

		DbClient dbClient = DbClientFactory.getDbClient();
		if (dbClient == null) {
			logger.error("数据库联接不正常");
			rsltJObj.put("ecd", "9");
			rsltJObj.put("emsg", "数据库联接不正常");
			return rsltJObj.toJSONString();
		}

		// 将用户的SQL脚本存入缓存
		String clientId = requestParam.get("clientid");
		if (clientId != null && clientId.length() > 0) {
			SQLsSession.saveSQLsSession(clientId, sqlScript);
		}

		try {
			int sqlType = dbClient.getExecScriptType(sqlScript);
			if (sqlType == 1) {
				// 数据检索
				ResultSet rs = dbClient.directQuery(sqlScript, pageNum);
				if (rs == null) {
					logger.error("执行时错误 " + sqlScript);
					rsltJObj.put("ecd", "3");
					return rsltJObj.toJSONString();
				}

				ResultSetMetaData rsm = rs.getMetaData();
				if (rsm == null) {
					logger.error("无元数据信息 " + sqlScript);
					rsltJObj.put("ecd", "3");
					return rsltJObj.toJSONString();
				}

				// 设置列名信息
				String colName = null;
				ArrayList<JSONObject> columnInfo = new ArrayList<JSONObject>();
				for (int i = 1, lengs = rsm.getColumnCount() + 1; i < lengs; i ++) {
					colName = rsm.getColumnName(i);
					JSONObject params = new JSONObject();
					params.put("header", colName);
					params.put("name", colName);
					columnInfo.add(params);
				}

				// 设置grid数据
				JSONArray dataInfo = new JSONArray();
				while (rs.next()) {
					JSONObject params = new JSONObject();
					for (int i = 1, lengs = rsm.getColumnCount() + 1; i < lengs; i ++) {
						colName = rsm.getColumnName(i);
						params.put(colName, dbClient.procCellData(rs.getObject(i)));
					}
					dataInfo.add(params);
				}

				rsltJObj.put("ecd", "0");
				rsltJObj.put("total", dbClient.size());
				rsltJObj.put("rows", dataInfo);
				rsltJObj.put("colmodel", columnInfo);
				return rsltJObj.toJSONString();

			} else {
				// 更新数据
				int rs = dbClient.directExec(sqlScript);
				if (sqlType == 2) {
					if (rs > 0) {
						rsltJObj.put("ecd", "1");
					} else {
						rsltJObj.put("ecd", "2");
					}
				} else {
					rsltJObj.put("ecd", "1");
				}
			}

			return rsltJObj.toJSONString();

		} catch (Exception exp) {
			logger.error(sqlScript, exp);
			rsltJObj.put("ecd", "4");
			rsltJObj.put("emsg", exp.toString());
			return rsltJObj.toJSONString();
		}
	}

}
