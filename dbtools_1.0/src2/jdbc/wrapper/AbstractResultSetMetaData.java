/**
 * 
 */
package jdbc.wrapper;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author jiangjs
 *
 */
public abstract class AbstractResultSetMetaData implements ResultSetMetaData {

	/* (non-Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getColumnCount()
	 */
	@Override
	public int getColumnCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#isAutoIncrement(int)
	 */
	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#isCaseSensitive(int)
	 */
	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#isSearchable(int)
	 */
	@Override
	public boolean isSearchable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#isCurrency(int)
	 */
	@Override
	public boolean isCurrency(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#isNullable(int)
	 */
	@Override
	public int isNullable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#isSigned(int)
	 */
	@Override
	public boolean isSigned(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getColumnDisplaySize(int)
	 */
	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getColumnLabel(int)
	 */
	@Override
	public String getColumnLabel(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getSchemaName(int)
	 */
	@Override
	public String getSchemaName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getPrecision(int)
	 */
	@Override
	public int getPrecision(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getScale(int)
	 */
	@Override
	public int getScale(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getTableName(int)
	 */
	@Override
	public String getTableName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getCatalogName(int)
	 */
	@Override
	public String getCatalogName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getColumnType(int)
	 */
	@Override
	public int getColumnType(int column) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getColumnTypeName(int)
	 */
	@Override
	public String getColumnTypeName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#isReadOnly(int)
	 */
	@Override
	public boolean isReadOnly(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#isWritable(int)
	 */
	@Override
	public boolean isWritable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#isDefinitelyWritable(int)
	 */
	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSetMetaData#getColumnClassName(int)
	 */
	@Override
	public String getColumnClassName(int column) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
