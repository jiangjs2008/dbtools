package jdbc.wrapper.mongo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import jdbc.wrapper.AbstractResultSet;

public class MongoResultSet extends AbstractResultSet {

	/**
	 * Meta data for result set or null.
	 */
	private MongoResultSetMetaData md;

	private int rowIdx = 0;
	private int rowCnt = 0;

//	private String[] header = null;
	private String[][] datas = null;

	/**
	 * 构造函数
	 */
	MongoResultSet(String[] header, String[][] datas) {
		rowIdx = -1;
		rowCnt = -1;
		if (datas != null) {
			rowCnt = datas.length;
		}

//		this.header = header;
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

	public int findColumn(String columnName) throws SQLException {
		return 0;
	}

	public int getRow() throws SQLException {
		return 0;
	}

	public boolean previous() throws SQLException {
		return false;
	}

	public boolean absolute(int row) throws SQLException {
		return true;
	}

	public boolean relative(int row) throws SQLException {

		return true;
	}

	public void setFetchDirection(int dir) throws SQLException {
		if (dir != ResultSet.FETCH_FORWARD) {
			throw new SQLException("only forward fetch direction supported");
		}
	}

	public int getFetchDirection() throws SQLException {
		return ResultSet.FETCH_FORWARD;
	}

	public void setFetchSize(int fsize) throws SQLException {
		if (fsize != 1) {
			throw new SQLException("fetch size must be 1");
		}
	}

	public int getFetchSize() throws SQLException {
		return 1;
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
			md = new MongoResultSetMetaData(null);
		}
		return md;
	}

	public Object getObject(int columnIndex) throws SQLException {
		return null;
	}

	public Object getObject(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return getObject(col);
	}


	public boolean isFirst() throws SQLException {
		return false;
	}

	public boolean isBeforeFirst() throws SQLException {
		return false;
	}

	public void beforeFirst() throws SQLException {
	}

	public boolean first() throws SQLException {
		return false;
	}

	public boolean isAfterLast() throws SQLException {
		return false;
	}

	public void afterLast() throws SQLException {

	}

	public boolean isLast() throws SQLException {
		return false;
	}

	public boolean last() throws SQLException {
		return false;
	}

	public int getType() throws SQLException {
		return TYPE_SCROLL_SENSITIVE;
	}

	public int getConcurrency() throws SQLException {
		return CONCUR_UPDATABLE;
	}

	public boolean rowUpdated() throws SQLException {
		return false;
	}

	public boolean rowInserted() throws SQLException {
		return false;
	}

	public boolean rowDeleted() throws SQLException {
		return false;
	}

	public void insertRow() throws SQLException {
	}

	public void updateRow() throws SQLException {
	}

	public void deleteRow() throws SQLException {
	}

	public void refreshRow() throws SQLException {
	}

	public void cancelRowUpdates() throws SQLException {
	}

	public void moveToInsertRow() throws SQLException {
	}

	public void moveToCurrentRow() throws SQLException {
	}


	public void updateInt(int colIndex, int b) throws SQLException {
	}

	public void updateString(int colIndex, String s) throws SQLException {
	}

	public void updateInt(String colName, int b) throws SQLException {
		int col = findColumn(colName);
		updateInt(col, b);
	}

	public void updateString(String colName, String s) throws SQLException {
		int col = findColumn(colName);
		updateString(col, s);
	}

	public void updateObject(String colName, Object obj) throws SQLException {
		int col = findColumn(colName);
		updateObject(col, obj);
	}

	public void updateObject(String colName, Object obj, int s) throws SQLException {
		int col = findColumn(colName);
		updateObject(col, obj, s);
	}

	public Statement getStatement() throws SQLException {
		return null;
	}

	public void close() throws SQLException {
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		return null;
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		return null;
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		return null;
	}


	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

}
