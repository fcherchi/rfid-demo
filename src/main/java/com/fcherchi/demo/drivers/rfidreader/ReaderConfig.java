/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader;

import java.util.Map;

import com.fcherchi.demo.config.file.ConfigurationException;

/**
 * Holds the configuration of a reader.
 * @author Fernando
 */
public class ReaderConfig {

	// constants
	public static final String READERS = "readers";
	public static final String IP = "ip";
	public static final String ID = "id";
	public static final String POWER = "power";
	public static final String ANTENNA_GAIN = "antennaGain";
	public static final String RSSI_THRESHOLD = "rssiThreshold";
	public static final String PORT = "port";
	public static final String IS_ENABLED = "isEnabled";
	public static final String COMMUNICATION_PROFILE = "communicationProfile";
	public static final String USE_GPIO_TRIGGER = "useGPIOTrigger";

	// members
	private String ip;
	private String id;
	private int power;
	private int rssiThreshold;
	private int antennaGain;
	private int port;
	private Boolean isEnabled;
	private int communicationProfile;
	private Boolean useGPIOTrigger;

	public ReaderConfig(String id, String ip) {
		this.id = id;
		this.ip = ip;
	}

    /**
     * Converts the map containing the configuration into a POJO.
     * @param entry
     * @return
     */
	public static ReaderConfig parse(Map<String, Object> entry) {

		ReaderConfig res = new ReaderConfig((String) entry.get(ID), (String) entry.get(IP));

		try {
			res.power = (Integer) entry.get(POWER);
		} catch (Exception e1) {
			try {
				// maybe an hex representation
				res.power = Integer.decode((String) entry.get(POWER));
			} catch (Exception e2) {
				throw new ConfigurationException("Error in reader configuration. Power is not a number: " + entry.get(POWER));
			}
		}
		try {
			res.antennaGain = (int) entry.get(ANTENNA_GAIN);
		} catch (Exception e) {
			throw new ConfigurationException("Error in reader configuration. antennaGain is not a number: " + entry.get(ANTENNA_GAIN));
		}

		try {
			res.rssiThreshold = (int) entry.get(RSSI_THRESHOLD);
		} catch (Exception e) {
			throw new ConfigurationException("Error in reader configuration. rssiThreshold is not a number: " + entry.get(RSSI_THRESHOLD));
		}

		try {
			res.port = (int) entry.get(PORT);
		} catch (Exception e) {
			throw new ConfigurationException("Error in reader configuration. Port is not a number: " + entry.get(PORT));
		}

		try {
			res.isEnabled = (Boolean) entry.get(IS_ENABLED);
		} catch (Exception e) {
			throw new ConfigurationException("Error in reader configuration. isEnabled is not a boolean: " + entry.get(IS_ENABLED));
		}
		
		try {
			res.communicationProfile = (int) entry.get(COMMUNICATION_PROFILE);
		} catch (Exception e) {
			throw new ConfigurationException("Error in reader configuration. communicationProfile is not a number: " + entry.get(COMMUNICATION_PROFILE));
		}
		
		try {
			res.useGPIOTrigger = (Boolean) entry.get(USE_GPIO_TRIGGER);
			//backwards compatibility
			if (res.useGPIOTrigger == null) {
				res.useGPIOTrigger = false;
			}
		} catch (Exception e) {
			throw new ConfigurationException("Error in reader configuration. useGPIOTrigger is not a boolean: " + entry.get(USE_GPIO_TRIGGER));
		}

		return res;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the power
	 */
	public int getPower() {
		return power;
	}

	/**
	 * @return the RSSI
	 */
	public int getAntennaGain() {
		return antennaGain;
	}

	/**
	 * @return the rssiThreshold
	 */
	public int getRssiThreshold() {
		return rssiThreshold;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param power
	 *            the power to set
	 */
	public void setPower(int power) {
		this.power = power;
	}

	/**
	 * @param antennaGain
	 *            the antennaGain to set
	 */
	public void setAntennaGain(int antennaGain) {
		this.antennaGain = antennaGain;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the isEnabled
	 */
	public Boolean getIsEnabled() {
		return isEnabled;
	}

	/**
	 * @param rssiThreshold
	 *            the rssiThreshold to set
	 */
	public void setRssiThreshold(int rssiThreshold) {
		this.rssiThreshold = rssiThreshold;
	}

	/**
	 * @param isEnabled
	 *            the isEnabled to set
	 */
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * @return
	 */
	public int getCommunicationProfile() {

		return this.communicationProfile;
	}
	
	/**
	 * Sets the communication profile.
	 * @param profileId
	 */
	public void setCommuncationProfile(int profileId) {
		this.communicationProfile = profileId;
	}
	
	/**
	 * @param useGPIOTrigger
	 *            if true the GPIO trigger will be used.
	 */
	public void setUseGPIOTrigger(boolean useGPIOTrigger) {
		this.useGPIOTrigger = useGPIOTrigger;
	}

	/**
	 * @return the value of GPIO trigger.
	 */
	public Boolean getUseGPIOTrigger() {
		return this.useGPIOTrigger;
	}

}
