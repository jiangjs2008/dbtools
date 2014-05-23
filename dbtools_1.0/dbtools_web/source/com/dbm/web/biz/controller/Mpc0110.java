package com.dbm.web.biz.controller;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * [name]<br>
 * Mpc0110 Controller<br><br>
 * [function]<br>
 * 修改车机信息<br><br>
 * [history]<br>
 * 2014/05/05 ver1.00 JiangJusheng<br>
 */
@Controller
public class Mpc0110 extends DefaultController {


	@RequestMapping("/ajax/gridcol.do")
	@ResponseBody
	public String mpc0110query(@RequestParam Map<String,String> requestParam) {
		logger.debug("/ajax/sale/mpc0110query.do =>mpc0110query()");
		String _tblName = requestParam.get("tblname");
		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection _dbConn = DriverManager.getConnection("jdbc:mysql://172.60.100.101:3306/ctdw", "root", "123456");

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
		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection _dbConn = DriverManager.getConnection("jdbc:mysql://172.60.100.101:3306/ctdw", "root", "123456");
			String action = "select * from " + _tblName;
			Statement stmt = _dbConn.createStatement();
			ResultSet rs = stmt.executeQuery(action);

			String colName = null;
			ResultSetMetaData rsm = rs.getMetaData();
			
			ArrayList<JSONObject> columnInfo = new ArrayList<JSONObject>();
			while (rs.next()) {
				JSONObject params = new JSONObject();
				for (int i = 1, lengs = rsm.getColumnCount() + 1; i < lengs; i ++) {
					colName = rsm.getColumnName(i);
					params.put(colName, rs.getString(i));
				}
				columnInfo.add(params);
			}

			JSONObject params = new JSONObject();
			params.put("total", columnInfo.size());
			params.put("rows", columnInfo);
			return params.toJSONString();

		} catch (Exception exp) {
			logger.error("", exp);
			return "";
		}
	}


	@RequestMapping("/ajax/alldata.do")
	@ResponseBody
	public String alldispinfo(@RequestParam Map<String,String> requestParam) {
		logger.debug("/ajax/sale/mpc0120dispinfo.do =>mpc0120dispinfo()");
		String _tblName = requestParam.get("tblname");
		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection _dbConn = DriverManager.getConnection("jdbc:mysql://172.60.100.101:3306/ctdw", "root", "123456");
			String action = "select * from " + _tblName;
			Statement stmt = _dbConn.createStatement();
			ResultSet rs = stmt.executeQuery(action);

			String colName = null;
			ResultSetMetaData rsm = rs.getMetaData();
			
			ArrayList<String> columnInfo = null;
			ArrayList<ArrayList<String>> dataInfo = new ArrayList<ArrayList<String>>();
			while (rs.next()) {
				ArrayList<String> rowdata = new ArrayList<String>();
				
				if (columnInfo == null) {
					columnInfo = new ArrayList<String>();
					for (int i = 1, lengs = rsm.getColumnCount() + 1; i < lengs; i ++) {
						colName = rsm.getColumnName(i);
						columnInfo.add(colName);
					}
				}

				for (int i = 1, lengs = rsm.getColumnCount() + 1; i < lengs; i ++) {
					rowdata.add(rs.getString(i));
				}
				dataInfo.add(rowdata);
			}

			JSONObject params = new JSONObject();
			params.put("coldata", columnInfo);
			params.put("gridata", dataInfo);
			return params.toJSONString();

		} catch (Exception exp) {
			logger.error("", exp);
			return "";
		}
	}

}
