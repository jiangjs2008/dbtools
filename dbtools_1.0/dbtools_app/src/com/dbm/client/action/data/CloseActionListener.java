package com.dbm.client.action.data;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Msg01Dialog;
import com.dbm.client.ui.Session;
import com.dbm.client.ui.tbllist.ObjectsTreeModel;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;

public class CloseActionListener extends AbstractActionListener {

	@Override
	public void doActionPerformed(ActionEvent e) {
		// close current db connection
		DbClient dbClient = DbClientFactory.getDbClient();
		if (!dbClient.isConnected()) {
			Msg01Dialog.showMsgDialog(10003);
			return;
		}
		// 关闭数据库连接
		DbClientFactory.close();
		// 重置应用程序标题栏
		((Frame) AppUIAdapter.getUIObj(AppUIAdapter.AppMainGUI)).setTitle(Session.APP_TITLE);

		// 重置数据库信息栏
		JTree jTree1 = (JTree) AppUIAdapter.getUIObj(AppUIAdapter.TableTreeUIObj);
		((ObjectsTreeModel) jTree1.getModel()).clearList();

		// 重置数据显示
		JTable jTable1 = (JTable) AppUIAdapter.getUIObj(AppUIAdapter.TableDataUIObj);
		jTable1.setModel(Session.EmptyTableModel);
		JPanel jPanel = (JPanel) AppUIAdapter.getUIObj(AppUIAdapter.PagePanel);
		jPanel.setVisible(false);
	}

}
