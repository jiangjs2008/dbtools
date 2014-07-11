package com.dbm.client.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.ui.tbldata.TableHeaderSelectedListener;
import com.dbm.common.util.SecuUtil;

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
 * Drm01Dialog<br><br>
 * [function]<br>
 * 新建jdbc驱动信息<br><br>
 * [history]<br>
 * 2014/06/08 ver1.0 JiangJusheng<br>
 */
public class Drm01Dialog extends javax.swing.JDialog {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private JTextField jTextField3;
	private JTextField jTextField4;
	private JTextField jTextField2;
	private JTextField jTextField1;
	private JTable jTable1;

	/**
	 * 构造函数
	 */
	public Drm01Dialog() {
		super();
		rootPane.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		JPanel jPanel1 = new JPanel();
		getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.setLayout(null);
		jPanel1.setPreferredSize(new java.awt.Dimension(500, 200));

		this.setSize(579, 315);
		setModal(true);

		// set location
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - 500) / 2, (dim.height - 200) / 2);

		ButtonActionListener btnActListener = new ButtonActionListener();

		JButton jButton1 = new JButton();
		jPanel1.add(jButton1);
		jButton1.setText("Cancel");
		jButton1.setBounds(55, 110, 85, 30);
		jButton1.addActionListener(btnActListener);

		JButton jBtnConnect = new JButton();
		jPanel1.add(jBtnConnect);
		jBtnConnect.setText("Encrypt");
		jBtnConnect.setBounds(200, 110, 85, 30);
		jBtnConnect.addActionListener(btnActListener);

		JLabel jLabel3 = new JLabel();
		jPanel1.add(jLabel3);
		jLabel3.setText("User Name");
		jLabel3.setBounds(20, 20, 90, 25);

		JLabel jLabel4 = new JLabel();
		jPanel1.add(jLabel4);
		jLabel4.setText("Password");
		jLabel4.setBounds(20, 60, 90, 25);

		jTextField3 = new JTextField();
		jPanel1.add(jTextField3);
		jTextField3.setBounds(120, 20, 150, 25);

		jTextField4 = new JTextField();
		jPanel1.add(jTextField4);
		jTextField4.setBounds(120, 60, 150, 25);

		jTextField2 = new JTextField();
		jPanel1.add(jTextField2);
		jTextField2.setBounds(320, 60, 150, 25);

		jTextField1 = new JTextField();
		jPanel1.add(jTextField1);
		jTextField1.setBounds(320, 20, 150, 25);
		{
			TableModel jTable1Model = 
				new DefaultTableModel(
						new String[][] { { "1", } },
						new String[] { "NO.", "Column 1", "Column 2", "Column 3", "Column 4" });
			jTable1 = new JTable();
			jPanel1.add(jTable1);
			jTable1.setModel(jTable1Model);
			jTable1.getTableHeader().setReorderingAllowed(false);
			jTable1.getTableHeader().addMouseListener(new TableHeaderSelectedListener());
			jTable1.setBounds(296, 108, 174, 78);
		}
	}


	/**
	 * 按钮事件监听器
	 */
	private class ButtonActionListener extends AbstractActionListener {
		@Override
		protected void doActionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if ("Encrypt".equals(command)) {
				// 加密
				String name1 = jTextField3.getText();
				if (name1 != null && name1.length() > 0) {
					name1 = name1.trim();
					String name2 = SecuUtil.encryptBASE64(name1);
					jTextField1.setText(name2);
				}

				String passwd1 = jTextField4.getText();
				if (passwd1 != null && passwd1.length() > 0) {
					passwd1 = passwd1.trim();
					String passwd2 = SecuUtil.encryptBASE64(passwd1);
					jTextField2.setText(passwd2);
				}

			} else if ("Decrypt".equals(command)) {
				// 解密
				String name1 = jTextField3.getText();
				if (name1 != null && name1.length() > 0) {
					name1 = name1.trim();
					String name2 = SecuUtil.decryptBASE64(name1);
					jTextField1.setText(name2);
				}

				String passwd1 = jTextField4.getText();
				if (passwd1 != null && passwd1.length() > 0) {
					passwd1 = passwd1.trim();
					String passwd2 = SecuUtil.decryptBASE64(passwd1);
					jTextField2.setText(passwd2);
				}

			} else {
				// 关闭对话框
				setVisible(false);
			}
		}
	}

}
