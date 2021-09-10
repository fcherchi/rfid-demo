/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.exception.DriverException;
import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820ResultFlag;

public class SetProfile implements Command<Byte> {

	// according to specs 0x0015
	// response 0x8015

	public static final byte[] COMMAND_ID = { 0x15, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x15, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8015;
	private static SetProfile instance;

	public static SetProfile getInstance() {
		if (instance == null) {
			instance = new SetProfile();
		}
		return instance;
	}
	
	private SetProfile() {
	}

	public Byte parseResponse(String readerId, byte[] value) {
		byte val = value[0];
		if (DTE820ResultFlag.Values.RRUI4RESULTFLAG_NOERROR.getValue() != val) {
			throw new DriverException(readerId, "Error sending SetProfile command. Result Flag: " + val);
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
