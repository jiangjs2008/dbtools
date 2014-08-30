package com.dbm.web.biz.controller;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
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
 * Mpc0110 Controller<br><br>
 * [function]<br>
 * 修改车机信息<br><br>
 * [history]<br>
 * 2014/05/05 ver1.00 JiangJusheng<br>
 */
@Controller
public class Man0022 extends DefaultController {

	@RequestMapping("/biz/inf001.do")
	@ResponseBody
	public String getCmp0030View(@RequestParam Map<String,String> requestParam){
		logger.debug("/sale/cmp0010.do =>getCmp0010View()");
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
			params.put("ecd", 0);
			params.put("rows", allData);
			return params.toJSONString();

		} catch (Exception exp) {
			logger.error("", exp);
			JSONObject params = new JSONObject();
			params.put("ecd", 1);
			return params.toJSONString();
		}

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
