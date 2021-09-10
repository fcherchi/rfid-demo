/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.rfidreader.commands.Command;

/**
 * Saves the parameter of the commands “SetCommStandard”, “SetPortPower”,
 * “SetCarrierFollowUpTime”, “SetPortMultiplexSequenceAndExposureTime”,
 * “SetCableLossAndAntennaGain”, “SetETSIPortChannelList”,
 * “SetETSIPortChannelSwitchingMode”, “SetProfile”, “SetModulationType”,
 * “SetExtResultFlag”, “SetSelSessionAndTarget”, “SetInitialQValue”,
 * “SetMaxAirCommErrors” and “SetASyncObservedListParameters” in the internal
 * EEPROM. The parameters saved are used again automatically after a re-start.
 *
 * @author Fernando
 *
 */
public class SaveConfigurationCommand implements Command<Byte> {
	// according to specs : 0x001F
	// response 0x801F
	
	public static final byte[] COMMAND_ID = { 0x1F, 0x00 };
	public static final byte[] RESPONSE_ID = { 0x1F, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x801F;
	
	private static SaveConfigurationCommand instance;
	
	public static SaveConfigurationCommand getInstance() {
		if (instance == null) {
			instance = new SaveConfigurationCommand();
		}
		return instance;
	}

	private SaveConfigurationCommand() {
		
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
