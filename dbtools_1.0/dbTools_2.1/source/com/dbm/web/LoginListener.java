package com.dbm.web;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.dbm.common.db.DbClient;

/**
 * [name]<br>
 * LoginListener<br><br>
 * [function]<br>
 * 用户登录验证<br><br>
 * [history]<br>
 * 2014/04/07 ver1.00 WangJianshan<br>
 */
public class LoginListener implements HttpSessionListener,HttpSessionAttributeListener {

	/**
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {
	}

	/**
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		DbClient dbClient = (DbClient) event.getSession().getAttribute("dbclient");
		if (dbClient != null) {
			dbClient.close();
		}
	}

	/**
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeAdded(HttpSessionBindingEvent)
	 */
	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {
	}

	/**
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeRemoved(HttpSessionBindingEvent)
	 */
	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {
		if (event.getName().equals("_userid")) {
			DbClient dbClient = (DbClient) event.getSession().getAttribute("dbclient");
			if (dbClient != null) {
				dbClient.close();
			}
		}
	}

	/**
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeReplaced(HttpSessionBindingEvent)
	 */
	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {
	}
}
