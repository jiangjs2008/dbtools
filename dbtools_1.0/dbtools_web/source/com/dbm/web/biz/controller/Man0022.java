package com.dbm.web.biz.controller;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.util.StringUtil;

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
	 * @param requestParam
	 *
	 * @return
	 */
	@RequestMapping("/ajax/biz/inf001.do")
	@ResponseBody
	public String getCmp0030View(@RequestParam Map<String,String> requestParam){
		logger.debug("/sale/cmp0010.do =>getCmp0010View()");
		JSONObject rsltJObj = new JSONObject();
		rsltJObj.put("total", 0);
		rsltJObj.put("rows", "{}");

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

			ArrayList<HashMap<String, String>> allData = new ArrayList<HashMap<String, String>>();
			// 取得指定表的所有列的信息
			ResultSet columnRs = dmd.getColumns(null, null, realName, "%");
			String colName = null;
			String tempValue = null;
			int total = 0;

			while (columnRs.next()) {
				HashMap<String, String> columnInfo = new HashMap<String, String>(6);

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

				// 是否自动增加(IS_AUTOINCREMENT)
				tempValue = columnRs.getString(23);
				if ("YES".equalsIgnoreCase(tempValue)) {
					columnInfo.put("autoinc", "Y");
				} else {
					columnInfo.put("autoinc", "");
				}

				// 默认值(COLUMN_DEF)
				columnInfo.put("colvalue", columnRs.getString(13));

				// 列的注释
				columnInfo.put("remark", StringUtil.NVL(columnRs.getString(12)));

				total ++;
				allData.add(columnInfo);
			}

			rsltJObj.put("total", total);
			rsltJObj.put("rows", allData);

		} catch (Exception exp) {
			logger.error("", exp);
			rsltJObj.put("ecd", 1);
		}
		return rsltJObj.toJSONString();
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

}
