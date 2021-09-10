/**
 * Just for demo purposes


 */

package com.fcherchi.demo.readers.impl;

import com.fcherchi.demo.config.file.ConfigurationException;
import com.fcherchi.demo.drivers.rfidreader.DTE820Reader;
import com.fcherchi.demo.drivers.rfidreader.HeartbeatListener;
import com.fcherchi.demo.drivers.rfidreader.ReaderConfig;
import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.readers.RFIDReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This factory creates the RFID reader instances at startup time of the application based on the configuration file.
 *
 * The system is not meant to add or remove readers during operation, but they can be turned on or off or change
 * their configuration.
 *
 * The input for the instantiation is a configuration map that matches the JSON file.
 *
 * The factory creates the readers as Spring Beans dynamically based on the file, that's the reason for autowiring
 * the application context.
 *
 * @author Fernando
 */
@Component
public class RFIDReaderFactoryImpl implements RFIDReaderFactory {

	/** Key is reader ID. Value the reader. */
    private Map<String, DTE820Reader> readers;

    /** Each element of the list is the configuration of the reader. */
	private List<Map<String, Object>> readersConfig;

    /** Being used for dynamically creation of the RFID reader spring beans (they depend on the configuration file
     * and they could change at runtime */
	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * Simple ctor.
	 */
	public RFIDReaderFactoryImpl() {
		this.readers = new HashMap<String, DTE820Reader>();
	}

	@Override
    @SuppressWarnings("unchecked")
	public void instantiateReaders(Map<String, Object> readersConfig, ReaderListener tagListener, HeartbeatListener heartbeatListener) {
		try {

			this.readersConfig = (List<Map<String, Object>>) readersConfig.get(ReaderConfig.READERS);
			this.readersConfig.forEach(entry -> createReader(entry, tagListener, heartbeatListener));

		} catch (Exception e) {
			throw new ConfigurationException("Error parsing the configuration for readers. ", e);
		}
	}


	private void createReader(Map<String, Object> entry, ReaderListener taglistener, HeartbeatListener heartbeatListener) {

		ReaderConfig readerConfig = ReaderConfig.parse(entry);
		DTE820Reader reader = this.applicationContext.getBean(DTE820Reader.class, readerConfig, taglistener, heartbeatListener);
		this.readers.put(readerConfig.getId(), reader);
	}

	/**
	 * @return the readers
	 */
	@Override
    public Map<String, DTE820Reader> getReaders() {
		return readers;
	}

    /**
     * Retrieves a reader by its id.
     * @param readerId
     * @return
     */
	@Override
    public DTE820Reader getReaderById(String readerId) {
		return this.readers.get(readerId);
	}
}
