/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.AntennaGain;

/**
 * Command to set antenna gain.
 * @author Fernando
 *
 */
public class SetCableLossAndAntennaGain implements Command<Byte> {

	// according to specs : 0x000E
	// response 0x800E

	public static final byte[] COMMAND_ID = {0x0E, 0x00};
	public static final byte[] RESPONSE_ID = {0x0E, (byte) 0x80};
	public static final short RESPONSE_AS_SHORT = (short) 0x800E;

	private static SetCableLossAndAntennaGain instance;

	public static SetCableLossAndAntennaGain getInstance() {
		if (instance == null) {
			instance = new SetCableLossAndAntennaGain();
		}
		return instance;
	}

	private SetCableLossAndAntennaGain() {

	}

	@Override
	public Byte parseResponse(String readerId, byte[] value) {
		// intentionally removed
		return null;
	}

	public byte[] getParameterBytes(AntennaGain antennaGain) {
		byte[] res = new byte[]{(byte) antennaGain.getAntennaPortNumber(), (byte) antennaGain.getCableLoss(), (byte) antennaGain.getAntennaGain()};
		return res;
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
