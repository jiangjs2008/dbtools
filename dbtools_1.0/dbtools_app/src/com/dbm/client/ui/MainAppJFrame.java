package com.dbm.client.ui;

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

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Event;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang3.StringUtils;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.action.MyActionListener;
import com.dbm.client.action.data.CloseActionListener;
import com.dbm.client.action.data.ExecActionListener;
import com.dbm.client.action.data.PageJumpActionListener;
import com.dbm.client.action.data.UpdActionListener;
import com.dbm.client.action.menu.FavrMenuActionListener;
import com.dbm.client.ui.menu.Drm01Dialog;
import com.dbm.client.ui.menu.Drm02Dialog;
import com.dbm.client.ui.menu.Fav01Dialog;
import com.dbm.client.ui.menu.Fav02Dialog;
import com.dbm.client.ui.menu.His01Dialog;
import com.dbm.client.ui.menu.Sec01Dialog;
import com.dbm.client.ui.tbldata.TableCellSelectedListener;
import com.dbm.client.ui.tbldata.TableHeaderSelectedListener;
import com.dbm.client.ui.tbllist.BaseNode;
import com.dbm.client.ui.tbllist.ObjectsTreeModel;
import com.dbm.client.ui.tbllist.TableTreeClickListener;
import com.dbm.client.ui.tbllist.TableTypesGroupNode;
import com.dbm.client.util.StringUtil;
import com.dbm.common.db.DbClient;
import com.dbm.common.property.ConnBean;
import com.dbm.common.property.FavrBean;
import com.dbm.common.property.PropUtil;

/**
 * [name]<br>
 * main frame class<br><br>
 * [function]<br>
 * frame initial, add each component's event action listener<br>
 * menubar initial<br><br>
 * [history]<br>
 * 2013/05/10 ver1.0 JiangJusheng<br>
 */
public class MainAppJFrame extends javax.swing.JFrame {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6338002822670303714L;

	private JPanel jmainPanel;
	private JTable jTable1;

	private JTree jTree1;

	private JTextArea jTextArea1;

	private JButton jLabel4;
	private JTextField jTextField2;

	private JButton jButton1;
	private JTextField jTextField1;
	private JButton jLabel3;
	private JButton jLabel2;
	private JButton jLabel1;

	private AbstractActionListener execAction = null;
	private AbstractActionListener updAction = null;

	/**
	 * default constructor
	 */
	public MainAppJFrame() {
		super();

		initGui();
		initMenuBar();
		pack();
		setSize(1024, 768);

		AppUIAdapter.setUIObj(AppUIAdapter.AppMainGUI, this);
	}

	/**
	 * 
	 * handler window event
	 */
	@Override
	protected void processWindowEvent(final WindowEvent pEvent) {
		if (pEvent.getID() == WindowEvent.WINDOW_CLOSING) {
			// 结束程序
			DbClient dbClient = Session.getDbClient();
			if (dbClient != null) {
				dbClient.close();
			}
			setVisible(false);
			System.exit(0);
		} else {
			super.processWindowEvent(pEvent);
		}
	}

