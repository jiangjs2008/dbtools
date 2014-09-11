package com.dbm.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * [name]<br>
 * LoginFilter<br><br>
 * [function]<br>
 * 用户登录验证<br><br>
 * [history]<br>
 * 2014/04/07 ver1.00 WangJianshan<br>
 */
public class LoginFilter implements Filter {

	/**
	 * LOG出力对象
	 */
	private final static Logger logger = Logger.getLogger(LoginFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String servletPath = httpRequest.getServletPath();

		// 如果访问首页则不验证
		if ("/".equals(servletPath)
				|| "/jsp/man001.jsp".equals(servletPath)
				|| "/ajax/getdblist.do".equals(servletPath)
				|| "/ajax/getdblogininfo.do".equals(servletPath)
				|| "/ajax/login.do".equals(servletPath)
				|| "/logout.do".equals(servletPath)) {

		} else {
			HttpSession session = httpRequest.getSession(true);
			// 从session 里面获取用户名的信息
			String userId = (String) session.getAttribute("_userid");
			// 判断如果没有取到用户信息，就跳转到登陆页面，提示用户进行登陆
			if (userId == null) {
				logger.error("用户未登陆：" + servletPath);
				if (servletPath.startsWith("/ajax/")) {
					PrintWriter out = response.getWriter();
					StringBuilder sbRes = new StringBuilder();
					sbRes.append("{\"ecd\":\"9\"}");
					out.print(sbRes.toString());
					out.flush();
					out.close();
					return;
				} else {
					request.getRequestDispatcher("/jsp/sessionerror.jsp").forward(request, response);
					return;
				}
			}
		}

		chain.doFilter(request, response);
		return;
	}

	@Override
	public void destroy() {
	}

}
