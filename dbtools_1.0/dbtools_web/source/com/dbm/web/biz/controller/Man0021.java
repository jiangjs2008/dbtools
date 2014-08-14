package com.dbm.web.biz.controller;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
		try {
			sqlScript = new String(sqlScript.getBytes("ISO-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException eop) {
			logger.error("", eop);
		}

		JSONObject params = new JSONObject();
		if (sqlScript == null || sqlScript.length() == 0) {
			params.put("ecd", "0");
			return params.toJSONString();
		}

		try {
			DbClient dbClient = DbClientFactory.getDbClient();

			if (dbClient.getExecScriptType(sqlScript) == 1) {
				// 数据检索
				ResultSet rs = dbClient.directQuery(sqlScript, 1);
				if (rs == null) {
					params.put("ecd", "0");
					return params.toJSONString();
				}
				


			} else {
				// 更新数据
				if (dbClient.directExec(sqlScript)) {
					params.put("ecd", "1");
				} else {
					params.put("ecd", "2");
				}
			}

			return params.toJSONString();

		} catch (Exception exp) {
			logger.error("", exp);
			params.put("ecd", "3");
			params.put("msg", exp.toString());
			return params.toJSONString();
		}
	}

}