	/**
	 * frame initial
	 */
	private void initGui() {
		setTitle(Session.APP_TITLE);
		setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getClassLoader().getResource("dbpilot.jpg")));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		jmainPanel = new JPanel(new BorderLayout());
		add(jmainPanel);

		JPanel jLeftPanel = new JPanel();
		BoxLayout jLeftPanelLayout = new BoxLayout(jLeftPanel, javax.swing.BoxLayout.Y_AXIS);
		jLeftPanel.setLayout(jLeftPanelLayout);
		jLeftPanel.setBorder(new EmptyBorder(6, 4, 2, 3));

		JPanel jRightPanel = new JPanel();
		jRightPanel.setLayout(new BorderLayout());
		jRightPanel.setBorder(new EmptyBorder(6, 4, 3, 3));

		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jLeftPanel, jRightPanel);
		jsp.setDividerSize(2);
		jsp.setDividerLocation(750);
		jsp.setResizeWeight(1.0);
		jmainPanel.add(jsp, BorderLayout.CENTER);

		// SQL EditText ***********************************************************************
		jTextArea1 = new JTextArea();
		jLeftPanel.add(new JScrollPane(jTextArea1));

		jTextArea1.setFont(new Font("", Font.BOLD, 12));
		jTextArea1.setAutoscrolls(true);
		jTextArea1.setTabSize(2);

		// Button ******************************************************************************
		jLeftPanel.add(initButtonLayout());

		// database tree =========================================================================
		jTree1 = new JTree();
		jTree1.setModel(new ObjectsTreeModel());
		TableTreeClickListener treeListener = new TableTreeClickListener();
		jTree1.addMouseListener(treeListener);
		jTree1.addTreeWillExpandListener(treeListener);
		jTree1.addTreeExpansionListener(new MyExpansionListener());

		AppUIAdapter.setUIObj(AppUIAdapter.TableTreeUIObj, jTree1);

		JScrollPane jscroll = new JScrollPane(jTree1);
		jRightPanel.add(jscroll, BorderLayout.CENTER);

		// table data =========================================================================
		jTable1 = new JTable();
		jLeftPanel.add(new JScrollPane(jTable1));
		AppUIAdapter.setUIObj(AppUIAdapter.TableDataUIObj, jTable1);

		jTable1.setModel(Session.EmptyTableModel);
		jTable1.getTableHeader().setReorderingAllowed(false);
		jTable1.getTableHeader().addMouseListener(new TableHeaderSelectedListener());

		jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jTable1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jTable1.setCellSelectionEnabled(true);
		jTable1.addMouseListener(new TableCellSelectedListener());
		jTable1.setRowHeight(20);

		// copy&paste action
		MyActionListener myListener = new MyActionListener();
		myListener.setJTable(jTable1);
	}

	/**
	 * button layout initial
	 */
	private JPanel initButtonLayout() {
		// Button ******************************************************************************
		JPanel jBtnPanel = new JPanel();
		jBtnPanel.setLayout(null);

		// [执行]按钮
		JButton jBtnExecute = new JButton("Execute");
		jBtnExecute.setBounds(0, 9, 80, 30);
		jBtnPanel.add(jBtnExecute);

		execAction = new ExecActionListener(jTextArea1);
		// 注册快捷键(Alt-X)
		jBtnExecute.setMnemonic(KeyEvent.VK_X);
		// 添加普通点击事件
		jBtnExecute.addActionListener(execAction);

		// [更新]按钮
		JButton jBtnUpdate = new JButton("Update");
		jBtnUpdate.setBounds(120, 9, 80, 30);
		jBtnPanel.add(jBtnUpdate);
		AppUIAdapter.setUIObj(AppUIAdapter.BTN_UPDATE, jBtnUpdate);

		updAction = UpdActionListener.getInstance();
		// 注册快捷键(Alt-U)
		jBtnUpdate.setMnemonic(KeyEvent.VK_U);
		// 添加普通点击事件
		jBtnUpdate.addActionListener(updAction);

		// 分页相关组件 STA#####################################
		JPanel pagejumpPanel = new JPanel();
		pagejumpPanel.setLayout(null);
		pagejumpPanel.setBounds(335, 0, 415, 50);
		pagejumpPanel.setVisible(false);
		jBtnPanel.add(pagejumpPanel);

		// 翻页控制
		// 当前页数/总页数
		jTextField2 = new JTextField();
		pagejumpPanel.add(jTextField2);
		jTextField2.setEditable(false);
		jTextField2.setBounds(0, 20, 120, 25);
		jTextField2.setHorizontalAlignment(JTextField.RIGHT);
		jTextField2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		// 指定要查看的页数
		jTextField1 = new JTextField();
		jTextField1.setBounds(315, 20, 45, 26);
		jTextField1.addKeyListener(new KeyAdapter() {
			// 只允许输入数字
			public void keyTyped(KeyEvent e) {
				int keyChar = e.getKeyChar();
				if (keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) {
					return;
				} else {
					e.consume();
				}
			}
		});
		pagejumpPanel.add(jTextField1);

		PageJumpActionListener pagejumpAction = new PageJumpActionListener(pagejumpPanel, jTextField2, jTextField1);
		AppUIAdapter.setUIObj(AppUIAdapter.PageAction, pagejumpAction);
		AppUIAdapter.setUIObj(AppUIAdapter.PagePanel, pagejumpPanel);

		// 翻页按钮
		jLabel1 = new JButton();
		pagejumpPanel.add(jLabel1);
		jLabel1.setText("|<");
		jLabel1.setActionCommand("first");
		jLabel1.setBounds(125, 20, 45, 25);
		jLabel1.setMnemonic(KeyEvent.VK_LEFT);
		jLabel1.addActionListener(pagejumpAction);

		jLabel2 = new JButton();
		pagejumpPanel.add(jLabel2);
		jLabel2.setText("<");
		jLabel2.setActionCommand("backward");
		jLabel2.setBounds(169, 20, 45, 25);
		jLabel2.addActionListener(pagejumpAction);

		jLabel3 = new JButton();
		pagejumpPanel.add(jLabel3);
		jLabel3.setText(">");
		jLabel3.setActionCommand("forward");
		jLabel3.setBounds(213, 20, 45, 25);
		jLabel3.addActionListener(pagejumpAction);

		jLabel4 = new JButton();
		pagejumpPanel.add(jLabel4);
		jLabel4.setText(">|");
		jLabel4.setActionCommand("last");
		jLabel4.setBounds(257, 20, 45, 25);
		jLabel4.addActionListener(pagejumpAction);

		jButton1 = new JButton();
		pagejumpPanel.add(jButton1);
		jButton1.setText("go");
		jButton1.setActionCommand("go");
		jButton1.setBounds(358, 20, 48, 25);
		jButton1.addActionListener(pagejumpAction);
		// 分页相关组件 END#####################################

		jBtnPanel.setMaximumSize(new java.awt.Dimension(835, 50));
		jBtnPanel.setPreferredSize(new java.awt.Dimension(835, 50));
		return jBtnPanel;
	}

	/**
	 * menubar initial
	 */
	private void initMenuBar() {
		JMenuBar jMenuBar1 = new JMenuBar();
		setJMenuBar(jMenuBar1);

		// File menu ==============================================================================
		JMenu jMenu1 = new JMenu("     File    ");
		jMenu1.setMnemonic(KeyEvent.VK_F);
		jMenuBar1.add(jMenu1);

		JMenuItem jMenuItem3 = new JMenuItem("Close Connection");
		jMenu1.add(jMenuItem3);

		jMenuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		jMenuItem3.addActionListener(new CloseActionListener());

		jMenu1.add(new JSeparator());

		JMenuItem jMenuItem4 = new JMenuItem("Exit");
		jMenu1.add(jMenuItem4);
		jMenuItem4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK));
		jMenuItem4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 结束程序
				DbClient dbClient = Session.getDbClient();
				if (dbClient != null) {
					dbClient.close();
				}
				setVisible(false);
				System.exit(0);
			}
		});

		// Edit menu =========================================================================
		JMenu jMenu12 = new JMenu("    Edit    ");
		jMenu12.setMnemonic(KeyEvent.VK_E);
		jMenuBar1.add(jMenu12);

		// 拷贝粘贴操作
		JMenuItem jMenuItem121 = new JMenuItem("Cut");
		jMenu12.add(jMenuItem121);
		KeyStroke ctrlXKeyStroke = KeyStroke.getKeyStroke("control X");
		jMenuItem121.setAccelerator(ctrlXKeyStroke);

		JMenuItem jMenuItem122 = new JMenuItem("Copy");
		KeyStroke ctrlCKeyStroke = KeyStroke.getKeyStroke("control C");
		jMenuItem122.setAccelerator(ctrlCKeyStroke);
		jMenu12.add(jMenuItem122);

		JMenuItem jMenuItem123 = new JMenuItem("Paste");
		KeyStroke ctrlVKeyStroke = KeyStroke.getKeyStroke("control V");
		jMenuItem123.setAccelerator(ctrlVKeyStroke);
		jMenu12.add(jMenuItem123);

		// separate line
		jMenu12.add(new JSeparator());

		// 脚本历史查询
		JMenuItem jMenuItem124 = new JMenuItem("History");
		jMenu12.add(jMenuItem124);

		jMenuItem124.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
		jMenuItem124.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				His01Dialog dbDialog = new His01Dialog();
				dbDialog.setVisible(true);
			}
		});


		// separate line
		jMenu12.add(new JSeparator());

		// 标识为删除
		JMenuItem jMenuItem126 = new JMenuItem("Mark for delete");
		KeyStroke lKeyStroke6 = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK);
		jMenuItem126.setAccelerator(lKeyStroke6);
		jMenu12.add(jMenuItem126);

		JMenuItem jMenuItem127 = new JMenuItem("Unmark Delete");
		KeyStroke lKeyStroke67 = KeyStroke.getKeyStroke("ctrl shift DELETE");
		jMenuItem127.setAccelerator(lKeyStroke67);
		jMenu12.add(jMenuItem127);

		// separate line
		jMenu12.add(new JSeparator());

		// 数据操作
		JMenuItem jMenuItem128 = new JMenuItem("Execute Script");
		KeyStroke lKeyStroke8 = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK);
		jMenuItem128.setAccelerator(lKeyStroke8);
		jMenuItem128.registerKeyboardAction(execAction, lKeyStroke8, JComponent.WHEN_IN_FOCUSED_WINDOW);
		jMenuItem128.setMnemonic(KeyEvent.VK_X);
		jMenuItem128.addActionListener(execAction);
		jMenu12.add(jMenuItem128);

		JMenuItem jMenuItem129 = new JMenuItem("Update data");
		KeyStroke nKeyStroke9 = KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_DOWN_MASK);
		jMenuItem129.setAccelerator(nKeyStroke9);
		jMenuItem129.registerKeyboardAction(updAction, nKeyStroke9, JComponent.WHEN_IN_FOCUSED_WINDOW);
		jMenuItem129.setMnemonic(KeyEvent.VK_U);
		jMenuItem129.addActionListener(updAction);
		jMenu12.add(jMenuItem129);

		// Favorites menu =========================================================================
		JMenu jMenu2 = new JMenu("   Favorites   ");
		jMenu2.setMnemonic(KeyEvent.VK_V);
		jMenuBar1.add(jMenu2);

		JMenuItem jMenuItem23 = new JMenuItem("Add to Favorites");
		jMenu2.add(jMenuItem23);
		jMenuItem23.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Fav01Dialog dbDialog = new Fav01Dialog();
				dbDialog.setVisible(true);
			}
		});

		JMenuItem jMenuItem21 = new JMenuItem("Favorites Manage");
		jMenu2.add(jMenuItem21);
		jMenuItem21.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Fav02Dialog dbDialog = new Fav02Dialog();
				dbDialog.setVisible(true);
			}
		});
		// separate line
		jMenu2.add(new JSeparator());

		FavrMenuActionListener connAction = new FavrMenuActionListener();
		// 显示最近使用数据库一览
		// load favorite database
		for (FavrBean fbInfo : PropUtil.getFavrInfo()) {
			if (fbInfo == null || !fbInfo.useFlg) {
				continue;
			}
			JMenuItem jMenuItem22 = new JMenuItem();
			jMenu2.add(jMenuItem22);
			jMenuItem22.setName("favr:" + Integer.toString(fbInfo.favrId));
			jMenuItem22.setText(fbInfo.name);
			jMenuItem22.setToolTipText(StringUtil.printTipText(fbInfo.url, "user :=  " + StringUtils.trimToEmpty(fbInfo.user)));
			jMenuItem22.addActionListener(connAction);
		}

		// Database menu ==========================================================================
		JMenu jMenu3 = new JMenu("   Database   ");
		jMenu3.setMnemonic(KeyEvent.VK_B);
		jMenuBar1.add(jMenu3);

		JMenuItem jMenuItem31 = new JMenuItem("New Db Driver");
		jMenu3.add(jMenuItem31);
		jMenuItem31.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Drm01Dialog dbDialog = new Drm01Dialog();
				dbDialog.setVisible(true);
			}
		});
		JMenuItem jMenuItem33 = new JMenuItem("Driver Manage");
		jMenu3.add(jMenuItem33);
		jMenuItem33.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Drm02Dialog dbDialog = new Drm02Dialog();
				dbDialog.setVisible(true);
			}
		});
		// separate line
		jMenu3.add(new JSeparator());

		// 显示所有被支持的数据库
		// load database
		for (ConnBean cbInfo : PropUtil.getDbConnInfo()) {
			if (cbInfo == null) {
				continue;
			}
			JMenuItem jMenuItem32 = new JMenuItem();
			jMenu3.add(jMenuItem32);
			jMenuItem32.setName("conn:" + Integer.toString(cbInfo.driverid));
			jMenuItem32.setText(cbInfo.name);
			jMenuItem32.setToolTipText(StringUtil.printTipText(cbInfo.driver, cbInfo.description));
			jMenuItem32.addActionListener(connAction);
		}

		// Help menu ==============================================================================
		JMenu jMenu4 = new JMenu("    Help    ");
		jMenu4.setMnemonic(KeyEvent.VK_H);
		jMenuBar1.add(jMenu4);
		
		JMenuItem jMenuItem41 = new JMenuItem("encrypt tool");
		jMenu4.add(jMenuItem41);
		jMenuItem41.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 打开一个新的对话框，输入明文字符，加密后输出
				Sec01Dialog dbDialog = new Sec01Dialog();
				dbDialog.setVisible(true);
			}
		});
		// separate line
		jMenu4.add(new JSeparator());

		JMenuItem jMenuItem42 = new JMenuItem("Help Contents");
		jMenu4.add(jMenuItem42);
		jMenuItem42.setAccelerator(KeyStroke.getKeyStroke("F1"));
		jMenuItem42.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 使用系统默认浏览器打开帮助文档
				// 目前只支持windows系统，后续支持linux系统
				// 要加入os判断
				// System.getProperties().getProperty("os.name")  "os.arch" "os.version"
				// linux系统必须要有gnome环境，kde桌面暂未测试
				// gnome-open doc/help.html
				try {
					Desktop dp = Desktop.getDesktop();
					dp.open(new File("doc/help.html"));
					//Runtime.getRuntime().exec("cmd.exe /c start doc/help.html");
				} catch (IOException ioexp) {
					ioexp.printStackTrace();
				}
			}
		});
		// separate line
		jMenu4.add(new JSeparator());

		JMenuItem jMenuItem43 = new JMenuItem("About");
		jMenu4.add(jMenuItem43);
		jMenuItem43.setAccelerator(KeyStroke.getKeyStroke("F2"));
		jMenuItem43.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 使用系统默认浏览器打开说明文档
				try {
					Desktop dp = Desktop.getDesktop();
					dp.open(new File("doc/about.html"));
					//Runtime.getRuntime().exec("cmd.exe /c start doc/about.html");
				} catch (IOException ioexp) {
					ioexp.printStackTrace();
				}
			}
		});
	}

	/**
	 * 根节点(Database)的展开和关闭事件处理
	 *
	 */
	private final static class MyExpansionListener implements TreeExpansionListener {
		@Override
		public void treeExpanded(TreeExpansionEvent evt) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) evt.getPath().getLastPathComponent();
			if (node instanceof TableTypesGroupNode) {
				// 显示DB所属对象一览
				DbClient dbClient = Session.getDbClient();
				((BaseNode) node).expand(dbClient.getTableList(null, null, "%", new String[] { ((TableTypesGroupNode) node).getCatalogIdentifier() }));
			}
		}
		@Override
		public void treeCollapsed(TreeExpansionEvent evt) {
		}
	}
}
