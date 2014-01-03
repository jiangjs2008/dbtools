package com.dbm.client.action.data;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JTextArea;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Msg01Dialog;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;

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
		if (dbClient == null) {
			// 数据库未连接
		}
		if (!dbClient.isConnected()) {
			Msg01Dialog.showMsgDialog("have not connection now");
			return;
		}

		// 执行输入SQL脚本时禁用[更新]按钮
		JButton button = (JButton) AppUIAdapter.getUIObj(AppUIAdapter.BTN_UPDATE);
		button.setEnabled(false);

		if (dbClient.getExecScriptType(action) == 1) {
			ResultSet rs = dbClient.directQuery(action);
			
			
			
		} else {
			
			
			
		}
	}

}
