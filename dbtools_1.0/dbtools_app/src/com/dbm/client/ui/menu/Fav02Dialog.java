package com.dbm.client.ui.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSON;
import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.action.menu.FavrMenuActionListener;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.util.AppPropUtil;
import com.dbm.client.util.StringUtil;
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

	private static boolean isUpdate = false;

	private JTable jTable1;
	private JButton jButton1;
	private JButton jButton3;
	private JButton jButton2;
	private JLabel jLabel1;
	private DefaultCellEditor _editor = null;

	private FavrBean[] _favrList = null;

	/**
	 * 构造函数
	 */
	public Fav02Dialog() {
		super();
		isUpdate = false;
		setTitle("快捷方式一览");
		rootPane.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

	    setSize(1100, 570);
		setLocationRelativeTo(null);
		//setModal(true);

		JPanel jPanel1 = new JPanel();
		getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.setLayout(null);

		ButtonActionListener btnActListener = new ButtonActionListener();

		jButton1 = new JButton();
		jPanel1.add(jButton1);
		jButton1.setBounds(111, 485, 90, 30);
		jButton1.setText("取消");
		jButton1.addActionListener(btnActListener);

		jButton2 = new JButton();
		jPanel1.add(jButton2);
		jButton2.setBounds(550, 485, 90, 30);
		jButton2.setText("确定");
		jButton2.addActionListener(btnActListener);

		jButton3 = new JButton();
		jPanel1.add(jButton3);
		jButton3.setText("另存为");
		jButton3.setBounds(320, 485, 90, 30);
		jButton3.addActionListener(btnActListener);

		jLabel1 = new JLabel();
		jPanel1.add(jLabel1);
		jLabel1.setText("可以在此处修改数据库连接信息,  并可设置该连接是否有效.  确认修改请点击\"确定\"按钮.");
		jLabel1.setBounds(25, 10, 650, 30);

		jTable1 = new JTable();
		JScrollPane jsp = new JScrollPane(jTable1);
		jsp.setBounds(25, 45, 1050, 419);
		jPanel1.add(jsp);

		String[] tableHeads = new String[] { "NO.", "", "driverid", "name", "description", "url", "user", "password" };
		DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
		dtm.setColumnIdentifiers(tableHeads);

		FavrBean[] favList = PropUtil.getFavrInfo();
		_favrList = favList.clone();
		int leng = favList.length;

		for (int i = 0; i < leng; i++) {
			FavrBean favItem = favList[i];
			if (favItem == null) {
				continue;
			}
			Object[] favData = new Object[8];
			favData[0] = Integer.toString(i + 1);
			favData[1] = Boolean.valueOf(favItem.useFlg);
			favData[2] = Integer.toString(favItem.driverId);
			favData[3] = favItem.name;
			favData[4] = favItem.description;
			favData[5] = favItem.url;
			favData[6] = favItem.user;
			favData[7] = favItem.password;
			dtm.addRow(favData);
		}

		jTable1.setRowHeight(20);
		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		

	    TableColumn col = jTable1.getColumnModel().getColumn(1);
		col.setCellRenderer(new JCheckBoxRenderer());
		JCheckBox jcb = new JCheckBox();
		jcb.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultCellEditor dce = new DefaultCellEditor(jcb);
		col.setCellEditor(dce);

		DefaultTableCellRenderer cellRenderer3 = new DefaultTableCellRenderer();
		cellRenderer3.setHorizontalAlignment(SwingConstants.CENTER);
		 col = jTable1.getColumn("driverid");
		col.setCellRenderer(cellRenderer3);

		DefaultTableCellRenderer cellRenderer2 = new DefaultTableCellRenderer();
		cellRenderer2.setHorizontalAlignment(SwingConstants.CENTER);
		cellRenderer2.setBackground(Color.LIGHT_GRAY);
		col = jTable1.getColumn("NO.");
		col.setCellRenderer(cellRenderer2);

		_editor = new DefaultCellEditor(new JTextField());
		_editor.addCellEditorListener(new TableCellEditorListener());
		fitTableColumns(jTable1, _editor);
	}

	// 调整表格每列宽度(自适应列宽)
	private void fitTableColumns(JTable myTable, DefaultCellEditor cellEditor) {
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
			column.setWidth(width + 15);
			column.setPreferredWidth(width + 15); // 此行很重要,如果不设置，那么CheckBox将异常，原因不明
			if (col > 1) {
				column.setCellEditor(cellEditor);
			}
		}
	}

	// 编辑/修改连接信息
	private class TableCellEditorListener implements CellEditorListener {

		@Override
		public void editingStopped(ChangeEvent e) {
			DefaultCellEditor dce = (DefaultCellEditor) e.getSource();
			Object obj = dce.getCellEditorValue();

			int row = jTable1.getSelectedRow();
			if (row == -1) {
				return;
			}
			FavrBean favBean = PropUtil.getFavrInfo(row);

			int col = jTable1.getSelectedColumn();
			switch (col) {
			case 1:
				// 若是点击了checkbox,那么值肯定是修改过的，必须保存
				isUpdate = true;
				favBean.useFlg = !favBean.useFlg;
				break;

			case 2:
				// 若是其他项目被编辑，则必须判断改过的值是否与初始值一致
				String strObj = StringUtils.trimToNull((String) obj);
				int objValue = NumberUtils.toInt(strObj);
				if (favBean.driverId != objValue) {
					isUpdate = true;
					favBean.driverId = objValue;
				}
				break;
			case 3:
				if (!favBean.name.equals(obj)) {
					isUpdate = true;
					favBean.name = (String) obj;
				}
				break;
			case 4:
				if (!favBean.description.equals(obj)) {
					isUpdate = true;
					favBean.description = (String) obj;
				}
				break;
			case 5:
				if (!favBean.url.equals(obj)) {
					isUpdate = true;
					favBean.url = (String) obj;
				}
				break;
			case 6:
				if (!favBean.user.equals(obj)) {
					isUpdate = true;
					favBean.user = (String) obj;
				}
				break;
			case 7:
				if (!favBean.password.equals(obj)) {
					isUpdate = true;
					favBean.password = (String) obj;
				}
				break;
			default : 
				isUpdate = false;
			}

			if (isUpdate) {
				saveFavrInfo(favBean);
			}
		}

		@Override
		public void editingCanceled(ChangeEvent e) {
		}
	}

	private void saveFavrInfo(FavrBean favBean) {
		if (_favrList != null) {
			_favrList[favBean.favrId] = favBean;
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
				if (_editor != null) {
					_editor.stopCellEditing();
				}

				if (isUpdate) {
					PropUtil.setFavrInfo(_favrList);
					AppPropUtil.saveFavrInfo();
					// 修改菜单显示
					JFrame jFrame = (JFrame) AppUIAdapter.getUIObj(AppUIAdapter.AppMainGUI);
					JMenuBar menuBar = jFrame.getJMenuBar();
					JMenu menu = menuBar.getMenu(2);
					// 先删除旧的菜单
					for (int i = menu.getMenuComponentCount() - 1; i > 2; i --) {
						menu.remove(i);
					}
					// 重新设置
					FavrMenuActionListener connAction = new FavrMenuActionListener();
					// 显示最近使用数据库一览
					// load favorite database
					for (FavrBean fbInfo : PropUtil.getFavrInfo()) {
						if (fbInfo == null || !fbInfo.useFlg) {
							continue;
						}
						JMenuItem jMenuItem22 = new JMenuItem();
						menu.add(jMenuItem22);
						jMenuItem22.setName("favr:" + Integer.toString(fbInfo.favrId));
						jMenuItem22.setText(fbInfo.name);
						jMenuItem22.setToolTipText(StringUtil.printTipText(fbInfo.url, "user :=  " + StringUtils.trimToEmpty(fbInfo.user)));
						jMenuItem22.addActionListener(connAction);
					}
				}
				// 关闭对话框
				setVisible(false);
			} else if ("另存为".equals(command)) {
				// 保存修改的结果
				if (_editor != null) {
					_editor.stopCellEditing();
				}

				if (isUpdate) {
					OutputStream out = null;
					try {
						Properties p = new Properties();
						for (FavrBean favrBean : _favrList) {
							String json = JSON.toJSONString(favrBean);
							p.put(Integer.toString(favrBean.favrId), json);
						}

						out = new FileOutputStream(System.getProperty("user.dir") + "/conf/__favrinfo-2.properties");
						p.store(out, "");

					} catch (Exception exp) {
						logger.error(exp);
					} finally {
						if (out != null) {
							try {
								out.close();
							} catch (IOException ioex) {
								logger.error(ioex);
							}
						}
					}
				}
				
				
			} else {
				// 关闭对话框
				setVisible(false);
			}

		}
	}

	private class JCheckBoxRenderer extends JCheckBox implements TableCellRenderer {

		private static final long serialVersionUID = 7841871541650508488L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			if (value != null) {
			this.setSelected((Boolean) value);
			}
			setHorizontalAlignment(SwingConstants.CENTER);
			return this;
		}
	}
}