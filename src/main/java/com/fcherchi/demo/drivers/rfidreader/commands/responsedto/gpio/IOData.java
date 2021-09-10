/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.commands.responsedto.gpio;

/**
 * Entity that holds the response of the GetIOData method.
 * It models all the statuses of the signals
 * 
 * @author Fernando
 *
 */
public class IOData {

	/** Holds the input values */
	private InputValues inputs;
	
	/** Holds the output values */
	private OutputValues outputs;
	

	/**
	 * Creates the entity.
	 * @param inputs Input values.
	 * @param outputs Output values.
	 */
	public IOData(InputValues inputs, OutputValues outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
	}

	/**
	 * @return the inputs
	 */
	public InputValues getInputs() {
		return inputs;
	}

	/**
	 * @return the outputs
	 */
	public OutputValues getOutputs() {
		return outputs;
	}

	/**
	 * @param inputs the inputs to set
	 */
	public void setInputs(InputValues inputs) {
		this.inputs = inputs;
	}

	/**
	 * @param outputs the outputs to set
	 */
	public void setOutputs(OutputValues outputs) {
		this.outputs = outputs;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "inputs=" + inputs + ", outputs=" + outputs + "]";
	}
}
