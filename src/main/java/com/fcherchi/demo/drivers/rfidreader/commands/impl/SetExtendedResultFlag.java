/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.rfidreader.commands.Command;

/**
 * Sets the configuration for the mode the reader transmits the tags
 * @author Fernando
 *
 */
public class SetExtendedResultFlag implements Command<Byte> {

	// according to specs 0x0019
	// response 0x8019

	public static final byte[] COMMAND_ID = { 0x19, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x19, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8019;
	private static SetExtendedResultFlag instance;

	public static SetExtendedResultFlag getInstance() {
		if (instance == null) {
			instance = new SetExtendedResultFlag();
		}
		return instance;
	}
	
	private SetExtendedResultFlag() {
		
	}

	@Override
	public Byte parseResponse(String readerId, byte[] value) {
		// intentionally removed
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
