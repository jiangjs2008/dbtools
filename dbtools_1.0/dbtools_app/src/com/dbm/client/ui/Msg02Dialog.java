package com.dbm.client.ui;

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
 * 动作确认对话框<br><br>
 * [function]<br>
 * 提示用户继续还是放弃操作<br><br>
 * [history]<br>
 * 2013/05/10 ver1.0 JiangJusheng<br>
 */
public class Msg02Dialog extends javax.swing.JDialog {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7113020314733218145L;

	private static Msg02Dialog inst = new Msg02Dialog();
	private boolean rsltSts = false;
	private JLabel jTextArea1 = null;


	public static Msg02Dialog showMsgDialog(int titleId, int msgId) {
		inst.setTitle(LoggerWrapper.getMessage(titleId));
		inst.setMessage(LoggerWrapper.getMessage(msgId));
		inst.setVisible(true);
		return inst;
	}

	private void setMessage(String msgs) {
		jTextArea1.setText(StringUtil.printTipText(msgs));
	}

	private Msg02Dialog() {
		super((Frame) null);
		setLayout(null);

		jTextArea1 = new JLabel();
		add(jTextArea1);
		jTextArea1.setHorizontalAlignment(SwingConstants.CENTER);
		jTextArea1.setBounds(40, 30, 320, 70);

		JButton jButton1 = new JButton();
		add(jButton1);
		jButton1.setText("OK");
		jButton1.setBounds(240, 125, 75, 25);
		jButton1.addActionListener(new AbstractActionListener() {
			@Override
			protected void doActionPerformed(ActionEvent e) {
				rsltSts = true;
				setVisible(false);
			}
		});

		JButton jButton2 = new JButton();
		add(jButton2);
		jButton2.setText("Cancel");
		jButton2.setBounds(80, 125, 75, 25);
		jButton2.addActionListener(new AbstractActionListener() {
			@Override
			protected void doActionPerformed(ActionEvent e) {
				rsltSts = false;
				setVisible(false);
			}
		});

		setSize(400, 200);
		setModal(true);
		// set location
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - 400) / 2, (dim.height - 200) / 2);
	}

	public boolean isOK() {
		return rsltSts;
	}

}
