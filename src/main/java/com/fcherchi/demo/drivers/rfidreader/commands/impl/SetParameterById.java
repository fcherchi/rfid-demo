/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.exception.DriverException;
import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820ResultFlag;

/**
 * Sets the parameter value given its param ID.
 * @author Fernando
 *
 */
public class SetParameterById implements Command<Byte> {

	// according to specs 0x0021
	// response 0x8021

	public static final byte[] COMMAND_ID = { 0x21, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x21, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8021;
	private static SetParameterById instance;

	public static SetParameterById getInstance() {
		if (instance == null) {
			instance = new SetParameterById();
		}
		return instance;
	}
	
	private SetParameterById() {
	}

	@Override
	public Byte parseResponse(String readerId, byte[] value) {
		byte val = value[0];
		if (DTE820ResultFlag.Values.RRUI4RESULTFLAG_NOERROR.getValue() != val) {
			throw new DriverException(readerId, "Error sending SetParameterById command. Result Flag: " + val);
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
