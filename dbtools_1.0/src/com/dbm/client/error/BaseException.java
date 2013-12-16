/**
*
* Copyright c JiangJusheng 2003 All Rights Reserved.
*
* ShinkinException.java
* 
*/
package com.dbm.client.error;


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
	 * 
	 *
	 * @param cause Throwable
	 */
	public BaseException(Throwable cause) {
		super(cause);
	}

	/**
	 *
	 *
	 * @param message 
	 */
	public BaseException(String message) {
		super(message);
	}

	/**
	 *
	 *
	 * @param message 
	 */
	public BaseException(int errorNumber, String... message) {
		this.errorMsgs = message;
		this.errorNumber = errorNumber;
	}

}
