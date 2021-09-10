/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import org.apache.commons.lang.NotImplementedException;

/**
 * Type of tag reports.
 * It is a configuration that defines the information sent by the reader during a tag read event.
 *
 * So far, only 0TRA is supported.
 *
 * @author Fernando
 *
 */
public class TagReportType {
	
	
	/*
	 * From the manual
	 * 
	 * // Explanation of the codes
// --------------------
// A - Antenna port
// R - RSSI value
// T - Time stamp
// PC - Tag protocol control word including where necessary XPC_W1 and XPC_W2
typedef enum
{
RRUI4ERF_0000 = 0, // no transmission of PC, T, R or A
RRUI4ERF_000A, // A is transmitted
RRUI4ERF_00R0, // R is transmitted
RRUI4ERF_00RA, // R and A are transmitted
RRUI4ERF_0T00, // T is transmitted
RRUI4ERF_0T0A, // T and A are transmitted
RRUI4ERF_0TR0, // T and R are transmitted
RRUI4ERF_0TRA, // T, R and A are transmitted
RRUI4ERF_P000, // PC is transmitted
RRUI4ERF_P00A, // PC and A are transmitted
RRUI4ERF_P0R0, // PC and R are transmitted
RRUI4ERF_P0RA, // PC, R and A are transmitted
RRUI4ERF_PT00, // PC and T are transmitted
RRUI4ERF_PT0A, // PC, T and A are transmitted
RRUI4ERF_PTR0, // PC, T and R are transmitted
RRUI4ERF_PTRA, // PC, T, R and A are transmitted
RRUI4ERF_LAST
} tRRUI4ExtendedResultFlag;
	 * 
	 */
	
	
		// A - Antenna port
		// R - RSSI value
		// T - Time stamp

	
	public static final int RF_0000 = 0; // no transmission of PC, T, R or A
	public static final int RF_000A = 1; // A is transmitted
	public static final int RF_00R0 = 2; // R is transmitted
	public static final int RF_00RA = 3; // R and A are transmitted
	public static final int RF_0T00 = 4; // T is transmitted
	public static final int RF_0T0A = 5; // T and A are transmitted
	public static final int RF_0TR0 = 6; // T and R
	public static final int RF_0TRA = 7; // T, R and A are transmitted

	//default values (matches the RF_0TRA configuration)
	private int comingGoingFlagPosition = 0;
	private int extededResultFlagPosition = 1;
	private int antennaPortPotision = 2;
	private int rssiValuePosition = 3;
	private int timestampValuePosition = 4;
	private int numberOfEPCWordsPosition = 8;
	private int EPCposition = 9;

	private TagReportType() {
		
	}
	
	public static TagReportType create(int extendedFlag) {

		TagReportType type;
		switch (extendedFlag) {
			case RF_0TRA:
				type = new TagReportType();
				break;
			default:
				throw new NotImplementedException("Not yet implemented, only extended flag 7 is supported");
		}
		return type;
	}

	/**
	 * @return the comingGoingFlagPosition
	 */
	public int getComingGoingFlagPosition() {
		return comingGoingFlagPosition;
	}

	/**
	 * @return the extededResultFlagPosition
	 */
	public int getExtededResultFlagPosition() {
		return extededResultFlagPosition;
	}

	/**
	 * @return the antennaPortPotision
	 */
	public int getAntennaPortPosition() {
		return antennaPortPotision;
	}

	/**
	 * @return the rssiValuePosition
	 */
	public int getRssiPosition() {
		return rssiValuePosition;
	}

	/**
	 * @return the timestampValuePosition
	 */
	public int getTimestampPosition() {
		return timestampValuePosition;
	}

	/**
	 * @return the numberOfEPCWordsPosition
	 */
	public int getNumberOfEPCWordsPosition() {
		return numberOfEPCWordsPosition;
	}

	/**
	 * @return the ePCposition
	 */
	public int getEPCposition() {
		return EPCposition;
	}
}
