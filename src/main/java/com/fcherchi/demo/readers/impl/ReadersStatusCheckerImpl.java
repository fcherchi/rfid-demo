/**
 * Just for demo purposes


 */

package com.fcherchi.demo.readers.impl;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fcherchi.demo.drivers.rfidreader.HeartbeatListener;
import com.fcherchi.demo.readers.HeartbeatReport;
import com.fcherchi.demo.readers.ReadersStatusChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This class holds the status of the readers.
 * It is being called from the reader driver at every heartbeat.
 *
 * It can be queried from the UI to receive a map of the last known readers temperature, and if they are online.
 *
 * @author Fernando
 */
@Component
public class ReadersStatusCheckerImpl implements ReadersStatusChecker, HeartbeatListener {

    /** Some tolerance is required before reporting a reader as dead due to false positives of the reader behavior
     * (sometimes the reader is too busy to answer on time). This constant is how many attempts are meade before declaring
     * a reader as dead.*/
	private static final Integer TOTAL_ATTEMPTS = 3;

	/** Key is the readerId, value is the heartbeat report (includes the temperature of the reader being shown in
	 * monitor)*/
	private Map<String, HeartbeatReport> lastHeartbeats;

	/**
	 * Key is the readerId, value is the counter of pending attempts (normally 3
	 * attempts should be made before reporting dead)
	 */
	private Map<String, Integer> pendingAttempts;

	@Value("${readers.heartbeatInterval}")
	private int heartbeatInterval;

	final Logger logger = (Logger) LoggerFactory.getLogger(ReadersStatusCheckerImpl.class);

    /** Flag to prevent checking the status of the readers previous to its initialisation. */
	private AtomicBoolean initialised = new AtomicBoolean(false);

    /** Flag to log the status alive only once and not polluting the log files with tons of messages. Just debug.*/
	private boolean reported;

	@Override
	public void initialise(List<String> availableReaders) {

		if (availableReaders != null) {

			this.lastHeartbeats = Collections.synchronizedMap(new HashMap<String, HeartbeatReport>(availableReaders.size()));
			this.pendingAttempts = Collections.synchronizedMap(new HashMap<String, Integer>(availableReaders.size()));

			synchronized (this.lastHeartbeats) {
				for (String readerId : availableReaders) {
					this.lastHeartbeats.putIfAbsent(readerId, new HeartbeatReport(false, null, 0));
				}
			}
			synchronized (this.pendingAttempts) {
				for (String readerId : availableReaders) {
					this.pendingAttempts.putIfAbsent(readerId, TOTAL_ATTEMPTS);
				}
			}
			this.initialised.set(true);

		} else {
			logger.warn("No readers available.");
		}
	}

	@Override
	public Map<String, HeartbeatReport> getReadersStatus() {

        //pull request to know the status of the readers
		if (this.initialised.get()) {
			this.lastHeartbeats.forEach((readerId, heartbeatReport) -> {
                //if timed out, set alive to false
				if (heartbeatReport.getLastTimestampAlive() != null && timedOut(heartbeatReport.getLastTimestampAlive(), this.heartbeatInterval, readerId)) {
					heartbeatReport.setAlive(false);
				}
			});
		}
		// return copy to keep reference
		return new HashMap<String, HeartbeatReport>(this.lastHeartbeats);
	}

    /**
     * Checks if the reader has timed out.
     * @param lastTimestamp
     * @param heartbeatInterval
     * @param readerId
     * @return
     */
	private boolean timedOut(Instant lastTimestamp, int heartbeatInterval, String readerId) {

		Instant timeoutTimestamp = Instant.now().minusMillis(heartbeatInterval);
		boolean timedOut = lastTimestamp.isBefore(timeoutTimestamp);
		Integer countdown = this.pendingAttempts.get(readerId);
		if (timedOut) {

			if (countdown > 0) {
				this.pendingAttempts.put(readerId, countdown - 1);
				timedOut = false;
			}

		} else {
			this.pendingAttempts.put(readerId, TOTAL_ATTEMPTS);
		}

		return timedOut;

	}

	@Override
	public void readerIsAlive(String readerId, int internalTemperature) {
        //just for debug logging
		if (!this.reported) {
			logger.debug("[{}] - is alive. {} C", readerId, internalTemperature);
			this.reported = true;
		}

		if (this.initialised.get()) {
			synchronized (this.lastHeartbeats) {

				if (!this.lastHeartbeats.containsKey(readerId)) {
					logger.warn("Unknown reader id received! ", readerId);
				} else {
					HeartbeatReport updated = new HeartbeatReport(true, Instant.now(), internalTemperature);
					this.lastHeartbeats.put(readerId, updated);
				}
			}
		}
	}

}
