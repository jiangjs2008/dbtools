/**
 * Copyright (c) 2013 JiangJusheng. All rights reserved.
 */
package com.dbm.web.util;

/**
 * [class]<br>
 * string utility function class<br><br>
 * [function]<br>
 * string utility function<br><br>
 * [history]<br>
 * 2013/05/10 first edition  JiangJusheng<br>
 *
 * @version 1.00
 */
public final class StringUtil {

	/**
	 * add zero ahead of the string
	 *
	 * @param val A digital
	 * @param length The length of the converted string
	 *
	 * @return String The converted string
	 */
	public static String addPreZero(int val, int length) {
		return addPreZero(Integer.toString(val), length);
	}

	public static String addPreZero(String val, int length) {
		if (length <= val.length()) {
			return val;
		}
		while (length > val.length()) {
			val = "0" + val;
		}
		return val;
	}
}
