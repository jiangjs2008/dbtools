package jdbc.wrapper.mongo;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map.Entry;

import jdbc.wrapper.AbstractResultSet;

import com.mongodb.BasicDBObject;

public class MongoResultSet extends AbstractResultSet {

	/**
	 * Meta data for result set or null.
	 */
	private MongoResultSetMetaData md;

	/**
	 * 	是否有缺省"_id"
	 */
	boolean hasRawId = false;

	private int rowIdx = 0;
	private int rowCnt = 0;
	private int colCnt = 0;

	private String[] header = null;
	private String[][] datas = null;

	/**
	 * 构造函数
	 */
	public MongoResultSet(String[] header, String[][] datas) {
		rowIdx = -1;
		rowCnt = -1;
		this.datas = datas;
		if (datas != null) {
			rowCnt = datas.length;
		}

		this.header = header;
		if (header != null) {
			colCnt = header.length;
		}
		if (colCnt >= 1 && "_id".equals(header[0])) {
			hasRawId = true;
		}
	}

	/**
	 * 构造函数
	 */
	public MongoResultSet(BasicDBObject rsltObj) {
		rowIdx = -1;
		rowCnt = -1;

		int size = rsltObj.values().size();
		ArrayList<String> headerList = new ArrayList<String>(size);
		ArrayList<String> dataList = new ArrayList<String>(size);

		Object itemObj = null;
		for (Entry<String, Object> obj : rsltObj.entrySet()) {
			headerList.add(obj.getKey());
			itemObj = obj.getValue();
			if (itemObj == null) {
				dataList.add("");
			} else {
				dataList.add(itemObj.toString());
			}
		}

		this.datas = new String[1][size];
		datas[0] = dataList.toArray(datas[0]);

		if (datas != null) {
			rowCnt = datas.length;
		}

		this.header = new String[size];
		header = headerList.toArray(header);
		if (header != null) {
			colCnt = header.length;
		}
		if (colCnt >= 1 && "_id".equals(header[0])) {
			hasRawId = true;
		}
	}

	@Override
	public boolean next() throws SQLException {
		if (rowIdx < rowCnt - 1) {
			rowIdx ++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		return getString(columnIndex);
	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		return getString(columnLabel);
	}

	/** 
	 * 第一列{"_id"}的索引为0,并且第一列不在画面上表示
	 * 第二列的索引为1 ...
	 * 
	 * @see java.sql.ResultSet#getString(int)
	 */
	@Override
	public String getString(int columnIndex) throws SQLException {
		return datas[rowIdx][columnIndex];
	}

	@Override
	public String getString(String columnName) throws SQLException {
		int col = findColumn(columnName);
		return datas[rowIdx][col];
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		if (md == null) {
			md = new MongoResultSetMetaData(header);
		}
		return md;
	}

	/**
	 * 返回原始列的序号
	 */
	@Override
	public int findColumn(String columnLabel) throws SQLException {
		if (header == null) {
			return -1;
		}
		for (int i = 0; i < colCnt; i ++) {
			if (columnLabel.equals(header[i])) {
				return i;
			}
		}
		return -1;
	}
}
