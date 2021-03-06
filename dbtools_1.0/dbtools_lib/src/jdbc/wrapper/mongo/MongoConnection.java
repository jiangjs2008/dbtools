package jdbc.wrapper.mongo;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import jdbc.wrapper.AbstractConnection;

import com.mongodb.DB;

public class MongoConnection extends AbstractConnection {

	private DB _dbObj = null;

	/**
	 * Reference to meta data or null.
	 */
	private MongoDatabaseMetaData meta = null;

	/**
	 * 缺省构造函数
	 */
	MongoConnection(DB dbObj) {
		this._dbObj = dbObj;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		if (meta == null) {
			meta = new MongoDatabaseMetaData(_dbObj);
		}
		return meta;
	}

	public DB getMongoDb() {
		return _dbObj;
	}

}
