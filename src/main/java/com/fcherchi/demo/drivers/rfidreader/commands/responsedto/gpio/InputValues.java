/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.responsedto.gpio;

/**
 * Pojo holding the input values to be used in GPIO protocol.
 * @author Fernando
 *
 */
public class InputValues {
	
	/** The values of Logical Inputs */
	private Values logical;
	
	/** The values of Physical Inputs */
	private Values physical;
	
	/** The values of DebouncedLogical Inputs */
	private Values debouncedLogical;

	/**
	 * @param logical
	 * @param physical
	 * @param debouncedLogical
	 */
	public InputValues(Values logical, Values physical, Values debouncedLogical) {
		super();
		this.logical = logical;
		this.physical = physical;
		this.debouncedLogical = debouncedLogical;
	}

	/**
	 * @return the logical
	 */
	public Values getLogical() {
		return logical;
	}

	/**
	 * @return the physical
	 */
	public Values getPhysical() {
		return physical;
	}

	/**
	 * @return the debouncedLogical
	 */
	public Values getDebouncedLogical() {
		return debouncedLogical;
	}

	/**
	 * @param logical the logical to set
	 */
	public void setLogical(Values logical) {
		this.logical = logical;
	}

	/**
	 * @param physical the physical to set
	 */
	public void setPhysical(Values physical) {
		this.physical = physical;
	}

	/**
	 * @param debouncedLogical the debouncedLogical to set
	 */
	public void setDebouncedLogical(Values debouncedLogical) {
		this.debouncedLogical = debouncedLogical;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		//return "InputValues [logical=" + logical + ", physical=" + physical + ", debouncedLogical=" + debouncedLogical + "]";
		return logical.toString();
	}
}
