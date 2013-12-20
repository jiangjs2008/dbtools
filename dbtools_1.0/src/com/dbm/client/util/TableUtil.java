/**
 * Copyright (c) 2013 JiangJusheng. All rights reserved.
 */
package com.dbm.client.util;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.dbm.client.action.data.PageJumpActionListener;
import com.dbm.client.action.data.UpdActionListener;
import com.dbm.client.db.DbClient;
import com.dbm.client.db.DbClientFactory;
import com.dbm.client.error.BaseExceptionWrapper;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Session;
import com.dbm.client.ui.tbldata.MyDefaultTableModel;

/**
 * [class]<br>
 * JTable utility function class<br><br>
 * [function]<br>
 * JTable utility function<br><br>
 * [history]<br>
 * 2013/05/10 first edition  JiangJusheng<br>
 *
 * @version 1.00
 */
public final class TableUtil {

	/**
	 * instances of the log class
	 */
	private static LoggerWrapper logger = new LoggerWrapper(TableUtil.class); 

	/**
	 * set db data to JTable cell
	 *
	 * @param jTable1 JTable object
	 * @param rslt    db data, first row is column identifiers 
	 */
	public static void setTableData(ArrayList<ArrayList<Object>> rslt, boolean hasRowId) {
		JTable jTable1 = (JTable) AppUIAdapter.getUIObj(AppUIAdapter.TableDataUIObj);
		if (rslt == null || jTable1 == null || rslt.size() == 0) {
			return;
		}

		// set table column name
		int dataSize = rslt.size() - 1;
		ArrayList<Object> colList = rslt.get(0);
		String[] colName = new String[colList.size() + 1];
		colName[0] = "NO.";
		int i = 0;
		Object objValue = null;
		for (i = 0; i < colList.size(); i ++) {
			objValue = colList.get(i);
			if (objValue != null) {
				colName[i + 1] = objValue.toString();
			}
		}

		String[][] allData = new String[dataSize][colName.length];
		// set table data
		for (int x = 0; x < dataSize; x ++) {
			colList = rslt.get(x + 1);
			allData[x][0] = Integer.toString(x + 1);

			for (i = 0; i < colList.size(); i ++) {
				objValue = colList.get(i);
				if (objValue != null) {
					allData[x][i + 1] = objValue.toString();
				}
			}
		}

		MyDefaultTableModel jTable1Model = new MyDefaultTableModel();
		jTable1Model.setDataVector(allData, colName);
		jTable1Model.addRow(new String[]{});
		jTable1.setModel(jTable1Model);
		fitTableColumns(jTable1);
	}

	/**
	 * auto resize table column's width
	 *
	 * @param myTable JTable object
	 */
	public static void fitTableColumns(JTable myTable) {
		DefaultCellEditor editor = new DefaultCellEditor(new JTextField());
		editor.addCellEditorListener(new CellEditorListener() {
			public void editingStopped(ChangeEvent e) {
				DefaultCellEditor dce = (DefaultCellEditor) e.getSource();
				Object obj = dce.getCellEditorValue();

				JTable jTable1 = (JTable) AppUIAdapter.getUIObj(AppUIAdapter.TableDataUIObj);
				int col = jTable1.getSelectedColumn();
				int row = jTable1.getSelectedRow() + 1;

				// 先判断是更新数据还是插入数据
				// 判断当前更该行是否大于该表的数据总件数
				PageJumpActionListener pageAction = (PageJumpActionListener) AppUIAdapter.getUIObj(AppUIAdapter.PageAction);
				int dataCnt = pageAction.getAllRowSize();
				int pageNum = pageAction.getCurrPageNum();

				UpdActionListener updMng = UpdActionListener.getInstance();
				if ((pageNum - 1) * Session.PageDataLimit + row > dataCnt) {
					// 插入数据
					updMng.addTblParams(row, col, (String) obj);

				} else {
					// 更新数据
					String value = DbClientFactory.getDbClient().getTableDataAt(row, col);
					if (!obj.equals(value)) {
						updMng = UpdActionListener.getInstance();
						updMng.addTblParams(row, col, (String) obj);
					}
				}
			}

			public void editingCanceled(ChangeEvent e) {
			}
		});

		JTableHeader header = myTable.getTableHeader();
		int rowCount = myTable.getRowCount();

		final DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		cellRenderer.setBackground(Color.LIGHT_GRAY);

		int colWidth = 0;
		int colIndex = 0;
		int preferedWidth = 0;
		TableColumn column = null;
		Enumeration<?> columns = myTable.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			column = (TableColumn) columns.nextElement();
			colIndex = header.getColumnModel().getColumnIndex(column.getIdentifier());
			colWidth = (int) myTable.getTableHeader().getDefaultRenderer().getTableCellRendererComponent(myTable,
					column.getIdentifier(), false, false, -1, colIndex).getPreferredSize().getWidth();

			// 计算列的宽度(遍历所有行，取最大的为列宽度)
			for (int row = 0; row < rowCount; row ++) {
				preferedWidth = (int) myTable.getCellRenderer(row, colIndex).getTableCellRendererComponent(myTable,
						myTable.getValueAt(row, colIndex), false, false, row, colIndex).getPreferredSize().getWidth();
				colWidth = Math.max(colWidth, preferedWidth);
			}
			header.setResizingColumn(column);

			colWidth = colWidth + 16;
			if (colIndex == 0) {
				// first column
				column.setResizable(false);
				column.setWidth(colWidth-5);
				column.setCellRenderer(cellRenderer);
			} else {
				column.setCellEditor(editor);
				if (colWidth > 400) {
					// 设置列的最大宽度为400像素
					colWidth = 400;
				}
				column.setWidth(colWidth);
			}
		}
	}

}
