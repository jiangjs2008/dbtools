/**
 * Copyright (c) 2013 JiangJusheng. All rights reserved.
 */
package com.dbm.client.util;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

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
			String value = StringUtils.trimToEmpty(item);
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
