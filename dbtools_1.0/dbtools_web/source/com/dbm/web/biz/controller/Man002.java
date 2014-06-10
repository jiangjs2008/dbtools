package com.dbm.web.biz.controller;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
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


	@RequestMapping("/ajax/gridcol.do")
	@ResponseBody
	public String mpc0110query(@RequestParam Map<String,String> requestParam) {
		logger.debug("/ajax/sale/mpc0110query.do =>mpc0110query()");
		String _tblName = requestParam.get("tblname");

		if (_tblName.indexOf('.') > 0) {
			String[] arr = StringUtils.split(_tblName, '.');
			_tblName = arr[1];
		}

		DbClient dbClient = DbClientFactory.getDbClient();

		try {
			Connection _dbConn = dbClient.getConnection();
			DatabaseMetaData dmd = _dbConn.getMetaData();

			// 取得指定表的所有列的信息
			ResultSet columnRs = dmd.getColumns(null, null, _tblName, "%");
			String colName = null;
			ArrayList<JSONObject> columnInfo = new ArrayList<JSONObject>();
			while (columnRs.next()) {
				// 列名
				colName = columnRs.getString(4);
				JSONObject params = new JSONObject();
				params.put("header", colName);
				params.put("name", colName);
				columnInfo.add(params);
			}
			return JSON.toJSONString(columnInfo);

		} catch (Exception exp) {
			logger.error("", exp);
			return "";
		}
	}

	@RequestMapping("/ajax/griddata.do")
	@ResponseBody
	public String mpc0120dispinfo(@RequestParam Map<String,String> requestParam) {
		logger.debug("/ajax/sale/mpc0120dispinfo.do =>mpc0120dispinfo()");
		String _tblName = requestParam.get("tblname");
		int start = NumberUtils.toInt(requestParam.get("start"), -1);
		int pageNum = 0;
		if (start == -1) {
			pageNum = 1;
		} else {
			pageNum = start / 100 + 1;
		}
		try {

			DbClient dbClient = DbClientFactory.getDbClient();
			dbClient.setTableName(_tblName);
			ResultSet rs = dbClient.defaultQuery(pageNum);

			String colName = null;
			ResultSetMetaData rsm = rs.getMetaData();
			
			ArrayList<JSONObject> columnInfo = new ArrayList<JSONObject>();
			while (rs.next()) {
				JSONObject params = new JSONObject();
				for (int i = 1, lengs = rsm.getColumnCount() + 1; i < lengs; i ++) {
					colName = rsm.getColumnName(i);
					params.put(colName, dbClient.procCellData(rs.getObject(i)));
				}
				columnInfo.add(params);
			}

			JSONObject params = new JSONObject();
			params.put("total", dbClient.size());
			params.put("rows", columnInfo);
			return params.toJSONString();

		} catch (Exception exp) {
			logger.error("", exp);
			JSONObject params = new JSONObject();
			params.put("total", 0);
			params.put("rows", new ArrayList<JSONObject>());
			params.put("errorMsg ", exp.toString());
			return params.toJSONString();
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

		String _tblName = requestParam.get("tblname");
		String realName = _tblName;
		String[] tblNameArr = StringUtil.split(_tblName, ".");
		if (tblNameArr.length == 2) {
			realName = tblNameArr[1];
		}

		ArrayList<JSONObject> allData = new ArrayList<JSONObject>();
		try {
			DbClient dbClient = DbClientFactory.getDbClient();
			DatabaseMetaData dmd = dbClient.getConnection().getMetaData();
			// 取得指定表的主键信息
			ResultSet pkRs = dmd.getPrimaryKeys(null, null, realName);
			ArrayList<String> pkList = new ArrayList<String>();
			while (pkRs.next()) {
				pkList.add(pkRs.getString(4));
			}
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

		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}

	}
}
