/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands;

/**
 * Abstraction of a command.
 * The commands are sent using the command
 * @author Fernando
 *
 */
public interface Command<T> {
	/**
	 * Parses the value part of the response and returns it as a typed object
	 * @param readerId 
	 * @param value
	 * @return
	 */
	T parseResponse(String readerId, byte[] value);
	
	/**
	 * Returns the response code as a short value
	 * @return
	 */
	short getResponseAsShort();
	
	/**
	 * Returns the response code as a short value
	 * @return
	 */
	byte[] getCommand();
}
