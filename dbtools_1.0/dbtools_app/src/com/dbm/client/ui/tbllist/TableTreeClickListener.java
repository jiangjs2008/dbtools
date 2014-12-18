package com.dbm.client.ui.tbllist;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;

import org.apache.commons.lang3.StringUtils;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.action.CursorChanger;
import com.dbm.client.action.data.PageJumpActionListener;
import com.dbm.client.action.data.UpdActionListener;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.help.Inf00Dialog;
import com.dbm.client.ui.help.Inf01Dialog;
import com.dbm.client.ui.help.Inf02Dialog;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.error.BaseExceptionWrapper;
import com.dbm.common.log.LoggerWrapper;

public class TableTreeClickListener extends MouseAdapter implements TreeWillExpandListener {

	/**
	 * instances of the log class
	 */
	private static LoggerWrapper logger = new LoggerWrapper(TableTreeClickListener.class); 

	private JPopupMenu jPopupMenu1 = null;

	@Override
	public void mouseReleased(MouseEvent e) {
		JTree tblTree = (JTree) e.getSource();
		DefaultMutableTreeNode tblNode = (DefaultMutableTreeNode) tblTree.getLastSelectedPathComponent();
		if (tblNode == null) {
			return;
		}
		String tblName = (String) tblNode.getUserObject();
		if (!tblNode.isLeaf()) {
			if (!"Database".equals(tblName)) {
				return;
			}
		}

		if (e.getModifiers() == InputEvent.BUTTON1_MASK && e.getClickCount() == 2) {
			if ("Database".equals(tblNode.getUserObject())) {
				return;
			}

			CursorChanger cc = new CursorChanger(tblTree);
			cc.show();
			try {
				getTableData(tblNode);
			} finally {
				cc.restore();
			}

		} else if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
			jPopupMenu1 = new JPopupMenu();
			setComponentPopupMenu( tblTree, jPopupMenu1);

		} else if (e.getModifiers() == InputEvent.BUTTON3_MASK) {

			if ("Database".equals(tblName)) {
				if (jPopupMenu1 == null) {
					logger.info("JPopupMenu 未定义");
					return;
				}
				DbClient dbClient = DbClientFactory.getDbClient();
				if (dbClient == null) {
					return;
				}
				if (dbClient.getConnection() == null) {
					return;
				}
				JMenuItem jMenuItem2 = new JMenuItem("Database Info");
				jPopupMenu1.add(jMenuItem2);
				jMenuItem2.addActionListener(new DbInfoActionListener());

				jPopupMenu1.add(new JSeparator());
				jPopupMenu1.add(new JCheckBoxMenuItem("t1"));
				jPopupMenu1.add(new JCheckBoxMenuItem("t2"));
				
			} else {
				if (jPopupMenu1 == null) {
					logger.info("JPopupMenu 未定义");
					return;
				}

				JMenuItem jMenuItem2 = new JMenuItem("Table Info");
				jPopupMenu1.add(jMenuItem2);
				jMenuItem2.addActionListener(new TblInfoActionListener(tblName));
	
				JMenuItem jMenuItem3 = new JMenuItem("Index Info");
				jPopupMenu1.add(jMenuItem3);
				jMenuItem3.addActionListener(new IdxInfoActionListener(tblName));
	
				jPopupMenu1.add(new JSeparator());
				JMenuItem jMenuItem1 = new JMenuItem("Copy Table Name");
				jPopupMenu1.add(jMenuItem1);
				jMenuItem1.addActionListener(new CpTblNmActionListener(tblName));
			}
		}
	}

	/**
	 * 取得数据库的基本信息
	 */
	private class DbInfoActionListener extends AbstractActionListener {

		@Override
		public void doActionPerformed(ActionEvent e) {

			DbClient dbClient = DbClientFactory.getDbClient();
			if (dbClient == null) {
				return;
			}
			if (dbClient.getConnection() == null) {
				return;
			}
			try {
				DatabaseMetaData dmd = dbClient.getConnection().getMetaData();

				Inf00Dialog inf00 = new Inf00Dialog();
				inf00.setDbMetaInfo(dmd);
				inf00.setVisible(true);
			} catch (SQLException exp) {
				throw new BaseExceptionWrapper(exp);
			}
		}

		@Override
		protected void doAfterFinally() {
			if (jPopupMenu1 != null) {
				jPopupMenu1.setVisible(false);
				jPopupMenu1 = null;
			}
		}
	}

	/**
	 * 取得指定表的所有列的定义信息
	 */
	private class TblInfoActionListener extends AbstractActionListener {
		private String _tblName = null;
		private TblInfoActionListener(String tblName) {
			_tblName = tblName;
		}

		@Override
		public void doActionPerformed(ActionEvent e) {

			DbClient dbClient = DbClientFactory.getDbClient();
			String realName = _tblName;
			String[] tblNameArr = StringUtils.split(_tblName, '.');
			if (tblNameArr.length == 2) {
				realName = tblNameArr[1];
			}

			Vector<Vector<String>> allData = null;
			try {
				DatabaseMetaData dmd = dbClient.getConnection().getMetaData();
				// 取得指定表的主键信息
				ResultSet pkRs = dmd.getPrimaryKeys(null, null, realName);
				ArrayList<String> pkList = new ArrayList<String>();
				while (pkRs.next()) {
					pkList.add(pkRs.getString(4));
				}
				// 取得指定表的所有列的信息
				ResultSet columnRs = dmd.getColumns(null, null, realName, "%");
				String colName = null;
				Vector<String> columnInfo = null;
				allData = new Vector<Vector<String>>();
				int no = 1;
				while (columnRs.next()) {
					columnInfo = new Vector<String>(7);
					// 序号
					columnInfo.add(Integer.toString(no));
					no ++;
					// 列名(COLUMN_NAME )
					colName = columnRs.getString(4);
					columnInfo.add(colName);
					// 类型名(TYPE_NAME)
					columnInfo.add(columnRs.getString(6));
					// 列的大小(COLUMN_SIZE)
					columnInfo.add(columnRs.getString(7));

					// 是否为主键
					if (pkList.indexOf(colName) >= 0) {
						// 是主键
						columnInfo.add("Y");
					} else {
						columnInfo.add("");
					}

					// 是否可为空(NULLABLE)
					if (columnRs.getInt(11) == 1) {
						// 可为空
						columnInfo.add("Y");
					} else {
						// 不可为空
						columnInfo.add("");
					}

					// 列的注释(REMARKS)
					columnInfo.add(columnRs.getString(12));
					// 默认值(COLUMN_DEF)
					columnInfo.add(columnRs.getString(13));

					// 是否自动增加(IS_AUTOINCREMENT)
					columnInfo.add(columnRs.getString(23));

					allData.add(columnInfo);
				}

			} catch (SQLException exp) {
				throw new BaseExceptionWrapper(exp);
			}
			Inf01Dialog inf01 = new Inf01Dialog();
			inf01.setColumnInfo(allData);
			inf01.setVisible(true);
		}

		@Override
		protected void doAfterFinally() {
			if (jPopupMenu1 != null) {
				jPopupMenu1.setVisible(false);
				jPopupMenu1 = null;
			}
		}
	}

	/**
	 * 取得指定表的索引信息
	 */
	private class IdxInfoActionListener extends AbstractActionListener {
		private String _tblName = null;
		private IdxInfoActionListener(String tblName) {
			_tblName = tblName;
		}

		@Override
		public void doActionPerformed(ActionEvent e) {

			DbClient dbClient = DbClientFactory.getDbClient();
			String realName = _tblName;
			String[] tblNameArr = StringUtils.split(_tblName, '.');
			if (tblNameArr.length == 2) {
				realName = tblNameArr[1];
			}

			Vector<Vector<String>> allData = null;
			try {
				DatabaseMetaData dmd = dbClient.getConnection().getMetaData();

				// 取得指定表的索引信息
				ResultSet columnRs = dmd.getIndexInfo(null, null, realName, true, true);
				String colName = null;
				Vector<String> columnInfo = null;
				allData = new Vector<Vector<String>>();
				int no = 1;
				while (columnRs.next()) {
					columnInfo = new Vector<String>(7);
					// 序号
					columnInfo.add(Integer.toString(no));
					no ++;

					// 是否不唯一(NON_UNIQUE)
					columnInfo.add(columnRs.getString(4));
					// 索引类别(INDEX_QUALIFIER)
					columnInfo.add(columnRs.getString(5));
					// 索引名称(INDEX_NAME)
					colName = columnRs.getString(6);
					columnInfo.add(colName);
					// 索引类型(TYPE)
					columnInfo.add(columnRs.getString(7));

					// 过滤器条件(FILTER_CONDITION)
					columnInfo.add(columnRs.getString(12));

					allData.add(columnInfo);
				}

			} catch (SQLException exp) {
				throw new BaseExceptionWrapper(exp);
			}
			Inf02Dialog inf02 = new Inf02Dialog();
			inf02.setColumnInfo(allData);
			inf02.setVisible(true);
		}

		@Override
		protected void doAfterFinally() {
			if (jPopupMenu1 != null) {
				jPopupMenu1.setVisible(false);
				jPopupMenu1 = null;
			}
		}
	}

	private static final Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
	/**
	 * 拷贝表名到系统剪贴板
	 */
	private class CpTblNmActionListener implements ActionListener {
		private String _tblName = null;
		private CpTblNmActionListener(String tblName) {
			_tblName = tblName;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			StringSelection stsel = new StringSelection(_tblName);
			system.setContents(stsel, null);
		}
	}

	/**
	 * 查询指定表的所有数据
	 *
	 * @param tblNode
	 */
	private void getTableData(DefaultMutableTreeNode tblNode) {
		String tblName = (String) tblNode.getUserObject();
		logger.debug("所选择的表名：" + tblName);

		UpdActionListener updMng = UpdActionListener.getInstance();
		updMng.setTblName(tblName);

		DbClient dbClient = DbClientFactory.getDbClient();
		dbClient.setTableName(tblName);
		ResultSet rowSet = dbClient.defaultQuery(1);

		PageJumpActionListener pageAction = (PageJumpActionListener) AppUIAdapter.getUIObj(AppUIAdapter.PageAction);
		pageAction.displayTableData(rowSet, 1);

		// 使[更新]按钮可用
		JButton button = (JButton) AppUIAdapter.getUIObj(AppUIAdapter.BTN_UPDATE);
		button.setEnabled(true);
	}

	/**
	* Auto-generated method for setting the popup menu for a component
	*/
	private void setComponentPopupMenu(final java.awt.Component parent, final javax.swing.JPopupMenu menu) {
		parent.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()) {
					JTree tblTree = (JTree) parent;
					DefaultMutableTreeNode tblNode = (DefaultMutableTreeNode) tblTree.getLastSelectedPathComponent();
					if (tblNode == null) {
						return;
					}
					String tblName = (String) tblNode.getUserObject();
					if (!tblNode.isLeaf()) {
						if (!"Database".equals(tblName)) {
							return;
						}
					}

					Rectangle rt = tblTree.getPathBounds(tblTree.getLeadSelectionPath());
					if (rt != null) {
						if (rt.contains(e.getX(), e.getY())) {
							menu.show(parent, e.getX(), e.getY());
						}
					}
				}
			}
		});
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent evt) throws ExpandVetoException {
	}

	/**
	 * 禁止根节点折叠
	 */
	@Override
	public void treeWillCollapse(TreeExpansionEvent evt) throws ExpandVetoException {
		JTree tree = (JTree) evt.getSource();

		// Get the path that will be collapsed
		DefaultMutableTreeNode tblNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (tblNode == null) {
			return;
		}
		String tblName = (String) tblNode.getUserObject();
		if (!tblNode.isLeaf()) {
			if ("Database".equals(tblName)) {
				throw new ExpandVetoException(evt);
			}
		}
	}
}
