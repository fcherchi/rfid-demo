/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import com.fcherchi.demo.drivers.rfidreader.impl.ByteUtils;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820Conversion;

/**
 * Command to get the time of the reader.
 * @author Fernando
 */
public class GetTimeCommand implements Command<Instant> {

	final Logger logger = (Logger) LoggerFactory.getLogger(GetTimeCommand.class);
	// according to specs GET TIME: 0x0026
	// response 0x8026

	public static final byte[] COMMAND_ID = { 0x26, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x26, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8026;
	private static GetTimeCommand instance;
	
	public static GetTimeCommand getInstance() {
		if (instance == null) {
			instance = new GetTimeCommand();
		}
		return instance;
	}

	private GetTimeCommand() {
		
	}
	
	@Override
	public Instant parseResponse(String readerId, byte[] value) {
		
		int seconds = ByteUtils.getInt(value);
		return DTE820Conversion.getDatetime(seconds);
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
