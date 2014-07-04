package com.dbm.client.ui.tbllist;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import com.dbm.client.action.data.UpdActionListener;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Session;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;

public class TableCellEditorListener implements CellEditorListener {

	@Override
	public void editingStopped(ChangeEvent e) {
		DefaultCellEditor dce = (DefaultCellEditor) e.getSource();
		Object obj = dce.getCellEditorValue();

		JTable jTable1 = (JTable) AppUIAdapter.getUIObj(AppUIAdapter.TableDataUIObj);
		int col = jTable1.getSelectedColumn();
		int row = jTable1.getSelectedRow() + 1;

		// 先判断是更新数据还是插入数据
		// 判断当前更该行是否大于该表的数据总件数
		DbClient dbClient = DbClientFactory.getDbClient();
		int dataCnt = dbClient.size();
		int curPage = dbClient.getCurrPageNum();

		UpdActionListener updMng = UpdActionListener.getInstance();
		if (row + (curPage - 1) * Session.PageDataLimit <= dataCnt) {
			// 更新数据
			String value = DbClientFactory.getDbClient().getTableDataAt(row, col);
			if (!obj.equals(value)) {
				updMng = UpdActionListener.getInstance();
				updMng.setUpdParams(row, col, (String) obj);
			}
		} else {
			// 插入数据
			updMng.setAddParams(row, col, (String) obj);

		}
	}

	@Override
	public void editingCanceled(ChangeEvent e) {
	}
}
