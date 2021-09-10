/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events.impl;

import com.fcherchi.demo.config.file.ConfigurationChangesListener;
import com.fcherchi.demo.events.EventConfigurationChangesListener;
import com.fcherchi.demo.events.EventConfigurationProvider;
import com.fcherchi.demo.config.file.ConfigurationException;
import com.fcherchi.demo.config.file.ConfigurationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class EventConfigurationProviderImpl implements EventConfigurationProvider, ConfigurationChangesListener {

	/** Config file for the events generator */
	private static final String EVENTS_CONFIG_FILE = "events.json";

	
	/** Configuration (here we have the time to consider an event) */
	private Map<String, EventConfiguration> configurationMap;

	@Override
	public Map<String, EventConfiguration> getConfigurationMap() {
		return configurationMap;
	}

	private Object configurationLocker = new Object();

	@Autowired
	private ConfigurationProvider configProvider;

	private List<EventConfigurationChangesListener> listeners;

	@PostConstruct
	public void init() {
		this.listeners = new ArrayList<>();
		
		this.configProvider.listenToChanges(EVENTS_CONFIG_FILE, this);
		synchronized (this.configurationLocker) {
			Map<String, Object> map = this.getEventConfiguration();
			this.configurationMap = getConfiguration(map);
		}
	}

	
	private Map<String, Object> getEventConfiguration() {
		return this.configProvider.getConfigurationMap(EVENTS_CONFIG_FILE);
	}
	
	@Override
	public EventConfiguration getConfigForReader(String readerId) {
		EventConfiguration eventConfiguration;
		synchronized (this.configurationLocker) {
			eventConfiguration = this.configurationMap.get(readerId);
		}

		if (eventConfiguration == null) {
			throw new ConfigurationException("Error reading configuration. Missing configuration for the reader with id : '" + readerId + "'.");
		}

		return eventConfiguration;
	}

	private Map<String, EventConfiguration> getConfiguration(Map<String, Object> map) {

		Map<String, EventConfiguration> config = Collections.synchronizedMap(new HashMap<String, EventConfiguration>(map.size()));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listOfConfig = (List<Map<String, Object>>) map.get(EventConfiguration.EVENTS);
		listOfConfig.forEach((configMap) -> {
			EventConfiguration entity = EventConfiguration.parse(configMap);
			config.put(entity.getReaderId(), entity);
		});
		return config;
	}


	@Override
	public void onConfigurationChanged(Map<String, Object> map) {
		synchronized (this.configurationLocker) {
			this.configurationMap = getConfiguration(map);
		}
		//notify listeners
		this.listeners.forEach((listener) -> listener.eventConfigurationChanged(this.configurationMap));
		
	}


	@Override
	public void addEventConfigurationChangeListener(EventConfigurationChangesListener listener) {
		this.listeners.add(listener);
	}

}
