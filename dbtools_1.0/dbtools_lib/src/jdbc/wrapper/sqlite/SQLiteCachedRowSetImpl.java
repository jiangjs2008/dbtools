/**
 * 
 */
package jdbc.wrapper.sqlite;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import jdbc.wrapper.AbstractCachedRowSet;

/**
 * @author jiangjs
 *
 */
public class SQLiteCachedRowSetImpl extends AbstractCachedRowSet {


	/**
	 * The integer value indicating size of the page.
	 */
	private int pageSize = 0;
	private int curDataIdx = -1;

	private ArrayList<HashMap<String, Object>> dataList = null;

	ResultSetMetaData _rsm = null;

	/**
	 * 构造函数
	 */
	public SQLiteCachedRowSetImpl() {
	}

	@Override
	public void setPageSize(int size) throws SQLException {
		pageSize = size;
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		dataList = new ArrayList<HashMap<String, Object>>();
		_rsm = new SQLiteResultSetMetaData(rs.getMetaData());

		HashMap<String, Object> rowValue = null;
		int colCnt = _rsm.getColumnCount();
		int i = 0;

		while (rs.next()) {
			rowValue = new HashMap<String, Object>();
			for (i = 1; i < colCnt; i ++) {
				rowValue.put(_rsm.getColumnName(i), rs.getObject(i));
			}
			dataList.add(rowValue);
		}
		System.out.print("");
	}

	@Override
	public boolean next() throws SQLException {
		if (dataList != null && dataList.size() > 0) {

			if (curDataIdx < dataList.size() - 1) {
				curDataIdx ++;
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}

	@Override
	public String getString(int columnIndex) throws SQLException {
		if (curDataIdx == -1) {
			throw new SQLException("have no data");
		}
		HashMap<String, Object> rowObj = dataList.get(curDataIdx);
		if (rowObj == null) {
			return null;
		} else {
//			if (columnIndex >= rowObj.size()) {
//				return null;
//			}
			Object objValue = rowObj.get(_rsm.getColumnName(columnIndex));
			if (objValue == null) {
				return null;
			} else {
				return objValue.toString();
			}
		}
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		if (curDataIdx == -1) {
			throw new SQLException("have no data");
		}
		HashMap<String, Object> rowObj = dataList.get(curDataIdx);
		if (rowObj == null) {
			return null;
		} else {
//			if (columnIndex >= rowObj.size()) {
//				return null;
//			}
			Object objValue = rowObj.get(_rsm.getColumnName(columnIndex));
			return objValue;
		}
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return _rsm;
	}

}
