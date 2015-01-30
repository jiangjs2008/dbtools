package com.dbm.web.biz.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

/**
 * [name]<br>
 * Controller基类<br><br>
 * [function]<br>
 * Controller的缺省实现<br><br>
 * [history]<br>
 * 2014/03/26 ver1.00 JiangJusheng<br>
 */
public abstract class DefaultController {

	/**
	 * LOG出力对象
	 */
	protected final static Logger logger = Logger.getLogger(DefaultController.class);

	/**
	 * 异常处理
	 *
	 * @param exp 异常信息
	 * @param request HTTP request对象
	 *
	 * @return ModelAndView 迁移到系统异常画面
	 */
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public final String processException(Exception exp, HttpServletRequest request) {
		logger.error(request.getServletPath(), exp);
		JSONObject rsltJObj = new JSONObject();
		rsltJObj.put("emsg", exp.getMessage());
		rsltJObj.put("ecd", "-1");
		return rsltJObj.toJSONString();
	}

}
