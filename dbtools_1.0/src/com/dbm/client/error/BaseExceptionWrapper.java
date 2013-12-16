/**
*
* Copyright c JiangJusheng 2003 All Rights Reserved.
*
* ShinkinException.java
* 
*/
package com.dbm.client.error;


public class BaseExceptionWrapper extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 *
	 * @param cause Throwable
	 */
	public BaseExceptionWrapper(Throwable cause) {
		super(cause);
	}

}
