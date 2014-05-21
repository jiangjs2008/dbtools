package com.dbm.client.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.ui.tbllist.BaseNode;
import com.dbm.client.ui.tbllist.ObjectsTreeModel;
import com.dbm.client.ui.tbllist.TableTypesGroupNode;
import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.log.LoggerWrapper;
import com.dbm.common.property.ConnBean;
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
 * 数据库登陆画面<br><br>
 * [function]<br>
 * 数据库登陆<br><br>
 * [history]<br>
 * 2013/05/10 ver1.0 JiangJusheng<br>
 */
public class Lgn01Dialog extends javax.swing.JDialog {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * instances of the log class
	 */
	private static LoggerWrapper logger = new LoggerWrapper(Lgn01Dialog.class);

	private JTextField jTextField1;
	private JTextField jTextField2;
	private JTextField jTextField3;
	private JPasswordField jTextField4;


	public Lgn01Dialog() {
		super();

		initGUI();
		setDbInfo();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				DbClientFactory.close();
			}
		});
	}

	private void initGUI() {
		JPanel jPanel1 = new JPanel();
		getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.setLayout(null);

		JButton jButton1 = new JButton();
		jPanel1.add(jButton1);
		jButton1.setText("Cancel");
		jButton1.setBounds(100, 200, 85, 30);
		jButton1.addActionListener(new AbstractActionListener() {
			@Override
			protected void doActionPerformed(ActionEvent e) {
				DbClientFactory.close();
				setVisible(false);
			}
		});

		JButton jBtnConnect = new JButton();
		jPanel1.add(jBtnConnect);
		jBtnConnect.setText("Connect");
		jBtnConnect.setBounds(415, 200, 85, 30);
		jBtnConnect.addActionListener(new ConnActionListener());

		JLabel jLabel1 = new JLabel();
		jPanel1.add(jLabel1);
		jLabel1.setText("Alias");
		jLabel1.setBounds(20, 40, 70, 20);

		JLabel jLabel2 = new JLabel();
		jPanel1.add(jLabel2);
		jLabel2.setText("Db Url");
		jLabel2.setBounds(20, 70, 70, 20);

		JLabel jLabel3 = new JLabel();
		jPanel1.add(jLabel3);
		jLabel3.setText("User Name");
		jLabel3.setBounds(20, 100, 70, 20);

		JLabel jLabel4 = new JLabel();
		jPanel1.add(jLabel4);
		jLabel4.setText("Password");
		jLabel4.setBounds(20, 130, 70, 20);

		jTextField1 = new JTextField();
		jPanel1.add(jTextField1);
		jTextField1.setBounds(100, 40, 300, 25);

		jTextField2 = new JTextField();
		jPanel1.add(jTextField2);
		jTextField2.setBounds(100, 70, 470, 25);

		jTextField3 = new JTextField();
		jPanel1.add(jTextField3);
		jTextField3.setBounds(100, 100, 150, 25);

		jTextField4 = new JPasswordField();
		jPanel1.add(jTextField4);
		jTextField4.setBounds(100, 130, 150, 25);

		setSize(600, 300);
		setModal(true);

		// set location
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - 600) / 2, (dim.height - 300) / 2);
	}

	/**
	 * 在登陆画面显示数据库连接信息
	 */
	private void setDbInfo() {
		FavrBean favrInfo = Session.getCurrFavrInfo();
		ConnBean connInfo = null;

		if (favrInfo != null) {
			// 如果是从点击快捷方式而来
			connInfo = PropUtil.getDbConnInfo(favrInfo.driverId);
			jTextField1.setText(favrInfo.name);
			jTextField2.setText(favrInfo.url);
			jTextField3.setText(favrInfo.user);
			jTextField4.setText(favrInfo.password);

		} else {
			connInfo = Session.getCurrConnInfo();
			jTextField1.setText(connInfo.dbType);
			jTextField2.setText(connInfo.sampleUrl);
			jTextField3.setText("");
			jTextField4.setText("");
		}
	}

	/**
	 * 登陆数据库事件监听器
	 */
	private class ConnActionListener extends AbstractActionListener {
		@Override
		protected void doActionPerformed(ActionEvent e) {
			// 数据库URL
			String item2 = jTextField2.getText();
			// 用户名
			String item3 = jTextField3.getText();
			// 密码
			String item4 = new String(jTextField4.getPassword());

			// 将用户的数据库信息保存到缓存
			FavrBean favrInfo = Session.getCurrFavrInfo();
			ConnBean connInfo = null;

			if (favrInfo != null) {
				// 如果是从点击快捷方式而来
				// 用户可能会修改连接信息
				connInfo = PropUtil.getDbConnInfo(favrInfo.driverId);
				favrInfo.url = item2;
				favrInfo.user = item3;
				favrInfo.password = item4;

			} else {
				// 直接选择数据库方式
				// 用户可能会修改连接信息
				connInfo = Session.getCurrConnInfo();
				connInfo.sampleUrl = item2;
				connInfo.user = item3;
				connInfo.password = item4;
			}

			// 登陆到数据库
			DbClientFactory.createDbClient(connInfo.action);
			DbClient dbClient = DbClientFactory.getDbClient();

//			if (dbClient instanceof DbClient4SQLiteImpl) {
//				// TODO --特殊情况：连接手机上的sqlite时需要再修正一次连接URL
//				AppUIAdapter.setUIObj(AppUIAdapter.DbUrlTxtField, jTextField2);
//			}

			dbClient.start(new String[] { connInfo.driver, item2, item3, item4 });

			// 显示数据库内容：表、视图等等
			List<String> objList = dbClient.getDbMetaData();
			if (objList != null && objList.size() > 0) {
				JTree jTree1 = (JTree) AppUIAdapter.getUIObj(AppUIAdapter.TableTreeUIObj);
				jTree1.addTreeExpansionListener(new MyExpansionListener());

				ObjectsTreeModel treeModel = (ObjectsTreeModel) jTree1.getModel();
				treeModel.loadList(objList);
				treeModel.reload();

				// 关闭登陆画面
				setVisible(false);
			}
			((Frame) AppUIAdapter.getUIObj(AppUIAdapter.AppMainGUI)).setTitle(Session.APP_TITLE + "  -- " + item2);
		}
	}

	private final static class MyExpansionListener implements TreeExpansionListener {

		public void treeExpanded(TreeExpansionEvent evt) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) evt.getPath().getLastPathComponent();
			if (node instanceof TableTypesGroupNode) {
				// 显示DB所属对象一览
				DbClient dbClient = DbClientFactory.getDbClient();
				String schema = null;
				try {
					schema = dbClient.getConnection().getMetaData().getUserName();
				} catch (SQLException ex) {
					logger.error(ex);
				}
				((BaseNode) node).expand(dbClient.getDbObjList(null, schema, "%", new String[] { ((TableTypesGroupNode) node).getCatalogIdentifier() }));
			}
		}

		public void treeCollapsed(TreeExpansionEvent evt) {
		}
	}
}
