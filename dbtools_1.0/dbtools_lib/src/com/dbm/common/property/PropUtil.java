/**
 * Copyright c NTT DATA CORPORATION 2012 All Rights Reserved.
 * PropUtil.java
 */
package com.dbm.common.property;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import com.dbm.common.log.LoggerWrapper;
import com.dbm.common.util.SecuUtil;

/**
 * [name]<br>
 * 数据库驱动信息类<br><br>
 * [function]<br>
 * 保存数据库驱动信息<br><br>
 * [history]<br>
 * 2012/02/11 ver1.00 JiangJusheng<br>
 *
 * @author JiangJusheng
 * @version 1.00
 */
public class PropUtil {

	/**
	 * instances of the log class
	 */
	protected static LoggerWrapper logger = new LoggerWrapper(PropUtil.class); 

	private static ArrayList<ConnBean> connList = new ArrayList<ConnBean>();
	private static ArrayList<FavrBean> favrList = new ArrayList<FavrBean>();
	private static HashMap<String, String> appenvMap = new HashMap<String, String>();

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException cnfexp) {
			logger.error(cnfexp);
		}
	}

	/**
	 * 初期化
	 */
	public static void load() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:doc/settings.db");
			stmt = conn.createStatement();

			rs = stmt.executeQuery("select * from driverinfo");
			while (rs.next()) {
				// read the result set
				ConnBean connBean = new ConnBean();
				connBean.driverid = rs.getInt("driverid");
				connBean.dbType = rs.getString("drivertype");
				connBean.description = rs.getString("description");
				connBean.action = rs.getString("action");
				connBean.driver = rs.getString("driver");
				connBean.sampleUrl = rs.getString("sampleurl");
				connList.add(connBean);
			}

			rs = stmt.executeQuery("select * from favrinfo where useflg=1 order by id");
			while (rs.next()) {
				// read the result set
				FavrBean favrBean = new FavrBean();
				favrBean.name = rs.getString("name");
				favrBean.driverId = rs.getInt("driverId");
				favrBean.description = rs.getString("description");
				favrBean.url = rs.getString("url");
				favrBean.user = SecuUtil.decryptBASE64(rs.getString("user"));
				favrBean.password = SecuUtil.decryptBASE64(rs.getString("password"));
				favrList.add(favrBean);
			}

			rs = stmt.executeQuery("select * from appenv");
			while (rs.next()) {
				// read the result set
				appenvMap.put(rs.getString("entrykey"), rs.getString("entryvalue"));
			}

			HashMap<Integer, String> msgMap = new HashMap<Integer, String>();
			rs = stmt.executeQuery("select * from msglist");
			while (rs.next()) {
				// read the result set
				msgMap.put(rs.getInt("msgid"), rs.getString("msgvalue"));
			}
			LoggerWrapper.addMessage(msgMap);

		} catch (SQLException sqlexp) {
			logger.error(sqlexp);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 取得最常用数据库列表
	 *
	 * @return ArrayList<FavrBean> 最常用数据库列表
	 */
	public static ArrayList<FavrBean> getFavrInfo() {
		return favrList;
	}

	/**
	 * 根据数据库类型Id取得数据库驱动信息
	 *
	 * @param id 数据库类型Id
	 *
	 * @return ConnBean 数据库驱动信息
	 */
	public static ConnBean getDbConnInfo(int id) {
		for (ConnBean cbInfo : connList) {
			if (cbInfo.driverid == id) {
				return cbInfo;
			}
		}
		return null;
	}

	/**
	 * 取得数据库驱动信息列表
	 *
	 * @return ArrayList<ConnBean> 数据库驱动信息列表
	 */
	public static ArrayList<ConnBean> getDbConnInfo() {
		return connList;
	}

	/**
	 * 取得应用程序设置信息
	 *
	 * @param key 设置信息KEY
	 *
	 * @return String 设置信息
	 */
	public static String getAppConfig(String key) {
		return appenvMap.get(key);
	}


}
