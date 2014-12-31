package com.dbm.common.util;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import com.dbm.common.db.DbClient;
import com.dbm.common.db.DbClientFactory;
import com.dbm.common.log.LoggerWrapper;

/**
 * [name]<br>
 * MetaDataUtil<br><br>
 * [function]<br>
 * 获取表定义及索引定义信息<br><br>
 * [history]<br>
 * 2014/12/22 ver1.00 JiangJusheng<br>
 */
public class MetaDataUtil {

	/**
	 * instances of the log class
	 */
	private final static LoggerWrapper logger = new LoggerWrapper(MetaDataUtil.class); 

	/**
	 * 获取表定义信息<br>
	 * 为与DefaultTableModel兼容，这里输出 Vector&ltVector&ltString&gt&gt
	 *
	 * @param requestParam HTTP请求参数
	 *
	 * @return Vector<Vector<String>> 表定义信息
	 */
	public static Vector<Vector<String>> getTblDefInfo(String tblName) {
		Vector<Vector<String>> allData = null;
		try {
			DbClient dbClient = DbClientFactory.getDbClient();
			if (dbClient == null) {
				logger.error("数据库联接不正常");
				return null;
			}
			String realName = getRealTblName(dbClient, tblName);
			if (realName == null) {
				logger.error("执行时错误 参数不对");
				return null;
			}
			DatabaseMetaData dmd = dbClient.getConnection().getMetaData();

			String schema = null;
			try {
				if (dbClient.hasSchema()) {
					schema = dbClient.getConnection().getMetaData().getUserName();
				}
			} catch (SQLException ex) {
				logger.error(ex);
			}

			// 取得指定表的主键信息
			ResultSet pkRs = dmd.getPrimaryKeys(null, schema, realName);
			ArrayList<String> pkList = new ArrayList<String>();
			if (pkRs != null) {
				while (pkRs.next()) {
					pkList.add(pkRs.getString(4)); // COLUMN_NAME
				}
			}

			allData = new Vector<Vector<String>>();
			// 取得指定表的所有列的信息
			ResultSet columnRs = dmd.getColumns(null, schema, realName, "%");
			String tempValue = null;
			String col12Value = null;String col13Value = null;String col23Value = null;
			int no = 1;

			while (columnRs.next()) {
				Vector<String> columnInfo = new Vector<String>(8);
				columnInfo.add(Integer.toString(no));
				no ++;
				// *注：这里必须按照列的顺序(按列序号由小到大)来取值

				// 列名 COLUMN_NAME
				tempValue = columnRs.getString(4);
				columnInfo.add(tempValue);
				// 类型名 TYPE_NAME
				columnInfo.add(columnRs.getString(6));
				// 列的大小 COLUMN_SIZE
				columnInfo.add(columnRs.getString(7));

				// 是否为主键
				if (pkList.indexOf(tempValue) >= 0) {
					// 是主键
					columnInfo.add("Y");
				} else {
					columnInfo.add("");
				}

				// 是否可为空 NULLABLE
				tempValue = columnRs.getString(11);
				if ("YES".equalsIgnoreCase(tempValue) || "true".equalsIgnoreCase(tempValue) || "1".equals(tempValue)) {
					// 可为空
					columnInfo.add("Y");
				} else {
					// 不可为空
					columnInfo.add("");
				}

				col12Value = columnRs.getString(12);
				col13Value = columnRs.getString(13);
				col23Value = columnRs.getString(23);

				// 是否自动增加 IS_AUTOINCREMENT
				if ("YES".equalsIgnoreCase(col23Value) || "true".equalsIgnoreCase(col23Value) || "1".equals(col23Value)) {
					columnInfo.add("Y");
				} else {
					columnInfo.add("");
				}
				// 默认值 COLUMN_DEF
				columnInfo.add(col13Value);
				// 列的注释 REMARKS
				columnInfo.add(col12Value);

				allData.add(columnInfo);
			}

		} catch (Exception exp) {
			logger.error(exp);
		}
		return allData;
	}

	/**
	 * 获取索引定义信息
	 *
	 * @param requestParam HTTP请求参数
	 *
	 * @return Vector<Vector<String>> 表定义信息
	 */
	public static Vector<Vector<String>> getTblIdxInfo(String tblName) {
		Vector<Vector<String>> allData = null;
		try {
			DbClient dbClient = DbClientFactory.getDbClient();
			if (dbClient == null) {
				logger.error("数据库联接不正常");
				return null;
			}
			String realName = getRealTblName(dbClient, tblName);
			if (realName == null) {
				logger.error("执行时错误 参数不对");
				return null;
			}
			DatabaseMetaData dmd = dbClient.getConnection().getMetaData();

			String schema = null;
			try {
				if (dbClient.hasSchema()) {
					schema = dbClient.getConnection().getMetaData().getUserName();
				}
			} catch (SQLException ex) {
				logger.error(ex);
			}

			// 取得指定表的索引信息
			ResultSet columnRs = dmd.getIndexInfo(null, schema, realName, true, true);
			String tempValue = null;
			Vector<String> columnInfo = null;
			allData = new Vector<Vector<String>>();
			int no = 1;
			String col14Value = null;

			while (columnRs.next()) {
				columnInfo = new Vector<String>(6);
				columnInfo.add(Integer.toString(no));
				no ++;

				// 是否不唯一(NON_UNIQUE)
				col14Value = columnRs.getString(4);

				// 索引名称(INDEX_NAME)
				columnInfo.add(columnRs.getString(6));
				// 索引类型(TYPE)
				tempValue = columnRs.getString(7);
				if ("0".equals(tempValue)) {
					tempValue = "0:tableIndexStatistic";
				} else if ("1".equals(tempValue)) {
					tempValue = "1:tableIndexClustered";
				} else if ("2".equals(tempValue)) {
					tempValue = "2:tableIndexHashed";
				} else if ("3".equals(tempValue)) {
					tempValue = "3:tableIndexOther";
				}
				columnInfo.add(tempValue);

				if ("YES".equalsIgnoreCase(col14Value) || "true".equalsIgnoreCase(col14Value) || "1".equals(col14Value)) {
					columnInfo.add("Y");
				} else {
					columnInfo.add("");
				}

				// 索引中的列序列号(ORDINAL_POSITION)
				columnInfo.add(columnRs.getString(8));
				// 列名称 COLUMN_NAME
				columnInfo.add(columnRs.getString(9));
				// 列排序序列(ASC_OR_DESC)
				columnInfo.add(columnRs.getString(10));

				allData.add(columnInfo);
			}

		} catch (Exception exp) {
			logger.error(exp);
		}
		return allData;
	}

	/**
	 * 获取真实表名称，过滤Schema名
	 *
	 * @param dbClient 数据库客户端对象
	 * @param tblName  表名
	 *
	 * @return String 真实表名称
	 */
	private static String getRealTblName(DbClient dbClient, String tblName) {
		if (tblName == null || tblName.length() == 0) {
			return null;
		}
		if (dbClient.hasSchema()) {
			String realName = tblName;
			if (tblName.indexOf('.') > 0) {
				String[] arr = tblName.split("\\.");
				realName = arr[1];
			}
			return realName;
		} else {
			return tblName;
		}
	}
}
