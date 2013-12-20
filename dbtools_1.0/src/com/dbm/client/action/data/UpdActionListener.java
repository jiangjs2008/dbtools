package com.dbm.client.action.data;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.db.DbClient;
import com.dbm.client.db.DbClientFactory;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Msg02Dialog;

/**
 * [name]<br>
 * 数据更新操作类<br><br>
 * [function]<br>
 * 更新数据<br><br>
 * [history]<br>
 * 2012/02/11 ver1.0.0 JiangJusheng<br>
 */
public class UpdActionListener extends AbstractActionListener {

	/**
	 * 更新操作类实例
	 */
	private static UpdActionListener updMng = new UpdActionListener();
	/**
	 * 数据库表名
	 */
	private String tblName = null;
	/**
	 * 变更值集合
	 */
	private HashMap<Integer, HashMap<Integer, String>> params = new HashMap<Integer, HashMap<Integer, String>>();

	/**
	 * 取得更新操作类实例
	 *
	 * @return UpdActionListener 更新操作类实例
	 */
	public static UpdActionListener getInstance() {
		return updMng;
	}

	/**
	 * 设定要更新的数据库表名
	 *
	 * @param tblName 表名称
	 */
	public void setTblName(String tblName) {
		this.tblName = tblName;
		params.clear();
	}

	/**
	 * 设定要更新的值
	 *
	 * @param rowNum 行号
	 * @param colNum 列号
	 * @param colValue 要更新的值
	 */
	public void addTblParams(int rowNum, int colNum, String colValue) {
		HashMap<Integer, String> rowMap = params.get(rowNum);
		if (rowMap == null) {
			rowMap = new HashMap<Integer, String>();
			rowMap.put(colNum, colValue);
			params.put(rowNum, rowMap);
		} else {
			rowMap.put(colNum, colValue);
		}
	}

	@Override
	protected void doActionPerformed(ActionEvent e) {
		logger.debug("UpdActionListener");
		JTable jt = (JTable) AppUIAdapter.getUIObj(AppUIAdapter.TableDataUIObj);
		TableCellEditor editor = jt.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}

		if (params.size() == 0) {
			return;
		}
		// 让用户确认数据更新
		Msg02Dialog msg02 = Msg02Dialog.getDialog(10002);
		if (msg02.isOK()) {
			// 开始更新
			DbClient dbClient = DbClientFactory.getDbClient();
			dbClient.executeUpdate(tblName, params);
			params.clear();
		}

		// 更新后再刷新画面
		DbClient dbClient = DbClientFactory.getDbClient();
		dbClient.executeQuery(tblName);
	}

}
