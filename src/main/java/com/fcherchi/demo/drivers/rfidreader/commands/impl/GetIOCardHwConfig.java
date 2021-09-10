/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets the IOCard HW config. The values are set in order to use the
 * GPIO trigger (when configuration useGPIO is true).
 * @author Fernando
 */
public class GetIOCardHwConfig implements Command<Integer> {

	
	public static final byte[] COMMAND_ID = { 0x22, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x22, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x8022;
	
	/** This is the value of Card Type by default (AUTO) */
	public static final int CARD_TYPE_AUTO = 0x00;
	
	/** This is the value of Card Type RFID (needed for using GPIO trigger) */
	public static final int CARD_TYPE_RFID = 0x07;
	
	private static GetIOCardHwConfig instance;

	final Logger logger = (Logger) LoggerFactory.getLogger(GetEPCAsyncResponse.class);

	
	public static GetIOCardHwConfig getInstance() {
		if (instance == null) {
			instance = new GetIOCardHwConfig();
		}
		return instance;
	}
	
	private GetIOCardHwConfig() {
		
	}
	
	@Override
	public Integer parseResponse(String readerId, byte[] value) {
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
