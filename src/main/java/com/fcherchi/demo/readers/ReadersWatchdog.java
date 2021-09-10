/**
 * Just for demo purposes


 */

package com.fcherchi.demo.readers;

/**
 * Watchdog to coordinate the watching of the readers.
 * @author Fernando
 */
public interface ReadersWatchdog {

    /**
     * Starts the loop.
     * @param listener Listener to be notified when a connection is lost.
     */
	void startWatchdog(WatchdogListener listener);

    /**
     * Stops the loop.
     */
	void stop();
}