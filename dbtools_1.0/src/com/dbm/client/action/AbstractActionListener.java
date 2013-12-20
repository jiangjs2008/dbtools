package com.dbm.client.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.dbm.client.error.ExceptionHandler;
import com.dbm.client.util.LoggerWrapper;

public abstract class AbstractActionListener implements ActionListener{

	/**
	 * instances of the log class
	 */
	protected final static LoggerWrapper logger = new LoggerWrapper(AbstractActionListener.class); 


	@Deprecated
	public final void actionPerformed(ActionEvent e) {
		CursorChanger cc = new CursorChanger((Component) e.getSource());
		cc.show();
		try {
			doActionPerformed(e);
		} catch (Throwable exp) {
			ExceptionHandler.getInstance().execute(exp);
		} finally {
			cc.restore();
		}
	}

	protected abstract void doActionPerformed(ActionEvent e);

}
