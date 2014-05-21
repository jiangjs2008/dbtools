/**
 * 
 */
package jdbc.wrapper.mongo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.sql.RowSetEvent;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.spi.SyncProviderException;

import jdbc.wrapper.AbstractCachedRowSet;

import com.dbm.common.log.LoggerWrapper;
import com.dbm.common.property.PropUtil;
import com.dbm.common.util.StringUtil;
import com.mongodb.BasicDBObject;
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

	private static int dataLimit = StringUtil.parseInt(PropUtil.getAppConfig("page.data.count"));
	private MongoResultSetMetaData mrsr = null;

	private DBCollection _tblObj = null;
	private DBCursor _cur = null;

	private ArrayList<String> colNameList = null;
	private ArrayList<ArrayList<Object>> dataList = null;

	private int curDataIdx = -1;
	private int dataCount = 0;
	private int pageSize = 0;
	private int curPageIdx = -1;
	private int curPageDataCnt = 0;

	/**
	 * 构造函数
	 */
	public MongoCachedRowSetImpl(DBCollection tblObj) {
		// 查询所有的数据
		_tblObj = tblObj;

		// 如果数据量过大 考虑分页显示
		// 分页时才在画面上显示分页相关信息及组件(默认不显示)
		if (dataLimit == 0) {
			// 默认每页显示300行数据
			dataLimit = 300;
		}
		_cur = _tblObj.find().limit(dataLimit);
		setInitData();
	}

	/**
	 * 构造函数
	 */
	public MongoCachedRowSetImpl(DBCursor dataObj) {
		_tblObj = null;
		_cur = dataObj;
		setInitData();
	}

	private void setInitData() {
		dataCount = _cur.count();
		if (dataCount > dataLimit) {
			if (dataCount % dataLimit == 0) {
				pageSize = dataCount / dataLimit;
			} else {
				pageSize = dataCount / dataLimit + 1;
			}
			curPageDataCnt = dataLimit;
		} else {
			pageSize = 1;
			curPageDataCnt = dataCount;
		}

		// 复制数据(只复制当前页数据)
		if (_cur.hasNext()) {
			dataList = new ArrayList<ArrayList<Object>>(curPageDataCnt);

			ArrayList<Object> rowValue = null;
			while (_cur.hasNext()) {
				DBObject data = _cur.next();
				
				rowValue = new ArrayList<Object>();
				rowValue.addAll(data.toMap().values());
				dataList.add(rowValue);
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
		}
	}

	// 索引Key对象
	private DBObject idxKeyObj = null;
	/**
	 * 更新/删除用的值对象
	 */
	private HashMap<String, String> valueMap = null;


	/* (non-Javadoc)
	 * @see javax.sql.RowSet#clearParameters()
	 */
	@Override
	public void clearParameters() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.RowSet#execute()
	 */
	@Override
	public void execute() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.RowSet#setRowId(int, java.sql.RowId)
	 */
	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.RowSet#setRowId(java.lang.String, java.sql.RowId)
	 */
	@Override
	public void setRowId(String parameterName, RowId x) throws SQLException {
	}

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

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#wasNull()
	 */
	@Override
	public boolean wasNull() throws SQLException {
		return false;
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
		ArrayList<Object> rowObj = dataList.get((int) curDataIdx);
		if (rowObj == null) {
			return null;
		} else {
			if (columnIndex >= rowObj.size()) {
				return null;
			}
			Object objValue = rowObj.get(columnIndex);
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
		ArrayList<Object> rowObj = dataList.get(curDataIdx);
		if (rowObj == null) {
			return null;
		} else {
			return rowObj.get(columnIndex);
		}
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getObject(java.lang.String)
	 */
	@Override
	public Object getObject(String columnLabel) throws SQLException {
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#findColumn(java.lang.String)
	 */
	@Override
	public int findColumn(String columnLabel) throws SQLException {
		return 0;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#isBeforeFirst()
	 */
	@Override
	public boolean isBeforeFirst() throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	@Override
	public boolean isAfterLast() throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#isFirst()
	 */
	@Override
	public boolean isFirst() throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#isLast()
	 */
	@Override
	public boolean isLast() throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#beforeFirst()
	 */
	@Override
	public void beforeFirst() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#afterLast()
	 */
	@Override
	public void afterLast() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#first()
	 */
	@Override
	public boolean first() throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#last()
	 */
	@Override
	public boolean last() throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getRow()
	 */
	@Override
	public int getRow() throws SQLException {
		return 0;
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
	 * @see java.sql.ResultSet#relative(int)
	 */
	@Override
	public boolean relative(int rows) throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#previous()
	 */
	@Override
	public boolean previous() throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	@Override
	public boolean rowUpdated() throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#rowInserted()
	 */
	@Override
	public boolean rowInserted() throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	@Override
	public boolean rowDeleted() throws SQLException {
		return false;
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
	 * @see java.sql.ResultSet#refreshRow()
	 */
	@Override
	public void refreshRow() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	@Override
	public void cancelRowUpdates() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#moveToInsertRow()
	 */
	@Override
	public void moveToInsertRow() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	@Override
	public void moveToCurrentRow() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getRowId(int)
	 */
	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#getRowId(java.lang.String)
	 */
	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		return null;
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateRowId(int, java.sql.RowId)
	 */
	@Override
	public void updateRowId(int columnIndex, RowId x) throws SQLException {
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#updateRowId(java.lang.String, java.sql.RowId)
	 */
	@Override
	public void updateRowId(String columnLabel, RowId x) throws SQLException {
	}

	/* (non-Javadoc)
	 * @see java.sql.ResultSet#isClosed()
	 */
	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#populate(java.sql.ResultSet)
	 */
	@Override
	public void populate(ResultSet data) throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#execute(java.sql.Connection)
	 */
	@Override
	public void execute(Connection conn) throws SQLException {
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
	 * @see javax.sql.rowset.CachedRowSet#restoreOriginal()
	 */
	@Override
	public void restoreOriginal() throws SQLException {
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
		curPageIdx = -1;
		curPageDataCnt = 0;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#undoDelete()
	 */
	@Override
	public void undoDelete() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#undoInsert()
	 */
	@Override
	public void undoInsert() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#undoUpdate()
	 */
	@Override
	public void undoUpdate() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#columnUpdated(int)
	 */
	@Override
	public boolean columnUpdated(int idx) throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#columnUpdated(java.lang.String)
	 */
	@Override
	public boolean columnUpdated(String columnName) throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#size()
	 */
	@Override
	public int size() {
		return dataCount;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#setMetaData(javax.sql.RowSetMetaData)
	 */
	@Override
	public void setMetaData(RowSetMetaData md) throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#getOriginal()
	 */
	@Override
	public ResultSet getOriginal() throws SQLException {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#getOriginalRow()
	 */
	@Override
	public ResultSet getOriginalRow() throws SQLException {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#setOriginalRow()
	 */
	@Override
	public void setOriginalRow() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#getTableName()
	 */
	@Override
	public String getTableName() throws SQLException {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#setTableName(java.lang.String)
	 */
	@Override
	public void setTableName(String tabName) throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#commit()
	 */
	@Override
	public void commit() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#rollback()
	 */
	@Override
	public void rollback() throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#rollback(java.sql.Savepoint)
	 */
	@Override
	public void rollback(Savepoint s) throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#rowSetPopulated(javax.sql.RowSetEvent, int)
	 */
	@Override
	public void rowSetPopulated(RowSetEvent event, int numRows) throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#populate(java.sql.ResultSet, int)
	 */
	@Override
	public void populate(ResultSet rs, int startRow) throws SQLException {
		_cur = _tblObj.find().skip(startRow).limit(dataLimit);
		setInitData();
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#setPageSize(int)
	 */
	@Override
	public void setPageSize(int size) throws SQLException {
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#getPageSize()
	 */
	@Override
	public int getPageSize() {
		return pageSize;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#nextPage()
	 */
	@Override
	public boolean nextPage() throws SQLException {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.sql.rowset.CachedRowSet#previousPage()
	 */
	@Override
	public boolean previousPage() throws SQLException {
		return false;
	}

}
