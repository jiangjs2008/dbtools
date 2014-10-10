package com.dbm.web.biz.controller;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.web.util.SQLsSession;

/**
 * [name]<br>
 * Man0022 Controller<br><br>
 * [function]<br>
 * 获取表定义及索引定义信息<br><br>
 * [history]<br>
 * 2014/05/05 ver1.00 JiangJusheng<br>
 */
@Controller
public class Man0022 extends DefaultController {

	/**
	 * 获取表定义信息
	 *
	 * @param requestParam HTTP请求参数
	 *
	 * @return String 表定义信息
	 */
	@RequestMapping("/ajax/biz/inf001.do")
	@ResponseBody
	public String getTblDefInfo(@RequestParam Map<String,String> requestParam){
		logger.debug("/sale/cmp0010.do =>getCmp0010View()");
		JSONObject rsltJObj = new JSONObject();
		rsltJObj.put("total", 0);
		rsltJObj.put("rows", "{}");

		try {
			DbClient dbClient = DbClientFactory.getDbClient();
			if (dbClient == null) {
				logger.error("数据库联接不正常");
				rsltJObj.put("ecd", "9");
				rsltJObj.put("emsg", "数据库联接不正常");
				return rsltJObj.toJSONString();
			}
			String realName = requestParam.get("tblname");
			realName = getRealTblName(dbClient, realName);
			if (realName == null) {
				logger.error("执行时错误 参数不对");
				rsltJObj.put("ecd", "5");
				return rsltJObj.toJSONString();
			}
			DatabaseMetaData dmd = dbClient.getConnection().getMetaData();

			String schema = null;
			try {
				if (dbClient.hasSchema()) {
					schema = dbClient.getConnection().getMetaData().getUserName();
				}
			} catch (SQLException ex) {
				logger.error(ex);
			}

			// 取得指定表的主键信息
			ResultSet pkRs = dmd.getPrimaryKeys(null, schema, realName);
			ArrayList<String> pkList = new ArrayList<String>();
			if (pkRs != null) {
				while (pkRs.next()) {
					pkList.add(pkRs.getString(4)); // COLUMN_NAME
				}
			}

			ArrayList<HashMap<String, String>> allData = new ArrayList<HashMap<String, String>>();
			// 取得指定表的所有列的信息
			ResultSet columnRs = dmd.getColumns(null, schema, realName, "%");
			String tempValue = null;

			while (columnRs.next()) {
				HashMap<String, String> columnInfo = new HashMap<String, String>(8);
				// *注：这里必须按照列的顺序(按列序号由小到大)来取值

				// 列名 COLUMN_NAME
				tempValue = columnRs.getString(4);
				columnInfo.put("colname", tempValue);
				// 类型名 TYPE_NAME
				columnInfo.put("type", columnRs.getString(6));
				// 列的大小 COLUMN_SIZE
				columnInfo.put("size", columnRs.getString(7));

				// 是否为主键
				if (pkList.indexOf(tempValue) >= 0) {
					// 是主键
					columnInfo.put("pk", "Y");
				} else {
					columnInfo.put("pk", "");
				}

				// 是否可为空 NULLABLE
				tempValue = columnRs.getString(11);
				if ("YES".equalsIgnoreCase(tempValue) || "true".equalsIgnoreCase(tempValue) || "1".equals(tempValue)) {
					// 可为空
					columnInfo.put("nullable", "Y");
				} else {
					// 不可为空
					columnInfo.put("nullable", "");
				}

				// 列的注释 REMARKS
				columnInfo.put("remark", columnRs.getString(12));

				// 默认值 COLUMN_DEF
				columnInfo.put("colvalue", columnRs.getString(13));

				// 是否自动增加 IS_AUTOINCREMENT
				tempValue = columnRs.getString(23);
				if ("YES".equalsIgnoreCase(tempValue) || "true".equalsIgnoreCase(tempValue) || "1".equals(tempValue)) {
					columnInfo.put("autoinc", "Y");
				} else {
					columnInfo.put("autoinc", "");
				}

				allData.add(columnInfo);
			}

			rsltJObj.put("ecd", "0");
			rsltJObj.put("total", allData.size());
			rsltJObj.put("rows", allData);

		} catch (Exception exp) {
			logger.error("", exp);
			rsltJObj.put("ecd", "4");
			rsltJObj.put("emsg ", exp.toString());
		}
		return rsltJObj.toJSONString();
	}

