package jdbc.wrapper.sqlite;

import java.sql.SQLException;

public class SQLCipherResultSetMetaData implements java.sql.ResultSetMetaData {

	// private JDBCResultSet r;

	/**
	 * 构造函数
	 */
	SQLCipherResultSetMetaData(SQLCipherResultSet r) {
		// this.r = r;
	}

	public String getCatalogName(int column) throws java.sql.SQLException {
		return null;
	}

	public String getColumnClassName(int column) throws java.sql.SQLException {
		return null;
	}

	public int getColumnCount() throws java.sql.SQLException {
		return 0;
	}

	public int getColumnDisplaySize(int column) throws java.sql.SQLException {
		return 0;
	}

	public String getColumnLabel(int column) throws java.sql.SQLException {
		return null;
	}

	public String getColumnName(int column) throws java.sql.SQLException {
		return null;
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
