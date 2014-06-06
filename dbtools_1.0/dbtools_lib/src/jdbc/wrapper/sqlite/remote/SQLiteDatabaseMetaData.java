package jdbc.wrapper.sqlite.remote;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import jdbc.wrapper.AbstractDatabaseMetaData;

import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;

public class SQLiteDatabaseMetaData extends AbstractDatabaseMetaData {


	/**
	 * 缺省构造函数
	 */
	SQLiteDatabaseMetaData() {
	}

	@Override
	public ResultSet getTableTypes() throws SQLException {
		String[] cols = { "TABLE_TYPE" };
		String[][] row = { { "TABLE" }, { "VIEW" } };
		SQLiteResultSet rs = new SQLiteResultSet(cols, row);
		return rs;
	}

    private static String sql_quote(String str) {
		if (str == null) {
			return "NULL";
		}
		int i, single = 0, dbl = 0;
		for (i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '\'') {
				single++;
			} else if (str.charAt(i) == '"') {
				dbl++;
			}
		}
		if (single == 0) {
			return "'" + str + "'";
		}
		if (dbl == 0) {
			return "\"" + str + "\"";
		}
		StringBuffer sb = new StringBuffer("'");
		for (i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\'') {
				sb.append("''");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
			throws SQLException {

		StringBuilder sb = new StringBuilder(
			"SELECT '' AS 'TABLE_CAT', '' AS 'TABLE_SCHEM',  tbl_name AS 'TABLE_NAME' FROM sqlite_master WHERE tbl_name like ");
		if (tableNamePattern != null) {
		    sb.append(sql_quote(tableNamePattern));
		} else {
		    sb.append("'%'");
		}
		sb.append(" AND ");
		if (types == null || types.length == 0) {
		    sb.append("(type = 'table' or type = 'view')");
		} else {
		    sb.append("(");
		    String sep = "";
		    for (int i = 0; i < types.length; i++) {
				sb.append(sep);
				sb.append("type = ");
				sb.append(sql_quote(types[i].toLowerCase()));
				sep = " or ";
		    }
		    sb.append(")");
		}
		sb.append(" order by tbl_name ");

		DbClient dbClient = DbClientFactory.getDbClient();
		// // TODO -- 此处需修改，直接使用SQL文查询 query
		ArrayList<TreeMap<Integer, String>> rslt = new ArrayList<TreeMap<Integer, String>>();
		//	(ArrayList<TreeMap<Integer, String>>) dbClient.execute(8, sb.toString());

		if (rslt == null || rslt.size() == 0) {
			return new SQLiteResultSet(null, null);
		}
		// set table column name
		int dataSize = rslt.size() - 1;
		TreeMap<Integer, String> colMap = rslt.get(dataSize);
		String[] colName = new String[colMap.size()];
		colName[0] = "NO.";
		int i = 1;
		for (Iterator<Entry<Integer, String>> iterator = colMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, String> entry = iterator.next();
			if (entry.getKey() == 0) {
				continue;
			}
			colName[i] = (String) entry.getValue();
			i ++;
		}

		String[][] allData = new String[dataSize][colName.length];
		// set table data
		for (int x = 0; x < dataSize; x ++) {
			colMap = rslt.get(x);
			allData[x][0] = Integer.toString(x + 1);

			i = 1;
			for (Map.Entry<Integer, String> col : colMap.entrySet()) {
				if (col.getKey() == 0) {
					continue;
				}

				allData[x][i] = col.getValue();
				i ++;
			}
		}

		SQLiteResultSet rs = new SQLiteResultSet(colName, allData);
		return rs;
	}

}
