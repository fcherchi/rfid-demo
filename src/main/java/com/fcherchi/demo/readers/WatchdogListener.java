/**
 * Just for demo purposes


 */

package com.fcherchi.demo.readers;

/**
 * The watchdog will notify with this listener when a reconnection has to be tried.
 * @author Fernando
 */
public interface WatchdogListener {
	
	/**
	 * Invoked when a connection has to be attempted
	 * @param readerId The ID of the reader which has to be reconnected.
	 */
	void reconnectionRequired(String readerId);
}
