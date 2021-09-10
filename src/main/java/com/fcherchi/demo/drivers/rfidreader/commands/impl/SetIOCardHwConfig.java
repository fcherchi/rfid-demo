/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.exception.DriverException;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820ResultFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fcherchi.demo.drivers.rfidreader.commands.Command;

/**
 * Configuration of IO Type, see reader manual for more details.
 * @author Fernando
 *
 */
public class SetIOCardHwConfig implements Command<Byte> {

	public static final byte[] COMMAND_ID = { 0x23, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x23, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8023;

	private static SetIOCardHwConfig instance;

	final Logger logger = (Logger) LoggerFactory.getLogger(GetEPCAsyncResponse.class);

	public static SetIOCardHwConfig getInstance() {
		if (instance == null) {
			instance = new SetIOCardHwConfig();
		}
		return instance;
	}

	private SetIOCardHwConfig() {

	}

	@Override
	public Byte parseResponse(String readerId, byte[] value) {
		byte val = value[0];
		if (DTE820ResultFlag.Values.RRUI4RESULTFLAG_NOERROR.getValue() != val) {
			throw new DriverException(readerId, "Error sending GetIOCardHwConfig command. Result Flag: " + val);
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

	public byte[] getParameterBytes(byte cardType) {
		// intentionally removed
		return null;
	}
}
