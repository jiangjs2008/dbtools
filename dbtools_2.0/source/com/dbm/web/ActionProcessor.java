package com.dbm.web;

public class ActionProcessor {

	/**
	 * 登陆数据库
	 */
	public final static int ACT_LOGIN = 1;
	/**
	 * 查询数据( 执行脚本)
	 */
	public final static int ACT_QUERY = 2;
	/**
	 * 更新数据
	 */
	public final static int ACT_UPDATE = 3;
	/**
	 * 追加数据
	 */
	public final static int ACT_INSERT = 4;
	/**
	 * 删除数据
	 */
	public final static int ACT_DELETE = 5;


	/**
	 * 取得数据库信息
	 */
	public final static int ACT_DBINFO = 7;
	/**
	 * 取得表信息
	 */
	public final static int ACT_TBLINFO = 8;
	/**
	 * 取得表数据(所有数据)
	 */
	public final static int ACT_TBLDATA = 9;

}
