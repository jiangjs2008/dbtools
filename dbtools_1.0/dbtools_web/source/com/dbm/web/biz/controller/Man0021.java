package com.dbm.web.biz.controller;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
public class Man0021 extends DefaultController {


	@RequestMapping("/ajax/sqlscript.do")
	@ResponseBody
	public String mpc0110query(@RequestParam Map<String,String> requestParam) {
		String sqlScript = requestParam.get("sqlscript");
		JSONObject rsltJObj = new JSONObject();
		try {
			sqlScript = new String(sqlScript.getBytes("ISO-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException eop) {
			logger.error(sqlScript, eop);
			rsltJObj.put("ecd", "0");
			return rsltJObj.toJSONString();
		}

		if (sqlScript == null || sqlScript.length() == 0) {
			rsltJObj.put("ecd", "0");
			return rsltJObj.toJSONString();
		}

		try {
			DbClient dbClient = DbClientFactory.getDbClient();

			if (dbClient.getExecScriptType(sqlScript) == 1) {
				// 数据检索
				ResultSet rs = dbClient.directQuery(sqlScript, 1);
				if (rs == null) {
					logger.error("执行时错误 " + sqlScript);
					rsltJObj.put("ecd", "3");
					return rsltJObj.toJSONString();
				}
				rsltJObj.put("ecd", "1");

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
					params.put("field", colName);
					params.put("title", colName);
					params.put("editor", "text");
					columnInfo.add(params);
				}

				JSONArray rslt = new JSONArray();
				rslt.add(columnInfo);

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

				JSONObject params = new JSONObject();
				params.put("total", dbClient.size());
				params.put("rows", dataInfo);

				rsltJObj.put("coldata", rslt);
				rsltJObj.put("gridata", params);
				return rsltJObj.toJSONString();

			} else {
				// 更新数据
				if (dbClient.directExec(sqlScript)) {
					rsltJObj.put("ecd", "1");
				} else {
					rsltJObj.put("ecd", "2");
				}
			}

			return rsltJObj.toJSONString();

		} catch (Exception exp) {
			logger.error(sqlScript, exp);
			rsltJObj.put("ecd", "3");
			return rsltJObj.toJSONString();
		}
	}

}
