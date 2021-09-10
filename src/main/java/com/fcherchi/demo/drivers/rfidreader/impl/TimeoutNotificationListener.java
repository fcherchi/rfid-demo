/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

/**
 * A listener to be notified when a timeout has occurred.
 * @author Fernando
 *
 */
public interface TimeoutNotificationListener {
	void timeoutOccurred(Short responseId);
}
