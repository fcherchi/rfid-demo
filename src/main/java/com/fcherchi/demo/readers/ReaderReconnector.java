/**
 * Just for demo purposes


 */

package com.fcherchi.demo.readers;

import java.util.List;

/**
 * When the connection of a reader has been lost, a loop trying to reconnect starts.
 *
 * The granularity of the connections attempt is decreasing, so in this way if the connection
 * is gone forever, we don't overload reconnecting very often.
 *
 * The actual reconnection is not technically happening here. Only a listener method is triggered
 * to be reconnected outside.
 *
 * @author Fernando
 */
public interface ReaderReconnector {

    /**
     * Called when the connectivity with the reader is lost, so it tries to recover the connectivity.
     * @param listener
     */
	void startReconnectorLoop(WatchdogListener listener);

    /**
     * Called when somehow the list of active readers has changed.
     * @param deadReaders
     * @param aliveReaders
     */
	void updateListOfDeadReaders(List<String> deadReaders, List<String> aliveReaders);

    /**
     * Stops the reconnector loop. To be called for gracefully close all threads when the application is closed.
     */
	void stop();

}