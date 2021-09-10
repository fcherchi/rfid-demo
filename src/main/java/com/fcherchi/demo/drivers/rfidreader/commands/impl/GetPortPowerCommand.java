/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.exception.DriverException;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.AntennaPortPower;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820ResultFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fcherchi.demo.drivers.rfidreader.commands.Command;

/**
 * Gets the power for the port.
 * @author Fernando
 *
 */
public class GetPortPowerCommand implements Command<AntennaPortPower> {

	// according to specs GET PORT POWER : 0x0006
	// response 0x8005

	public static final byte[] COMMAND_ID = { 0x05, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x05, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8005;
	private static GetPortPowerCommand instance;
	
	final Logger logger = (Logger) LoggerFactory.getLogger(GetPortPowerCommand.class);

	
	public static GetPortPowerCommand getInstance() {
		if (instance == null) {
			instance = new GetPortPowerCommand();
		}
		return instance;
	}
	
	private GetPortPowerCommand() {
		
	}
	
	/**
	 * @see Command#parseResponse(java.lang.String,
	 *      byte[])
	 */
	@Override
	public AntennaPortPower parseResponse(String readerId, byte[] value) {
		
		if (value.length != 3) {
			throw new DriverException(readerId, "Unexpected response from GetPortPower command.");
		}
		int resultFlag = value[0];
		if (resultFlag != DTE820ResultFlag.Values.RRUI4RESULTFLAG_NOERROR.getValue()) {
			throw new DriverException(readerId, "Error response from GetPortPower command. Result Flag: " + resultFlag);
		}
		AntennaPortPower res;
		try {
			int antennaPortNumber = value[1];
			int portPower = value[2];
		
			res = new AntennaPortPower(antennaPortNumber, portPower);
		} catch (Exception e) {
			throw new DriverException(readerId, "Error parsing response from GetPortPower command.", e);
		}
		
		return res;
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
