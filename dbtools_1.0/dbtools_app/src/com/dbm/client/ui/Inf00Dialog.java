package com.dbm.client.ui;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;

import com.dbm.common.error.BaseExceptionWrapper;


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
 * 数据库信息<br><br>
 * [function]<br>
 * 显示数据库信息<br><br>
 * [history]<br>
 * 2013/05/10 ver1.0.0  JiangJusheng<br>
 */
public class Inf00Dialog extends javax.swing.JDialog {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7113020314733218145L;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JLabel jLabel5;
	private JLabel jLabel8;
	private JLabel jLabel7;
	private JLabel jLabel6;
	private JLabel jLabel1;
	private JLabel jLabel9;
	private JLabel jLabel10;

	public Inf00Dialog() {
		super();
		setLayout(null);

		// set location
		int width = 600;
		int height = 300;
		setSize(width, height);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - width) / 2, (dim.height - height) / 2);

		//pack();
		setModal(true);
		{
			jLabel1 = new JLabel("Database Name");
			getContentPane().add(jLabel1);

			jLabel1.setBounds(40, 25, 150, 25);
		}
		{
			jLabel2 = new JLabel();
			getContentPane().add(jLabel2);
			jLabel2.setBounds(220, 25, 300, 25);
		}
		{
			jLabel3 = new JLabel("Database Version");
			getContentPane().add(jLabel3);
			jLabel3.setBounds(40, 60, 150, 25);
		}
		{
			jLabel4 = new JLabel();
			getContentPane().add(jLabel4);
			jLabel4.setBounds(220, 60, 300, 25);
		}
		{
			jLabel5 = new JLabel("Jdbc Driver Name");
			getContentPane().add(jLabel5);
			jLabel5.setBounds(40, 95, 150, 25);
		}
		{
			jLabel6 = new JLabel();
			getContentPane().add(jLabel6);
			jLabel6.setBounds(220, 95, 300, 25);
		}
		{
			jLabel7 = new JLabel("Jdbc Driver Version");
			getContentPane().add(jLabel7);
			jLabel7.setBounds(40, 130, 150, 25);
		}
		{
			jLabel8 = new JLabel();
			getContentPane().add(jLabel8);
			jLabel8.setBounds(220, 130, 300, 25);
		}
		jLabel9 = new JLabel();
		getContentPane().add(jLabel9);
		jLabel9.setBounds(40, 160, 500, 25);
		jLabel10 = new JLabel();
		getContentPane().add(jLabel10);
		jLabel10.setBounds(40, 190, 500, 25);
	}

	public void setDbMetaInfo(DatabaseMetaData dmd) {
		try {
			if (dmd.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY)) {
				jLabel9.setText("该结果集 光标只能向前移动");
			} else if (dmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
				jLabel9.setText("该结果集 光标可滚动但通常受底层数据更改影响");
			} else if (dmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
				jLabel9.setText("该结果集 光标可滚动但通常不受底层数据更改影响");
			}
			if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
				jLabel10.setText("该结果集 可更新 光标只能向前移动");
			} else if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
				jLabel10.setText("该结果集 可更新 光标可滚动但通常受底层数据更改影响");
			} else if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
				jLabel10.setText("该结果集 可更新 光标可滚动但通常不受底层数据更改影响");
			} else if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
				jLabel10.setText("该结果集 不可更新 光标只能向前移动");
			} else if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
				jLabel10.setText("该结果集 不可更新 光标可滚动但通常受底层数据更改影响");
			} else if (dmd.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
				jLabel10.setText("该结果集 不可更新 光标可滚动但通常不受底层数据更改影响");
			}

			// 获取此数据库产品的名称
			jLabel2.setText(dmd.getDatabaseProductName());
			// 获取此数据库产品的版本号
			jLabel4.setText(dmd.getDatabaseProductVersion());
			// 获取此 JDBC 驱动程序的名称
			jLabel6.setText(dmd.getDriverName());
			// 获取此 JDBC 驱动程序的 String 形式的版本号
			jLabel8.setText(dmd.getDriverVersion());
		} catch (SQLException exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

}
