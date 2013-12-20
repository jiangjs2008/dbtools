/**
*
* Copyright c JiangJusheng 2003 All Rights Reserved.
*
* WarningException.java
*
*/

package com.dbm.client.error;




public class WarningException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 构造函数
	 *
	 * @param message 
	 */
	public WarningException(String message) {
		super(message);
	}

	/**
	 * 构造函数
	 *
	 * @param errorNumber 
	 * @param message 
	 */
	public WarningException(int errorNumber, String... message) {
		super(errorNumber, message);
	}

}
