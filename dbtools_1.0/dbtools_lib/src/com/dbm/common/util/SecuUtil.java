package com.dbm.common.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.dbm.common.log.LoggerWrapper;

public class SecuUtil {

	/**
	 * instances of the log class
	 */
	private final static LoggerWrapper logger = new LoggerWrapper(SecuUtil.class); 

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
			return new String(new BASE64Decoder().decodeBuffer(key)).trim();
		} catch (Exception exp) {
			logger.error(exp);
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
			return new BASE64Encoder().encodeBuffer(key.getBytes()).trim();
		} catch (Exception exp) {
			logger.error(exp);
			return "";
		}
	}


	private static KeyPair keyPair = null;

	private static KeyPair getKeyPair() {
		if (keyPair != null) {
			return keyPair;
		}
		try {
			// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			// 初始化密钥对生成器，密钥大小为1024位
			keyPairGen.initialize(1024);
			// 生成一个密钥对，保存在keyPair中
			keyPair = keyPairGen.generateKeyPair();
			return keyPair;
		} catch (Exception exp) {
			logger.error(exp);exp.printStackTrace();
			return null;
		}
	}

	/**
	 * 加密
	 *
	 * @param publicKey
	 * @param srcBytes
	 * @return
	 */
	public static String encryptRSA(String srcBytes) {
		try {
			// Cipher负责完成加密或解密工作，基于RSA
			Cipher cipher = Cipher.getInstance("RSA");
			// 根据公钥，对Cipher对象进行初始化
			cipher.init(Cipher.ENCRYPT_MODE, (RSAPublicKey) getKeyPair().getPublic());
			byte[] resultBytes = cipher.doFinal(srcBytes.getBytes());
			return new String(resultBytes);
		} catch (Exception exp) {
			logger.error(exp);exp.printStackTrace();
			return "";
		}
	}

	/**
	 * 解密
	 *
	 * @param privateKey
	 * @param srcBytes
	 * @return
	 */
	public static String decryptRSA(String srcBytes) {
		try {
			// Cipher负责完成加密或解密工作，基于RSA
			Cipher cipher = Cipher.getInstance("RSA");
			// 根据公钥，对Cipher对象进行初始化
			cipher.init(Cipher.DECRYPT_MODE, (RSAPrivateKey) getKeyPair().getPrivate());
			byte[] resultBytes = cipher.doFinal(srcBytes.getBytes());
			return new String(resultBytes);
		} catch (Exception exp) {
			logger.error(exp);exp.printStackTrace();
			return "";
		}
	}
}
