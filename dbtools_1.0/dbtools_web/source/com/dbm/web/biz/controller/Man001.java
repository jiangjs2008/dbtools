package com.dbm.web.biz.controller;

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
import com.dbm.common.property.FavrBean;
import com.dbm.common.property.PropUtil;

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

	@RequestMapping("/man001.do")
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

		return new ModelAndView("man002");
	}

}
