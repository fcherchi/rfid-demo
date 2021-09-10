package com.fcherchi.demo.drivers.rfidreader.impl;

import com.fcherchi.demo.drivers.rfidreader.impl.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

public class ByteUtilsTest {

	@Test
	public void testGetInt() throws Exception {
		
		//the reader works in little endiand (the least significant byte goes to the least significant position in the array)
		byte[] zero = { 0x00, 0x00, 0x00, 0x00 };
		compareArrayToInt (0, zero);
		
		byte[] _255 = {(byte)0xFF, 0x00, 0x00, 0x00}; 
		compareArrayToInt (255, _255);
		
		byte[] _65535 = {(byte)0xFF, (byte)0xFF, 0x00, 0x00}; 
		compareArrayToInt (65535, _65535);
		
		byte[] _65545 = {(byte)0x09, (byte)0x00, 0x01, 0x00}; 
		compareArrayToInt (65545, _65545);

	}
	
	
	private void compareArrayToInt (int expected, byte[] actual) {
		
		int res = ByteUtils.getInt(actual);
		Assert.assertEquals(expected, res);
	}


	@Test
	public void testGetBytesRemovingDoubleAA() throws Exception {
		byte[] fullFrame = {(byte)0xAA, (byte)0xAA, 0x00, 0x01};
		byte[] expectedFrame = {(byte)0xAA, 0x00, 0x01};
		
		testRemovalAA(expectedFrame, fullFrame);
		
		fullFrame = new byte[] {(byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xAA, 0x00, 0x01};
		expectedFrame = new byte[] {(byte)0xAA, (byte)0xAA, 0x00, 0x01};
		
		testRemovalAA(expectedFrame, fullFrame);
	}
	
	private void testRemovalAA(byte[] expected, byte[] fullFrame) {
		byte[] actual = ByteUtils.getBytesRemovingDoubleAA(fullFrame);
		Assert.assertArrayEquals(expected, actual);
	}

}
