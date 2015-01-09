package com.dbm.web.biz.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.dbm.web.util.SQLsSession;

/**
 * [name]<br>
 * Man001 Controller<br><br>
 * [function]<br>
 * 数据库登录<br><br>
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

	@RequestMapping("/ajax/createopid.do")
	@ResponseBody
	public String createOpId() {
		JSONObject item = new JSONObject();
		item.put("opid", SQLsSession.createOpId());
		return item.toJSONString();
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

	@RequestMapping("/index.do")
	public ModelAndView getMain(@RequestParam Map<String,String> requestParam, HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		Integer errCnt = (Integer) session.getAttribute("errCnt");
		if (errCnt == null) {
			errCnt = 0;
		}
		if (errCnt != null && errCnt > 6) {
			return new ModelAndView("syserror").addObject("errlimit", "1");
		}
//		String userName = StringUtils.trimToNull(requestParam.get("username"));
//		String password = StringUtils.trimToNull(requestParam.get("password"));
//		if (userName == null || userName == null) {
//			session.setAttribute("errCnt", errCnt + 1);
//			return new ModelAndView("index").addObject("ecd", "1");
//		}
//		if (!userName.equals("Y3h1c2VyMjAxNA==") || !password.equals("Y3hAMjAxNF8jUHE=")) {
//			session.setAttribute("errCnt", errCnt + 1);
//			return new ModelAndView("index").addObject("ecd", "1");
//		}
		return new ModelAndView("man001");
	}

	@RequestMapping(value="/login.do", method=RequestMethod.POST)
	public ModelAndView login(@RequestParam Map<String,String> requestParam, HttpServletRequest request) {
		int favrId = NumberUtils.toInt(requestParam.get("favrid"), -1);
		if (favrId < 0) {
			return new ModelAndView("man001").addObject("ecd", 5);
		}
		String userId = requestParam.get("user");
		String passwd = requestParam.get("pwd");

		FavrBean favr = PropUtil.getFavrInfo(favrId);
		if (!userId.equals(favr.user) || !passwd.equals(favr.password)) {
			return new ModelAndView("man001").addObject("ecd", 1);
		}

		// 连接信息
		userId = SecuUtil.decryptBASE64(userId);
		passwd = SecuUtil.decryptBASE64(passwd);
		ConnBean connInfo = PropUtil.getDbConnInfo(favr.driverId);

		// 登陆到数据库
		try {
			DbClientFactory.createDbClient(connInfo.action);
			DbClient dbClient = DbClientFactory.getDbClient();
			int dataLimit = NumberUtils.toInt(PropUtil.getAppConfig("page.data.count"));
			dbClient.setPageSize(dataLimit);

			if (!dbClient.start(new String[] { connInfo.driver, favr.url, userId, passwd })) {
				return new ModelAndView("man001").addObject("ecd", 6);
			}

			// 显示数据库内容：表、视图等等
			List<String> objList = dbClient.getTableTypes();
			ArrayList<HashMap<String, Object>> dbInfo = new ArrayList<HashMap<String, Object>>(objList.size());
			for (String item : objList) {
				HashMap<String, Object> objMap = new HashMap<String, Object>(2);
				objMap.put("text", item);
				objMap.put("hasChildren", true);
				objMap.put("isCatalog", true);
				objMap.put("isQuery", false);
				dbInfo.add(objMap);
			}

			HttpSession session = request.getSession(true);
			session.setAttribute("_userid", session.getId());
			SQLsSession.setCurrFavrInfo(favr);

			return new ModelAndView("man002").addObject("dbInfo", JSON.toJSONString(dbInfo)).addObject("dataLimit", dataLimit);

		} catch (Exception exp) {
			logger.error("登陆到数据库时发生错误", exp);
			return new ModelAndView("man001").addObject("ecd", 9);
		}
	}

	@RequestMapping("/logout.do")
	public ModelAndView logout() {
		// 关闭数据库连接
		DbClientFactory.close();
		return new ModelAndView("man001");
	}

}
