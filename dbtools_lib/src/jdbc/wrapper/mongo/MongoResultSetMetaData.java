package jdbc.wrapper.mongo;

import java.util.ArrayList;

import jdbc.wrapper.AbstractResultSetMetaData;

public class MongoResultSetMetaData extends AbstractResultSetMetaData {

	private ArrayList<Object> colNameList = null;

	/**
	 * 缺省构造函数
	 *
	 * @param cursor
	 */
	MongoResultSetMetaData(ArrayList<Object> colNameList) {
		this.colNameList = colNameList;
	}

	/**
	 * 取得该表的列的个数
	 */
	public int getColumnCount() throws java.sql.SQLException {
		if (colNameList == null) {
			return 0;
		} else {
			return colNameList.size() - 1;
		}
	}

	public String getColumnLabel(int column) throws java.sql.SQLException {
		return getColumnName(column);
	}

	/**
	 * 取得指定列的名称<br>
	 * 第一列的索引为0
	 */
	public String getColumnName(int column) throws java.sql.SQLException {
		if (colNameList == null) {
			return "";
		} else {
			return (String) colNameList.get(column);
		}
	}

}
