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

		JSONObject rsltJObj = new JSONObject();
		try {

			DbClient dbClient = DbClientFactory.getDbClient();
			if (dbClient == null) {
				logger.error("数据库联接不正常");
				rsltJObj.put("ecd", "9");
				rsltJObj.put("emsg", "数据库联接不正常");
				return rsltJObj.toJSONString();
			}
			dbClient.setTableName(_tblName);
			ResultSet rs = dbClient.defaultQuery(pageNum);
			if (rs == null) {
				logger.error("执行时错误 " + _tblName);
				rsltJObj.put("ecd", "3");
				return rsltJObj.toJSONString();
			}

			String colName = null;
			ResultSetMetaData rsm = rs.getMetaData();
			if (rsm == null) {
				logger.error("无元数据信息 " + _tblName);
				rsltJObj.put("ecd", "3");
				return rsltJObj.toJSONString();
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

			rsltJObj.put("total", dbClient.size());
			rsltJObj.put("rows", dataInfo);
			rsltJObj.put("colmodel", columnInfo);
			rsltJObj.put("ecd", "0");
			return rsltJObj.toJSONString();

		} catch (Exception exp) {
			logger.error("获取数据时异常 表：" + _tblName, exp);
			rsltJObj.put("total", 0);
			rsltJObj.put("rows", "{}");
			rsltJObj.put("colmodel", "[[]]");
			rsltJObj.put("ecd", "4");
			rsltJObj.put("emsg ", exp.toString());
			return rsltJObj.toJSONString();
		}
	}

}
