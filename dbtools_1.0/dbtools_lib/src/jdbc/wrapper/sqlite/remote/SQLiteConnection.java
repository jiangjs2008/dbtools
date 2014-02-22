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

	public Statement createStatement() {
		return null;
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) {
		return null;
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		if (meta == null) {
			meta = new SQLiteDatabaseMetaData();
		}
		return meta;
	}

	public void close() throws SQLException {

	}

	public boolean isClosed() throws SQLException {
		return true;
	}

}
