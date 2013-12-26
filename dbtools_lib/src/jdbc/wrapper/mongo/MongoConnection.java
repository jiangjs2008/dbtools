package jdbc.wrapper.mongo;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

public class MongoConnection implements java.sql.Connection {

	/**
	 * Reference to meta data or null.
	 */
	private MongoDatabaseMetaData meta = null;

	/**
	 * 缺省构造函数
	 */
	MongoConnection() {
	}

	public Statement createStatement() {
		return null;
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) {
		return null;
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		if (meta == null) {
			meta = new MongoDatabaseMetaData();
		}
		return meta;
	}

	public void close() throws SQLException {

	}

	public boolean isClosed() throws SQLException {
		return true;
	}

	public boolean isReadOnly() throws SQLException {
		return false;
	}

	public void clearWarnings() throws SQLException {
	}

	public void commit() throws SQLException {
	}

	public boolean getAutoCommit() throws SQLException {
		return false;
	}

	public String getCatalog() throws SQLException {
		return null;
	}

	public int getTransactionIsolation() throws SQLException {
		return 0;
	}

	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	public String nativeSQL(String sql) throws SQLException {
		throw new SQLException("not supported");
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		throw new SQLException("not supported");
	}

	public CallableStatement prepareCall(String sql, int x, int y) throws SQLException {
		throw new SQLException("not supported");
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return null;
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return null;
	}

	public void rollback() throws SQLException {
	}

	public void setAutoCommit(boolean ac) throws SQLException {
	}

	public void setCatalog(String catalog) throws SQLException {
	}

	public void setReadOnly(boolean ro) throws SQLException {
	}

	public void setTransactionIsolation(int level) throws SQLException {
	}

	public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
		throw new SQLException("not supported");
	}

	public int getHoldability() throws SQLException {
		return ResultSet.HOLD_CURSORS_OVER_COMMIT;
	}

	public void setHoldability(int holdability) throws SQLException {
		if (holdability == ResultSet.HOLD_CURSORS_OVER_COMMIT) {
			return;
		}
		throw new SQLException("not supported");
	}

	public Savepoint setSavepoint() throws SQLException {
		throw new SQLException("not supported");
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		throw new SQLException("not supported");
	}

	public void rollback(Savepoint x) throws SQLException {
		throw new SQLException("not supported");
	}

	public void releaseSavepoint(Savepoint x) throws SQLException {
		throw new SQLException("not supported");
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (resultSetHoldability != ResultSet.HOLD_CURSORS_OVER_COMMIT) {
			throw new SQLException("not supported");
		}
		return createStatement(resultSetType, resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		if (resultSetHoldability != ResultSet.HOLD_CURSORS_OVER_COMMIT) {
			throw new SQLException("not supported");
		}
		return prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql, int x, int y, int z) throws SQLException {
		throw new SQLException("not supported");
	}

	public PreparedStatement prepareStatement(String sql, int autokeys) throws SQLException {
		if (autokeys != Statement.NO_GENERATED_KEYS) {
			throw new SQLException("not supported");
		}
		return prepareStatement(sql);
	}

	public PreparedStatement prepareStatement(String sql, int colIndexes[]) throws SQLException {
		throw new SQLException("not supported");
	}

	public PreparedStatement prepareStatement(String sql, String columns[]) throws SQLException {
		throw new SQLException("not supported");
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		return null;
	}

	@Override
	public Clob createClob() throws SQLException {
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return null;
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return null;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return false;
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
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
