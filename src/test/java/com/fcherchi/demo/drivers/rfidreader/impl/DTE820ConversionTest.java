package com.fcherchi.demo.drivers.rfidreader.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;

import com.fcherchi.demo.drivers.rfidreader.impl.ByteUtils;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820Conversion;
import org.junit.Assert;
import org.junit.Test;

public class DTE820ConversionTest {

	@Test
	public void testToDate() {
		//AA BB 01 01 26 80 DC 2F 4B 56 AA CC 
		byte[] values = { (byte) 0xDC, 0x2F, 0x4B, 0x56 };
		int seconds = ByteUtils.getInt(values);
		
		LocalDateTime expected = LocalDateTime.of(2015, Month.NOVEMBER, 17, 13, 47, 8);
		
		Instant datetime = DTE820Conversion.getDatetime(seconds);
		LocalDateTime ldt = LocalDateTime.ofInstant(datetime, ZoneId.of("UTC"));
		Assert.assertEquals(expected, ldt);
	}
	
	@Test
	public void testToBytes() {
		
		byte[] expected = { (byte) 0xDC, 0x2F, 0x4B, 0x56 };
		LocalDateTime dateTime = LocalDateTime.of(2015, Month.NOVEMBER, 17, 13, 47, 8);
		byte[] actual = DTE820Conversion.getBytes(dateTime.toInstant(ZoneOffset.UTC));
		
		Assert.assertArrayEquals(expected, actual);
	}
}
