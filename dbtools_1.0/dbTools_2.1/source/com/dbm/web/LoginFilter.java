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

/**
 * [name]<br>
 * LoginFilter<br><br>
 * [function]<br>
 * 用户登录验证<br><br>
 * [history]<br>
 * 2014/04/07 ver1.00 WangJianshan<br>
 */
public class LoginFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String servletPath = httpRequest.getServletPath();

		if (!servletPath.endsWith(".do")) {
			if (servletPath.indexOf(".do?") <= 3) {
				PrintWriter out = response.getWriter();
				out.print("bye.");
				out.flush();
				out.close();
				return;
			}
		}
		// 验证用户(用户名，密码，校验码)
		String userId = httpRequest.getHeader("s1");
		String passWd = httpRequest.getHeader("s2");
		String qurCode = httpRequest.getHeader("s3");

		// 验证会话状态
		
		
		chain.doFilter(request, response);
		return;
	}

	@Override
	public void destroy() {
	}

}
