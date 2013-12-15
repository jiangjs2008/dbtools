/**
 * 
 */
package com.dbm.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jiangjs
 *
 */
public class DispatchServlet extends HttpServlet {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4064344546438070112L;

	public DispatchServlet() {

	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}

	/**
	 * <p>Process an HTTP "GET" request.</p>
	 *
	 * @param request  The servlet request we are processing
	 * @param response The servlet response we are creating
	 * @throws IOException      if an input/output error occurs
	 * @throws ServletException if a servlet exception is thrown
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}

	/**
	 * <p>Process an HTTP "POST" request.<br>
	 * Perform the standard request processing for this request, 
	 * and create the corresponding response.</p>
	 *
	 * @param request  The servlet request we are processing
	 * @param response The servlet response we are creating
	 * @throws IOException      if an input/output error occurs
	 * @throws ServletException if a servlet exception is thrown
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)  {
//		try {
//			String actionName = request.getParameter("action");
//			//process(request, response);
//		} catch (IOException ioexp) {
//			
//			
//		} catch (ServletException svexp) {
//			
//			
//		}
	}

}
