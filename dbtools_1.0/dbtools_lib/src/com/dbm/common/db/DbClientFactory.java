/**
 * Copyright (c) 2013 JiangJusheng. All rights reserved.
 */
package com.dbm.common.db;

import com.dbm.common.error.BaseExceptionWrapper;

/**
 * [class]<br>
 * database client factory<br><br>
 * [function]<br>
 * create a DB2 client object<br><br>
 * [history]<br>
 * 2013/05/10 first edition  JiangJusheng<br>
 *
 * @version 1.00
 */
public class DbClientFactory {

	public static DbClient createDbClient(String implClass) {
		try {
			DbClient dbClient = (DbClient) DbClientFactory.class.getClassLoader().loadClass(implClass).newInstance();
			return dbClient;
		} catch (Throwable exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

}

