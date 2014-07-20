package com.dbm.client.ui.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.common.property.ConnBean;
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
 * Drm02Dialog<br><br>
 * [function]<br>
 * 管理jdbc驱动信息<br><br>
 * [history]<br>
 * 2014/06/08 ver1.0 JiangJusheng<br>
 */
public class Drm02Dialog extends javax.swing.JDialog {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private JTable jTable1;
	private JButton jButton1;
	private JButton jButton2;

	/**
	 * 构造函数
	 */
	public Drm02Dialog() {
		super();
		setTitle("数据库驱动一览");
		rootPane.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

	    setSize(1300, 460);
		setLocationRelativeTo(null);
		setModal(true);

		JPanel jPanel1 = new JPanel();
		getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.setLayout(null);

		ButtonActionListener btnActListener = new ButtonActionListener();

		jButton1 = new JButton();
		jPanel1.add(jButton1);
		jButton1.setBounds(300, 375, 90, 30);
		jButton1.setText("取消");
		jButton1.addActionListener(btnActListener);

		jButton2 = new JButton();
		jPanel1.add(jButton2);
		jButton2.setBounds(650, 375, 90, 30);
		jButton2.setText("确定");
		jButton2.addActionListener(btnActListener);

		JLabel jLabel1 = new JLabel();
		jPanel1.add(jLabel1);
		jLabel1.setText("可以在此处修改数据库Jdbc驱动设置");
		jLabel1.setBounds(25, 10, 600, 30);

		jTable1 = new JTable();
		JScrollPane jsp = new JScrollPane(jTable1);
		jsp.setBounds(25, 45, 1250, 300);
		jPanel1.add(jsp);
		
		ConnBean[] connList = PropUtil.getDbConnInfo();
		int leng = connList.length - 1;
		String[][] favData = new String[leng][8];
		for (int i = 0; i < leng; i++) {
			ConnBean connItem = connList[i + 1];
			if (connItem == null) {
				continue;
			}
			favData[i][0] = Integer.toString(i + 1);
			favData[i][1] = connItem.name;
			favData[i][2] = connItem.description;
			favData[i][3] = connItem.action;
			favData[i][4] = connItem.driver;
			favData[i][5] = connItem.sampleUrl;
			favData[i][6] = "";
		}

		TableModel jTable1Model =
			new DefaultTableModel(
					new String[] { "NO.", "name", "description", "action", "driver", "sampleurl", "jarname" }, 1);
		jTable1.setModel(jTable1Model);
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jTable1.getTableHeader().setReorderingAllowed(false);
		jTable1Model = new DefaultTableModel( favData,
					new String[] { "NO.", "name", "description", "action", "driver", "sampleurl", "jarname" });

		jTable1.setModel(jTable1Model);
		jTable1.setRowHeight(20);

		DefaultTableCellRenderer cellRenderer2 = new DefaultTableCellRenderer();
		cellRenderer2.setHorizontalAlignment(SwingConstants.CENTER);
		cellRenderer2.setBackground(Color.LIGHT_GRAY);
		TableColumn  col = jTable1.getColumn("NO.");
		col.setCellRenderer(cellRenderer2);

		fitTableColumns(jTable1);
	}

	private void fitTableColumns(JTable myTable) {
		JTableHeader header = myTable.getTableHeader();
		int rowCount = myTable.getRowCount();

		Enumeration<?> columns = myTable.getColumnModel().getColumns();
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