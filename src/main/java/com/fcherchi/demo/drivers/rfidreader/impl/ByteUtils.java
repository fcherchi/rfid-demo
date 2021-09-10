/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

/**
 * Common utils for bytes operations.
 * @author Fernando
 *
 */
public class ByteUtils {

    /**
     * Gets the integer given its bytes representation (Little Endian)
     *
     * @param bytes
     * @return
     */
    public static int getInt(byte[] bytes) {

        if (bytes.length != Integer.BYTES) {
            throw new IllegalArgumentException("4 bytes are expected");
        }

        //the reader works in little endian
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int res = buffer.getInt();
        return res;
    }

    /**
     * Gets the short representation of the received bytes (Little endian)
     *
     * @param bytes
     * @return
     */
    public static short getShort(byte[] bytes) {
        if (bytes.length != Short.BYTES) {
            throw new IllegalArgumentException("2 bytes are expected");
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        short res = buffer.getShort();
        return res;
    }

    /**
     * Receives 2 bytes to be treated as "unsigned short"
     *
     * @param bytes
     * @return the integer representation of an unsigned short
     */
    public static int getUnsignedShort(byte[] bytes) {
        if (bytes.length != Short.BYTES) {
            throw new IllegalArgumentException("2 bytes are expected");
        }
        byte[] zeroes = new byte[]{0, 0};
        byte[] fourBytes = ArrayUtils.addAll(bytes, zeroes);
        return getInt(fourBytes);
    }


    /**
     * For debug purposes is useful to see the Hex representation of a frame of bytes.
     *
     * @param frame bytes.
     * @return Hex representation of the received bytes.
     */
    public static String getHexString(byte[] frame) {

        String readable = "";
        for (byte b : frame) {
            readable += String.format("%02X ", b);
        }
        return readable;
    }

    /**
     * The reader escapes AA values as AAAA. This method returns unescaped values.
     *
     * @param fullFrame
     * @return
     */
    public static byte[] getBytesRemovingDoubleAA(byte[] fullFrame) {

        byte[] frame = Arrays.copyOf(fullFrame, fullFrame.length);

        //first retrieve all positions of double AA
        List<Integer> positions = new ArrayList<Integer>();

        for (int i = 0; i < frame.length - 1; i++) {
            //if current byte is 0xAA && next also,
            if (frame[i] == (byte) 0xAA && frame[i + 1] == (byte) 0xAA) {
                positions.add(i); //add index
                //skip next one (to prevent 4 AAs to become 1
                i++;
            }
        }
        if (positions.size() > 0) {
            for (Integer position : positions) {
                frame = removeByteAtPosition(frame, position);
            }
        }
        return frame;
    }

    /**
     * Removes the byte at the given position
     *
     * @param frame
     * @param position
     * @return
     */
    private static byte[] removeByteAtPosition(byte[] frame, int position) {
        if (position != -1) {

            byte[] begin = Arrays.copyOf(frame, position);
            byte[] end = null;

            if (position < frame.length - 2) {
                end = Arrays.copyOfRange(frame, position + 1, frame.length);
            }
            if (end != null) {
                frame = ArrayUtils.addAll(begin, end);
            } else {
                frame = begin;
            }
        }
        return frame;
    }
}
