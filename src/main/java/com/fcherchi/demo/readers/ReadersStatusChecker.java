/**
 * Just for demo purposes


 */

package com.fcherchi.demo.readers;

import java.util.List;
import java.util.Map;

import com.fcherchi.demo.drivers.rfidreader.HeartbeatListener;

/**
 * Holds the current status of the readers.
 *
 * @author Fernando
 */
public interface ReadersStatusChecker extends HeartbeatListener {

	/**
	 * Sets the list of readers to check.
	 * @param availableReaders
	 */
	void initialise(List<String> availableReaders);
	
	/**
	 * Gets the current status of the readers.
	 * @return
	 */
	Map<String, HeartbeatReport> getReadersStatus();
}
