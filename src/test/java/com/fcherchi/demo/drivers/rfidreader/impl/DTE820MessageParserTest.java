package com.fcherchi.demo.drivers.rfidreader.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.fcherchi.demo.drivers.rfidreader.impl.ByteUtils;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820MessageParser;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import com.fcherchi.demo.drivers.exception.DriverException;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetEPCAsyncResponse;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetTimeCommand;

public class DTE820MessageParserTest {

	private DTE820MessageParser msg = new DTE820MessageParser("R1");

	@Test
	public void testParseMessage() throws Exception {

		// response of the reader to the command getTime is something like
		// AA BB 01 01 26 80 EE 4C 6E 38 AA CC

		byte[] data = { (byte) 0xEE, 0x4C, 0x6E, 0x38 };
		byte[] fullFrame = getWholeFrameArrayForGetDateResponse();

		this.msg.parseMessage(fullFrame);

		Assert.assertArrayEquals(data, msg.getData());
		Assert.assertEquals(ByteUtils.getShort(GetTimeCommand.RESPONSE_ID), msg.getResponseId());

	}

	@Test
	public void testParseMessageEPC() throws Exception {

		byte[] data = { (byte) 0xFF, (byte) 0x07, (byte) 0x01, (byte) 0x36, (byte) 0x0E, (byte) 0x19, (byte) 0xD7, (byte) 0x56, (byte) 0x06, (byte) 0x00,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x01, (byte) 0xD9, (byte) 0xDD, (byte) 0xB2, (byte) 0x33, (byte) 0x08, (byte) 0x30 };
		byte[] fullFrame = getWholeFrameArrayForGetEPC(data);

		this.msg.parseMessage(fullFrame);

		Assert.assertArrayEquals(data, msg.getData());
		Assert.assertEquals(ByteUtils.getShort(GetEPCAsyncResponse.RESPONSE_ID), msg.getResponseId());

	}

	@Test
	public void testParseMessageEPCWithEscapeBytes() throws Exception {

		// response of the reader to the command getTime is something like
		// AA BB 01 01 26 80 EE 4C 6E 38 AA CC

		byte[] data = { (byte) 0xFF, (byte) 0x07, (byte) 0x01, (byte) 0x38, (byte) 0xAA, (byte) 0xAA, (byte) 0x15, (byte) 0xD7, (byte) 0x56, (byte) 0x06,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x01, (byte) 0xD9, (byte) 0xDD, (byte) 0xB2, (byte) 0x33, (byte) 0x08,
				(byte) 0x30 };
		
		byte[] dataResult = { (byte) 0xFF, (byte) 0x07, (byte) 0x01, (byte) 0x38, (byte) 0xAA, (byte) 0x15, (byte) 0xD7, (byte) 0x56, (byte) 0x06,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x01, (byte) 0xD9, (byte) 0xDD, (byte) 0xB2, (byte) 0x33, (byte) 0x08,
				(byte) 0x30 };
		
		byte[] fullFrame = getWholeFrameArrayForGetEPC(data);

		this.msg.parseMessage(fullFrame);

		Assert.assertArrayEquals(dataResult, msg.getData());
		Assert.assertEquals(ByteUtils.getShort(GetEPCAsyncResponse.RESPONSE_ID), msg.getResponseId());

	}

	// String unescaped = tagEpc.toString().replace("AAAA", "AA").trim();

	@Test
	public void testParseMessageWhenHasToEscape() throws Exception {

		// response of the reader to the command getTime is something like
		// AA BB 01 01 26 80 EE 4C 6E 38 AA CC

		// one AA has to be removed
		byte[] data = { (byte) 0xAA, 0x4C, 0x6E, 0x38 };
		byte[] fullFrame = getWholeFrameArrayForGetDateResponseWithDateToEscape();

		this.msg.parseMessage(fullFrame);

		Assert.assertArrayEquals(data, msg.getData());
		Assert.assertEquals(ByteUtils.getShort(GetTimeCommand.RESPONSE_ID), msg.getResponseId());

	}

