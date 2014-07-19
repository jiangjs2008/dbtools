package com.dbm.client.ui.help;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.common.log.LoggerWrapper;
import com.dbm.common.util.StringUtil;


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
 * 错误信息提示对话框<br><br>
 * [function]<br>
 * 显示错误信息<br><br>
 * [history]<br>
 * 2013/05/10 ver1.0 JiangJusheng<br>
 */
public class Msg01Dialog extends javax.swing.JDialog {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7113020314733218145L;

	private static Msg01Dialog inst = new Msg01Dialog();
	private JLabel jTextArea1;

	/**
	* Auto-generated main method to display this JDialog
	*/
	public static void showMsgDialog(int msgId) {
		String msgs = LoggerWrapper.getMessage(msgId);
		inst.setMessage(msgs);
		inst.setVisible(true);
	}

	private void setMessage(String msgs) {
		jTextArea1.setText(StringUtil.printTipText(msgs));
	}

	private Msg01Dialog() {
		super((Frame) null);
		setLayout(null);

		jTextArea1 = new JLabel();
		add(jTextArea1);
		jTextArea1.setHorizontalAlignment(SwingConstants.CENTER);
		jTextArea1.setBounds(50, 50, 300, 80);

		JButton jButton1 = new JButton();
		add(jButton1);
		jButton1.setText("OK");
		jButton1.setBounds(170, 170, 70, 25);
		jButton1.addActionListener(new AbstractActionListener() {
			@Override
			protected void doActionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		setSize(400, 250);
		setModal(true);
		// set location
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - 400) / 2, (dim.height - 250) / 2);
	}

}
