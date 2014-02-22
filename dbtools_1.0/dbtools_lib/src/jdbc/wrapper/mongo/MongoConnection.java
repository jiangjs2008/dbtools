package jdbc.wrapper.mongo;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import jdbc.wrapper.AbstractConnection;

public class MongoConnection extends AbstractConnection {

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


}
