package com.dbm.client.action.data;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JTextArea;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.db.DbClient;
import com.dbm.client.db.DbClientFactory;
import com.dbm.client.ui.AppUIAdapter;

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
		if (dbClient != null && dbClient.executeScript(action)) {
			// 执行输入SQL脚本时禁用[更新]和[删除]按钮
			JButton button = (JButton) AppUIAdapter.getUIObj(AppUIAdapter.BTN_UPDATE);
			button.setEnabled(false);

			button = (JButton) AppUIAdapter.getUIObj(AppUIAdapter.BTN_DELETE);
			button.setEnabled(false);
		}
	}

}
