/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.responsedto.gpio;

/**
 * POJO holding the output values of the GPIO protocol.
 *
 * @author Fernando
 */
public class OutputValues {
	
	/** Stores the physical output values */
	private Values physical;
	
	/** Stores the logical output values */
	private Values logical;

	/**
	 * @param physical
	 * @param logical
	 */
	public OutputValues(Values logical, Values physical) {
		this.physical = physical;
		this.logical = logical;
	}

	/**
	 * @return the logical
	 */
	public Values getLogical() {
		return logical;
	}

	/**
	 * @param logical the logical to set
	 */
	public void setLogical(Values logical) {
		this.logical = logical;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		//return "OutputValues [physical=" + physical + ", logical=" + logical + "]";
		return logical.toString();
	}

}
