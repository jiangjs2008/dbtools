package com.dbm.client.action.data;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Session;
import com.dbm.client.ui.tbldata.MyDefaultTableModel;
import com.dbm.client.util.TableUtil;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.error.BaseException;
import com.dbm.common.error.BaseExceptionWrapper;
import com.dbm.common.util.StringUtil;

/**
 * 数据多页显示时的翻页控制
 */
public class PageJumpActionListener extends AbstractActionListener {

	private JPanel pagejumpPanel = null;
	private JTextField pageInfoTxtField = null;
	private JTextField inputPageTxtField = null;
	// 当前页数
	private int currPage = 0;
	// 总页数
	private int pageCnt = 0;
	// 每页表示件数
	private int dataLimit = Session.PageDataLimit;
	// 总件数
	private int _dataCnt = 0;

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
	 * @param rs
	 * @param hasRowId
	 */
	public void displayTableData(ResultSet rowSet, int dataCnt) {
		dbClient = DbClientFactory.getDbClient();
		this._rowSet = rowSet;
		this._dataCnt = dataCnt;

		// 当前页表示件数
		int itemSize = 0;
		pageCnt = 0;
		currPage = 1;
		if (dataCnt > dataLimit) {
			if (dataCnt % dataLimit == 0) {
				pageCnt = dataCnt / dataLimit;
			} else {
				pageCnt = dataCnt / dataLimit + 1;
			}
		} else {
			pageCnt = 1;
		}

		if (pageCnt > 1) {
			// 分页处理
			itemSize = dataLimit;
			// 显示分页相关组件
			pagejumpPanel.setVisible(true);
			// 设置页数
			pageInfoTxtField.setText(1 + "/" + pageCnt);

		} else {
			// 小于一页时，若分页相关组件已在显示则要关闭
			itemSize = dataCnt;
			pagejumpPanel.setVisible(false);
		}

		setTableData(1, itemSize);
	}

	/**
	 * 在画面上显示数据
	 *
	 * @param rs
	 */
	private void setTableData(int offSite, int length) {

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
			// set table data
			for (int i = 1; i <= length; i ++) {

				if (!_rowSet.next()) {
					break;
				}

				Vector<String> colValue = new Vector<String>(colCount);
				colValue.add(Integer.toString(i + (currPage - 1) * dataLimit));

				for (int k = 1; k < colCount; k ++) {
					colValue.add(_rowSet.getString(k));
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
		TableUtil.fitTableColumns(jTable1);

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
	 * 必须先判断该数据驱动是否支持数据集滚动
	 */
	@Override
	protected void doActionPerformed(ActionEvent e) {
		String actionName = e.getActionCommand();
		int currRowIdx = 0;
		int length = 0;
		if ("first".equals(actionName)) {
			// 第一页
			if (currPage == 1) {
				return;
			}
			if (dbClient.supportsPageScroll() < 3) {
				return;
			}
			currPage = 1;
			currRowIdx = (currPage - 1) * dataLimit + 1;
			length = dataLimit;
			_rowSet = dbClient.getPage(currPage, currRowIdx, length);

		} else if ("backward".equals(actionName)) {
			// 上一页
			if (currPage == 1) {
				return;
			}
			if (dbClient.supportsPageScroll() == 1) {
				return;
			}
			currPage --;
			currRowIdx = (currPage - 1) * dataLimit + 1;
			length = dataLimit;
			
			if (dbClient.supportsPageScroll() == 3) {
				_rowSet = dbClient.getPage(currPage, currRowIdx, length);
			} else if (dbClient.supportsPageScroll() == 2) {
				_rowSet = dbClient.getPreviousPage();
			}

		} else if ("forward".equals(actionName)) {
			// 下一页
			if (currPage == pageCnt) {
				return;
			}
			currPage ++;
			currRowIdx = (currPage - 1) * dataLimit + 1;
			length = dataLimit;
			
			if (dbClient.supportsPageScroll() == 3) {
				_rowSet = dbClient.getPage(currPage, currRowIdx, length);
			} else if (dbClient.supportsPageScroll() == 2) {
				_rowSet = dbClient.getNextPage();
			}

		} else if ("last".equals(actionName)) {
			// 最后一页
			if (currPage == pageCnt) {
				return;
			}
			if (dbClient.supportsPageScroll() < 3) {
				return;
			}
			currPage = pageCnt;
			currRowIdx = (currPage - 1) * dataLimit + 1;
			length = _dataCnt - (currPage - 1) * dataLimit;
			_rowSet = dbClient.getPage(currPage, currRowIdx, length);

		} else if ("go".equals(actionName)) {
			// 查看指定页
			if (dbClient.supportsPageScroll() < 3) {
				return;
			}
			if (inputPageTxtField == null) {
				return;
			}
			// 指定页数
			int inputPage = StringUtil.parseInt(inputPageTxtField.getText());
			if (inputPage == 0) {
				return;
			}
			if (currPage == inputPage) {
				return;
			}
			currPage = inputPage;
			currRowIdx = (currPage - 1) * dataLimit + 1;
			length = dataLimit;
			if (currPage == pageCnt) {
				length = _dataCnt - (currPage - 1) * dataLimit;
			}
			_rowSet = dbClient.getPage(currPage, currRowIdx, length);

		} else {
			throw new BaseException(40002, actionName);
		}

		pageInfoTxtField.setText(currPage + "/" + pageCnt);
		setTableData(currRowIdx, length);
	}

}

