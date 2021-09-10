/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events.impl;

import java.time.Instant;

import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;

/**
 * @author Fernando
 *
 */
public class TagReportUtils {
	/**
	 * @param isComingMessage 
	 * @param count 
	 * @param rssi 
	 * @param tagEpc 
	 * @return
	 */
	private static TagReport createTagReport(boolean isComingMessage, int count, byte rssi, String tagEpc) {

		TagReport res = new TagReport();
		res.setAntennaPort((byte) 1);
		res.setComingMessage(isComingMessage);
		res.setCount(count);
		res.setRssi(rssi);
		res.setTagEpc(tagEpc);
		res.setTimestamp(Instant.now());
		
		return res;
	}
	
	/**
	 * @param epcTag
	 * @return
	 */
	public static TagReport createTagReportComing(String epcTag) {

		return TagReportUtils.createTagReport(true, 0, (byte)90, epcTag);
	}
	
	/**
	 * @param epcTag
	 * @return
	 */
	public static TagReport createTagReportGoing(String epcTag, int count) {

		return TagReportUtils.createTagReport(false, count, (byte)0, epcTag);
	}

}
