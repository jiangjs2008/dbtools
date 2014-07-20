package jdbc.wrapper.sqlite.remote;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import jdbc.wrapper.AbstractResultSet;

public class SQLiteResultSet extends AbstractResultSet {

	/**
	 * Meta data for result set or null.
	 */
	private SQLiteResultSetMetaData md;

	private int rowIdx = 0;
	private int rowCnt = 0;

//	private String[] header = null;
	private String[][] datas = null;

	/**
	 * 构造函数
	 */
	SQLiteResultSet(String[] header, String[][] datas) {
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

	public boolean getBoolean(int columnIndex) throws SQLException {
		return getInt(columnIndex) == 1 || Boolean.parseBoolean(getString(columnIndex));
	}

	public boolean getBoolean(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return getBoolean(col);
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		if (md == null) {
			md = new SQLiteResultSetMetaData(this);
		}
		return md;
	}

	public short getShort(int columnIndex) throws SQLException {

		return 0;
	}

	public short getShort(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return getShort(col);
	}

	public java.sql.Time getTime(int columnIndex) throws SQLException {
		return null;
	}

	public java.sql.Time getTime(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return getTime(col);
	}

	public java.sql.Time getTime(int columnIndex, java.util.Calendar cal) throws SQLException {
		return null;
	}

	public java.sql.Time getTime(String columnName, java.util.Calendar cal) throws SQLException {
		int col = findColumn(columnName);
		return getTime(col, cal);
	}

	public java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException {
		return null;
	}

	public java.sql.Timestamp getTimestamp(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return getTimestamp(col);
	}

	public java.sql.Timestamp getTimestamp(int columnIndex, java.util.Calendar cal) throws SQLException {
		return null;
	}

	public java.sql.Timestamp getTimestamp(String columnName, java.util.Calendar cal) throws SQLException {
		int col = findColumn(columnName);
		return getTimestamp(col, cal);
	}

	public java.sql.Date getDate(int columnIndex) throws SQLException {
		return null;
	}

	public java.sql.Date getDate(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return getDate(col);
	}

	public java.sql.Date getDate(int columnIndex, java.util.Calendar cal) throws SQLException {
		return null;
	}

	public java.sql.Date getDate(String columnName, java.util.Calendar cal) throws SQLException {
		int col = findColumn(columnName);
		return getDate(col, cal);
	}

	public double getDouble(int columnIndex) throws SQLException {

		return 0;
	}

	public double getDouble(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return getDouble(col);
	}

	public float getFloat(int columnIndex) throws SQLException {

		return 0;
	}

	public float getFloat(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return getFloat(col);
	}

	public long getLong(int columnIndex) throws SQLException {

		return 0;
	}

	public long getLong(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return getLong(col);
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

	public void updateNull(int colIndex) throws SQLException {
	}

	public void updateBoolean(int colIndex, boolean b) throws SQLException {
		updateString(colIndex, b ? "1" : "0");
	}

	public void updateByte(int colIndex, byte b) throws SQLException {
		throw new SQLException("not supported");
	}

	public void updateShort(int colIndex, short b) throws SQLException {
	}

	public void updateInt(int colIndex, int b) throws SQLException {
	}

	public void updateLong(int colIndex, long b) throws SQLException {
	}

	public void updateFloat(int colIndex, float f) throws SQLException {
	}

	public void updateDouble(int colIndex, double f) throws SQLException {
	}

	public void updateBigDecimal(int colIndex, BigDecimal f) throws SQLException {
		throw new SQLException("not supported");
	}

	public void updateString(int colIndex, String s) throws SQLException {
	}

	public void updateBytes(int colIndex, byte[] s) throws SQLException {
	}

	public void updateDate(int colIndex, java.sql.Date d) throws SQLException {
	}

	public void updateTime(int colIndex, java.sql.Time t) throws SQLException {
	}

	public void updateTimestamp(int colIndex, java.sql.Timestamp t) throws SQLException {
	}

	public void updateAsciiStream(int colIndex, java.io.InputStream in, int s) throws SQLException {
		throw new SQLException("not supported");
	}

	public void updateBinaryStream(int colIndex, java.io.InputStream in, int s) throws SQLException {
		throw new SQLException("not supported");
	}

	public void updateCharacterStream(int colIndex, java.io.Reader in, int s) throws SQLException {
		throw new SQLException("not supported");
	}

	public void updateObject(int colIndex, Object obj) throws SQLException {
		updateString(colIndex, obj.toString());
	}

	public void updateObject(int colIndex, Object obj, int s) throws SQLException {
		updateString(colIndex, obj.toString());
	}

	public void updateNull(String colName) throws SQLException {
		int col = findColumn(colName);
		updateNull(col);
	}

	public void updateBoolean(String colName, boolean b) throws SQLException {
		int col = findColumn(colName);
		updateBoolean(col, b);
	}

	public void updateByte(String colName, byte b) throws SQLException {
		throw new SQLException("not supported");
	}

	public void updateShort(String colName, short b) throws SQLException {
		int col = findColumn(colName);
		updateShort(col, b);
	}

	public void updateInt(String colName, int b) throws SQLException {
		int col = findColumn(colName);
		updateInt(col, b);
	}

	public void updateLong(String colName, long b) throws SQLException {
		int col = findColumn(colName);
		updateLong(col, b);
	}

	public void updateFloat(String colName, float f) throws SQLException {
		int col = findColumn(colName);
		updateFloat(col, f);
	}

	public void updateDouble(String colName, double f) throws SQLException {
		int col = findColumn(colName);
		updateDouble(col, f);
	}

	public void updateBigDecimal(String colName, BigDecimal f) throws SQLException {
		throw new SQLException("not supported");
	}

	public void updateString(String colName, String s) throws SQLException {
		int col = findColumn(colName);
		updateString(col, s);
	}

	public void updateBytes(String colName, byte[] s) throws SQLException {
		int col = findColumn(colName);
		updateBytes(col, s);
	}

	public void updateDate(String colName, java.sql.Date d) throws SQLException {
		int col = findColumn(colName);
		updateDate(col, d);
	}

	public void updateTime(String colName, java.sql.Time t) throws SQLException {
		int col = findColumn(colName);
		updateTime(col, t);
	}

	public void updateTimestamp(String colName, java.sql.Timestamp t) throws SQLException {
		int col = findColumn(colName);
		updateTimestamp(col, t);
	}

	public void updateAsciiStream(String colName, java.io.InputStream in, int s) throws SQLException {
		throw new SQLException("not supported");
	}

	public void updateBinaryStream(String colName, java.io.InputStream in, int s) throws SQLException {
		throw new SQLException("not supported");
	}

	public void updateCharacterStream(String colName, java.io.Reader in, int s) throws SQLException {
		throw new SQLException("not supported");
	}

	public void updateObject(String colName, Object obj) throws SQLException {
		int col = findColumn(colName);
		updateObject(col, obj);
	}

	public void updateObject(String colName, Object obj, int s) throws SQLException {
		int col = findColumn(colName);
		updateObject(col, obj, s);
	}

}
