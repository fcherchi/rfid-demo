/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.AntennaGain;
import com.fcherchi.demo.drivers.rfidreader.commands.Command;

/**
 * Get for Antanna gain value.
 * @author Fernando
 */
public class GetCableLossAndAntennaGain implements Command<AntennaGain> {
	// according to specs : 0x000D
	// response 0x800D

	public static final byte[] COMMAND_ID = { 0x0D, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x0D, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x800D;

	private static GetCableLossAndAntennaGain instance;

	public static GetCableLossAndAntennaGain getInstance() {
		if (instance == null) {
			instance = new GetCableLossAndAntennaGain();
		}
		return instance;
	}

	private GetCableLossAndAntennaGain() {
		
	}

	@Override
	public AntennaGain parseResponse(String readerId, byte[] value) {
		//intentionally removed
		
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
