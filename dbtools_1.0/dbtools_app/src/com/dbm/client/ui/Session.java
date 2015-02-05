package com.dbm.client.ui;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.math.NumberUtils;

import com.dbm.common.db.DbClient;
import com.dbm.common.property.ConnBean;
import com.dbm.common.property.FavrBean;
import com.dbm.common.property.PropUtil;

public class Session {

	public final static String APP_TITLE = "Keep it Simple"; //Easy DB Access

	/**
	 * Create a new session.
	 */
	private Session() {
	}

	/**
	 * 每页表示件数
	 */
	public final static int PageDataLimit = NumberUtils.toInt(PropUtil.getAppConfig("page.data.count"));

	/**
	 * 
	 */
	public static TableModel EmptyTableModel =
		new DefaultTableModel(
				new String[][] { { "1", } },
				new String[] { "NO.", "Column 1", "Column 2", "Column 3", "Column 4" });

	private static ConnBean infoItem = null;

	public static ConnBean getCurrConnInfo() {
		return infoItem;
	}

	public static void setCurrConnInfo(ConnBean dbInfo) {
		infoItem = dbInfo;
	}

	private static FavrBean fvrinfoItem = null;

	public static FavrBean getCurrFavrInfo() {
		return fvrinfoItem;
	}

	public static void setCurrFavrInfo(FavrBean fvrInfo) {
		fvrinfoItem = fvrInfo;
	}

	
	private static DbClient dbClient = null;
	public static DbClient getDbClient() {
		return dbClient;
	}

	public static void setDbClient(DbClient dbObj) {
		dbClient = dbObj;
	}

}

