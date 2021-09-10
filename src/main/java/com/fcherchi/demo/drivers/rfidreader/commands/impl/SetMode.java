/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.exception.DriverException;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820ResultFlag;
import com.fcherchi.demo.drivers.rfidreader.commands.Command;

/**
 * Sets the mode that defines the way the reader transmits the tags
 * @author Fernando
 */
public class SetMode implements Command<Byte> {

	public static final byte[] COMMAND_ID = { 0x02, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x02, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8002;

    private static SetMode instance;

    /**
     * Gets this command instance
     * @return
     */
	public static SetMode getInstance() {
		if (instance == null) {
			instance = new SetMode();
		}
		return instance;
	}

	private SetMode() {
		//empty. Don't want direct instantiation
	}

	@Override
	public Byte parseResponse(String readerId, byte[] value) {
		byte val = value[0];
		if (DTE820ResultFlag.Values.RRUI4RESULTFLAG_NOERROR.getValue() != val) {
			throw new DriverException(readerId, "Error sending SetMode command. Result Flag: " + val);
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
