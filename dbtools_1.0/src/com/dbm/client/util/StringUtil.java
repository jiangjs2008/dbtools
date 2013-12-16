/**
 * Copyright (c) 2013 JiangJusheng. All rights reserved.
 */
package com.dbm.client.util;

import java.util.StringTokenizer;

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

	/**
	 * convert a byte array to string
	 *
	 * @param btArr A byte array
	 *
	 * @return String The converted string
	 */
	public static String byte2CharStr(byte[] btArr) {
		if (btArr == null || btArr.length == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < btArr.length; i++) {
			sb.append((char) btArr[i]);
		}
		return sb.toString();
	}

	/**
	 * checks if a string is null. If it's null, return a empty string
	 *
	 * @param value A string
	 *
	 * @return String The converted string
	 */
	public static String NVL(String value) {
		if (value == null) {
			return "";
		} else {
			return value.trim();
		}
	}

	/**
	 * convert a string to int
	 *
	 * @param value A string
	 *
	 * @return String The converted int
	 */
	public static int parseInt(String value) {
		if (value == null || "".equals(value.trim())) {
			return 0;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	/**
	 * plits this string around matches of the given string
	 *
	 * @param str A string
	 * @param sep  the delimiting string
	 *
	 * @return String[] The array of strings computed by splitted
	 */
	public static String[] str2Array(String str, String sep) {
		if (str == null || sep == null) {
			return null;
		}
		StringTokenizer token = new StringTokenizer(str, sep);
		String[] array = new String[token.countTokens()];
		for (int i = 0; token.hasMoreTokens(); i++) {
			array[i] = token.nextToken();
		}
		return array;
	}

	/**
	 * 输出提示信息
	 *
	 * @param strings 提示内容
	 *
	 * @return String 输出信息
	 */
	public static String printTipText(String... strings) {
		if (strings == null || strings.length == 0) {
			return "";
		}

		StringBuilder rslt = new StringBuilder();
		rslt.append("<html><p style=\"font-size:12px;\">");
		for (String item : strings) {
			String value = StringUtil.NVL(item);
			if (value.length() == 0) {
				continue;
			}
			rslt.append(value);
			rslt.append("<br>");
		}
		rslt.append("</p></html>");
		return rslt.toString();
	}
}
