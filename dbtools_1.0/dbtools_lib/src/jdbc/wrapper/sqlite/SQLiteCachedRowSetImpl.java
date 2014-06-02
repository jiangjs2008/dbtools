/**
 * 
 */
package jdbc.wrapper.sqlite;

import java.sql.SQLException;

import jdbc.wrapper.AbstractCachedRowSet;

/**
 * @author jiangjs
 *
 */
public class SQLiteCachedRowSetImpl extends AbstractCachedRowSet {



	/**
	 * 构造函数
	 */
	public SQLiteCachedRowSetImpl() {

	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


}
