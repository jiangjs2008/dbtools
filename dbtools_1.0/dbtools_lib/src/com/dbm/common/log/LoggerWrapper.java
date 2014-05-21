package com.dbm.common.log;

import java.text.MessageFormat;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * [name]<br>
 * 既存のロギングをラッピングしログ機能を提供します。<br><br>
 * [function]<br>
 * ログ出力を行う。<br><br>
 * [history]<br>
 * 2003/10/16 ver1.0 JiangJusheng<br>
 */
public class LoggerWrapper {

	static {
		//log4j初期化
		String url = System.getProperty("user.dir") + "/conf/log4j.xml";
		DOMConfigurator.configure(url);
	}

	private static HashMap<Integer, String> _msgMap = new HashMap<Integer, String>();

	/**
	 * ロギングラッパーのname
	 */
	private String FQCN;

	/**
	 * ロギング本体
	 */
	private org.apache.log4j.Logger logger;

	/**
	 * コンストラクタ
	 *
	 * @param cls ログを出力するクラス
	 */
	public LoggerWrapper(Class<?> cls) {
		logger = org.apache.log4j.Logger.getLogger(cls);
		FQCN = this.getClass().getName();
	}

	/**
	 * デバックログを出力する。
	 *
	 * @param message 出力するログメッセージ
	 */
	public void debug(String message) {
		logger.log(FQCN, Level.DEBUG, message, null);
	}

	/**
	 * 情報ログを出力する。
	 *
	 * @param msg 出力するログメッセージ
	 */
	public void info(String msg) {
		logger.log(FQCN, Level.INFO, msg, null);
	}

	/**
	 * 警告ログを出力する。
	 *
	 * @param id 出力するログメッセージID
	 * @param user ユーザー情報（toStringの結果を出力）
	 */
	public void warn(String msg) {
		logger.log(FQCN, Level.WARN, msg, null);
	}

	/**
	 * エラーログを出力する。
	 *
	 * @param String 出力するメッセージ
	 */
	public void error(String cause) {
		logger.log(FQCN, Level.ERROR, cause, null);
	}

	/**
	 * エラーログを出力する。
	 *
	 * @param Throwable 出力するSQLエラー
	 */
	public void error(Throwable cause) {
		logger.log(FQCN, Level.ERROR, "", cause);
	}

	public static void addMessage(Integer key, String msg) {
		_msgMap.put(key, msg);
	}
	public static void addMessage(HashMap<Integer, String> msgMap) {
		if (msgMap != null) {
			_msgMap.putAll(msgMap);
		}
	}

	/**
	 * 取得画面表示文言
	 *
	 * @param key 文言ID
	 *
	 * @return String 文言信息
	 */
	public static String getMessage(int key, String... params) {
		String errMsg = _msgMap.get(key);
		if (errMsg == null || errMsg.length() == 0) {
			if (params != null && params.length > 0) {
				return params[0];
			} else {
				return "";
			}
		}
		if (params == null || params.length == 0) {
			return errMsg;
		}
		MessageFormat form = new MessageFormat(errMsg);
		return form.format(params);
	}

}
