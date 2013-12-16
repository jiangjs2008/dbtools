/**
 * Copyright (c) 2013 JiangJusheng. All rights reserved.
 */
package com.dbm.client.db;

import com.dbm.client.error.BaseExceptionWrapper;

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

	private static DbClient dbClient = null;

	public static void createDbClient(String implClass) {
		try {
			dbClient = (DbClient) DbClientFactory.class.getClassLoader().loadClass(implClass).newInstance();
		} catch (Throwable exp) {
			throw new BaseExceptionWrapper(exp);
		}
	}

	public static DbClient getDbClient() {
		return dbClient;
	}

	public static void close() {
		if (dbClient != null) {
			dbClient.close();
			dbClient = null;
		}
	}
}

