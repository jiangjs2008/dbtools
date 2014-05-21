package com.dbm.client.ui;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.dbm.client.util.TableUtil;

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
 * 表字段信息<br><br>
 * [function]<br>
 * 显示表字段信息<br><br>
 * [history]<br>
 * 2013/05/10 ver1.0 JiangJusheng<br>
 */
public class Inf01Dialog extends javax.swing.JDialog {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7113020314733218145L;
	private JScrollPane jScrollPane1;
	private JTable jTable2;

	private static String[] tblHeader = new String[] { "NO.", "Name", "Type", "Size", "PK", "Nullable", "Remarks" };


	public Inf01Dialog() {
		super();
		setLayout(new BorderLayout());

		// set location
		int width = 700;
		int height = 400;
		setSize(width, height);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - width) / 2, (dim.height - height) / 2);

		jTable2 = new JTable();
		jTable2.setModel(Session.EmptyTableModel);
		jTable2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jTable2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jTable2.setCellSelectionEnabled(true);
		//jTable2.setPreferredSize(new java.awt.Dimension(700, 134));

		jScrollPane1 = new JScrollPane(jTable2);
		add(jScrollPane1, BorderLayout.CENTER);

		pack();
		setModal(true);
	}

	public void setColumnInfo(Vector<Vector<String>> allData) {
		Vector<String> columnInfo = new Vector<String>(7);
		for (int i = 0; i < tblHeader.length; i++) {
			columnInfo.add(tblHeader[i]);
		}

		TableModel tableModel = new DefaultTableModel(allData, columnInfo);
		jTable2.setModel(tableModel);
		TableUtil.fitTableColumns2(jTable2);
	}
}
