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

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.action.CursorChanger;
import com.dbm.client.action.data.PageJumpActionListener;
import com.dbm.client.action.data.UpdActionListener;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Session;
import com.dbm.client.ui.help.Inf00Dialog;
import com.dbm.client.ui.help.Inf01Dialog;
import com.dbm.client.ui.help.Inf02Dialog;
import com.dbm.common.db.DbClient;
import com.dbm.common.error.BaseExceptionWrapper;
import com.dbm.common.log.LoggerWrapper;
import com.dbm.common.util.MetaDataUtil;

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
				DbClient dbClient = Session.getDbClient();
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
			DbClient dbClient = Session.getDbClient();
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
			DbClient dbClient = Session.getDbClient();
			Vector<Vector<String>> allData = MetaDataUtil.getTblDefInfo(dbClient, _tblName);

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
			DbClient dbClient = Session.getDbClient();
			Vector<Vector<String>> allData = MetaDataUtil.getTblIdxInfo(dbClient, _tblName);

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

		DbClient dbClient = Session.getDbClient();
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
