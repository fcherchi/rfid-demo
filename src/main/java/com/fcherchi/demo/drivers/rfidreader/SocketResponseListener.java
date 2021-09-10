/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader;

/**
 * Low level listener used to notify a response to a command.
 * @author Fernando
 */
public interface SocketResponseListener {
    /**
     * A response has been received.
     * @param response The frame received.
     */
	void responseReceived(byte[] response);
}
