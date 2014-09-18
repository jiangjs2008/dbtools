package com.dbm.web.biz.controller;

import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.property.PropUtil;
import com.dbm.common.util.StringUtil;

/**
 * [name]<br>
 * Mpc0110 Controller<br><br>
 * [function]<br>
 * 取得指定数据库表的数据<br><br>
 * [history]<br>
 * 2014/05/05 ver1.00 JiangJusheng<br>
 */
@Controller
public class Man002 extends DefaultController {

	@RequestMapping("/ajax/griddata.do")
	@ResponseBody
	public String mpc0120dispinfo(@RequestParam Map<String,String> requestParam) {
		logger.debug("/ajax/sale/mpc0120dispinfo.do =>mpc0120dispinfo()");
		String _tblName = null;
		try {
			_tblName = URLDecoder.decode(requestParam.get("tblname"), "UTF-8");
		} catch (Exception exp) {
			logger.error("", exp);
		}
		int start = NumberUtils.toInt(requestParam.get("start"), -1);
		int pageNum = 0;
		if (start == -1) {
			pageNum = 1;
		} else {
			int lmt = StringUtil.parseInt(PropUtil.getAppConfig("page.data.count"));
			pageNum = start / lmt + 1;
		}
		try {

			DbClient dbClient = DbClientFactory.getDbClient();
			dbClient.setTableName(_tblName);
			ResultSet rs = dbClient.defaultQuery(pageNum);

			String colName = null;
			ResultSetMetaData rsm = rs.getMetaData();
			if (rsm == null) {
				JSONObject params = new JSONObject();
				params.put("total", 0);
				params.put("rows", new ArrayList<JSONObject>());
				return params.toJSONString();
			}

			JSONObject edtObj = null;
			int deployType = NumberUtils.toInt(PropUtil.getAppConfig("deploy.type"));
			if (deployType == 0) {
				edtObj = new JSONObject();
				edtObj.put("editable", true);
			}

			ArrayList<JSONObject> columnInfo = new ArrayList<JSONObject>();
			int colCnt = rsm.getColumnCount() + 1;
			for (int i = 1; i < colCnt; i ++) {
				// 列名
				colName = rsm.getColumnName(i);
				JSONObject params = new JSONObject();
				params.put("header", colName);
				params.put("name", colName);
				params.put("editor", edtObj);
				columnInfo.add(params);
			}

			ArrayList<JSONObject> dataInfo = new ArrayList<JSONObject>();
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
			params.put("colmodel", columnInfo);
			return params.toJSONString();

		} catch (Exception exp) {
			logger.error("", exp);
			JSONObject params = new JSONObject();
			params.put("total", 0);
			params.put("rows", "{}");
			params.put("colmodel", "[[]]");
			params.put("errorMsg ", exp.toString());
			return params.toJSONString();
		}
	}

}
