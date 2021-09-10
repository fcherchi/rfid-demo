/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.exception.DriverException;
import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import com.fcherchi.demo.drivers.rfidreader.impl.ByteUtils;

/**
 * Command to retrieve the internal temperature of the reader.
 * @author Fernando
 */
public class GetTemperatureCommand implements Command<Integer> {

	final Logger logger = (Logger) LoggerFactory.getLogger(GetTemperatureCommand.class);
	
	// according to specs GET TIME: 0x0026
	// response 0x8026

	public static final byte[] COMMAND_ID = { 0x28, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x28, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8028;
	private static GetTemperatureCommand instance;
	
	public static GetTemperatureCommand getInstance() {
		if (instance == null) {
			instance = new GetTemperatureCommand();
		}
		return instance;
	}
	
	private GetTemperatureCommand() {
		
	}
	
    @Override
	public Integer parseResponse(String readerId, byte[] value) {
		
		if (value == null) {
			throw new NullArgumentException(readerId + " - Error parsing response of GetTemperature command. Data is null.");
		}
		
		if (value.length != 2) {
			throw new DriverException(readerId, "Error parsing response of GetTemperature command. 2 bytes were expected. Found " + value.length + " bytes");
		}
		try {
			int temp = ByteUtils.getShort(value);
			//temp is returned with a precision of 1/100
			return temp / 100;
		} catch (Exception e) {
			logger.error("{} - Error parsing Get Temperature response. {}", readerId, ByteUtils.getHexString(value));
		}
		return null;
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
