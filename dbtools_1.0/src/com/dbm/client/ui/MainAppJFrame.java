/**
 * Copyright (C) 2013  JiangJusheng
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.dbm.client.action.FavrMenuActionListener;
import com.dbm.client.action.MyActionListener;
import com.dbm.client.action.data.CloseActionListener;
import com.dbm.client.action.data.DelActionListener;
import com.dbm.client.action.data.ExecActionListener;
import com.dbm.client.action.data.PageJumpActionListener;
import com.dbm.client.action.data.UpdActionListener;
import com.dbm.client.db.DbClientFactory;
import com.dbm.client.error.ExceptionHandler;
import com.dbm.client.property.ConnBean;
import com.dbm.client.property.FavrBean;
import com.dbm.client.property.PropUtil;
import com.dbm.client.ui.tbldata.TableDataSelectedListener;
import com.dbm.client.ui.tbldata.TableHeaderSelectedListener;
import com.dbm.client.ui.tbllist.ObjectsTreeModel;
import com.dbm.client.ui.tbllist.TableTreeClickListener;
import com.dbm.client.util.StringUtil;

/**
 * [name]<br>
 * main frame class<br><br>
 * [function]<br>
 * frame initial, add each component's event action listener<br>
 * menubar initial<br><br>
 * [history]<br>
 * 2013/05/10 ver1.0.0  JiangJusheng<br>
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

	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainAppJFrame inst = new MainAppJFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	/**
	 * default constructor
	 */
	public MainAppJFrame() {
		super();
		PropUtil.load();

		initGui();
		initMenuBar();
		pack();
		setSize(1024, 768);

		AppUIAdapter.setUIObj(AppUIAdapter.AppMainGUI, this);
		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance());
	}

	/**
	 * 
	 * handler window event
	 */
	@Override
	protected void processWindowEvent(final WindowEvent pEvent) {
		if (pEvent.getID() == WindowEvent.WINDOW_CLOSING) {
			// 结束程序
			DbClientFactory.close();
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
		jTree1.addMouseListener(new TableTreeClickListener());
		AppUIAdapter.setUIObj(AppUIAdapter.TableTreeUIObj, jTree1);

		JScrollPane jscroll = new JScrollPane(jTree1);
		jRightPanel.add(jscroll, BorderLayout.CENTER);

		// table data =========================================================================
		jTable1 = new JTable();
		jLeftPanel.add(new JScrollPane(jTable1));
		AppUIAdapter.setUIObj(AppUIAdapter.TableDataUIObj, jTable1);

		jTable1.setModel(Session.EmptyTableModel);
		jTable1.getTableHeader().setReorderingAllowed(false);

		jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jTable1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jTable1.setCellSelectionEnabled(true);
		jTable1.addMouseListener(new TableDataSelectedListener());
		jTable1.getTableHeader().addMouseListener(new TableHeaderSelectedListener());

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
		JButton jBtnExecute = new JButton();
		jBtnExecute.setText("Execute");
		jBtnExecute.setBounds(0, 9, 80, 30);

		ExecActionListener execAction = new ExecActionListener(jTextArea1);
//		KeyStroke execKey = KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK, false);
//		jBtnExecute.registerKeyboardAction(execAction, "Execute", execKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
		jBtnExecute.setMnemonic(KeyEvent.VK_E);

		jBtnExecute.addActionListener(execAction);
		jBtnPanel.add(jBtnExecute);

		// [更新]按钮
		JButton jBtnUpdate = new JButton();
		jBtnUpdate.setText("Update");
		jBtnUpdate.setBounds(100, 9, 80, 30);

		ActionListener updAction = UpdActionListener.getInstance();
//		KeyStroke updKey = KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK, false);
//		jBtnUpdate.registerKeyboardAction(updAction, "Execute", updKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
		jBtnUpdate.setMnemonic(KeyEvent.VK_U);

		jBtnUpdate.addActionListener(updAction);
		jBtnPanel.add(jBtnUpdate);
		AppUIAdapter.setUIObj(AppUIAdapter.BTN_UPDATE, jBtnUpdate);

		// [删除]按钮
		JButton jBtnDelete = new JButton();
		jBtnDelete.setText("Delete");
		jBtnDelete.setBounds(200, 9, 80, 30);

		ActionListener delAction = DelActionListener.getInstance();
//		KeyStroke delKey = KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK, false);
//		jBtnDelete.registerKeyboardAction(delAction, "Execute", delKey, JComponent.WHEN_IN_FOCUSED_WINDOW);
		jBtnDelete.setMnemonic(KeyEvent.VK_D);

		jBtnDelete.addActionListener(delAction);
		jBtnPanel.add(jBtnDelete);
		AppUIAdapter.setUIObj(AppUIAdapter.BTN_DELETE, jBtnDelete);

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

		ActionListener closeAction = new CloseActionListener();
		// TODO-- *注: 此处组合键只能使用Alt+XXX的形式，不能使用Ctrl键，具体原因不明，待调查
		//KeyStroke closeKey = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK, false);
		//jMenuItem3.registerKeyboardAction(closeAction, "Close", closeKey, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		jMenuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.ALT_MASK));
		jMenuItem3.addActionListener(closeAction);

		jMenu1.add(new JSeparator());

		JMenuItem jMenuItem4 = new JMenuItem("Exit");
		jMenu1.add(jMenuItem4);
		jMenuItem4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK));
		jMenuItem4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 结束程序
				DbClientFactory.close();
				setVisible(false);
				System.exit(0);
			}
		});

		// Edit menu =========================================================================
		JMenu jMenu12 = new JMenu("    Edit    ");
		jMenuBar1.add(jMenu12);

		JMenuItem jMenuItem121 = new JMenuItem("Cut");
		jMenu12.add(jMenuItem121);
		JMenuItem jMenuItem122 = new JMenuItem("Copy");
		jMenu12.add(jMenuItem122);
		JMenuItem jMenuItem123 = new JMenuItem("Paste");
		jMenu12.add(jMenuItem123);

		// separate line
		jMenu12.add(new JSeparator());

		JMenuItem jMenuItem124 = new JMenuItem("Last Script");
		jMenu12.add(jMenuItem124);
		JMenuItem jMenuItem125 = new JMenuItem("Next Script");
		jMenu12.add(jMenuItem125);


		// Favorites menu =========================================================================
		JMenu jMenu2 = new JMenu("   Favorites   ");
		jMenu2.setMnemonic(KeyEvent.VK_V);
		jMenuBar1.add(jMenu2);

		JMenuItem jMenuItem23 = new JMenuItem("Add to Favorites");
		jMenu2.add(jMenuItem23);
		jMenuItem23.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		JMenuItem jMenuItem21 = new JMenuItem("Favorites Manage");
		jMenu2.add(jMenuItem21);
		jMenuItem21.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		// separate line
		jMenu2.add(new JSeparator());

		// 显示最近使用数据库一览
		// load favorite database
		for (FavrBean fbInfo : PropUtil.getFavrInfo()) {
			JMenuItem jMenuItem22 = new JMenuItem();
			jMenu2.add(jMenuItem22);
			jMenuItem22.setText(fbInfo.name);
			jMenuItem22.setToolTipText(StringUtil.printTipText(fbInfo.url, "user :=  " + StringUtil.NVL(fbInfo.user)));
			jMenuItem22.addActionListener(new FavrMenuActionListener(fbInfo, null));
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
				// TODO Auto-generated method stub
			}
		});
		// separate line
		jMenu3.add(new JSeparator());

		// 显示所有被支持的数据库
		// load database
		for (ConnBean cbInfo : PropUtil.getDbConnInfo()) {
			JMenuItem jMenuItem32 = new JMenuItem();
			jMenu3.add(jMenuItem32);
			jMenuItem32.setText(cbInfo.dbType);
			jMenuItem32.setToolTipText(StringUtil.printTipText(cbInfo.driver, cbInfo.description));
			jMenuItem32.addActionListener(new FavrMenuActionListener(null, cbInfo));
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

}