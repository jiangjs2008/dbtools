package com.dbm.client.ui.menu;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.dbm.client.action.AbstractActionListener;
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
 * Fav01Dialog<br><br>
 * [function]<br>
 * 把当前连接加入到快捷方式<br><br>
 * [history]<br>
 * 2014/06/08 ver1.0 JiangJusheng<br>
 */
public class Fav01Dialog extends javax.swing.JDialog {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private JTextField jTextField3;
	private JTextField jTextField4;
	private JTextField jTextField2;
	private JTextField jTextField1;
	private JTextField jTextField5;

	/**
	 * 构造函数
	 */
	public Fav01Dialog() {
		super();
		setTitle("添加当前数据库连接到快捷方式");
		rootPane.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		this.setSize(590, 320);
		setLocationRelativeTo(null);
		setModal(true);

		JPanel jPanel1 = new JPanel();
		getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.setLayout(null);

		ButtonActionListener btnActListener = new ButtonActionListener();

		JButton jButton1 = new JButton();
		jPanel1.add(jButton1);
		jButton1.setText("取消");
		jButton1.setBounds(120, 235, 85, 30);
		jButton1.addActionListener(btnActListener);

		JButton jBtnConnect = new JButton();
		jPanel1.add(jBtnConnect);
		jBtnConnect.setText("确定");
		jBtnConnect.setBounds(320, 235, 85, 30);
		jBtnConnect.addActionListener(btnActListener);

		JLabel jLabel3 = new JLabel();
		jPanel1.add(jLabel3);
		jLabel3.setText("名称");
		jLabel3.setBounds(20, 20, 50, 25);

		JLabel jLabel4 = new JLabel();
		jPanel1.add(jLabel4);
		jLabel4.setText("描述");
		jLabel4.setBounds(20, 60, 50, 25);

		JLabel jLabel1 = new JLabel();
		jPanel1.add(jLabel1);
		jLabel1.setText("访问URL");
		jLabel1.setBounds(20, 100, 50, 25);

		JLabel jLabel2 = new JLabel();
		jPanel1.add(jLabel2);
		jLabel2.setText("用户名");
		jLabel2.setBounds(20, 140, 50, 25);

		JLabel jLabel5 = new JLabel();
		jPanel1.add(jLabel5);
		jLabel5.setText("密码");
		jLabel5.setBounds(20, 180, 50, 25);

		jTextField1 = new JTextField();
		jPanel1.add(jTextField1);
		jTextField1.setBounds(80, 20, 400, 25);

		jTextField2 = new JTextField();
		jPanel1.add(jTextField2);
		jTextField2.setBounds(80, 60, 480, 25);

		jTextField3 = new JTextField();
		jPanel1.add(jTextField3);
		jTextField3.setBounds(80, 100, 480, 25);

		jTextField4 = new JTextField();
		jPanel1.add(jTextField4);
		jTextField4.setBounds(80, 140, 200, 25);

		jTextField5 = new JTextField();
		jPanel1.add(jTextField5);
		jTextField5.setBounds(80, 180, 200, 25);
	}


	/**
	 * 按钮事件监听器
	 */
	private class ButtonActionListener extends AbstractActionListener {
		@Override
		protected void doActionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if ("确定".equals(command)) {
				// 确定
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

			} else {
				// 关闭对话框
				setVisible(false);
			}
		}
	}

}
