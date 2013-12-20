/**
*
* Copyright c JiangJusheng 2003 All Rights Reserved.
*
* ShinkinException.java
* 
*/
package com.dbm.client.error;

import com.dbm.client.property.PropUtil;


public class BaseException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private int errorNumber = 0;
	/**
	 * 
	 */
	private String[] errorMsgs = null;

	/**
	 * 构造函数
	 *
	 * @param message 
	 */
	public BaseException(String message) {
		super(message);
	}

	/**
	 * 构造函数
	 *
	 * @param message 
	 */
	public BaseException(int errorNumber, String... message) {
		this.errorMsgs = message;
		this.errorNumber = errorNumber;
	}

	@Override
	public String getMessage() {
		return PropUtil.getMessage(errorNumber, errorMsgs);
	}
}
