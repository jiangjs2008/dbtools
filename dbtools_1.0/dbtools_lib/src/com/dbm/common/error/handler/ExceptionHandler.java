/**
 * Copyright c JiangJusheng 2012 All Rights Reserved.
 * ShougaiExceptionHandler.java
 */
package com.dbm.common.error.handler;

import java.lang.Thread.UncaughtExceptionHandler;




public interface ExceptionHandler extends UncaughtExceptionHandler {

	/**
	 * 异常时处理
	 *
	 * @param exp 异常信息
	 */
	public abstract void execute(Throwable exp);

}
