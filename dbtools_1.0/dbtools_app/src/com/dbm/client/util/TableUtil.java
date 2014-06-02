package com.dbm.client.util;

import java.awt.Color;
import java.util.Enumeration;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * [class]<br>
 * JTable utility function class<br><br>
 * [function]<br>
 * JTable utility function<br><br>
 * [history]<br>
 * 2013/05/10 ver1.0 JiangJusheng<br>
 */
public final class TableUtil {

	/**
	 * auto resize table column's width
	 *
	 * @param myTable JTable object
	 */
	public static void fitTableColumns(JTable myTable, DefaultCellEditor cellEditor) {

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
				if (cellEditor != null) {
					column.setCellEditor(cellEditor);
				}

				if (colWidth > 400) {
					// 设置列的最大宽度为400像素
					colWidth = 400;
				}
				column.setWidth(colWidth);
			}
		}
	}

}
