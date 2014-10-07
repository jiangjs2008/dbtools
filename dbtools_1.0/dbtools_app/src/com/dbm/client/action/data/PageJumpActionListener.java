package com.dbm.client.action.data;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Session;
import com.dbm.client.ui.tbldata.MyDefaultTableModel;
import com.dbm.client.ui.tbllist.TableCellEditorListener;
import com.dbm.client.util.TableUtil;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.error.BaseException;
import com.dbm.common.error.BaseExceptionWrapper;

/**
 * 数据多页显示时的翻页控制
 */
public class PageJumpActionListener extends AbstractActionListener {

	private JPanel pagejumpPanel = null;
	private JTextField pageInfoTxtField = null;
	private JTextField inputPageTxtField = null;
	// 当前页数,从1开始计数
	private int currPage = 0;
	// 总页数
	private int _pageCnt = 0;
	// 每页表示件数
	private int dataLimit = Session.PageDataLimit;

	private ResultSet _rowSet = null;
	private DbClient dbClient = null;

	public PageJumpActionListener(JPanel pagePanel, JTextField pgInfoTxt, JTextField iptPgText) {
		this.pagejumpPanel = pagePanel;
		this.pageInfoTxtField = pgInfoTxt;
		this.inputPageTxtField = iptPgText;
	}

	/**
	 * 在画面上显示查询结果(第一页数据)
	 *
	 * @param rowSet 查询结果集对象
	 * @param currPage 当前页数
	 */
	public void displayTableData(ResultSet rowSet, int currPage) {
		dbClient = DbClientFactory.getDbClient();
		this._rowSet = rowSet;
		this.currPage = currPage;
		// 总件数
		int dataCnt = dbClient.size();

		// 当前页表示件数
		int itemSize = 0;
		if (currPage == 1) {
			// 第一次显示数据时计算总页数
			if (dataCnt > dataLimit) {
				if (dataCnt % dataLimit == 0) {
					_pageCnt = dataCnt / dataLimit;
				} else {
					_pageCnt = dataCnt / dataLimit + 1;
				}
			} else {
				_pageCnt = 1;
			}
		}

		if (_pageCnt > 1) {
			// 分页处理，显示分页相关组件
			pagejumpPanel.setVisible(true);
			if (currPage == _pageCnt) {
				// 显示最后一页
				itemSize = dataCnt - dataLimit * (_pageCnt - 1);
			} else {
				itemSize = dataLimit;
			}
			// 设置页数
			pageInfoTxtField.setText(currPage + "/" + _pageCnt);

		} else {
			// 小于一页时，若分页相关组件已在显示则要关闭
			itemSize = dataCnt;
			pagejumpPanel.setVisible(false);
		}

		setTableData(itemSize);
	}

	/**
	 * 在画面上显示数据
	 *
	 * @param length 数据条数
	 */
	private void setTableData(int length) {
		Vector<String> colName = null;
		Vector<Vector<String>> allData = null;
		try {
			// set table column name
			int colCount = 0;
			ResultSetMetaData rsm = _rowSet.getMetaData();
			if (rsm != null) {
				colCount = rsm.getColumnCount() + 1;
			}

			colName = new Vector<String>(colCount);
			colName.add("NO.");
			for (int k = 1; k < colCount; k ++) {
				colName.add(rsm.getColumnName(k));
			}

			// 如果分页，开始只显示第一页的数据
			allData = new Vector<Vector<String>>(length);

			for (int i = 1; i <= length; i ++) {

				if (!_rowSet.next()) {
					break;
				}

				Vector<String> colValue = new Vector<String>(colCount);
				colValue.add(Integer.toString(i + (currPage - 1) * dataLimit));

				for (int k = 1; k < colCount; k ++) {
					Object obj = _rowSet.getObject(k);
					colValue.add(dbClient.procCellData(obj));
				}
				allData.add(colValue);
			}
		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}

		JTable jTable1 = (JTable) AppUIAdapter.getUIObj(AppUIAdapter.TableDataUIObj);
		if (jTable1 == null) {
			throw new BaseException(40001, "TableDataUIObj");
		}
		MyDefaultTableModel jTable1Model = new MyDefaultTableModel();
		jTable1Model.setDataVector(allData, colName);
		// 最后一页加空行
		jTable1Model.addRow(new String[]{});
		jTable1.setModel(jTable1Model);

		DefaultCellEditor editor = new DefaultCellEditor(new JTextField());
		editor.addCellEditorListener(new TableCellEditorListener());
		TableUtil.fitTableColumns(jTable1, editor);

		JScrollBar jscrollBar = ((JScrollPane) jTable1.getParent().getParent()).getVerticalScrollBar();
		if (jscrollBar != null) {
			jscrollBar.setValue(jscrollBar.getMinimum());
		}
		jscrollBar = ((JScrollPane) jTable1.getParent().getParent()).getHorizontalScrollBar();
		if (jscrollBar != null) {
			jscrollBar.setValue(jscrollBar.getMinimum());
		}
	}

	/**
	 * 翻页操作<br>
	 * 翻页前必须检查是否有数据被编辑过需要更新，并提醒用户<br>
	 * 对于直接执行查询所得结果只可向前翻页，且不可更新
	 */
	@Override
	protected void doActionPerformed(ActionEvent e) {
		String actionName = e.getActionCommand();
		String inputText = null;

		if ("first".equals(actionName)) {
			// 第一页
			if (currPage == 1) {
				return;
			}

			currPage = 1;
			_rowSet = dbClient.defaultQuery(currPage);

		} else if ("backward".equals(actionName)) {
			// 上一页
			if (currPage == 1) {
				return;
			}

			currPage --;
			_rowSet = dbClient.defaultQuery(currPage);

		} else if ("forward".equals(actionName)) {
			// 下一页
			if (currPage == _pageCnt) {
				return;
			}

			currPage ++;
			_rowSet = dbClient.defaultQuery(currPage);

		} else if ("last".equals(actionName)) {
			// 最后一页
			if (currPage == _pageCnt) {
				return;
			}

			currPage = _pageCnt;
			_rowSet = dbClient.defaultQuery(currPage);

		} else if ("go".equals(actionName)) {
			// 查看指定页
			if (inputPageTxtField == null) {
				return;
			}
			// 指定页数
			inputText = inputPageTxtField.getText();
			inputText = StringUtils.trimToNull(inputText);
			int inputPage = NumberUtils.toInt(inputText);
			if (inputPage == 0 || inputPage == currPage || _pageCnt < inputPage) {
				inputPageTxtField.setText("");
				return;
			}

			currPage = inputPage;
			_rowSet = dbClient.defaultQuery(currPage);

		} else {
			throw new BaseException(40002, actionName);
		}

		displayTableData(_rowSet, currPage);
		if (inputText != null) {
			inputPageTxtField.setText("");
		}
	}

}

