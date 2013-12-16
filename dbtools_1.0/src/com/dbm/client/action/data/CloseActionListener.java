package com.dbm.client.action.data;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JTable;
import javax.swing.JTree;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.db.DbClient;
import com.dbm.client.db.DbClientFactory;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Msg01Dialog;
import com.dbm.client.ui.Session;
import com.dbm.client.ui.tbllist.ObjectsTreeModel;

public class CloseActionListener extends AbstractActionListener {

	/**
	 * This method is activated on the Keystrokes we are listening to in this
	 * implementation. Here it listens for Copy and Paste ActionCommands.
	 * Selections comprising non-adjacent cells result in invalid selection and
	 * then copy action cannot be performed. Paste is done by aligning the upper
	 * left corner of the selection with the 1st element in the current
	 * selection of the JTable.
	 */
	@Override
	public void doActionPerformed(ActionEvent e) {
		// close current db connection
		if (!DbClient.isConnected) {
			Msg01Dialog.showMsgDialog("have not connection now");
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
	}

}
