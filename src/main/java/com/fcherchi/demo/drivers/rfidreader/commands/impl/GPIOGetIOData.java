/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.gpio.IOData;
import com.fcherchi.demo.drivers.rfidreader.impl.ByteUtils;
import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Gets the Data of the GPIO
 * @author Fernando
 */
public class GPIOGetIOData implements Command<IOData> {

	// according to specs GPIO Get IO DATA : 0x008C
	// response 0x808C

	public static final byte[] COMMAND_ID = { (byte)0x8C, 0x00 };
	public static final byte[] RESPONSE_ID = { (byte)0x8C, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x808C;
	private static GPIOGetIOData instance;
	
	final Logger logger = (Logger) LoggerFactory.getLogger(GPIOGetIOData.class);

	public static GPIOGetIOData getInstance() {
		if (instance == null) {
			instance = new GPIOGetIOData();
		}
		return instance;
	}
	
	private GPIOGetIOData() {
		
	}

    /**
     * Returns the params to be used in the invoke given a card number.
     * @param cardNumber
     * @return
     */
	public byte[] getParamsForCalling(int cardNumber) {
		byte[] res = new byte[]{(byte) cardNumber};
		return res;
	}
	
	@Override
	public IOData parseResponse(String readerId, byte[] value) {

		// intentionally removed
		return null;
	}

    /**
     * Converts the value to an integer.
     * @param value
     * @param startIndex
     * @param endIndex
     * @return
     */
	private int convertValue(byte[] value, int startIndex, int endIndex) {
		byte[] subSet = Arrays.copyOfRange(value, startIndex, endIndex);
		int res = ByteUtils.getShort(subSet);
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
