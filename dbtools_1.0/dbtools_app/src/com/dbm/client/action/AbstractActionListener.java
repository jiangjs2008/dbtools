package com.dbm.client.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.dbm.client.error.handler.ExceptionHandlerFactory;
import com.dbm.common.log.LoggerWrapper;

public abstract class AbstractActionListener implements ActionListener {

	/**
	 * instances of the log class
	 */
	protected final static LoggerWrapper logger = new LoggerWrapper(AbstractActionListener.class); 

	private long spTime = 0;

	@Deprecated
	public final void actionPerformed(ActionEvent e) {
		long nowTime = System.currentTimeMillis();
		if (nowTime - spTime < 1000) {
			// 与上次操作间隔小于1秒，忽略此次动作
			logger.info("与上次操作间隔小于1秒");
			spTime = nowTime; 
			return;
		}
		spTime = nowTime;

		CursorChanger cc = new CursorChanger((Component) e.getSource());
		cc.show();
		try {
			doActionPerformed(e);
		} catch (Throwable exp) {
			ExceptionHandlerFactory.getExceptionHandler().execute(exp);
		} finally {
			cc.restore();
			doAfterFinally();
		}
	}

	protected abstract void doActionPerformed(ActionEvent e);

	protected void doAfterFinally() {
	}
}
