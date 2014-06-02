package jdbc.wrapper.mongo;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.Executor;

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

	public DatabaseMetaData getMetaData() throws SQLException {
		if (meta == null) {
			meta = new MongoDatabaseMetaData(this._dbObj);
		}
		return meta;
	}

	public DB getMongoDb() {
		return _dbObj;
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
