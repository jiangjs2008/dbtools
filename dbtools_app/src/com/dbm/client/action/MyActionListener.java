package com.dbm.client.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumnModel;

import com.dbm.client.action.data.UpdActionListener;
import com.dbm.client.ui.Session;
import com.dbm.client.ui.tbldata.MyDefaultTableModel;
import com.dbm.client.util.TableUtil;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;

public class MyActionListener extends AbstractActionListener {

	private JTable jTable1;
	private static final Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();

	/**
	 * Public Accessor methods for the Table on which this adapter acts.
	 */
	public void setJTable(JTable jTable1) {
		this.jTable1 = jTable1;
		// Identifying the copy KeyStroke user can modify this
		// to copy on some other Key combination.
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		// Identifying the Paste KeyStroke user can modify this
		// to copy on some other Key combination.
		KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
		// bundle action to JTable
		jTable1.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
		jTable1.registerKeyboardAction(this, "Paste", paste, JComponent.WHEN_FOCUSED);
	}

	@Override
	public void doActionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareTo("Copy") == 0) {
			actionCopy(e);
		} else if (e.getActionCommand().compareTo("Paste") == 0) {
			actionPaste(e);
		}
	}

	private void actionCopy(ActionEvent e) {
		StringBuffer sbf = new StringBuffer();
		// Check to ensure we have selected only a contiguous block of cells
//		int numcols = jTable1.getSelectedColumnCount();
//		int numrows = jTable1.getSelectedRowCount();
//		int[] colsselected = jTable1.getSelectedColumns();
		int[] rowsselected = jTable1.getSelectedRows();
//		if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] && numrows == rowsselected.length)
//				&& (numcols - 1 == colsselected[colsselected.length - 1] - colsselected[0] && numcols == colsselected.length))) {
//			JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
//			return;
//		}
		int tblCols = jTable1.getColumnCount();
		
		if (rowsselected.length == 1 && rowsselected[0] == 0) {
			TableColumnModel tcm = jTable1.getTableHeader().getColumnModel();
			
			for (int i = 1; i < tblCols; i++) {
				String item = (String) tcm.getColumn(i).getHeaderValue();
				if (item == null) {
					sbf.append("");
				} else {
					sbf.append(item);
				}
				if (i < tblCols - 1) {
					sbf.append("\t");
				}
			}
			sbf.append("\n");
		}

		for (int i : rowsselected) {
			if (i == jTable1.getRowCount() - 1) {
				break;
			}
			for (int j = 1; j < tblCols; j++) {
				String item = (String) jTable1.getValueAt(i, j);
				if (item == null) {
					sbf.append("");
				} else {
					sbf.append(item);
				}
				if (j < tblCols - 1) {
					sbf.append("\t");
				}
			}
			sbf.append("\n");
		}
		StringSelection stsel = new StringSelection(sbf.toString());
		system.setContents(stsel, null);
	}

	private void actionPaste(ActionEvent e) {
		String trstring = null;
		try {
			trstring = (String) (system.getContents(null).getTransferData(DataFlavor.stringFlavor));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		StringTokenizer st1 = new StringTokenizer(trstring, "\n");
		if (st1.countTokens() < 1) {
			return;
		}

		// jTable1のtable bodyの処理
		Vector<Vector<String>> tblBody = new Vector<Vector<String>>();
		int lines = 0;

		while (st1.hasMoreTokens()) {
			Vector<String> tblRow = new Vector<String>();
			String rowstring = st1.nextToken();
			String[] st2 = rowstring.split("\t");
			lines ++;
			tblRow.addElement(Integer.toString(lines));
			for (String colValue : st2) {
				tblRow.addElement(colValue);
			}
			tblBody.addElement(tblRow);
		}

		MyDefaultTableModel jTable1Model = (MyDefaultTableModel) jTable1.getModel();

		// 计算行数,必须考虑到分页的情况
		int curRowIdx = jTable1.getSelectedRow();

		if (curRowIdx == jTable1.getRowCount() - 1) {
			// 添加新行
			// 先删除默认的空白行
			jTable1Model.removeRow(curRowIdx);
			// 显示复制的行数据,并添加到数据更新列表
			int newRowIdx = curRowIdx + 1;
			DbClient dbClient = DbClientFactory.getDbClient();
			if (dbClient != null) {
				if (dbClient.getCurrPageNum() > 1) {
					newRowIdx = (dbClient.getCurrPageNum() - 1) * Session.PageDataLimit + curRowIdx;
				}
			}
			UpdActionListener updMng = UpdActionListener.getInstance();

			for (Vector<String> tblRow : tblBody) {
				tblRow.setElementAt("*" + Integer.toString(newRowIdx), 0);

				for (int i = 1, lengs = tblRow.size(); i < lengs; i ++) {
					updMng.setTblParams(newRowIdx, i, tblRow.get(i));
				}

				jTable1Model.addRow(tblRow);
				newRowIdx ++;
			}
			// 添加默认的空白行
			jTable1Model.addRow(new String[]{});

		} else {
			if (curRowIdx + lines <= jTable1.getRowCount()) {
				
			} else {
				
			}
		}
		TableUtil.fitTableColumns(jTable1);
	}

}
