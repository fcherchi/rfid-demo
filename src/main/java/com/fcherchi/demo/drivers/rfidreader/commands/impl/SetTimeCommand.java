/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import java.time.Instant;

import com.fcherchi.demo.drivers.exception.DriverException;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820Conversion;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820ResultFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fcherchi.demo.drivers.rfidreader.commands.Command;

/**
 * Sets the time of the reader.
 * @author Fernando
 *
 */
public class SetTimeCommand implements Command<Byte> {

	final Logger logger = (Logger) LoggerFactory.getLogger(SetTimeCommand.class);
	// according to specs GET TIME: 0x0027
	// response 0x8027

	public static final byte[] COMMAND_ID = { 0x27, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x27, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8027;
	private static SetTimeCommand instance;
	
	public static SetTimeCommand getInstance() {
		if (instance == null) {
			instance = new SetTimeCommand();
		}
		return instance;
	}

	private SetTimeCommand() {
		
	}
	
	@Override
	public Byte parseResponse(String readerId, byte[] value) {
		
		byte val = value[0];
		if (DTE820ResultFlag.Values.RRUI4RESULTFLAG_NOERROR.getValue() != val) {
			throw new DriverException(readerId, "Error sending SetTime command. Result Flag: " + val);
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

	public byte[] getParameterBytes(Instant now) {
		return DTE820Conversion.getBytes(now);
	}
	
}
