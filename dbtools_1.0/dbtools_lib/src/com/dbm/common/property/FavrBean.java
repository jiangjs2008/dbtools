package com.dbm.common.property;

/**
 * [name]<br>
 * 最常用数据库<br><br>
 * [function]<br>
 * 保存最常用的数据库连接信息<br><br>
 * [history]<br>
 * 2012/02/11 ver1.00 JiangJusheng<br>
 */
public class FavrBean {

	/**
	 * 快捷方式名称
	 */
	public String name = null;
	/**
	 * 数据库类型
	 */
	public int driverId = 0;
	/**
	 * 数据库连接信息简介
	 */
	public String description = null;
	/**
	 * 数据库URL
	 */
	public String url = null;
	/**
	 * 数据库用户名
	 */
	public String user = null;
	/**
	 * 数据库用户密码
	 */
	public String password = null;
	/**
	 * 利用标志位，1：可用； 0：不可用
	 */
	//public boolean useFlg = false;
}
