package jdbc.wrapper.sqlite;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import jdbc.wrapper.AbstractResultSetMetaData;


public class SQLiteResultSetMetaData extends AbstractResultSetMetaData {

	private int colCnt = 0;
	private ArrayList<String> colNameList = null;

	/**
	 * 构造函数
	 */
	SQLiteResultSetMetaData(ResultSetMetaData rsm) {
		try {
			colCnt = rsm.getColumnCount();
			colNameList = new ArrayList<String>(colCnt);
			for (int i = 1; i <= colCnt; i ++) {
				colNameList.add(rsm.getColumnName(i));
			}
		} catch (SQLException sqlexp) {
			sqlexp.printStackTrace();
		}
	}

	@Override
	public int getColumnCount() {
		return colCnt - 1;
	}

	@Override
	public String getColumnLabel(int column) {
		return getColumnName(column);
	}

	@Override
	/**
	 * 取得指定列的名称<br>
	 * 第一列("_id")的索引为0, 实际应用中索引从1开始
	 */
	public String getColumnName(int column) {
		return colNameList.get(column);
	}


}
