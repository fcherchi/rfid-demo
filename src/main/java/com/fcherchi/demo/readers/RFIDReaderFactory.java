package com.fcherchi.demo.readers;

import com.fcherchi.demo.drivers.rfidreader.DTE820Reader;
import com.fcherchi.demo.drivers.rfidreader.HeartbeatListener;
import com.fcherchi.demo.drivers.rfidreader.ReaderListener;

import java.util.Map;

public interface RFIDReaderFactory {

    /**
     * Creates the instance of the readers based on the configuation map.
     * @param readersConfig
     * @param tagListener
     * @param heartbeatListener
     */
    void instantiateReaders(Map<String, Object> readersConfig, ReaderListener tagListener,
                            HeartbeatListener heartbeatListener);

    /**
     * Gets the readers created by the factory so they all can be managed in a loop.
     * @return
     */
    Map<String, DTE820Reader> getReaders();

    /**
     * Gets the reader given its id.
     * @param readerId The id of the reader (as defined in the config file).
     * @return The reader with that id or null.
     */
    DTE820Reader getReaderById(String readerId);
}
