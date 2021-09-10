/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events;


/**
 * Manages the production of events in autonomous mode.
 * It calls the Event Persistor when it is time to produce an event.
 * @author Fernando
 *
 */
public interface EventProducer {

    /** Stop running loop */
	void cleanUp();

	/**
	 * Every time a going message arrives, this method is being invoked. 
	 * @param readerId
	 * @param tagEpc
	 */
	void tagReadArrived(String readerId, String tagEpc, boolean isComing);

    /**
     * Called directly when an event has to be generated. (GPIO mode)
     * @param tagReport
     * @param isFillingStation
     */
	void triggerEventGeneration(ExtendedTagReport tagReport, boolean isFillingStation);

}