	/**
	 * 获取索引定义信息
	 *
	 * @param requestParam HTTP请求参数
	 *
	 * @return String 表定义信息
	 */
	@RequestMapping("/ajax/biz/inf002.do")
	@ResponseBody
	public String getTblIdxInfo(@RequestParam Map<String,String> requestParam){
		logger.debug("/sale/cmp0010.do =>getCmp0010View()");
		JSONObject rsltJObj = new JSONObject();
		rsltJObj.put("total", 0);
		rsltJObj.put("rows", "{}");

		try {
			DbClient dbClient = DbClientFactory.getDbClient();
			if (dbClient == null) {
				logger.error("数据库联接不正常");
				rsltJObj.put("ecd", "9");
				rsltJObj.put("emsg", "数据库联接不正常");
				return rsltJObj.toJSONString();
			}
			String realName = getRealTblName(dbClient, requestParam.get("tblname"));
			if (realName == null) {
				logger.error("执行时错误 参数不对");
				rsltJObj.put("ecd", "5");
				return rsltJObj.toJSONString();
			}
			DatabaseMetaData dmd = dbClient.getConnection().getMetaData();

			String schema = null;
			try {
				if (dbClient.hasSchema()) {
					schema = dbClient.getConnection().getMetaData().getUserName();
				}
			} catch (SQLException ex) {
				logger.error(ex);
			}

			// 取得指定表的索引信息
			ResultSet columnRs = dmd.getIndexInfo(null, schema, realName, true, true);
			String tempValue = null;
			HashMap<String, String>  columnInfo = null;
			ArrayList<HashMap<String, String>> allData = new ArrayList<HashMap<String, String>>();

			while (columnRs.next()) {
				columnInfo = new HashMap<String, String>(6);

				// 是否不唯一(NON_UNIQUE)
				tempValue = columnRs.getString(4);
				if ("YES".equalsIgnoreCase(tempValue) || "true".equalsIgnoreCase(tempValue) || "1".equals(tempValue)) {
					columnInfo.put("nun", "Y");
				} else {
					columnInfo.put("nun", "");
				}

				// 索引名称(INDEX_NAME)
				columnInfo.put("name", columnRs.getString(6));
				// 索引类型(TYPE)
				tempValue = columnRs.getString(7);
				if ("0".equals(tempValue)) {
					tempValue = "0:tableIndexStatistic";
				} else if ("1".equals(tempValue)) {
					tempValue = "1:tableIndexClustered";
				} else if ("2".equals(tempValue)) {
					tempValue = "2:tableIndexHashed";
				} else if ("3".equals(tempValue)) {
					tempValue = "3:tableIndexOther";
				}
				columnInfo.put("type", tempValue);

				// 索引中的列序列号(ORDINAL_POSITION)
				columnInfo.put("ord", columnRs.getString(8));
				// 列名称 COLUMN_NAME
				columnInfo.put("colname", columnRs.getString(9));
				// 列排序序列(ASC_OR_DESC)
				columnInfo.put("asc", columnRs.getString(10));

				allData.add(columnInfo);
			}

			rsltJObj.put("total", allData.size());
			rsltJObj.put("rows", allData);

		} catch (Exception exp) {
			logger.error("", exp);
			rsltJObj.put("ecd", "4");
			rsltJObj.put("emsg ", exp.toString());
		}
		return rsltJObj.toJSONString();
	}
	
	/**
	 * 获取真实表名称，过滤Schema名
	 *
	 * @param dbClient 数据库客户端对象
	 * @param tblName  表名
	 *
	 * @return String 真实表名称
	 */
	private String getRealTblName(DbClient dbClient, String tblName) {
		if (tblName == null || tblName.length() == 0) {
			return null;
		}
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

	/**
	 * 获取SQL脚本执行纪录
	 *
	 * @param requestParam HTTP请求参数
	 *
	 * @return String SQL脚本执行纪录
	 */
	@RequestMapping("/ajax/biz/sqlhis001.do")
	@ResponseBody
	public String getSqlHis(@RequestParam Map<String,String> requestParam){
		logger.debug("/sale/cmp0010.do =>getCmp0010View()");
		JSONObject rsltJObj = new JSONObject();
		rsltJObj.put("total", 0);
		rsltJObj.put("rows", "{}");

		String clientId = requestParam.get("clientid");
		if (clientId == null || clientId.length() == 0) {
			logger.error("执行时错误 参数不对");
			rsltJObj.put("ecd", "5");
			return rsltJObj.toJSONString();
		}
		Map<String, String> vmap = SQLsSession.getSqlHis(clientId, null);
		ArrayList<HashMap<String, String>> allData = new ArrayList<HashMap<String, String>>();

		if (vmap != null) {
			for (Entry<String, String> entry : vmap.entrySet()) {
				HashMap<String, String> columnInfo = new HashMap<String, String>(2);
				columnInfo.put("sqls", entry.getKey());
				columnInfo.put("time", entry.getValue());
				allData.add(columnInfo);
			}
		}

		rsltJObj.put("ecd", "0");
		rsltJObj.put("total", allData.size());
		rsltJObj.put("rows", allData);

		return rsltJObj.toJSONString();
	}

}
