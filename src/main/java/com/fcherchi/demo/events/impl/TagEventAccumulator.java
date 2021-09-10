/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events.impl;

import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;
import com.fcherchi.demo.events.ExtendedTagReport;

/**
 * @author Fernando
 *
 */
public interface TagEventAccumulator {

	/** When a tag arrives, is added to the accumulator */
	void addReadTag(String readerId, TagReport tagReport);

	/** Get the best reads of all readers 
	 * @param isFillingStation 
	 * @param readerId */
	ExtendedTagReport getBestReport (String tagEpc, String readerId, boolean isFillingStation);
	
	/** After an event has been generated, it makes no sense to 
	 * keep the tag in memory for future competitions, call this method 
	 * to remove the tag from memory */
	void purgeTagEpc(String tagEpc);

}