/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader;

import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.gpio.IOData;

/**
 * @author Fernando
 *
 */
public interface GPIOSignalListener {
	
	/** Occurs when a signal has changed */
	void signalsReceived(IOData payload);
}
