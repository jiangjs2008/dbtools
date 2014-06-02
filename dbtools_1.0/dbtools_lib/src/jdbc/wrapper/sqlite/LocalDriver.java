/**
 * 
 */
package jdbc.wrapper.sqlite;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author jiangjs
 *
 */
public class LocalDriver implements Driver {

	static {
		try {
			java.sql.DriverManager.registerDriver(new LocalDriver());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

//    Connection conn = null;
//    URL url = new URL("file:"+"/usr/db28/db2java8.zip");//要加载的JAR或ZIP包
//     URLClassLoader urlCL =URLClassLoader.newInstance(new URL[] { url });
//     Class c =urlCL.loadClass("COM.ibm.db2.jdbc.net.DB2Driver");//加载类
//     Driver dd=   (Driver) c.newInstance();//类的实例化
//     String  dbUrl  =  "jdbc:db2://1.2.3.4:6789/aaa";//数据库URL
//     Properties info = new Properties();  
//     info.setProperty("user", "db2inst1");  
//     info.setProperty("password", "password"); 
//     conn = dd.connect(dbUrl, info);//获得连接
     
	/**
	 * 缺省构造函数
	 */
	public LocalDriver() {
	}

	/* (non-Javadoc)
	 * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
	 */
	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		//return new SQLCipherConnection();
		return null;
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

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
