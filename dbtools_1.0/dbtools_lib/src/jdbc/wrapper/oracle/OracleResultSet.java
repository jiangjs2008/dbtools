package jdbc.wrapper.oracle;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import jdbc.wrapper.AbstractResultSet;

public class OracleResultSet extends AbstractResultSet {


	private ResultSet _wrapper = null;

	/**
	 * 构造函数
	 */
	public OracleResultSet(ResultSet rs) {
		this._wrapper = rs;
	}

	public boolean next() throws SQLException {
		return _wrapper.next();
	}

	public String getString(int columnIndex) throws SQLException {
		return _wrapper.getString(columnIndex);
	}

	public String getString(String columnName) throws SQLException {
		return _wrapper.getString(columnName);
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return _wrapper.getMetaData();
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
