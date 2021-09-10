/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader;

import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;

/**
 * Listens to the reader main functions. (Output to the application level)
 * @author Fernando
 */

public interface ReaderListener {

	/**
     * Thrown when the response could not be parsed.
	 * @param readerId 
	 * @param data
	 */
	void onUnknownResponseReceived(String readerId, byte[] data);

	/**
     * When the TAG has been read.
	 * @param tagReport
     * @return True if the tag is of the same prefix (EPC company prefix).
     * False otherwise.
	 */
	boolean onTagRead(String readerId, TagReport tagReport);
}
