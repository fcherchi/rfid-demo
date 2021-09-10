/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events;

import java.util.List;

/**
 * @author Fernando
 *
 */
public interface GenerateEventTrigger {

	/**
	 * Watches if the elapsed time has been consumed. If so, notifies the events to the listener
	 */
	void startWatching();

	/**
	 * Stops loop. Only during cleanUp phase 
	 */
	void stop();

    /**
     * Initialises the component.
     * @param listener
     */
	void initialise(GenerateEventListener listener);

	/**
	 * It is being invoked when an event has been received.
	 * @param tagReport
	 */
	void addEvent(ExtendedTagReport tagReport);

	/**
     * TODO remove this method
	 * @param tagEpc
	 */
	void removeEvent(String tagEpc);

	/**
	 * Returns a string representation of all events waiting to be analysed. 
	 * @return
	 */
	List<ExtendedTagReport> getStatusOfMemory();

}