/**
 * Copyright c JiangJusheng 2012 All Rights Reserved.
 * ShougaiExceptionHandler.java
 */
package com.dbm.client.error.handler;

import com.dbm.common.error.handler.ExceptionHandler;



public class ExceptionHandlerFactory {

	private static ExceptionHandler expHandler= null;

	public static ExceptionHandler getExceptionHandler() {
		if (expHandler == null) {
			expHandler = new ExceptionHandlerImpl();
		}
		return expHandler;
	}

}
