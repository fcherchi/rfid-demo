/**
 * Just for demo purposes


 */

package com.fcherchi.demo.readers;

import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The listener is invoked when a tag has been read. For debug purposes.
 * @author Fernando
 */
//@Component
public class TagReadListener implements ReaderListener {

	final Logger logger = (Logger) LoggerFactory.getLogger(TagReadListener.class);

	@Override
	public void onUnknownResponseReceived(String readerId, byte[] data) {
		this.logger.warn("[{}] - Unexpected response received", readerId);
	}

	@Override
	public boolean onTagRead(String readerId, TagReport tagReport) {
		this.logger.debug("[{}] - Tag received {}. RSSI {}, Count: {}", 
				readerId, tagReport.getTagEpc(), tagReport.getRssi(), tagReport.getCount());
		return true;
	}

}
