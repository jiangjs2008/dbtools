package com.dbm.client.ui.menu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.dbm.client.ui.Session;
import com.dbm.client.util.TableUtil;
import com.dbm.common.db.DbClient4SqliteImpl;

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
 * His01Dialog<br><br>
 * [function]<br>
 * 数据操作历史纪录<br><br>
 * [history]<br>
 * 2013/05/10 ver1.0 JiangJusheng<br>
 */
public class His01Dialog extends javax.swing.JDialog {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7113020314733218145L;
	private JScrollPane jScrollPane1;
	private JTable jTable2;

	private static Vector<String> tblHeader = new Vector<String>(Arrays.asList("NO.", "Time", "Text" ));

	public His01Dialog() {
		super();
		setTitle("数据操作历史纪录");
		setLayout(new BorderLayout());

		// set location
		int width = 850;
		int height = 320;
		setPreferredSize(new java.awt.Dimension(width, height));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - width) / 2, (dim.height - height) / 2);

		jTable2 = new JTable();
		jTable2.setModel(Session.EmptyTableModel);
		jTable2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jTable2.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jTable2.setCellSelectionEnabled(true);

		jScrollPane1 = new JScrollPane(jTable2);
		add(jScrollPane1, BorderLayout.CENTER);

		// 取得操作历史纪录
		DbClient4SqliteImpl impl = new DbClient4SqliteImpl();
		String[] args = new String[4];
		args[0] = "org.sqlite.JDBC";
		args[1] = "jdbc:sqlite:build/settings.db";
		args[2] = "";
		args[3] = "";
		impl.start(args);
		ResultSet rs = impl.directQuery("history", 1);
		
		
		pack();
		setModal(true);
	}

	public void setColumnInfo(Vector<Vector<String>> allData) {
		TableModel tableModel = new DefaultTableModel(allData, tblHeader);
		jTable2.setModel(tableModel);
		TableUtil.fitTableColumns(jTable2, null);
	}
}
