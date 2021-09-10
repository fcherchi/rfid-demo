/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events;

import com.fcherchi.demo.events.impl.EventConfiguration;

import java.util.Map;

/**
 * Provides access to the configuration of the events.
 * @author Fernando
 *
 */
public interface EventConfigurationProvider {

	
	/**
	 * Adds a listener to be notified about configuration changes.
	 * @param listener
	 */
	void addEventConfigurationChangeListener(EventConfigurationChangesListener listener);

	/**
     * Gets the configuration for the given reader.
	 * @param readerId The reader id.
	 * @return Configuration of that reader.
	 */
	EventConfiguration getConfigForReader(String readerId);

    /**
     * Gets the whole events configuration for all readers
     * @return
     */
	Map<String, EventConfiguration> getConfigurationMap();
}