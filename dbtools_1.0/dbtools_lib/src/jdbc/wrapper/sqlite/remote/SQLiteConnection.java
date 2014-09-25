package jdbc.wrapper.sqlite.remote;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import jdbc.wrapper.AbstractConnection;

public class SQLiteConnection extends AbstractConnection {

	/**
	 * Reference to meta data or null.
	 */
	private SQLiteDatabaseMetaData meta = null;

	/**
	 * 缺省构造函数
	 */
	SQLiteConnection() {
	}

	@Override
	public Statement createStatement() {
		return null;
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) {
		return null;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		if (meta == null) {
			meta = new SQLiteDatabaseMetaData();
		}
		return meta;
	}

	@Override
	public void close() throws SQLException {

	}

	@Override
	public boolean isClosed() throws SQLException {
		return true;
	}

}
