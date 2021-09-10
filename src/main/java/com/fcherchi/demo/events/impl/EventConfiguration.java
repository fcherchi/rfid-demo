/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events.impl;

import java.util.Map;

import com.fcherchi.demo.config.file.ConfigurationException;

/**
 * @author Fernando
 *
 */
public class EventConfiguration {

	public static final String EVENTS = "events";
	public static final String READER_ID = "readerId";
	public static final String COMPUTATION_TIME = "computationTimeMs";
	public static final String COUNT_THRESHOLD = "countThreshold";
	public static final String IS_FILLING_STATION = "isFillingStation";

	/** The reader id */
	private String readerId;

	/** The elapsed time from the last read to persist an event in database. */
	private long computationTimeMs;

	/**
	 * If true the reader belongs to a filling station. Otherwise belongs to the
	 * printing barcode.
	 */
	private boolean isFillingStation;

	/** The minimum number of count for having an event generated. */
	private int countThreshold;

	/**
	 * Default constructor
	 */
	public EventConfiguration() {

	}

	/**
	 * Creates an event configuration element out of the config map
	 * 
	 * @param entry
	 * @return
	 */
	public static EventConfiguration parse(Map<String, Object> entry) {

		try {

			EventConfiguration res = new EventConfiguration();
			res.setReaderId((String) entry.get(READER_ID));
			res.setComputationTimeMs(((Integer) entry.get(COMPUTATION_TIME)).longValue());
			res.setFillingStation((boolean) entry.get(IS_FILLING_STATION));
			res.setCountThreshold((int) entry.get(COUNT_THRESHOLD));

			return res;
		} catch (Exception e) {
			throw new ConfigurationException("Error parsing events configuration", e);
		}
	}

	/**
	 * @return the readerId
	 */
	public String getReaderId() {
		return readerId;
	}

	/**
	 * @param readerId
	 *            the readerId to set
	 */
	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}

	/**
	 * @return the computationTimeMs
	 */
	public long getComputationTimeMs() {
		return computationTimeMs;
	}

	/**
	 * @param computationTimeMs
	 *            the computationTimeMs to set
	 */
	public void setComputationTimeMs(long computationTimeMs) {
		this.computationTimeMs = computationTimeMs;
	}

	/**
	 * @return the isFillingStation
	 */
	public boolean isFillingStation() {
		return isFillingStation;
	}

	/**
	 * @return the countThreshold
	 */
	public int getCountThreshold() {
		return countThreshold;
	}

	/**
	 * @param isFillingStation
	 *            the isFillingStation to set
	 */
	public void setFillingStation(boolean isFillingStation) {
		this.isFillingStation = isFillingStation;
	}

	/**
	 * @param countThreshold
	 *            the countThreshold to set
	 */
	public void setCountThreshold(int countThreshold) {
		this.countThreshold = countThreshold;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EventConfiguration [readerId=" + readerId + ", computationTimeMs=" + computationTimeMs + ", isFillingStation=" + isFillingStation
				+ ", countThreshold=" + countThreshold + "]";
	}

}
