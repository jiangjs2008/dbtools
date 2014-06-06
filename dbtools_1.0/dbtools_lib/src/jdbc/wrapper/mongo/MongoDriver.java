/**
 * 
 */
package jdbc.wrapper.mongo;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import com.dbm.common.util.StringUtil;
import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * @author jiangjs
 *
 */
public class MongoDriver implements Driver {

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
	 */
	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		String[] dbType = url.split("//");
		String dbArr[] = dbType[1].split("/");
		String urlArr[] = dbArr[0].split(":");
		DB dbObj = null;
		try {
			MongoClient mongoClient = new MongoClient(urlArr[0], StringUtil.parseInt(urlArr[1]));
			dbObj = mongoClient.getDB(dbArr[1]);
		} catch (Exception exp) {
			throw new SQLException("create MongoClient " + url + " error: " + exp.toString());
		}
		if (dbObj == null) {
			throw new SQLException("create MongoClient error: no db =>" + url);
		}

		String user = info.getProperty("user");
		String password = info.getProperty("password");
		if (user != null && password != null) {
			if (!dbObj.authenticate(user, password.toCharArray())) {
				throw new SQLException("no authenticate: " + url + " <= " + user + " : " + password);
			}
		}
		return new MongoConnection(dbObj);
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#acceptsURL(java.lang.String)
	 */
	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#getPropertyInfo(java.lang.String, java.util.Properties)
	 */
	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#getMajorVersion()
	 */
	@Override
	public int getMajorVersion() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#getMinorVersion()
	 */
	@Override
	public int getMinorVersion() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#jdbcCompliant()
	 */
	@Override
	public boolean jdbcCompliant() {
		return false;
	}

}
