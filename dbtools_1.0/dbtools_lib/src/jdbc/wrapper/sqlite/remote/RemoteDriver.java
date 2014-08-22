/**
 * 
 */
package jdbc.wrapper.sqlite.remote;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import jdbc.wrapper.AbstractDriver;

/**
 * @author jiangjs
 *
 */
public class RemoteDriver extends AbstractDriver {

	static {
		try {
			java.sql.DriverManager.registerDriver(new RemoteDriver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 缺省构造函数
	 */
	public RemoteDriver() {
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
	 */
	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		return new SQLiteConnection();
	}

}
