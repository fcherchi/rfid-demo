/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import com.google.common.primitives.Bytes;
import com.fcherchi.demo.drivers.exception.DriverException;
import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820ResultFlag;

/**
 * Command to set the value in an output of the GPIO.
 * @author Fernando
 */
public class GPIOSetOutput implements Command<Byte> {

	// 0x008D
	public static final byte[] COMMAND_ID = { (byte) 0x8D, 0x00 };
	public static final byte[] RESPONSE_ID = { (byte) 0x8D, (byte) 0x80 };
	public static final short RESPONSE_AS_SHORT = (short) 0x808D;
	private static GPIOSetOutput instance;

	public static GPIOSetOutput getInstance() {
		if (instance == null) {
			instance = new GPIOSetOutput();
		}
		return instance;
	}

	private GPIOSetOutput() {
		
	}

	@Override
	public Byte parseResponse(String readerId, byte[] value) {

		int resultFlag = value[0];
		if (resultFlag != DTE820ResultFlag.Values.RRUI4RESULTFLAG_NOERROR.getValue()) {
			throw new DriverException(readerId, "Error response from GPIOSetOutput command. Result Flag: " + resultFlag);
		}

		return (byte) resultFlag;
	}

	@Override
	public short getResponseAsShort() {
		return RESPONSE_AS_SHORT;
	}

	@Override
	public byte[] getCommand() {
		return COMMAND_ID;
	}
	
	/**
	 * Gets the bytes to pass as parameter for this method
	 * @param cardNumber From 0 to 15. normally 0
	 * @param outputNumber Zero based output number
	 * @param isOn If true the output will be set to ON, otherwise to OFF
	 * @return The bytes ready to be used as parameter
	 */
	public byte[] getParameterBytes(int cardNumber,  int outputNumber, boolean isOn) {
		
		byte[] onOff;
		
		if (isOn) {
			onOff = new byte[] {(byte) 0xFF, (byte) 0xFF};
		} else {
			onOff = new byte[] {(byte) 0x00, (byte) 0x00};
		}
		byte[] params = new byte[] { (byte) cardNumber, (byte) outputNumber};
		return Bytes.concat(params, onOff);
	}
}
