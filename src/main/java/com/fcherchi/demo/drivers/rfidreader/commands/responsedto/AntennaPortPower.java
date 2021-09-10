/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.responsedto;

/**
 * Response given by the reader when the GetPortPower command is sent. (TX power)
 * @author Fernando
 *
 */
public class AntennaPortPower {
	
	private int antennaPortNumber;
	private int portPower;
	/**
	 * @param antennaPortNumber
	 * @param portPower
	 */
	public AntennaPortPower(int antennaPortNumber, int portPower) {
		super();
		this.antennaPortNumber = antennaPortNumber;
		this.portPower = portPower;
	}
	/**
	 * @return the antennaPortNumber
	 */
	public int getAntennaPortNumber() {
		return antennaPortNumber;
	}
	/**
	 * @return the portPower
	 */
	public int getPortPower() {
		return portPower;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AntennaPortPower [antennaPortNumber=" + antennaPortNumber + ", portPower=" + portPower + "]";
	}
}
