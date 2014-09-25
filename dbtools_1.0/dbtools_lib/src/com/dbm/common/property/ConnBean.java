package com.dbm.common.property;

/**
 * [name]<br>
 * 数据库驱动信息类<br><br>
 * [function]<br>
 * 保存数据库驱动信息<br><br>
 * [history]<br>
 * 2012/02/11 ver1.0 JiangJusheng<br>
 */
public class ConnBean {
	/**
	 * 数据库类型Id
	 */
	public int driverid = 0;
	/**
	 * 数据库类型名称
	 */
	public String name = null;
	/**
	 * 数据库类型简介
	 */
	public String description = null;
	/**
	 * 数据库操作实行类
	 */
	public String action = null;
	/**
	 * 数据库jdbc/java驱动信息
	 */
	public String driver = null;
	/**
	 * 数据库示例用URL
	 */
	public String sampleUrl = null;
	/**
	 * 数据库用户名
	 */
	public String user = null;
	/**
	 * 数据库用户密码
	 */
	public String password = null;

	@Override
	public String toString() {
		StringBuilder rs = new StringBuilder();
		rs.append("ConnBean:={'name':'");
		rs.append(name);
		rs.append("', 'driver':'");
		rs.append(driver);
		rs.append("', 'action':'");
		rs.append(action);
		rs.append("'}");
		return rs.toString();
	}
}
