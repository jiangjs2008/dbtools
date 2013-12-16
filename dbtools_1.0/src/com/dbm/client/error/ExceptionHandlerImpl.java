/**
 * Copyright c JiangJusheng 2012 All Rights Reserved.
 * ShougaiExceptionHandler.java
 */
package com.dbm.client.error;

import com.dbm.client.ui.Msg01Dialog;
import com.dbm.client.util.LoggerWrapper;



public class ExceptionHandlerImpl extends ExceptionHandler {

	/**
	 * LOG
	 */
	private static LoggerWrapper logger = new LoggerWrapper(ExceptionHandlerImpl.class);

	ExceptionHandlerImpl() {
	}

	@Override
	public void execute(Throwable exp) {

		if (exp instanceof WarningException) {
			// 输出警告信息
			logger.warn(exp.getMessage());
			// 弹出确认对话框
			Msg01Dialog.showMsgDialog(exp.getMessage());

		} else if (exp instanceof BaseException) {
			// 
			logger.error(exp.getMessage());
			logErrStackMsg(exp);

		} else if (exp instanceof BaseExceptionWrapper) {
			// 
			logger.error(exp.getCause().getMessage());
			logErrStackMsg(exp.getCause());
			Msg01Dialog.showMsgDialog("操作数据库时发生异常，请查看日志文件，或联系数据库管理员");

		} else {
			logger.error(exp);
			logThreadInfo(Thread.currentThread());
		}
	}

	/**
	 * @see Thread$UncaughtExceptionHandler#uncaughtException()
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		logThreadInfo(thread);
		logErrStackMsg(ex);
	}

	/**
	 * 记录错误信息到日志文件
	 * 
	 * @param ex 异常事件对象
	 */
	private void logErrStackMsg(Throwable ex) {
		Throwable thexp = ex.getCause();
		if (thexp == null) {
			logger.error(ex);
		} else {
			logger.error(ex);
			logErrStackMsg(thexp);
		}
	}

	/**
	 * 记录线程信息到日志文件
	 * 
	 * @param thread 线程对象
	 */
	private void logThreadInfo(Thread thread) {
		StringBuilder errInfo = new StringBuilder();
		errInfo.append("Thread: id=");
		errInfo.append(thread.getId());
		errInfo.append(" name=");
		errInfo.append(thread.getName());
		errInfo.append(" state=");
		errInfo.append(thread.getState().name());
		errInfo.append(" [current active thread count:");
		errInfo.append(Thread.activeCount());
		errInfo.append("]");
		logger.info(errInfo.toString());
	}
}
