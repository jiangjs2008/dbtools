package jdbc.wrapper.mongo;

import java.sql.SQLException;
import java.util.ArrayList;

public class MongoResultSetMetaData implements java.sql.ResultSetMetaData {

	private ArrayList<Object> colNameList = null;

	/**
	 * 缺省构造函数
	 *
	 * @param cursor
	 */
	MongoResultSetMetaData(ArrayList<Object> colNameList) {
		this.colNameList = colNameList;
	}

	public String getCatalogName(int column) throws java.sql.SQLException {
		return null;
	}

	public String getColumnClassName(int column) throws java.sql.SQLException {
		return null;
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

	public int getColumnDisplaySize(int column) throws java.sql.SQLException {
		return 0;
	}

	public String getColumnLabel(int column) throws java.sql.SQLException {
		return null;
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

	public int getColumnType(int column) throws java.sql.SQLException {
		return 0;
	}

	public String getColumnTypeName(int column) throws java.sql.SQLException {
		return null;
	}

	public int getPrecision(int column) throws java.sql.SQLException {
		return 0;
	}

	public int getScale(int column) throws java.sql.SQLException {
		return 0;
	}

	public String getSchemaName(int column) throws java.sql.SQLException {
		return null;
	}

	public String getTableName(int column) throws java.sql.SQLException {
		return null;
	}

	public boolean isAutoIncrement(int column) throws java.sql.SQLException {
		return false;
	}

	public boolean isCaseSensitive(int column) throws java.sql.SQLException {
		return false;
	}

	public boolean isCurrency(int column) throws java.sql.SQLException {
		return false;
	}

	public boolean isDefinitelyWritable(int column) throws java.sql.SQLException {
		return true;
	}

	public int isNullable(int column) throws java.sql.SQLException {
		return columnNullableUnknown;
	}

	public boolean isReadOnly(int column) throws java.sql.SQLException {
		return false;
	}

	public boolean isSearchable(int column) throws java.sql.SQLException {
		return true;
	}

	public boolean isSigned(int column) throws java.sql.SQLException {
		return false;
	}

	public boolean isWritable(int column) throws java.sql.SQLException {
		return true;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

}
