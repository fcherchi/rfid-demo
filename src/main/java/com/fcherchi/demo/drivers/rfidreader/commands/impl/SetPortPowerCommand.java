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
 * Sets the power per port.
 * @author Fernando
 */
public class SetPortPowerCommand implements Command<Byte> {

	// according to specs SET PORT POWER : 0x0006
	// response 0x8006

	public static final byte[] COMMAND_ID = { 0x06, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x06, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8006;
	private static SetPortPowerCommand instance;
	
	final Logger logger = (Logger) LoggerFactory.getLogger(SetPortPowerCommand.class);

	
	public static SetPortPowerCommand getInstance() {
		if (instance == null) {
			instance = new SetPortPowerCommand();
		}
		return instance;
	}
	
	private SetPortPowerCommand() {
		
	}
	
	@Override
	public Byte parseResponse(String readerId, byte[] value) {
		
		if (value.length != 2) {
			throw new DriverException(readerId, "Unexpected response from SetPortPower command.");
		}
		byte resultFlag = value[0];
		if (resultFlag != DTE820ResultFlag.Values.RRUI4RESULTFLAG_NOERROR.getValue()) {
			throw new DriverException(readerId, "Error response from SetPortPower command. Result Flag: " + resultFlag);
		}
		
		return resultFlag;
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
