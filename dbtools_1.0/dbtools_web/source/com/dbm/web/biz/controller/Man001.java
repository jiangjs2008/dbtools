package com.dbm.web.biz.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.dbm.common.property.ConnBean;
import com.dbm.common.property.FavrBean;
import com.dbm.common.property.PropUtil;
import com.dbm.common.util.SecuUtil;
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
public class Man001 extends DefaultController {


	@RequestMapping("/ajax/getdblist.do")
	@ResponseBody
	public String getDbList() {
		try {
			FavrBean[] favrList = PropUtil.getFavrInfo();
			ArrayList<JSONObject> columnInfo = new ArrayList<JSONObject>();
			for (FavrBean favr : favrList) {
				if (favr == null) {
					continue;
				}
				JSONObject item = new JSONObject();
				item.put("description", favr.description);
				item.put("name", favr.name);
				item.put("favrid", favr.favrId);
				columnInfo.add(item);
			}
			return JSON.toJSONString(columnInfo);

		} catch (Exception exp) {
			logger.error("", exp);
			return "";
		}
	}

	@RequestMapping("/ajax/getdblogininfo.do")
	@ResponseBody
	public String getDbLoginInfo(@RequestParam Map<String,String> requestParam) {

		JSONObject item = new JSONObject();

		int deployType = NumberUtils.toInt(PropUtil.getAppConfig("deploy.type"));
		if (deployType == 1) {
			// 本程序部署在远程服务器上，为确保安全性，必须手动输入用户名及密码
			return item.toJSONString();
		}

		int favrId = NumberUtils.toInt(requestParam.get("favrid"), -1);
		if (favrId < 0) {
			return item.toJSONString();
		}

		FavrBean favr = PropUtil.getFavrInfo(favrId);

		item.put("status", "ok");
		item.put("account", SecuUtil.decryptBASE64(favr.user));
		item.put("password", SecuUtil.decryptBASE64(favr.password));

		return item.toJSONString();
	}

	@RequestMapping("/login.do")
	public ModelAndView login(@RequestParam Map<String,String> requestParam) {
		int favrId = NumberUtils.toInt(requestParam.get("favrid"), -1);
		if (favrId < 0) {
			return new ModelAndView("man001").addObject("errcode", 5);
		}
		String userId = requestParam.get("user");
		String passwd = requestParam.get("pwd");

		FavrBean favr = PropUtil.getFavrInfo(favrId);
		if (!userId.equals(favr.user) || !passwd.equals(favr.password)) {
			return new ModelAndView("man001").addObject("errcode", 1);
		}

		// 连接信息
		userId = SecuUtil.decryptBASE64(userId);
		passwd = SecuUtil.decryptBASE64(passwd);
		ConnBean connInfo = PropUtil.getDbConnInfo(favr.driverId);

		// 登陆到数据库
		DbClientFactory.createDbClient(connInfo.action);
		DbClient dbClient = DbClientFactory.getDbClient();
		dbClient.setPageSize(StringUtil.parseInt(PropUtil.getAppConfig("page.data.count")));
		dbClient.start(new String[] { connInfo.driver, favr.url, userId, passwd });

		// 显示数据库内容：表、视图等等
		List<String> objList = dbClient.getDbMetaData();
		ArrayList<HashMap<String, Object>> dbInfo = new ArrayList<HashMap<String, Object>>(objList.size());
		for (String item : objList) {
			HashMap<String, Object> objMap = new HashMap<String, Object>(2);
			objMap.put("text", item);
			objMap.put("hasChildren", true);
			objMap.put("isCatalog", true);
			objMap.put("isQuery", false);
			dbInfo.add(objMap);
		}
		
		return new ModelAndView("man002").addObject("dbInfo", JSON.toJSONString(dbInfo));
	}

	@RequestMapping("/logout.do")
	public ModelAndView logout() {
		// 关闭数据库连接
		DbClientFactory.close();
		return new ModelAndView("man001");
	}

}
