package com.dbm.common.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class SecuUtil {

	/**
	 * BASE64解密
	 * 
	 * @param key
	 * @return
	 */
	public static String decryptBASE64(String key) {
		if (key == null || key.length() == 0) {
			return "";
		}
		key = key.trim();
		if (key.length() == 0) {
			return "";
		}
		try {
			return new String(new BASE64Decoder().decodeBuffer(key));
		} catch (Exception exp) {
			exp.printStackTrace();
			return "";
		}
	}

	/**
	 * BASE64 加密
	 * 
	 * @param key
	 * @return
	 */
	public static String encryptBASE64(String key) {
		if (key == null || key.length() == 0) {
			return "";
		}
		key = key.trim();
		if (key.length() == 0) {
			return "";
		}
		try {
			return new BASE64Encoder().encodeBuffer(key.getBytes());
		} catch (Exception exp) {
			exp.printStackTrace();
			return "";
		}
	}

}
