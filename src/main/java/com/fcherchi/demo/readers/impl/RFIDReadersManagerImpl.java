/**
 * Just for demo purposes


 */

package com.fcherchi.demo.readers.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcherchi.demo.config.file.ConfigurationChangesListener;
import com.fcherchi.demo.config.file.ConfigurationProvider;
import com.fcherchi.demo.drivers.demoreader.DummyReaderImpl;
import com.fcherchi.demo.drivers.rfidreader.DTE820Reader;
import com.fcherchi.demo.drivers.rfidreader.ReaderConfig;
import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.readers.RFIDReadersManager;
import com.fcherchi.demo.readers.ReadersStatusChecker;
import com.fcherchi.demo.readers.ReadersWatchdog;
import com.fcherchi.demo.readers.WatchdogListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

/**
 * Central point where the high level managing of the readers happens.
 * They are instantiated and monitored (using delegated composed artifacts) from here.
 *
 * The configuration is listened from here, so if any change in the settings of the reader
 * occurs at runtime, it will be pushed to the devices.
 *
 * @author Fernando
 */
@Component
public class RFIDReadersManagerImpl implements ConfigurationChangesListener, WatchdogListener, RFIDReadersManager {

	private static final String ANTENNAS_CONFIG_FILE = "antennas.json";

	@Autowired
	private RFIDReaderFactoryImpl readerFactory;

	@Autowired
	private ConfigurationProvider config;

	@Autowired
	private ReaderListener tagReadListener;

	@Autowired
	private ReadersStatusChecker heartbeatListener;

	@Autowired
	private ReadersWatchdog readersWatchdog;

	final Logger logger = (Logger) LoggerFactory.getLogger(RFIDReadersManagerImpl.class);

	/** Stores the configuration to check the changes when file has changed */
	private HashMap<String, Object> lastKnownConfiguration;

	@Override
    @PostConstruct
	public void initialiseReaders() {

		Map<String, Object> configMap = this.config.getConfigurationMap(ANTENNAS_CONFIG_FILE);
		this.lastKnownConfiguration = new HashMap<>(configMap);
		List<String> listOfReaders = getAntennaIdList(configMap);

		this.config.listenToChanges(ANTENNAS_CONFIG_FILE, this);
		this.readerFactory.instantiateReaders(configMap, this.tagReadListener, this.heartbeatListener);

		this.readerFactory.getReaders().forEach((readerId, reader) -> initialiseReader(reader));

		this.heartbeatListener.initialise(listOfReaders);
		this.readersWatchdog.startWatchdog(this);
	}

	@Override
    @PreDestroy
	public void cleanUp() {
		// to get here invoke /shutdown endpoint
		logger.info("Shutting down readers....");

		this.readersWatchdog.stop();
		readerFactory.getReaders().forEach((readerId, reader) -> {
			reader.disconnect();
		});
	}

	@Override
	public void reconnectionRequired(String readerId) {

		// this is invoked from the watchdog to report that a reconnection has
		// to be sent
		DTE820Reader reader = this.readerFactory.getReaderById(readerId);
		if (reader != null) {
			try {
				// this is to clean up
				reader.disconnect();
			} catch (Exception e) {
				this.logger.error("Error re-starting reader in disconnect." + reader.getReaderId(), e);
			}
			try {
				reader.connect();
			} catch (Exception e) {
				this.logger.error("Error starting reader " + reader.getReaderId(), e);
			}
		}
	}

	@Override
	public void onConfigurationChanged(Map<String, Object> map) {
		// if there is an actual change
		if (!lastKnownConfiguration.equals(map)) {
			logger.debug("Configuration has changed");
			try {
				List<String> antennas = getAntennaIdList(map);
				this.heartbeatListener.initialise(antennas);
				this.lastKnownConfiguration = new HashMap<>(map);
				reconfigureReaders(map);
			} catch (Exception e) {
				logger.error("Error changing configuration.", e);
			}
		}

	}

