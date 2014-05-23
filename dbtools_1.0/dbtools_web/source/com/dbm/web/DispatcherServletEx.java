/**
 * 
 */
package com.dbm.web;

import org.springframework.web.servlet.DispatcherServlet;

import com.dbm.common.property.PropUtil;

/**
 * @author jiangjs
 *
 */
public class DispatcherServletEx extends DispatcherServlet {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4064344546438070112L;

	/**
	 * 缺省构造函数
	 */
	public DispatcherServletEx() {
		// 初始化配置信息
		PropUtil.load(ClassLoader.getSystemClassLoader().getResource("").getPath());
	}

	@Override
	public void destroy() {

	}


}
