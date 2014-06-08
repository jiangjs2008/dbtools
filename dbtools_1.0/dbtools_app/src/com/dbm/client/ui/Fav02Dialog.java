package com.dbm.client.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

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
 * Fav02Dialog<br><br>
 * [function]<br>
 * 管理连接快捷方式<br><br>
 * [history]<br>
 * 2014/06/08 ver1.0 JiangJusheng<br>
 */
public class Fav02Dialog extends javax.swing.JDialog {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private JTable jTable1;
	private JLabel jLabel1;

	/**
	 * 构造函数
	 */
	public Fav02Dialog() {
		super();
		JPanel jPanel1 = new JPanel();
		getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel1.setLayout(null);
		{
			jLabel1 = new JLabel();
			jPanel1.add(jLabel1);
			jLabel1.setText("\u5feb\u6377\u65b9\u5f0f\u4e00\u89c8");
			jLabel1.setBounds(25, 15, 120, 25);
		}
		{

			jTable1 = new JTable();
			jPanel1.add(jTable1);
			jTable1.setBounds(25, 45, 100, 100);
			
			FavrBean[] favList = PropUtil.getFavrInfo();
			int leng = favList.length;
			String[][] favData = new String[leng][7];
			for (int i = 0; i < leng; i++) {
				FavrBean favItem = favList[i];
				if (favItem == null) {
					continue;
				}
				favData[i][0] = Integer.toString(i + 1);
				favData[i][1] = Integer.toString(favItem.driverId);
				favData[i][2] = favItem.name;
				favData[i][3] = favItem.description;
				favData[i][4] = favItem.url;
				favData[i][5] = favItem.user;
				favData[i][6] = favItem.password;
				//favData[i][7] = favItem.
			}

			TableModel jTable1Model =
				new DefaultTableModel(
						favData,
						new String[] { "NO.", "driverid", "name", "description", "url", "user", "password" });
			
			jTable1.setModel(jTable1Model);
		}

		setSize(800, 600);
		setModal(true);

		// set location
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - 500) / 2, (dim.height - 200) / 2);

	}


}
