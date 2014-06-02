package com.dbm.client.action.data;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Msg01Dialog;
import com.dbm.client.ui.Msg02Dialog;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;

/**
 * [name]<br>
 * 数据更新操作类<br><br>
 * [function]<br>
 * 更新数据<br><br>
 * [history]<br>
 * 2012/02/11 ver1.0 JiangJusheng<br>
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
	private TreeMap<Integer, HashMap<Integer, String>> addMapPara = new TreeMap<Integer, HashMap<Integer, String>>();
	/**
	 * 删除值集合
	 */
	private ArrayList<Integer> delListPara = new ArrayList<Integer>();

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
		if (params != null && params.size() > 0) {
			params.clear();
		}
		if (addMapPara != null && addMapPara.size() > 0) {
			addMapPara.clear();
		}
		if (delListPara != null && delListPara.size() > 0) {
			delListPara.clear();
		}
	}

	/**
	 * 设定要更新的值
	 *
	 * @param rowNum 行号
	 * @param colNum 列号
	 * @param colValue 要更新的值
	 */
	public void setUpdParams(int rowNum, int colNum, String colValue) {
		if (params == null) {
			params = new HashMap<Integer, HashMap<Integer, String>>();
		}
		HashMap<Integer, String> rowMap = params.get(rowNum);
		if (rowMap == null) {
			rowMap = new HashMap<Integer, String>();
		}
		rowMap.put(colNum, colValue);
		params.put(rowNum, rowMap);
	}

	/**
	 * 设定要追加的值
	 *
	 * @param addedData 要追加的行
	 */
	public void setAddParams(int rowNum, int colNum, String addedData) {
		if (addMapPara == null) {
			addMapPara = new TreeMap<Integer, HashMap<Integer, String>>();
		}
		HashMap<Integer, String> rowMap = addMapPara.get(rowNum);
		if (rowMap == null) {
			rowMap = new HashMap<Integer, String>();
		}
		rowMap.put(colNum, addedData);
		addMapPara.put(rowNum, rowMap);
	}

	/**
	 * 设定要删除的值
	 *
	 * @param rowNum 行号
	 */
	public void setDelParams(int rowNum) {
		if (delListPara == null) {
			delListPara = new ArrayList<Integer>();
		}
		delListPara.add(rowNum);
	}

	/**
	 * 设定要更新的数据库表名
	 *
	 * @param tblName 表名称
	 */
	public boolean hasDataUpd() {
		if (params != null && params.size() > 0
			|| addMapPara != null && addMapPara.size() > 0
			|| delListPara != null && delListPara.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 更新数据<br>
	 * 执行顺序：更新->追加->删除
	 */
	@Override
	protected void doActionPerformed(ActionEvent e) {
		logger.debug("UpdActionListener tbl: " + tblName);
		DbClient dbClient = DbClientFactory.getDbClient();
		if (dbClient == null) {
			// 数据库未连接
			return;
		}
		if (!dbClient.isConnected()) {
			Msg01Dialog.showMsgDialog(10003);
			return;
		}

		JTable jt = (JTable) AppUIAdapter.getUIObj(AppUIAdapter.TableDataUIObj);
		TableCellEditor editor = jt.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}

		if (tblName == null) {
			logger.debug("have no table to update");
			return;
		}
		if ((params == null || params.isEmpty())
				&& (addMapPara == null || addMapPara.isEmpty())
				&& (delListPara == null || delListPara.isEmpty())) {
			logger.debug("have no data to update");
			return;
		}

		// 让用户确认数据更新
		Msg02Dialog msg02 = Msg02Dialog.showMsgDialog(10002);
		if (!msg02.isOK()) {
			// 不更新，退出
			logger.debug("don't update, exit");
			return;
		}

		// 开始更新
		logger.debug("begin to update data");

		ArrayList<HashMap<Integer, String>> addList = null;
		if (addMapPara != null && addMapPara.size() > 0) {
			addList = new ArrayList<HashMap<Integer, String>>(addMapPara.size());
			for (Iterator<HashMap<Integer, String>> iter2 = addMapPara.values().iterator(); iter2.hasNext(); ) {
				HashMap<Integer, String> entry2 = iter2.next();
				addList.add(entry2);
			}
		}

		dbClient.executeUpdate(params, addList, delListPara);

		if (params != null && params.size() > 0) {
			params.clear();
		}
		if (addMapPara != null && addMapPara.size() > 0) {
			addMapPara.clear();
			addList.clear();
		}
		if (delListPara != null && delListPara.size() > 0) {
			delListPara.clear();
		}

		// 更新后再刷新画面
		ResultSet rowSet = dbClient.getPage(1);
		int dataCnt = dbClient.size();

		PageJumpActionListener pageAction = (PageJumpActionListener) AppUIAdapter.getUIObj(AppUIAdapter.PageAction);
		pageAction.displayTableData(rowSet, dataCnt);
		logger.debug("update data success");
	}

}
