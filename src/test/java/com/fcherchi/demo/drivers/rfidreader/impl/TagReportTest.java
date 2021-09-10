package com.fcherchi.demo.drivers.rfidreader.impl;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagReportTest {

	/** The logger */
	final Logger logger = (Logger) LoggerFactory.getLogger(TagReportTest.class);

	@Test
	public void testFromDataComing() throws Exception {
		byte[] data = new byte[] { (byte) 0xFF, (byte) TagReportType.RF_0TRA, (byte) 0x01, (byte) 0x5B, (byte) 0x32, (byte) 0xEB, (byte) 0x52, (byte) 0x56,
				(byte) 0x06, (byte) 0x20, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x24, (byte) 0x30 };

		// AA BB 01 01 11 C1 00 07 01 00 50 4F AA AA 56 06 03 00 00 00 00 A0 4C
		// A0 10 00 24 30 19 00 AA CC

		TagReport tag = TagReport.fromData("R1", data);

		String tagEpcExpected = "302400000000028000000120";
		Assert.assertEquals(tagEpcExpected, tag.getTagEpc());
	}

	@Test
	public void testFromDataGoing() throws Exception {

		byte[] data = new byte[] { (byte) 0x00, (byte) TagReportType.RF_0TRA, (byte) 0x01, (byte) 0x5B, (byte) 0x32, (byte) 0xEB, (byte) 0x52, (byte) 0x56,
				(byte) 0x06, (byte) 0x21, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x24, (byte) 0x30, (byte) 0xFF, (byte) 0xFF };

//		byte[]  data = new byte[] { (byte) 0x00, (byte) 0x07, (byte) 0x01, (byte) 0x00, (byte) 0x50, (byte) 0x4F, (byte) 0xAA, (byte) 0xAA, (byte) 0x56, (byte) 0x06,
//				(byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xA0, (byte) 0x4C, (byte) 0xA0, (byte) 0x10, (byte) 0x00, (byte) 0x24,
//				(byte) 0x30, (byte) 0x19, (byte) 0x00 };

		// 00 07 01 00 50 4F AA AA 56 06 03 00 00 00 00 A0 4C A0 10 00 24 30 19
		// 00

		TagReport tag = TagReport.fromData("R1", data);

		String tagEpcExpected = "302400000000028000000121";
		Assert.assertEquals(tagEpcExpected, tag.getTagEpc());
		Assert.assertEquals(65535, tag.getCount());
		logger.info("Count: {}", tag.getCount());
	}

	
}
