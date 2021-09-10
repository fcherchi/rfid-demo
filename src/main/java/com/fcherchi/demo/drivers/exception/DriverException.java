/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.exception;

/**
 * @author Fernando
 *
 */
public class DriverException extends RuntimeException {

	private static final long serialVersionUID = 3033533890725257556L;
	
	public DriverException(String readerId, String message, Exception inner) {
		super(String.format("%s - %s", readerId, message), inner);
	}
	
	public DriverException(String readerId, String message) {
		super(String.format("%s - %s", readerId, message));
	}
}
