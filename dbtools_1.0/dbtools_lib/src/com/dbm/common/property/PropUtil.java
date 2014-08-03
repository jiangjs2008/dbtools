package com.dbm.common.property;

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * [name]<br>
 * 配置信息类<br><br>
 * [function]<br>
 * 读取配置信息<br><br>
 * [history]<br>
 * 2012/02/11 ver1.0 JiangJusheng<br>
 */
public abstract class PropUtil {

	/**
	 * instances of the log class
	 */
	protected static Logger logger = Logger.getLogger(PropUtil.class); 

	protected static ConnBean[] connList = null;
	protected static FavrBean[] favrList = null;

	protected static Properties appenv = new Properties();

	/**
	 * 设置数据库快捷连接的信息
	 *
	 * @param favrInfo 数据库快捷连接信息
	 */
	public static void setFavrInfo(FavrBean favrInfo) {
		int id = favrInfo.favrId;
		favrList[id] = favrInfo;
	}

	/**
	 * 设置数据库快捷连接的信息
	 *
	 * @param fvList 数据库快捷连接信息
	 */
	public static void setFavrInfo(FavrBean[] fvList) {
		favrList = fvList;
	}

	/**
	 * 取得指定常用数据库信息
	 *
	 * @param id 数据库类型Id
	 *
	 * @return FavrBean 常用数据库信息
	 */
	public static FavrBean getFavrInfo(int id) {
		return favrList[id];
	}

	/**
	 * 取得最常用数据库列表
	 *
	 * @return FavrBean[] 最常用数据库列表
	 */
	public static FavrBean[] getFavrInfo() {
		return favrList;
	}

	/**
	 * 设置数据库连接的信息
	 *
	 * @param connInfo 数据库连接信息
	 */
	public static void setConnInfo(ConnBean connInfo) {
		int id = connInfo.driverid;
		connList[id] = connInfo;
	}

	/**
	 * 设置数据库连接的信息
	 *
	 * @param cnList 数据库连接信息
	 */
	public static void setConnInfo(ConnBean[] cnList) {
		connList = cnList;
	}

	/**
	 * 根据数据库类型Id取得数据库驱动信息
	 *
	 * @param id 数据库类型Id
	 *
	 * @return ConnBean 数据库驱动信息
	 */
	public static ConnBean getDbConnInfo(int id) {
		return connList[id];
	}

	/**
	 * 取得数据库驱动信息列表
	 *
	 * @return ConnBean[] 数据库驱动信息列表
	 */
	public static ConnBean[] getDbConnInfo() {
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
		return appenv.getProperty(key);
	}

}
