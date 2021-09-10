/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;
import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets the response of the Get EPC Async. Response is a TagReport.
 * @author Fernando
 *
 */
public class GetEPCAsyncResponse implements Command<TagReport> {

	public static final byte[] RESPONSE_ID = { 0x11, (byte) 0xC1 };
	public static final short RESPONSE_AS_SHORT = (short) 0xC111;
	private static GetEPCAsyncResponse instance;

	final Logger logger = (Logger) LoggerFactory.getLogger(GetEPCAsyncResponse.class);

	
	public static GetEPCAsyncResponse getInstance() {
		if (instance == null) {
			instance = new GetEPCAsyncResponse();
		}
		return instance;
	}
	
	private GetEPCAsyncResponse() {
		
	}
	
	@Override
	public TagReport parseResponse(String readerId, byte[] value) {
		// intentionally removed
		return null;
	}

	@Override
	public short getResponseAsShort() {
		return RESPONSE_AS_SHORT;
	}

	@Override
	public byte[] getCommand() {
		return null;
	}
	
}
