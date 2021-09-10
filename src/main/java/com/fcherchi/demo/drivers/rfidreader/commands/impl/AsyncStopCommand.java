/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.exception.DriverException;
import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820ResultFlag;

/**
 * Stops the async read operation of the reader.
 * @author Fernando
 */
public class AsyncStopCommand implements Command<Byte>{

	//according to specs 
	//RRUI4CMD_ASyncStopCommand, // 0x0118
	//RRUI4RES_ASyncStopCommand, // 0x8118
	
	public static final byte[] COMMAND_ID = {0x18, 0x01};
	public static final byte[] RESPONSE_ID = {0x18, (byte)0x81};
	public static final short RESPONSE_AS_SHORT = (short) 0x8118;
	
	private static AsyncStopCommand instance;

	public static AsyncStopCommand getInstance() {
		if (instance == null) {
			instance = new AsyncStopCommand();
		}
		return instance;
	}
	
	private AsyncStopCommand() {
		
	}

	@Override
	public Byte parseResponse(String readerId, byte[] value) {
		if (value.length != 1) {
			throw new IllegalArgumentException("Unexpected response size " + value.length);
		}
		
		byte val = value[0];
		if (DTE820ResultFlag.Values.RRUI4RESULTFLAG_NOERROR.getValue() != val) {
			throw new DriverException(readerId, "Error stopping async command.");
		}
		return val;
	}

	@Override
	public short getResponseAsShort() {

		return RESPONSE_AS_SHORT;
	}

	@Override
	public byte[] getCommand() {
		return COMMAND_ID;
	}

}
