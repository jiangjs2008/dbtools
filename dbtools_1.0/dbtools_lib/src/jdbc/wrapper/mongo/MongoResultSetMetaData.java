package jdbc.wrapper.mongo;

import jdbc.wrapper.AbstractResultSetMetaData;

public class MongoResultSetMetaData extends AbstractResultSetMetaData {

	private String[] colNameList = null;

	/**
	 * 缺省构造函数
	 *
	 * @param cursor
	 */
	MongoResultSetMetaData(String[] colNameList) {
		this.colNameList = colNameList;
	}

	/**
	 * 取得该表的列的个数<br>
	 * 缺省主键("_id")不计入输出范围
	 */
	public int getColumnCount() throws java.sql.SQLException {
		if (colNameList == null) {
			return 0;
		} else {
			return colNameList.length - 1;
		}
	}

	public String getColumnLabel(int column) throws java.sql.SQLException {
		return getColumnName(column);
	}

	/**
	 * 取得指定列的名称<br>
	 * 第一列("_id")的索引为0, 实际应用中索引从1开始
	 */
	public String getColumnName(int column) throws java.sql.SQLException {
		if (colNameList == null) {
			return "";
		} else {
			return colNameList[column];
		}
	}

}
