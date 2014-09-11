package com.dbm.web.biz.controller;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

	@RequestMapping("/ajax/login.do")
	@ResponseBody
	public String login(@RequestParam Map<String,String> requestParam, HttpServletRequest request) {
		JSONObject rslt = new JSONObject();
		int favrId = NumberUtils.toInt(requestParam.get("favrid"), -1);
		if (favrId < 0) {
			rslt.put("ecd", 5);
			return rslt.toJSONString();
		}
		String userId = requestParam.get("user");
		String passwd = requestParam.get("pwd");

		FavrBean favr = PropUtil.getFavrInfo(favrId);
		if (!userId.equals(favr.user) || !passwd.equals(favr.password)) {
			rslt.put("ecd", 1);
			return rslt.toJSONString();
		}

		// 连接信息
		userId = SecuUtil.decryptBASE64(userId);
		passwd = SecuUtil.decryptBASE64(passwd);
		ConnBean connInfo = PropUtil.getDbConnInfo(favr.driverId);

		// 登陆到数据库
		try {
			DbClientFactory.createDbClient(connInfo.action);
			DbClient dbClient = DbClientFactory.getDbClient();
			dbClient.setPageSize(StringUtil.parseInt(PropUtil.getAppConfig("page.data.count")));
			if (!dbClient.start(new String[] { connInfo.driver, favr.url, userId, passwd })) {
				rslt.put("ecd", "6");
				return rslt.toJSONString();
			}
		} catch (Exception exp) {
			logger.error("登陆到数据库时发生错误", exp);
			rslt.put("ecd", "6");
			return rslt.toJSONString();
		}

		HttpSession session = request.getSession(true);
		session.setAttribute("_userid", session.getId());

		rslt.put("ecd", "ok");
		return rslt.toJSONString();
	}

	@RequestMapping("/biz/man001.do")
	public ModelAndView getCmp0030View() {
		return new ModelAndView("man002").addObject("deployType", PropUtil.getAppConfig("deploy.type"));
	}

	@RequestMapping("/logout.do")
	public ModelAndView logout() {
		// 关闭数据库连接
		DbClientFactory.close();
		return new ModelAndView("man001");
	}

}
