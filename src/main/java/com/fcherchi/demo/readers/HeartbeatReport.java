/**
 * Just for demo purposes


 */

package com.fcherchi.demo.readers;

import java.time.Instant;

/**
 * The information of one RFID Reader Heartbeat.
 *
 * To know if the reader is alive, it is being interrogated with a getTemperature method. For that reason
 * the heartbeat holds the last temperature of the reader. (So it is a double use as connectivity checker and diagnose
 * of an overheat for free).
 *
 * @author Fernando
 *
 */
public class HeartbeatReport {
	
	private boolean isAlive;
	private Instant lastTimestampAlive;
	private int temperature;

	/**
     * Creates the heartbeat report.
	 * @param isAlive If true, the reader is alive. Otherwise false.
	 * @param lastTimestampAlive Timestamp of the last time the reader was seen alive.
	 * @param temperature Last known temperature of the reader.
	 */
	public HeartbeatReport(boolean isAlive, Instant lastTimestampAlive, int temperature) {
		super();
		this.isAlive = isAlive;
		this.lastTimestampAlive = lastTimestampAlive;
		this.temperature = temperature;
	}
	/**
	 * @param isAlive the isAlive to set
	 */
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	/**
	 * @param lastTimestampAlive the lastTimestampAlive to set
	 */
	public void setLastTimestampAlive(Instant lastTimestampAlive) {
		this.lastTimestampAlive = lastTimestampAlive;
	}
	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}
	/**
	 * @return the isAlive
	 */
	public boolean isAlive() {
		return isAlive;
	}
	/**
	 * @return the lastTimestampAlive
	 */
	public Instant getLastTimestampAlive() {
		return lastTimestampAlive;
	}
	/**
	 * @return the temperature
	 */
	public int getTemperature() {
		return temperature;
	}
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isAlive ? 1231 : 1237);
		result = prime * result + ((lastTimestampAlive == null) ? 0 : lastTimestampAlive.hashCode());
		result = prime * result + temperature;
		return result;
	}
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HeartbeatReport other = (HeartbeatReport) obj;
		if (isAlive != other.isAlive)
			return false;
		if (lastTimestampAlive == null) {
			if (other.lastTimestampAlive != null)
				return false;
		} else if (!lastTimestampAlive.equals(other.lastTimestampAlive))
			return false;
		if (temperature != other.temperature)
			return false;
		return true;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HeartbeatReport [isAlive=" + isAlive + ", lastTimestampAlive=" + lastTimestampAlive + ", temperature=" + temperature + "]";
	}
	
	
	
}
