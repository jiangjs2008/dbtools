/**
 * Copyright (c) 2013 JiangJusheng. All rights reserved.
 */
package com.dbm.common.util;

import java.util.ArrayList;
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
	 * 文字列から文字列を抜き出し、配列にして返す
	 *
	 * @param str  ターゲット文字列
	 * @param sep 区切り文字(群)
	 * @return String[] 変換後文字列
	 */
	public static String[] split(String str, String sep) {
		StringTokenizer token = new StringTokenizer(str, sep);
		String[] array = new String[token.countTokens()];
		for (int i = 0; token.hasMoreTokens(); i++) {
			array[i] = token.nextToken();
		}
		return array;
	}

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
		return parseInt(value, 0);
	}

	public static int parseInt(String value, int defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		value = value.trim();
		if (value.length() == 0) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			return defaultValue;
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

	/**
	 * <p>Splits the provided text into an array, separator specified,
	 * preserving all tokens, including empty tokens created by adjacent
	 * separators. This is an alternative to using StringTokenizer.</p>
	 *
	 * <p>The separator is not included in the returned String array.
	 * Adjacent separators are treated as separators for empty tokens.
	 * For more control over the split use the StrTokenizer class.</p>
	 *
	 * <p>A <code>null</code> input String returns <code>null</code>.</p>
	 * <p>使用JDK缺省的split时，若分隔符是"\t", 且行末有空值，则这些空值不会出现在结果中</p>
	 *
	 * <pre>
	 * StringUtil.split(null, *)         = null
	 * StringUtil.split("", *)           = []
	 * StringUtil.split("a.b.c", '.')    = ["a", "b", "c"]
	 * StringUtil.split("a..b.c", '.')   = ["a", "", "b", "c"]
	 * StringUtil.split("a:b:c", '.')    = ["a:b:c"]
	 * StringUtil.split("a\tb\nc", null) = ["a", "b", "c"]
	 * StringUtil.split("a b c", ' ')    = ["a", "b", "c"]
	 * StringUtil.split("a b c ", ' ')   = ["a", "b", "c", ""]
	 * StringUtil.split("a b c  ", ' ')  = ["a", "b", "c", "", ""]
	 * StringUtil.split(" a b c", ' ')   = ["", a", "b", "c"]
	 * StringUtil.split("  a b c", ' ')  = ["", "", a", "b", "c"]
	 * StringUtil.split(" a b c ", ' ')  = ["", a", "b", "c", ""]
	 * </pre>
	 *
	 * @param str  the String to parse, may be <code>null</code>
	 * @param separatorChar the separate character, <code>null</code> splits on whitespace
	 *
	 * @return an array of parsed Strings, <code>null</code> if null String input
	 */
	public static String[] split(String str, char separatorChar) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return new String[0];
		}
		ArrayList<String> list = new ArrayList<String>();
		int i = 0, start = 0;
		while (i < len) {
			if (str.charAt(i) == separatorChar) {
				list.add(str.substring(start, i));
				start = i + 1;
			}
			i++;
		}
		if (start <= i) {
			list.add(str.substring(start, i));
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
}
