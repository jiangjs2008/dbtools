package jdbc.wrapper.mongo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import jdbc.wrapper.AbstractDatabaseMetaData;

import com.mongodb.DB;

public class MongoDatabaseMetaData extends AbstractDatabaseMetaData {

	private DB _dbObj = null;

	private final static String[] tableTypName = new String[] { "TABLE_TYPE" };
	private final static String[][] tableTypValue = new String[][] { {"Collections"}, {"Stored JavaScript"}, {"GridFs"} };

	private final static String[] columnName = new String[] { "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME" };

	/**
	 * 缺省构造函数
	 */
	MongoDatabaseMetaData(DB dbObj) {
		this._dbObj = dbObj;
	}

	@Override
	public ResultSet getTableTypes() throws SQLException {
		return new MongoResultSet(tableTypName, tableTypValue);
	}

	@Override
	public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
			throws SQLException {
		String[][] rslt = null;

		if ("Collections".equals(types[0])) {
			// 取得table一览
			Set<String> colls = _dbObj.getCollectionNames();

			ArrayList<String> list = new ArrayList<String>();
			for (String tblName : colls) {
				if ("fs.chunks".equals(tblName) || "fs.files".equals(tblName)
						|| "system.indexes".equals(tblName) || "system.users".equals(tblName)) {
					continue;
				} else {
					list.add(tblName);
				}
			}

			rslt = new String[list.size()][3];
			int i = 0;
			for (String tbl : list) {
				rslt[i][2] = tbl;
				i ++;
			}

		} else if ("GridFs".equals(types[0])) {
			rslt = new String[1][3];
			rslt[0][2] = "fs.files";
			//rslt[1][2] = "fs.chunks";
		}
		return new MongoResultSet(columnName, rslt);
	}

	@Override
	public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
			throws SQLException {
		ResultSet rs = new MongoCachedRowSetImpl(_dbObj, tableNamePattern, null, 1, 1);
		rs.beforeFirst();
		ResultSetMetaData rsm = rs.getMetaData();
		if (rsm == null) {
			return null;
		}

		int colCnt = rsm.getColumnCount();
		String[][] rslt = new String[colCnt][12];
		for (int i = 0; i < colCnt; i ++) {
			rslt[i][3] = rsm.getColumnName(i + 1);
		}
		return new MongoResultSet(columnName, rslt);
	}

}
