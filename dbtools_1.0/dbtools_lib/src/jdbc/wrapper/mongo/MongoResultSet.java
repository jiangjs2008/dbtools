package jdbc.wrapper.mongo;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;

import jdbc.wrapper.AbstractResultSet;

public class MongoResultSet extends AbstractResultSet {

	/**
	 * Meta data for result set or null.
	 */
	private MongoResultSetMetaData md;

	private int rowIdx = 0;
	private int rowCnt = 0;

	private String[] header = null;
	private String[][] datas = null;

	/**
	 * 构造函数
	 */
	public MongoResultSet(String[] header, String[][] datas) {
		rowIdx = -1;
		rowCnt = -1;
		if (datas != null) {
			rowCnt = datas.length;
		}

		this.header = header;
		this.datas = datas;
	}

	public boolean next() throws SQLException {
		if (rowIdx < rowCnt - 1) {
			rowIdx ++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		return ArrayUtils.indexOf(header, columnLabel) + 1;
	}

	public String getString(int columnIndex) throws SQLException {
		return datas[rowIdx][columnIndex - 1];
	}

	public String getString(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return getString(col);
	}

	public int getInt(int columnIndex) throws SQLException {

		return 0;
	}

	public int getInt(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return getInt(col);
	}


	public ResultSetMetaData getMetaData() throws SQLException {
		if (md == null) {
			md = new MongoResultSetMetaData(header);
		}
		return md;
	}

}