	@Test
	public void testGetDatasWithUnclompleteStartFrame() throws Exception {

		// response of the reader is something like
		// AA (REMOVED!! -> BB) 01 01 26 80 EE 4C 6E 38 AA CC
		List<Byte> wholeFrameList = new LinkedList<Byte>(getWholeFrameListForGetDate());
		wholeFrameList.remove(1);
		byte[] wholeFrame = ArrayUtils.toPrimitive(wholeFrameList.toArray(new Byte[wholeFrameList.size()]));

		try {
			// TEST
			this.msg.parseMessage(wholeFrame);
			Assert.fail("Exception should have occurred due to uncomplete frame");
		} catch (DriverException e) {
			// expected
			Assert.assertTrue("Expected Exception occurred.", true);
		}
	}

	@Test
	public void testGetValuesWithUnclompleteEndFrame() throws Exception {

		// response of the reader is something like
		// AA BB 01 01 26 80 EE 4C 6E 38 (Removed! -> AA) CC
		List<Byte> wholeFrameList = new LinkedList<Byte>(getWholeFrameListForGetDate());
		wholeFrameList.remove(10);
		byte[] wholeFrame = ArrayUtils.toPrimitive(wholeFrameList.toArray(new Byte[wholeFrameList.size()]));

		try {
			// TEST
			this.msg.parseMessage(wholeFrame);
			Assert.fail("Exception should have occurred due to uncomplete frame");
		} catch (DriverException e) {
			// expected
			Assert.assertTrue("Expected Exception occurred.", true);
		}
	}

	@Test
	public void testGetFullFrameCommand() throws Exception {
		byte[] fullFrame = this.msg.getFullFrameCommand(GetTimeCommand.COMMAND_ID);
		Assert.assertArrayEquals(getWholeFrameArrayForGetDateCommand(), fullFrame);

		try {
			// test that an uncomplete command leads to an exception
			this.msg.getFullFrameCommand(new byte[] { 0x04 });
			Assert.fail("Exception was expected.");
		} catch (DriverException e) {
			// expected
		}

	}

	private List<Byte> getWholeFrameListForGetDate() {

		byte[] wholeFrame = getWholeFrameArrayForGetDateResponse();
		List<Byte> wholeFrameList = Arrays.asList(ArrayUtils.toObject(wholeFrame));

		return wholeFrameList;
	}

	private byte[] getWholeFrameArrayForGetDateResponse() {
		byte[] start = DTE820MessageParser.START_TOKEN;
		byte[] cmdResponse = GetTimeCommand.RESPONSE_ID;
		byte[] values = { (byte) 0xEE, 0x4C, 0x6E, 0x38 };
		byte[] end = DTE820MessageParser.END_TOKEN;

		return ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(start, cmdResponse), values), end);
	}

	private byte[] getWholeFrameArrayForGetDateResponseWithDateToEscape() {
		byte[] start = DTE820MessageParser.START_TOKEN;
		byte[] cmdResponse = GetTimeCommand.RESPONSE_ID;
		byte[] values = { (byte) 0xAA, (byte) 0xAA, 0x4C, 0x6E, 0x38 };
		byte[] end = DTE820MessageParser.END_TOKEN;

		return ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(start, cmdResponse), values), end);
	}

	
	
	private byte[] getWholeFrameArrayForGetEPC(byte[] values) {

		byte[] start = DTE820MessageParser.START_TOKEN;
		byte[] cmdResponse = GetEPCAsyncResponse.RESPONSE_ID;
		// FF 07 01 36 0E 19 D7 56 06 00 00 00 00 40 01 D9 DD B2 33 08 30
		byte[] end = DTE820MessageParser.END_TOKEN;

		return ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(start, cmdResponse), values), end);
	}

	private byte[] getWholeFrameArrayForGetDateCommand() {
		byte[] start = DTE820MessageParser.START_TOKEN;
		byte[] cmdResponse = GetTimeCommand.COMMAND_ID;
		byte[] end = DTE820MessageParser.END_TOKEN;

		return ArrayUtils.addAll(ArrayUtils.addAll(start, cmdResponse), end);
	}

}
