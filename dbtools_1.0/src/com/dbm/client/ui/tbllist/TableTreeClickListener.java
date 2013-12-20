package com.dbm.client.ui.tbllist;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.dbm.client.action.CursorChanger;
import com.dbm.client.action.data.UpdActionListener;
import com.dbm.client.db.DbClient;
import com.dbm.client.db.DbClientFactory;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.Sec01Dialog;
import com.dbm.client.util.LoggerWrapper;

public class TableTreeClickListener extends MouseAdapter {

	/**
	 * instances of the log class
	 */
	private static LoggerWrapper logger = new LoggerWrapper(TableTreeClickListener.class); 

	JPopupMenu jPopupMenu1 = null;


	public void mouseReleased(MouseEvent e) {
		JTree tblTree = (JTree) e.getSource();
		DefaultMutableTreeNode tblNode = (DefaultMutableTreeNode) tblTree.getLastSelectedPathComponent();
		if (tblNode == null || !tblNode.isLeaf()) {
			return;
		}

		if (e.getModifiers() == MouseEvent.BUTTON1_MASK && e.getClickCount() == 2) {
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

		} else if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {

			jPopupMenu1 = new JPopupMenu();
			setComponentPopupMenu( tblTree, jPopupMenu1);

		} else if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {

			JMenuItem jMenuItem2 = new JMenuItem();
			jPopupMenu1.add(jMenuItem2);
			jMenuItem2.setText("Table Info");

			JMenuItem jMenuItem1 = new JMenuItem();
			jPopupMenu1.add(jMenuItem1);
			jMenuItem1.setText("Copy Table Name");
			jMenuItem1.addActionListener(new CpTblNmActionListener((String) tblNode.getUserObject()));
		}
	}

	private static final Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
	private class CpTblNmActionListener implements ActionListener {
		private String _tblName = null;
		CpTblNmActionListener(String tblName) {
			_tblName = tblName;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			StringSelection stsel = new StringSelection(_tblName);
			system.setContents(stsel, null);
		}
	}

	private void getTableData(DefaultMutableTreeNode tblNode) {
		String tblName = (String) tblNode.getUserObject();
		logger.debug("所选择的表名：" + tblName);
		UpdActionListener updMng = UpdActionListener.getInstance();
		updMng.setTblName(tblName);

		DbClient dbClient = DbClientFactory.getDbClient();
		dbClient.executeQuery(tblName);

		// 使[更新]和[删除]按钮可用
		JButton button = (JButton) AppUIAdapter.getUIObj(AppUIAdapter.BTN_UPDATE);
		button.setEnabled(true);

		button = (JButton) AppUIAdapter.getUIObj(AppUIAdapter.BTN_DELETE);
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
					if (tblNode == null || !tblNode.isLeaf()) {
						return;
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
}
