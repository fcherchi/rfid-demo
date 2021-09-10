/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Some conversions according to the reader's specs.
 * @author Fernando
 *
 */
public class DTE820Conversion {

    /**
     * Gets the Instant out of the seconds.
     * @param seconds
     * @return
     */
	public static Instant getDatetime (int seconds) {
		Instant epoch = Instant.EPOCH;
		Instant instant = epoch.plus(seconds, ChronoUnit.SECONDS);
		
		return instant;
	}

    /**
     * Gets the bytes given the instant. (Little Endian)
     * @param instant
     * @return
     */
	public static byte[] getBytes(Instant instant) {
		
		long seconds = instant.getEpochSecond();
		int secondInt = (int)seconds;
		
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(secondInt);
		byte[] array = buffer.array();
		
		return array;
	}
}
