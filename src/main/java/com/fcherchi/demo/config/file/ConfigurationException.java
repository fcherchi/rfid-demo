/**
 * Just for demo purposes


 */

package com.fcherchi.demo.config.file;

/**
 * Thrown when the configuration cannot be parsed.
 * @author Fernando
 *
 */
public class ConfigurationException extends RuntimeException {

	/** generated serialisation id	 */
	private static final long serialVersionUID = -4698199223357705999L;
	
    /** Creates the exception with inner as a cause.*/
	public ConfigurationException (String message, Exception inner) {
		super (message, inner);
	}

	/** Creates the exception */
	public ConfigurationException (String message) {
		super (message);
	}
}
