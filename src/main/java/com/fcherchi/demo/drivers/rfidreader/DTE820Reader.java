/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader;

import java.time.Instant;

import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.AntennaGain;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.AntennaPortPower;

/**
 * @author Fernando
 *
 */
public interface DTE820Reader {

	/**
	 * Connects to the reader
	 */
	void connect();

	/**
	 * Calls to gets the system date time of the reader. Notification will be done through listener .
	 */
	Instant getDateTime();
	
	
	/**
	 * Retrieves the power settings of the given antenna port.
	 */
	AntennaPortPower getPortPower(int antennaPort);
	
	/**
	 * Sets the power of the given antenna port. (TX power)
	 * @param antennaPort
	 * @param power
	 */
	void setPortPower(int antennaPort, int power);

	/**
	 * Disconnects the reader.
	 */
	void disconnect();

	/**
	 * @param antennaPort The antenna port to retrieve the setting from.
	 * @return Gets the sensibility power (RX) of the given antenna.
	 */
	AntennaGain getAntennaGain(int antennaPort);

	/**
	 * Persists the configuration.
	 */
	void saveConfiguration();

	/**
     * Sets the antenna receiving power (RX).
	 * @param antennaPort
	 * @param antennaGain
	 */
	void setAntennaGain(int antennaPort, int antennaGain);

	/**
	 * Gets the id of this reader.
	 * @return
	 */
	String getReaderId();

	
	/**
     * Gets the value of the parameter identified by its id.
	 * @param paramId The id of the parameter from which the status will be retrieved.
	 * @return The value of the parameter.
	 */
	Integer getParamById(int paramId);
	
	/**
     * Sets the value of a reader parameter.
	 * @param paramId The id of the parameter being set.
	 * @param paramValue The value to be set.
	 */
	void setParamById(int paramId, int paramValue);

	/**
     * Resets the configuration of the reader.
	 * @param readerConfig
	 */
	void reconfigure(ReaderConfig readerConfig);

    /**
     * Sets the mode of working as GPIO triggered.
     * (Reader will be set on via GPIO signal of the PLC)
     */
	void setModeToGPIOTrigger();

    /**
     * Sets the operation mode as always on (no GPIO involved).
     */
	void setModeToAlwaysOn();

}
