package com.dbm.client.action.data;

import java.awt.event.ActionEvent;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.sql.rowset.CachedRowSet;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.dbm.client.action.AbstractActionListener;
import com.dbm.client.property.PropUtil;
import com.dbm.client.ui.AppUIAdapter;
import com.dbm.client.ui.tbldata.MyDefaultTableModel;
import com.dbm.client.util.StringUtil;
import com.dbm.client.util.TableUtil;

/**
 * 数据多页显示时的翻页控制
 */
public class PageJumpActionListener extends AbstractActionListener {

	private static int dataLimit = StringUtil.parseInt(PropUtil.getAppConfig("page.data.count"));

	private JPanel pagejumpPanel = null;
	private JTextField pageInfoTxtField = null;
	private JTextField inputPageTxtField = null;
	// 当前页数
	private int currPage = 0;
	// 总页数
	private int pageCnt = 0;
	private CachedRowSet rowSet = null;

	public PageJumpActionListener(JPanel pagePanel, JTextField pgInfoTxt, JTextField iptPgText) {
		this.pagejumpPanel = pagePanel;
		this.pageInfoTxtField = pgInfoTxt;
		this.inputPageTxtField = iptPgText;
	}

	public void setTableData(CachedRowSet rs, boolean hasRowId) {
		this.rowSet = rs;
		if (rs == null) {
			return;
		}

		// 表示件数
		int itemSize = 0;

		// 显示页数
		pageCnt = rs.getPageSize();

		if (pageCnt > 1) {
			// 分页处理
			itemSize = dataLimit;
			// 显示分页相关组件
			pagejumpPanel.setVisible(true);
			// 设置页数
			pageInfoTxtField.setText(1 + "/" + pageCnt);

		} else {
			// 小于一页时，若分页相关组件已在显示则要关闭
			itemSize = rs.size();
			pagejumpPanel.setVisible(false);
		}

		Vector<String> colName = null;
		Vector<Vector<String>> allData = null;
		try {
			// set table column name
			ResultSetMetaData rsm = rs.getMetaData();
			int colCount = rsm.getColumnCount();

			colName = new Vector<String>(colCount + 1);
			colName.add("NO.");
			for (int k = 1; k <= colCount; k ++) {
				colName.add(rsm.getColumnName(k));
			}

			// 如果分页，开始只显示第一页的数据
			allData = new Vector<Vector<String>>(itemSize);
			// set table data
			for (int i = 1; i <= itemSize && rs.next(); i ++) {
				Vector<String> colValue = new Vector<String>(colCount + 1);
				colValue.add(Integer.toString(i));
				for (int k = 1; k <= colCount; k ++) {
					colValue.add(rs.getString(k));
				}
				allData.add(colValue);
			}
		} catch (SQLException exp) {
			exp.printStackTrace();
		}

		JTable jTable1 = (JTable) AppUIAdapter.getUIObj(AppUIAdapter.TableDataUIObj);
		if (jTable1 == null) {
			return;
		}
		MyDefaultTableModel jTable1Model = new MyDefaultTableModel();
		jTable1Model.setDataVector(allData, colName);
		jTable1Model.addRow(new String[]{});
		jTable1.setModel(jTable1Model);
		TableUtil.fitTableColumns(jTable1);
	}

	@Override
	protected void doActionPerformed(ActionEvent e) {
		String actionName = ((JButton) e.getSource()).getActionCommand();
		if ("first".equals(actionName)) {
			if (currPage == 1) {
				return;
			}
			
		} else if ("backward".equals(actionName)) {
			
			
		} else if ("forward".equals(actionName)) {
			
			
		} else if ("last".equals(actionName)) {
			if (currPage == pageCnt) {
				return;
			}
			
		} else if ("go".equals(actionName)) {
			// 查看指定页
			if (inputPageTxtField == null) {
				return;
			}
			// 指定页数
			int inputPage = StringUtil.parseInt(inputPageTxtField.getText());
			if (inputPage == 0) {
				return;
			}
			
			
			
		} else {
			
		}
	}

}