	@Override
    @SuppressWarnings("unchecked")
	public void setReaderOnOff(String readerId, boolean isReaderEnabled) {

		// change configuration to stop reader via config file. this way it is
		// persistent
		HashMap<String, Object> newConfig = new HashMap<String, Object>();
		List<Map<String, Object>> readers = new ArrayList<Map<String, Object>>();

		newConfig.put(ReaderConfig.READERS, readers);

		List<Map<String, Object>> readersConfig = (List<Map<String, Object>>) lastKnownConfiguration.get(ReaderConfig.READERS);
		readersConfig.forEach((entry) -> {
			ReaderConfig readerConfig = ReaderConfig.parse(entry);
			if (readerConfig.getId().equals(readerId)) {
				readerConfig.setEnabled(isReaderEnabled);
			}
			readers.add(convertToMap(readerConfig));
		});
		this.config.writeMapToFile(newConfig, ANTENNAS_CONFIG_FILE);
	}

	@Override
    @SuppressWarnings("unchecked")
	public void stopAllReaders() {

		// change configuration to stop reader via config file. this way it is
		// persistent
		HashMap<String, Object> newConfig = new HashMap<String, Object>();
		List<Map<String, Object>> readers = new ArrayList<Map<String, Object>>();

		newConfig.put(ReaderConfig.READERS, readers);

		List<Map<String, Object>> readersConfig = (List<Map<String, Object>>) lastKnownConfiguration.get(ReaderConfig.READERS);
		readersConfig.forEach((entry) -> {
			ReaderConfig readerConfig = ReaderConfig.parse(entry);
			readerConfig.setEnabled(false);
			readers.add(convertToMap(readerConfig));
		});
		this.config.writeMapToFile(newConfig, ANTENNAS_CONFIG_FILE);
	}

	/**
	 * Converts the readerConfig in a map
	 * 
	 * @param readerConfig
	 * @return
	 */
	private Map<String, Object> convertToMap(ReaderConfig readerConfig) {
		TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>(){};
		return new ObjectMapper().convertValue(readerConfig, typeRef);
	}

    /**
     * Gets the antennas Ids as a list.
     * @param configMap
     * @return
     */
	private List<String> getAntennaIdList(Map<String, Object> configMap) {
		List<String> list = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> readers = (List<Map<String, Object>>) configMap.get(ReaderConfig.READERS);
		readers.forEach(entry -> {
			if ((Boolean) entry.get(ReaderConfig.IS_ENABLED)) {
				list.add((String) entry.get(ReaderConfig.ID));
			}
		});
		return list;
	}

    /**
     * Sends the configuration to the readers.
     * @param config
     */
	private void reconfigureReaders(Map<String, Object> config) {

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> readersConfig = (List<Map<String, Object>>) config.get(ReaderConfig.READERS);
		readersConfig.forEach((entry) -> {
			ReaderConfig readerConfig = ReaderConfig.parse(entry);
			String readerId = readerConfig.getId();

			DTE820Reader reader = this.readerFactory.getReaderById(readerId);
			if (reader != null) {
				reader.reconfigure(readerConfig);
			}
		});
	}

	/**
     * Connects the reader.
	 * @param reader
	 */
	private void initialiseReader(DTE820Reader reader) {

		this.logger.info("Initialising reader {}.", reader.getReaderId());
		try {
			reader.connect();
		} catch (Exception e) {
			this.logger.error("Error starting reader " + reader.getReaderId(), e);
		}
	}

	//just for the demo, not there in the production code
	public void setCable(String readerId, boolean isOn) {
        DTE820Reader reader = this.readerFactory.getReaderById(readerId);
        if (reader instanceof DummyReaderImpl) {
            if (reader != null) {
                ((DummyReaderImpl)reader).setCable(isOn);
            }
        } else {
            logger.info("Try to change cable status of physical reader. Not supported remotely :)");
        }
    }
}
