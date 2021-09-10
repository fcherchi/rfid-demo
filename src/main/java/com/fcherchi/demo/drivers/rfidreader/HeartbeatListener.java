/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader;

/**
 * Listener to notify that the reader is alive.
 * @author Fernando
 */
public interface HeartbeatListener {
	void readerIsAlive(String readerId, int internalTemperature);
}
