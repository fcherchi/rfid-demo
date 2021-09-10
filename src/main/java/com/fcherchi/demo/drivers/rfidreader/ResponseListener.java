/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader;

/**
 * A response of a command has been received.
 * @author Fernando
 */
public interface ResponseListener<T> {
    /**
     * Response received from the reader.
     * @param payload
     */
	void onResponseReceived(T payload);
}
