package jdbc.wrapper.sqlite.remote;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executor;

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

	@Override
	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

}
