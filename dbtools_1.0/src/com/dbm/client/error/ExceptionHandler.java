/**
 * Copyright c JiangJusheng 2012 All Rights Reserved.
 * ShougaiExceptionHandler.java
 */
package com.dbm.client.error;

import java.lang.Thread.UncaughtExceptionHandler;




public abstract class ExceptionHandler implements UncaughtExceptionHandler {

	private static ExceptionHandler implHandler = new ExceptionHandlerImpl();

	public static ExceptionHandler getInstance() {
		return implHandler;
	}

	/**
	 * 异常时处理
	 *
	 * @param exp 异常信息
	 */
	public abstract void execute(Throwable exp);

}
