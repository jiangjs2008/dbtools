package com.dbm.client.action.data;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Msg02Dialog;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;

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
	 * 追加值集合
	 */
	private ArrayList<HashMap<Integer, String>> addParams = new ArrayList<HashMap<Integer, String>>();
	/**
	 * 删除值集合
	 */
	private ArrayList<Integer> delParams = new ArrayList<Integer>();

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
		if (params != null) {
			params.clear();
		}
		if (addParams != null) {
			addParams.clear();
		}
		if (delParams != null) {
			delParams.clear();
		}
	}

	/**
	 * 设定要更新的值
	 *
	 * @param rowNum 行号
	 * @param colNum 列号
	 * @param colValue 要更新的值
	 */
	public void setTblParams(int rowNum, int colNum, String colValue) {
		if (params == null) {
			params = new HashMap<Integer, HashMap<Integer, String>>();
		}
		HashMap<Integer, String> rowMap = params.get(rowNum);
		if (rowMap == null) {
			rowMap = new HashMap<Integer, String>();
			rowMap.put(colNum, colValue);
			params.put(rowNum, rowMap);
		} else {
			rowMap.put(colNum, colValue);
		}
	}

	/**
	 * 设定要追加的值
	 *
	 * @param addedData 要追加的行
	 */
	public void setTblParams(HashMap<Integer, String> addedData) {
		if (addParams == null) {
			addParams = new ArrayList<HashMap<Integer, String>>();
		}
		addParams.add(addedData);
	}

	/**
	 * 设定要删除的值
	 *
	 * @param rowNum 行号
	 */
	public void setTblParams(int rowNum) {
		if (delParams == null) {
			delParams = new ArrayList<Integer>();
		}
		delParams.add(rowNum);
	}

	/**
	 * 更新数据<br>
	 * 执行顺序：更新->追加->删除
	 */
	@Override
	protected void doActionPerformed(ActionEvent e) {
		logger.debug("UpdActionListener tbl: " + tblName);
		JTable jt = (JTable) AppUIAdapter.getUIObj(AppUIAdapter.TableDataUIObj);
		TableCellEditor editor = jt.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}

		if (tblName == null) {
			logger.debug("have no table to update");
			return;
		}
		if (params == null && addParams == null && delParams == null) {
			logger.debug("have no data to update");
			return;
		}

		// 让用户确认数据更新
		Msg02Dialog msg02 = Msg02Dialog.getDialog(10002);
		if (msg02.isOK()) {
			// 开始更新
			logger.debug("begin to update data");
			DbClient dbClient = DbClientFactory.getDbClient();
			dbClient.executeUpdate(tblName, params, addParams, delParams);

			if (params != null) {
				params.clear();
			}
			if (addParams != null) {
				addParams.clear();
			}
			if (delParams != null) {
				delParams.clear();
			}
		}
		logger.debug("update data success");

		//		JTable jt = (JTable) AppUIAdapter.getUIObj(AppUIAdapter.TableDataUIObj);
//		if (jt == null) {
//			return;
//		}
//		int selCnt = jt.getSelectedRowCount();
//		if (selCnt == 0) {
//			return;
//		}
//
//		// 让用户确认数据删除
//		Msg02Dialog msg02 = Msg02Dialog.getDialog(10001);
//		if (msg02.isOK()) {
//			// 开始删除
//			DbClient dbClient = DbClientFactory.getDbClient();
//			dbClient.executeDelete(tblName, jt.getSelectedRows());
//		}
//
//		// 删除后再刷新画面
		
		// 更新后再刷新画面
		DbClient dbClient = DbClientFactory.getDbClient();
		dbClient.executeQuery(tblName);
	}

}
