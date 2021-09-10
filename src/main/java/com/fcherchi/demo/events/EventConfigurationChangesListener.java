/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events;

import com.fcherchi.demo.events.impl.EventConfiguration;

import java.util.Map;

/**
 * Notifies changes in the configuration of events.
 * @author Fernando
 *
 */
public interface EventConfigurationChangesListener {

    /**
     * Configuration of events has changed.
     * @param newConfig
     */
    void eventConfigurationChanged(Map<String, EventConfiguration> newConfig);
}
