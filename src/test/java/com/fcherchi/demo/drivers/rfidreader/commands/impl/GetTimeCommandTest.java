package com.fcherchi.demo.drivers.rfidreader.commands.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import org.junit.Assert;
import org.junit.Test;

import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetTimeCommand;

public class GetTimeCommandTest {

	@Test
	public void testParseResponse() throws Exception {
		GetTimeCommand cmd = GetTimeCommand.getInstance();
		
		//AA BB 01 01 26 80 DC 2F 4B 56 AA CC 
		byte[] values = { (byte) 0xDC, 0x2F, 0x4B, 0x56 };
		
		Instant i = cmd.parseResponse("r1", values);
		LocalDateTime ldt = LocalDateTime.ofInstant(i, ZoneId.of("UTC"));
		LocalDateTime expected = LocalDateTime.of(2015, Month.NOVEMBER, 17, 13, 47, 8);
		Assert.assertEquals(expected, ldt);
		
	}
	


}
