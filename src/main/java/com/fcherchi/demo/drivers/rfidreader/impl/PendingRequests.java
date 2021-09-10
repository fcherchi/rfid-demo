/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.fcherchi.demo.drivers.exception.DriverException;

/**
 * Requests to the reader are synchronised to simplify invocation of commands which
 * are typically blocking (like set a certain working mode, or give me a tag).
 *
 * This class holds the requests which are pending to be answered by the reader.
 *
 * @author Fernando
 *
 */
public class PendingRequests {

	
	final Logger logger = (Logger) LoggerFactory.getLogger(PendingRequests.class);
	
	/** Requests pending to receive a response. Key is responseId, value is the list of pending requests
	 * Last parameter in initialisation is 1 because 2 thread (the reader) are expected to modify (add or remove)
	 * the status of the map. */
	private Map<Short, Queue<Instant>> requests = new ConcurrentHashMap<Short, Queue<Instant>>(16, 0.9f, 2);
	private String readerId;
	private int timeout;
	
	public PendingRequests(String readerId, int timeout, List<Short> commands) {
		this.readerId = readerId;
		this.timeout = timeout;
		if (! CollectionUtils.isEmpty(commands)) {
			for (Short cmd : commands) {
				logger.debug(String.format("Added command to the list of synchronous commands: 0x%02X", cmd));
				requests.put(cmd, new ConcurrentLinkedQueue<Instant>());
			}
		}
	}
	
	/**
	 * Adds a new request to the collection of requests pending to be responded.
	 * @param responseId
	 * @param requestTime
	 */
	public void addNewRequest(Short responseId, Instant requestTime) {

		//adds the request time
		if (!requests.containsKey(responseId)) {
			throw new DriverException(this.readerId, "Response Id not found in map, Pending requests class badly initialised.");
		}
		logger.debug("Adding new request {}", String.format("0x%02X", responseId));
		Queue<Instant> queue = requests.get(responseId);
		queue.add(requestTime);
		logger.debug("Added new request {}. Queue ID: {} length: {}", String.format("0x%02X", responseId), System.identityHashCode(queue), queue.size());
	}
	
	/**
	 * After a response is received this method is called to not have any timeout.
	 * @param responseId
	 */
	public boolean responseReceived(Short responseId) {
		return remove(responseId);
	}

	/**
     * Remove the request identified by its response id.
	 * @param responseId
	 */
	private boolean remove(Short responseId) {
		if (requests.containsKey(responseId)) {
			Queue<Instant> queue = requests.get(responseId);
			queue.poll();
			return true;
		}
		return false;
	}
	
	/**
	 * Notifies the given listener if timeout occurred
	 * @param listener
	 * @param currentTime
	 */
	public void checkTimeouts(TimeoutNotificationListener listener, Instant currentTime) {
		for (Entry<Short, Queue<Instant>> entry : this.requests.entrySet()) {
			Queue<Instant> queue = entry.getValue();
			for (Instant instant : queue) {
				notifyIfTimeout(listener, currentTime, instant, entry.getKey());
			}
		}
	}

	/**
     * Calls the listener in case of timeout.
	 * @param listener
	 * @param currentTime
	 * @param requestTime
	 * @param responseId 
	 * @return
	 */
	private void notifyIfTimeout(TimeoutNotificationListener listener, Instant currentTime, Instant requestTime, Short responseId) {
		logger.debug("Checking timeout for call with response id {} ", String.format("0x%02X", responseId));
		boolean timeout = requestTime.plusMillis(this.timeout).isBefore(currentTime);
		if (timeout) {
            remove(responseId);
			listener.timeoutOccurred(responseId);
		}
	}
}
