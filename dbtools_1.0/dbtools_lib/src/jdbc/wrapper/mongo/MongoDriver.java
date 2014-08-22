/**
 * 
 */
package jdbc.wrapper.mongo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import jdbc.wrapper.AbstractDriver;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * @author jiangjs
 *
 */
public class MongoDriver extends AbstractDriver {

	static {
		try {
			java.sql.DriverManager.registerDriver(new MongoDriver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 缺省构造函数
	 */
	public MongoDriver() {
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
	 * @see com.mongodb.MongoClientURI 使用"mongodb://user:passwd@172.60.100.114:27017/dntsp"形式
	 */
	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		if (url.indexOf("mongodb") < 0) {
			return null;
		}
		String[] dbType = url.split("//");
		String[] dbArr = dbType[1].split("/");

		String userName = info.getProperty("user");
		String passwd = info.getProperty("password");
		String dbUrl = "mongodb://" + userName + ":" + passwd + "@" + dbType[1];
		DB dbObj = null;
		try {

			MongoClient mongoClient = new MongoClient(new MongoClientURI(dbUrl));
			dbObj = mongoClient.getDB(dbArr[1]);

			//MongoClient mongoClient = new MongoClient(urlArr[0], StringUtil.parseInt(urlArr[1]));
			//mongoClient.getMongoClientOptions().getConnectTimeout()connectTimeout = 10;

		} catch (Exception exp) {
			throw new SQLException("create MongoClient " + url + " error: " + exp.toString());
		}
		if (dbObj == null) {
			throw new SQLException("create MongoClient error: no db =>" + url);
		}

		return new MongoConnection(dbObj);
	}

}
