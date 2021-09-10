/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import com.fcherchi.demo.drivers.exception.DriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;

/**
 * Contains the information of the tag just read.
 * @author Fernando
 */
public class TagReport {

	final static Logger logger = (Logger) LoggerFactory.getLogger(TagReport.class);

	private boolean isComingMessage;

	private int extendedResultFlag;

	private int antennaPort;

	private int rssi;

	private Instant timestamp;

	private String tagEpc;

	private int count;

	/* example of tag full fame:
	 * 
	 * 11 C1 FF 01 01 06 1B 01 00 00 80 02 00 00 00 00 24 30 AA CC I1 I2 CG EF
	 * AP RS [ ----------- epc code ----- -][END]
	 * 
	 * 
	 * if epc code contains AA it will be sent as AA AA!
	 *
	 * 
	 */

    /**
     * Constructs a tag report given a frame response of a synchronous tag read operation.
     * @param readerId
     * @param data
     * @return
     */
	public static TagReport fromSyncData(String readerId, byte[] data) {
		TagReport report = new TagReport();
		try {
			TagReportType type = TagReportType.create(TagReportType.RF_0TRA);
			fillCommonData(readerId, data, report, type);
			
			int epcWordCount = getInt(data[type.getNumberOfEPCWordsPosition()]);
			
			report.tagEpc = parseEPC(Arrays.copyOfRange(data, type.getEPCposition(), data.length), epcWordCount, readerId);
			
		} catch (DriverException e) {
			throw e;
		} catch (Exception e) {
			logger.debug("Error parsing tag information. {}", ByteUtils.getHexString(data));
		}
		
		return report;
	}
	
	/**
     * Returns a TagReport given a frame of data as a response of the
     * async tag read operation.
	 * @param data
	 * @return
	 */
	public static TagReport fromData(String readerId, byte[] data) {
		
		TagReport report = new TagReport();
		try {
			TagReportType type = TagReportType.create(TagReportType.RF_0TRA);
			fillCommonData(readerId, data, report, type);
			
			report.isComingMessage = data[type.getComingGoingFlagPosition()] == (byte)0xFF;
			int epcWordCount = getInt(data[type.getNumberOfEPCWordsPosition()]);
			
			if (report.isComingMessage) {
				report.tagEpc = parseEPC(Arrays.copyOfRange(data, type.getEPCposition(), data.length), epcWordCount, readerId);
			} else {
				report.tagEpc = parseEPC(Arrays.copyOfRange(data, type.getEPCposition(), data.length - 2), epcWordCount, readerId);
				int shortValue = ByteUtils.getUnsignedShort(Arrays.copyOfRange(data, data.length-2, data.length));
				
				report.count = shortValue;
			}
		} catch (DriverException e) {
			throw e;
		} catch (Exception e) {
			logger.debug("Error parsing tag information. {}", ByteUtils.getHexString(data));
		}
		
		return report;
	}

	/**
     * Fills the pojo data.
	 * @param readerId
	 * @param data
	 * @param report
	 * @param type 
	 * @return
	 */
	private static void fillCommonData(String readerId, byte[] data, TagReport report, TagReportType type) {
		
		report.extendedResultFlag = getInt(data[type.getExtededResultFlagPosition()]);
		
		if (report.extendedResultFlag != TagReportType.RF_0TRA) {
			String msg = String.format("Type of report not supported. Please set Extended Result Flag to 7!. Current value of Result Flag: %d", 
					report.extendedResultFlag);
			throw new DriverException(msg, readerId);
		}
		report.antennaPort = getInt(data[type.getAntennaPortPosition()]);
		report.rssi = getInt(data[type.getRssiPosition()]);
		report.timestamp = DTE820Conversion.getDatetime(ByteUtils.getInt(Arrays.copyOfRange(data, type.getTimestampPosition(), type.getTimestampPosition() + 4)));
	}

	/**
     * Gets an int out of a byte.
	 * @param b
	 * @return
	 */
	private static int getInt(byte b) {
		if (b < 0) {
			return 256 + b;
		}
		return b;
	}

	/**
     * Parses the tag information (EPC code).
	 * @param epcData
	 * @param readerId 
	 * @param epcWordCount 
	 */
	private static String parseEPC(byte[] epcData, int epcWordCount, String readerId) {

		StringBuffer tagEpc = new StringBuffer(12);
		if (epcData != null) {
			for (int i = epcData.length - 1; i >= 0; i--) {
				tagEpc.append(String.format("%02X", epcData[i]));
			}
		}
		if (epcWordCount != tagEpc.length() / 4) {
			logger.warn("Unexpected EPC Words Count value coming from the reader!. Expected {}, received {}.", (tagEpc.length() / 4), epcWordCount);
		}
		
		return tagEpc.toString();
	}

	/**
	 * @return True if the tag has entered into the field of view. False if has exited the field of view.
	 */
	public boolean isComingMessage() {
		return isComingMessage;
	}

	/**
	 * @return the extendedResultFlag
	 */
	public int getExtendedResultFlag() {
		return extendedResultFlag;
	}

	/**
	 * @return the antennaPort
	 */
	public int getAntennaPort() {
		return antennaPort;
	}

	/**
	 * @return the rssi
	 */
	public int getRssi() {
		return rssi;
	}

	/**
	 * @return the timestamp
	 */
	public Instant getTimestamp() {
		return timestamp;
	}

	/**
	 * @param isComingMessage
	 *            the isComingMessage to set
	 */
	public void setComingMessage(boolean isComingMessage) {
		this.isComingMessage = isComingMessage;
	}

	/**
	 * @param extendedResultFlag
	 *            the extendedResultFlag to set
	 */
	public void setExtendedResultFlag(byte extendedResultFlag) {
		this.extendedResultFlag = extendedResultFlag;
	}

	/**
	 * @param antennaPort
	 *            the antennaPort to set
	 */
	public void setAntennaPort(byte antennaPort) {
		this.antennaPort = antennaPort;
	}

	/**
	 * @param rssi
	 *            the rssi to set
	 */
	public void setRssi(byte rssi) {
		this.rssi = rssi;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the logger
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * @return the tagEpc
	 */
	public String getTagEpc() {
		return tagEpc;
	}

	/**
	 * @param tagEpc
	 *            the tagEpc to set
	 */
	public void setTagEpc(String tagEpc) {
		this.tagEpc = tagEpc;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TagReport [antennaPort=" + antennaPort + ", rssi="
				+ rssi + ", timestamp=" + timestamp + ", tagEpc=" + tagEpc + ", count=" + count + "]";
	}

}
