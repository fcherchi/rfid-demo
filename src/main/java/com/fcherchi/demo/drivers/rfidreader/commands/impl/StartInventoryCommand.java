/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts the async tag reading.
 * @author Fernando
 *
 */
public class StartInventoryCommand implements Command<Byte> {

	//according to specs RRUI4CMD_ASyncGetEPCs, // 0x0111
	//		RRUI4RES_ASyncGetEPCs, // 0x8111
	
	public static final byte[] COMMAND_ID = {0x11, 0x01};
	public static final byte[] RESPONSE_ID = {0x11, (byte)0x81};
	public static final short RESPONSE_AS_SHORT = (short) 0x8111;
	private static StartInventoryCommand instance;
	
	final Logger logger = (Logger) LoggerFactory.getLogger(StartInventoryCommand.class);
	
	public static StartInventoryCommand getInstance() {
		if (instance == null) {
			instance = new StartInventoryCommand();
		}
		return instance;
	}
	
	private StartInventoryCommand() {
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
