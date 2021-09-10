/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events;

/**
 * Used to be notified when an event has to be generated
 * @author Fernando
 *
 */
public interface GenerateEventListener {
	
	void triggerEventGeneration(ExtendedTagReport tagReport, boolean isFillingStation);
}
