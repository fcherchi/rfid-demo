/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.responsedto;

/**
 * Holds the measure of the antenna gain (RX power)
 * @author Fernando
 *
 */
public class AntennaGain {
	
	
	int cableLoss;
	int antennaGain;
	int antennaPortNumber;
	
	/**
	 * @param cableLoss
	 * @param antennaGain
	 * @param antennaPortNumber
	 */
	public AntennaGain(int antennaPortNumber, int cableLoss, int antennaGain) {
		super();
		this.cableLoss = cableLoss;
		this.antennaGain = antennaGain;
		this.antennaPortNumber = antennaPortNumber;
	}
	
	/**
	 * @return the cableLoss
	 */
	public int getCableLoss() {
		return cableLoss;
	}
	/**
	 * @return the antennaGain
	 */
	public int getAntennaGain() {
		return antennaGain;
	}
	/**
	 * @return the antennaPortNumber
	 */
	public int getAntennaPortNumber() {
		return antennaPortNumber;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AntennaGain [cableLoss=" + cableLoss + ", antennaGain=" + antennaGain + ", antennaPortNumber=" + antennaPortNumber + "]";
	}
		
}
