/**
 * 
 */
package jdbc.wrapper.mongo;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.rowset.spi.SyncProviderException;

import jdbc.wrapper.AbstractCachedRowSet;

import com.dbm.common.log.LoggerWrapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

/**
 * @author jiangjs
 *
 */
public class MongoCachedRowSetImpl extends AbstractCachedRowSet {

	/**
	 * instances of the log class
	 */
	protected static LoggerWrapper logger = new LoggerWrapper(MongoCachedRowSetImpl.class); 

	private MongoResultSetMetaData mrsr = null;

	private DBCollection _tblObj = null;
	private DBCursor _cur = null;

	private ArrayList<String> colNameList = null;
	private ArrayList<DBObject> dataList = null;

	private int curDataIdx = -1;
	private int dataCount = 0;
	private int pageSize = 0;

	/**
	 * 构造函数
	 */
	public MongoCachedRowSetImpl(DB dbObj, String tblName, int pageNum, int limit) {
		// 查询所有的数据
		_tblObj = dbObj.getCollection(tblName);

		if (pageNum == 0) {
			// 不分页
			_cur = _tblObj.find();
		} else if (pageNum == 1) {
			_cur = _tblObj.find().limit(limit);
		} else {
			_cur = _tblObj.find().skip((pageNum - 1) * limit).limit(limit);
		}
		dataCount = _cur.size();
		if (dataCount == 0) {
			logger.info("此次查询结果为0");
		}
	}

	// TODO -- 必须删除此函数，并入构造函数
	@Override
	public void beforeFirst() throws SQLException {
		// 复制数据(只复制当前页数据)
		if (_cur.hasNext()) {
			dataList = new ArrayList<DBObject>(dataCount);

			while (_cur.hasNext()) {
				DBObject data = _cur.next();
				dataList.add(data);
			}

			if (colNameList == null) {
				colNameList = new ArrayList<String>();
				colNameList.addAll(_cur.curr().keySet());

				// 生成元信息
				String[] nameArray = new String[colNameList.size()];
				for (int i = 0; i < nameArray.length; i ++) {
					nameArray[i] = colNameList.get(i).toString();
				}
				
				mrsr = new MongoResultSetMetaData(nameArray);
			}

		} else {
			logger.info("此次查询无结果");
		}
	}

	// 索引Key对象
	private DBObject idxKeyObj = null;
	/**
	 * 更新/删除用的值对象
	 */
	private HashMap<String, String> valueMap = null;

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#next()
	 */
	@Override
	public boolean next() throws SQLException {
		if (dataList != null && dataList.size() > 0) {

			if (curDataIdx < dataList.size() - 1) {
				curDataIdx ++;
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#close()
	 */
	@Override
	public void close() {
//		if (_cur != null) {
//			_cur.close();
//		}
	}

	/** 
	 * 第一列{"_id"}的索引为0,并且第一列不在画面上表示
	 * 第二列的索引为1 ...
	 * 
	 * @see java.sql.ResultSet#getString(int)
	 */
	@Override
	public String getString(int columnIndex) throws SQLException {
		if (curDataIdx == -1) {
			throw new SQLException("have no data");
		}
		DBObject rowObj = dataList.get(curDataIdx);
		if (rowObj == null) {
			return null;
		} else {
//			if (columnIndex >= rowObj.size()) {
//				return null;
//			}
			Object objValue = rowObj.get(colNameList.get(columnIndex));
			if (objValue == null) {
				return null;
			} else {
				return objValue.toString();
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getString(java.lang.String)
	 */
	@Override
	public String getString(String columnLabel) throws SQLException {
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getMetaData()
	 */
	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return mrsr;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(int)
	 */
	@Override
	public Object getObject(int columnIndex) throws SQLException {
		DBObject rowObj = dataList.get(curDataIdx);
		if (rowObj == null) {
			return null;
		} else {
//			if (columnIndex >= rowObj.size()) {
//				logger.error("IndexOutOfBoundsException");
//				return "";
//			} else {
				return rowObj.get(colNameList.get(columnIndex));
//			}
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#absolute(int)
	 */
	@Override
	public boolean absolute(int row) throws SQLException {
		curDataIdx = row - 1;
		idxKeyObj = new BasicDBObject("_id", getObject(0));
		return true;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateString(int, java.lang.String)
	 */
	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		String updKey = mrsr.getColumnName(columnIndex);
		if (valueMap == null) {
			valueMap = new HashMap<String, String>();
		}
		valueMap.put(updKey, x);
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#insertRow()
	 */
	@Override
	public void insertRow() throws SQLException {
		DBObject obj2 = new BasicDBObject();
		obj2.putAll(valueMap);

		WriteResult wr = _tblObj.insert(obj2);
		if (wr != null) {
			logger.debug(wr.toString());
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateRow()
	 */
	@Override
	public void updateRow() throws SQLException {
		DBObject obj2 = new BasicDBObject();
		obj2.putAll(valueMap);

		WriteResult wr = _tblObj.update(idxKeyObj, new BasicDBObject("$set", obj2));
		if (wr != null) {
			logger.debug(wr.toString());
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#deleteRow()
	 */
	@Override
	public void deleteRow() throws SQLException {
		WriteResult wr = _tblObj.remove(idxKeyObj);
		if (wr != null) {
			logger.debug(wr.toString());
		}
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#acceptChanges()
	 */
	@Override
	public void acceptChanges() throws SyncProviderException {
		idxKeyObj = null;
		if (valueMap != null) {
			valueMap.clear();
			valueMap = null;
		}
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#acceptChanges(java.sql.Connection)
	 */
	@Override
	public void acceptChanges(Connection con) throws SyncProviderException {
		acceptChanges();
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#release()
	 */
	@Override
	public void release() throws SQLException {
//		if (colNameList != null) {
//			colNameList.clear();
//		}
		if (dataList != null) {
			dataList.clear();
		}

		curDataIdx = -1;
		dataCount = 0;
		pageSize = 0;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#size()
	 */
	@Override
	public int size() {
		return dataCount;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#getPageSize()
	 */
	@Override
	public int getPageSize() {
		return pageSize;
	}

}
