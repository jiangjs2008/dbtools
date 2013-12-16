package com.dbm.client.action.data;

import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.db.DbClient;
import com.dbm.client.db.DbClientFactory;

public class ExecActionListener extends AbstractActionListener {

	private JTextArea jTextArea1;

	public ExecActionListener(JTextArea jTextArea1) {
		this.jTextArea1 = jTextArea1;
	}

	@Override
	public void doActionPerformed(ActionEvent e) {
		logger.debug("ExecActionListener");
		String action = jTextArea1.getSelectedText();
		if (action == null || action.length() == 0) {
			action = jTextArea1.getText();
			if (action == null || action.length() == 0) {
				return;
			}
		} else {
			jTextArea1.select(0, 0);
		}

		DbClient dbClient = DbClientFactory.getDbClient();
		dbClient.executeScript(action);
	}

}
