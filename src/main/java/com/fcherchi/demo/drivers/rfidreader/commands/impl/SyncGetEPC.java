/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;
import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a tag in synchronous mode. Used when GPIO trigger is configured.
 * @author Fernando
 *
 */
public class SyncGetEPC implements Command<TagReport> {

	// according to specs SyncGetEPC : 0x0101
	// response 0x8101

	public static final byte[] COMMAND_ID = { (byte)0x01, 0x01 };
	public static final byte[] RESPONSE_ID = { (byte)0x01, (byte) 0x81 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8101;
	private static SyncGetEPC instance;
	
	final Logger logger = (Logger) LoggerFactory.getLogger(SyncGetEPC.class);

	
	public static SyncGetEPC getInstance() {
		if (instance == null) {
			instance = new SyncGetEPC();
		}
		return instance;
	}
	
	private SyncGetEPC() {
		
	}
	

	@Override
	public TagReport parseResponse(String readerId, byte[] value) {

		// intentionally removed
		return null;
	}
	
	
	
	/**
	 * @see Command#getResponseAsShort()
	 */
	@Override
	public short getResponseAsShort() {

		return RESPONSE_AS_SHORT;
	}
	/**
	 * @see Command#getCommand()
	 */
	@Override
	public byte[] getCommand() {
		return COMMAND_ID;
	}
	
}
