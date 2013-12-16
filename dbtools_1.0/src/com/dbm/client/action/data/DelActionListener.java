package com.dbm.client.action.data;

import java.awt.event.ActionEvent;

import javax.swing.JTable;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.db.DbClient;
import com.dbm.client.db.DbClientFactory;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Msg02Dialog;

public class DelActionListener extends AbstractActionListener {

	private static DelActionListener delMng = new DelActionListener();

	private String tblName = null;


//	private HashMap<Integer, HashMap<Integer, String>> params = new HashMap<Integer, HashMap<Integer, String>>();

	
	public void setTblName(String tblName) {
		this.tblName = tblName;
		//params.clear();
	}

	
	public static DelActionListener getInstance() {
		return delMng;
	}

	@Override
	protected void doActionPerformed(ActionEvent e) {
		logger.debug("tset");
		JTable jt = (JTable) AppUIAdapter.getUIObj(AppUIAdapter.TableDataUIObj);
		if (jt == null) {
			return;
		}
		int selCnt = jt.getSelectedRowCount();
		if (selCnt == 0) {
			return;
		}

		// 让用户确认数据删除
		Msg02Dialog msg02 = Msg02Dialog.getDialog(10001);
		if (msg02.isOK()) {
			// 开始删除
			DbClient dbClient = DbClientFactory.getDbClient();
			dbClient.executeDelete(tblName, jt.getSelectedRows());
		}

		// 删除后再刷新画面
	
	}

}
