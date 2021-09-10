/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import java.util.Arrays;

import com.fcherchi.demo.drivers.exception.DriverException;
import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import com.fcherchi.demo.drivers.rfidreader.impl.ByteUtils;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820ResultFlag;

/**
 * Gets the value of the given parameter.
 * @author Fernando
 */
public class GetParameterById implements Command<Integer> {

	public static final byte[] COMMAND_ID = { 0x20, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x20, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8020;
	private static GetParameterById instance;

	public static GetParameterById getInstance() {
		if (instance == null) {
			instance = new GetParameterById();
		}
		return instance;
	}
	
	private GetParameterById() {
		
	}

	@Override
	public Integer parseResponse(String readerId, byte[] value) {
		byte val = value[0];
		if (DTE820ResultFlag.Values.RRUI4RESULTFLAG_NOERROR.getValue() != val) {
			throw new DriverException(readerId, "Error sending GetParameterById command. Result Flag: " + val);
		}
		try {
			int res = ByteUtils.getInt(Arrays.copyOfRange(value, 5, 9));
			return res;
		} catch (Exception e) {
			throw new DriverException(readerId, "Error sending GetParameterById command.", e);
		}
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
