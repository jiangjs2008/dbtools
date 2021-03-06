package com.dbm.web.biz.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dbm.common.util.MetaDataUtil;
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

	private final static String[] tbldefHeader = new String[] { "", "colname", "type", "size", "pk", "nullable", "autoinc", "colvalue", "remark" };
	private final static String[] tblidxHeader = new String[] { "", "name", "type", "nun", "ord", "colname", "asc" };

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
		logger.debug("/ajax/biz/inf001.do =>getTblDefInfo()");
		JSONObject rsltJObj = new JSONObject();
		String realName = requestParam.get("tblname");

		Vector<Vector<String>> allData = MetaDataUtil.getTblDefInfo(realName);
		if (allData == null) {
			logger.error("无法获取表定义信息 " + realName);
			rsltJObj.put("total", 0);
			rsltJObj.put("rows", "{}");
			rsltJObj.put("ecd", "4");
			rsltJObj.put("emsg ", "无法获取表定义信息");
		} else {
			rsltJObj.put("ecd", "0");
			rsltJObj.put("total", allData.size());

			ArrayList<HashMap<String, String>> defData = new ArrayList<HashMap<String, String>>(allData.size());
			for (Vector<String> obj : allData) {
				HashMap<String, String> columnInfo = new HashMap<String, String>(8);
				for (int i = 1; i < 9; i ++) {
					columnInfo.put(tbldefHeader[i], obj.get(i));
				}
				defData.add(columnInfo);
			}
			rsltJObj.put("rows", defData);
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
		logger.debug("/ajax/biz/inf002.do =>getTblIdxInfo()");
		JSONObject rsltJObj = new JSONObject();
		String realName = requestParam.get("tblname");

		Vector<Vector<String>> allData = MetaDataUtil.getTblIdxInfo(realName);
		if (allData == null) {
			logger.error("无法获取索引定义信息 " + realName);
			rsltJObj.put("total", 0);
			rsltJObj.put("rows", "{}");
			rsltJObj.put("ecd", "4");
			rsltJObj.put("emsg ", "无法获取索引定义信息");
		} else {
			rsltJObj.put("ecd", "0");
			rsltJObj.put("total", allData.size());

			ArrayList<HashMap<String, String>> idxData = new ArrayList<HashMap<String, String>>(allData.size());
			for (Vector<String> obj : allData) {
				HashMap<String, String> columnInfo = new HashMap<String, String>(6);
				for (int i = 1; i < 7; i ++) {
					columnInfo.put(tblidxHeader[i], obj.get(i));
				}
				idxData.add(columnInfo);
			}
			rsltJObj.put("rows", idxData);
		}

		return rsltJObj.toJSONString();
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
