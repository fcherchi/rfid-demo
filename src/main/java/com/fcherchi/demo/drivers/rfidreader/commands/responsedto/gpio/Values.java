/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.responsedto.gpio;

/**
 * Represents a set of input or output values.
 * 
 * @author Fernando
 *
 */
public class Values {

	/** The value of Input or Output 1*/
	private boolean valueOne;
	
	
	/** The value of Input or Output 2*/
	private boolean valueTwo;
	
	/** The value of Input or Output 3*/
	private boolean valueThree;
	
	/** Parses the int representation of the values */
	public static Values parse(int value) {
		
		String bits = Integer.toBinaryString(value);
		//reversing bits
		bits = new StringBuilder(bits).reverse().toString();
		
		Values res = new Values();
		
		//first bit is always there
		res.valueOne = bits.charAt(0) == '1';
		if (bits.length() > 1) {
			res.valueTwo = bits.charAt(1) == '1'; 
		}
		if (bits.length() > 2) {
			res.valueThree = bits.charAt(2) == '1'; 
		}
		return res;
	}
	
	
	/**
	 * Creates an instance with the given values
	 * @param valueOne
	 * @param valueTwo
	 * @param valueThree
	 */
	public Values(boolean valueOne, boolean valueTwo, boolean valueThree) {
		super();
		this.valueOne = valueOne;
		this.valueTwo = valueTwo;
		this.valueThree = valueThree;
	}

	public Values() {
	}


	/**
	 * @return the valueOne
	 */
	public boolean getValueOne() {
		return valueOne;
	}

	/**
	 * @return the valueTwo
	 */
	public boolean getValueTwo() {
		return valueTwo;
	}

	/**
	 * @return the valueThree
	 */
	public boolean getValueThree() {
		return valueThree;
	}

	/**
	 * @param valueOne the valueOne to set
	 */
	public void setValueOne(boolean valueOne) {
		this.valueOne = valueOne;
	}

	/**
	 * @param valueTwo the valueTwo to set
	 */
	public void setValueTwo(boolean valueTwo) {
		this.valueTwo = valueTwo;
	}

	/**
	 * @param valueThree the valueThree to set
	 */
	public void setValueThree(boolean valueThree) {
		this.valueThree = valueThree;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + valueOne + ", " + valueTwo + ", " + valueThree + "]";
	}
}
