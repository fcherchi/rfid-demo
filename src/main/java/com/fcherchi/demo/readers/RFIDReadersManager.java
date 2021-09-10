package com.fcherchi.demo.readers;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Central point where the coordination of the readers happens.
 * They are instantiated and monitored (using delegated composed artifacts) from here.
 *
 * The configuration is listened from here, so if any change in the settings of the reader
 * occurs at runtime, it will be pushed to the devices.
 *
 * @author Fernando
 */
public interface RFIDReadersManager {

    /**
     * After construction readers have to be created and initialised.
     * It starts also the components helping in the reader operation (watchdog, heartbeat, etc)
     */
    @PostConstruct
    void initialiseReaders();

    /**
     * Gracefully stops the operation of the readers.
     */
    @PreDestroy
    void cleanUp();

    /**
     * Sets a give reader on or off.
     * @param readerId The reader ID as in the config file.
     * @param isReaderEnabled If true, the reader is being turned on, otherwise off.
     */
    void setReaderOnOff(String readerId, boolean isReaderEnabled);

    /**
     * Stops all the readers.
     */
    void stopAllReaders();
}
