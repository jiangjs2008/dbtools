package jdbc.wrapper.mongo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import com.mongodb.DB;

import jdbc.wrapper.AbstractDatabaseMetaData;

public class MongoDatabaseMetaData extends AbstractDatabaseMetaData {

	private DB _dbObj = null;

	/**
	 * 缺省构造函数
	 */
	MongoDatabaseMetaData(DB dbObj) {
		this._dbObj = dbObj;
	}

	@Override
	public ResultSet getTableTypes() throws SQLException {
		String[][] rslt = new String[3][1];
		rslt[0][0] = "Collections";
		rslt[1][0] = "Stored JavaScript";
		rslt[2][0] = "GridFs";

		return new MongoResultSet(null, rslt);
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
						|| "system.indexes".equals(tblName) ||"system.users".equals(tblName)) {
					continue;
				} else {
					list.add(tblName);
				}
			}
			
			rslt = new String[list.size()][3];
			int i = 0;
			for (String tbl: list) {
				rslt[i][2] = tbl;
				i ++;
			}

		} else if ("GridFs".equals(types[0])) {
			rslt = new String[1][3];
			rslt[0][2] = "fs.files";
			//rslt[1][2] = "fs.chunks";
		}
		return new MongoResultSet(null, rslt);
	}

	@Override
	public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern,
			String columnNamePattern) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean generatedKeyAlwaysReturned() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
