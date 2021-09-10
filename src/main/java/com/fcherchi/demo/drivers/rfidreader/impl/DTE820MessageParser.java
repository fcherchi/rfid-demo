/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.fcherchi.demo.drivers.exception.DriverException;

/**
 * This class holds a reader message exposing its different fields individually.
 *
 * @author Fernando
 */
public class DTE820MessageParser {

    // all commands to/from this reader starts with this frame
    public static final byte[] START_TOKEN = {(byte) 0xAA, (byte) 0xBB, 0x01, 0x01};

    // all commands to/from this reader ends with this frame
    public static final byte[] END_TOKEN = {(byte) 0xAA, (byte) 0xCC};

    // command id is always a 2 bytes frame
    private static final int COMMAND_ID_LENGTH = 2;

    /** Holds the response id (part of the response frame after a command has been sent) */
    private short responseId;

    /** Data part of the response */
    private byte[] data;

    /**
     * A way to identify the reader in the application and in the logs.
     */
    private String readerId;


    /**
     * Creates the parser.
     * @param readerId
     */
    public DTE820MessageParser(String readerId) {
        this.readerId = readerId;
    }


    /**
     * Gets the command with begin token and end token.
     *
     * @param command
     * @return
     */
    public byte[] getFullFrameCommand(byte[] command) {

        return getFullFrameCommand(command, null);
    }


    /**
     * Gets the full frame of a command with begin token and end token
     *
     * @param command
     * @param params
     * @return
     */
    public byte[] getFullFrameCommand(byte[] command, byte[] params) {
        if (command.length != 2) {
            throw new DriverException(readerId, "Expected command should be an array of 2 bytes.");
        }
        if (params != null) {
            return ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(START_TOKEN, command), params), END_TOKEN);
        } else {
            return ArrayUtils.addAll(ArrayUtils.addAll(START_TOKEN, command), END_TOKEN);
        }
    }

    /**
     * Parses the message as being sent by the reader.
     *
     * @param fullFrame
     */
    public void parseMessage(byte[] fullFrame) {

        // all validation done. Frame is complete. Just extract the parts
        if (fullFrame != null) {
            validate(this.readerId, fullFrame);
            this.data = ByteUtils.getBytesRemovingDoubleAA(getValuesPart(ArrayUtils.toObject(fullFrame)));
            this.responseId = getResponseId(fullFrame);
        }
    }

    /**
     * @param fullFrame
     * @return
     */
    private short getResponseId(byte[] fullFrame) {
        byte[] subSet = Arrays.copyOfRange(fullFrame, START_TOKEN.length, START_TOKEN.length + COMMAND_ID_LENGTH);
        return ByteUtils.getShort(subSet);
    }


    /**
     * @param fullFrame
     */
    private void validate(String readerId, byte[] fullFrame) {
        // checking that the frame contains all the elements
        int minSize = START_TOKEN.length + END_TOKEN.length + COMMAND_ID_LENGTH;
        if (fullFrame.length < minSize) {
            throw new DriverException(readerId, "Frame is not completed. " + ByteUtils.getHexString(fullFrame));
        }

        // frame should start with the start subframe + command id
        if (!startsWith(fullFrame, START_TOKEN)) {
            throw new DriverException(readerId, "Frame is not completed. " + ByteUtils.getHexString(fullFrame));
        }

        // frame should end with the start subframe
        if (!endsWith(fullFrame, END_TOKEN)) {
            throw new DriverException(readerId, "Frame is not completed. " + ByteUtils.getHexString(fullFrame));
        }
    }

    /**
     * @param fullFrame
     * @return
     */
    private byte[] getValuesPart(Byte[] fullFrame) {

        int paramsLength = START_TOKEN.length + END_TOKEN.length + COMMAND_ID_LENGTH;
        int valuesLength = fullFrame.length - paramsLength;
        int startIndex = START_TOKEN.length + COMMAND_ID_LENGTH;


        byte[] values = new byte[valuesLength];

        //copy values
        if (values.length > 0) {
            for (int fullI = startIndex, valI = 0; fullI < startIndex + valuesLength; fullI++, valI++) {
                values[valI] = fullFrame[fullI];
            }
        }

        // find all position to be escaped
        List<Integer> positionsToRemove = new ArrayList<Integer>();
        for (int i = 0; i < values.length - 2; i++) {
            if (values[i] == (byte) 0xAA && values[i + 1] == (byte) 0xAA) {
                positionsToRemove.add(i);
            }
        }


        return values;
    }

    /**
     * @param fullFrame
     * @param start
     */
    private boolean startsWith(byte[] fullFrame, byte[] start) {
        boolean res = true;

        if (fullFrame.length >= start.length) {
            for (int i = 0; i < start.length; i++) {
                if (start[i] != fullFrame[i]) {
                    // quick exit
                    return false;
                }
            }
        } else {
            res = false;
        }

        return res;
    }


    private boolean endsWith(byte[] fullFrame, byte[] end) {
        boolean res = true;

        if (fullFrame.length >= end.length) {
            for (int endI = 0, fullI = fullFrame.length - end.length; endI < end.length; endI++, fullI++) {
                if (end[endI] != fullFrame[fullI]) {
                    // quick exit
                    return false;
                }
            }
        } else {
            res = false;
        }

        return res;
    }


    /**
     * @return the responseId
     */
    public short getResponseId() {
        return responseId;
    }


    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }
}
