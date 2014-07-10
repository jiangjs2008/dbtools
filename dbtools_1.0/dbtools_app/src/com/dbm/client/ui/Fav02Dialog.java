package com.dbm.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.common.property.FavrBean;
import com.dbm.common.property.PropUtil;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/

/**
 * [name]<br>
 * Fav02Dialog<br><br>
 * [function]<br>
 * 管理连接快捷方式<br><br>
 * [history]<br>
 * 2014/06/08 ver1.0 JiangJusheng<br>
 */
public class Fav02Dialog extends javax.swing.JDialog {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private JTable jTable1;
	private JButton jButton1;
	private JButton jButton2;
	private JLabel jLabel1;

	/**
	 * 构造函数
	 */
	public Fav02Dialog() {
		super();

		JPanel jPanel1 = new JPanel();
		getContentPane().add(jPanel1);
		jPanel1.setLayout(null);

		ButtonActionListener btnActListener = new ButtonActionListener();

		jButton1 = new JButton();
		jPanel1.add(jButton1);
		jButton1.setBounds(280, 570, 90, 30);
		jButton1.setText("取消");
		jButton1.addActionListener(btnActListener);

		jButton2 = new JButton();
		jPanel1.add(jButton2);
		jButton2.setBounds(550, 570, 90, 30);
		jButton2.setText("确定");
		jButton2.addActionListener(btnActListener);

		jLabel1 = new JLabel();
		jPanel1.add(jLabel1);
		jLabel1.setText("快捷方式一览");
		jLabel1.setBounds(25, 15, 120, 25);

		jTable1 = new JTable();
		jPanel1.add(jTable1);
		jTable1.setBounds(25, 45, 1000, 500);
		
		FavrBean[] favList = PropUtil.getFavrInfo();
		int leng = favList.length;
		String[][] favData = new String[leng][8];
		for (int i = 0; i < leng; i++) {
			FavrBean favItem = favList[i];
			if (favItem == null) {
				continue;
			}
			favData[i][0] = Integer.toString(i + 1);
			favData[i][1] = Boolean.toString(favItem.useFlg);
			favData[i][2] = Integer.toString(favItem.driverId);
			favData[i][3] = favItem.name;
			favData[i][4] = favItem.description;
			favData[i][5] = favItem.url;
			favData[i][6] = favItem.user;
			favData[i][7] = favItem.password;
		}

		TableModel jTable1Model =
			new DefaultTableModel(
					new String[] { "NO.", "v", "driverid", "name", "description", "url", "user", "password" }, 1);
		jTable1.setModel(jTable1Model);
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	    TableColumn col = jTable1.getColumn("v");
	    MyCheckBoxRenderer cellRenderer = new MyCheckBoxRenderer();
		cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		col = jTable1.getColumn("v");
		col.setCellRenderer(cellRenderer);
	    col.setCellEditor(new DefaultCellEditor(new JCheckBox()));

		jTable1.getTableHeader().setReorderingAllowed(false);
		jTable1Model = new DefaultTableModel( favData,
					new String[] { "NO.", "v", "driverid", "name", "description", "url", "user", "password" });

		jTable1.setModel(jTable1Model);
		jTable1.setRowHeight(20);

		DefaultTableCellRenderer cellRenderer3 = new DefaultTableCellRenderer();
		cellRenderer3.setHorizontalAlignment(SwingConstants.CENTER);
		col = jTable1.getColumn("driverid");
		col.setCellRenderer(cellRenderer3);

		DefaultTableCellRenderer cellRenderer2 = new DefaultTableCellRenderer();
		cellRenderer2.setHorizontalAlignment(SwingConstants.CENTER);
		cellRenderer2.setBackground(Color.LIGHT_GRAY);
		col = jTable1.getColumn("NO.");
		col.setCellRenderer(cellRenderer2);

	    setSize(1100, 660);
		setLocationRelativeTo(null);
		setModal(true);
		fitTableColumns(jTable1);
	}

	private void fitTableColumns(JTable myTable) {
		JTableHeader header = myTable.getTableHeader();
		int rowCount = myTable.getRowCount();

		Enumeration columns = myTable.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn column = (TableColumn) columns.nextElement();
			int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
			int width = (int) myTable.getTableHeader().getDefaultRenderer()
					.getTableCellRendererComponent(myTable, column.getIdentifier(), false, false, -1, col)
					.getPreferredSize().getWidth();
			for (int row = 0; row < rowCount; row++) {
				int preferedWidth = (int) myTable.getCellRenderer(row, col)
						.getTableCellRendererComponent(myTable, myTable.getValueAt(row, col), false, false, row, col)
						.getPreferredSize().getWidth();
				width = Math.max(width, preferedWidth);
			}
			header.setResizingColumn(column); // 此行很重要
			column.setWidth(width + myTable.getIntercellSpacing().width + 10);
		}
	}

	/**
	 * 按钮事件监听器
	 */
	private class ButtonActionListener extends AbstractActionListener {
		@Override
		protected void doActionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if ("确定".equals(command)) {
				// 保存修改的结果

			} else {
				// 关闭对话框
				setVisible(false);
			}
		}
	}

	// 下面是个Renderer类
	public class MyCheckBoxRenderer extends JCheckBox implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		public MyCheckBoxRenderer() {
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}
			if (value == null || !(value instanceof Boolean)) {
				value = new Boolean(false);
			}
			setSelected(((Boolean) value).booleanValue());
			return this;
		}
	}
}