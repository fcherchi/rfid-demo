/**
 * Just for demo purposes


 */

package com.fcherchi.demo.readers.impl;

import com.fcherchi.demo.readers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * It is responsible to watch the status of the readers and send to the listener a message if the reader went off.
 * @author Fernando
 */
@Component
public class ReadersWatchdogImpl implements ReadersWatchdog {

	/** 3s sleep every step */
	private int watchingInterval = 3000;

	@Autowired
	private ReadersStatusChecker statusChecker;
	
	@Autowired
	private ReaderReconnector readersReconnector;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(ReadersWatchdogImpl.class);

	/** Flag to stop loop when application is being shut down */
    private volatile boolean stop;

	@Override
	@Async
	public void startWatchdog(WatchdogListener listener) {
		
		List<String> deadReaders = new ArrayList<String>();
		List<String> aliveReaders = new ArrayList<String>();
        //the listener is delegated to the reader reconnector.
		this.readersReconnector.startReconnectorLoop(listener);
		
		this.stop = false;
		while (!stop) {
            //pulls to check the current readers status
			Map<String, HeartbeatReport> readersStatus = this.statusChecker.getReadersStatus();
			
			boolean updated;

			//it could happen that one reader has been disabled from settings, so it needs to be 
			//considered as off, and not retry re-connections anymore
			updated = reinitialiseListIfNeeded(readersStatus, deadReaders, aliveReaders);
			for(Entry<String, HeartbeatReport> entry : readersStatus.entrySet()) {
				if (updateReadersList(deadReaders, aliveReaders, entry.getKey(), entry.getValue())) {
					updated = true;
				}
			}
			
			if (updated) {
				logger.debug("Some readers have change connection status: Alive Readers {}. Dead Readers {}.", aliveReaders, deadReaders);
                //notify changes to reconnector so it can start trying to restore connection (or remove the ones already restored);
				readersReconnector.updateListOfDeadReaders(deadReaders, aliveReaders);
			}
			
			try {
				Thread.sleep(this.watchingInterval);
			} catch (InterruptedException e) {
				this.logger.error("Error in sleep in Watchdog.", e);
			}
		}
	}


	/**
	 * Re-adapts the lists of readers in case is necessary (because one new reader was turned on or off in the configuration)
	 * @param readersStatus
	 * @param deadReaders
	 * @param aliveReaders
	 * @return if connectivity of at least one reader has changed
	 */
	private boolean reinitialiseListIfNeeded(Map<String, HeartbeatReport> readersStatus, List<String> deadReaders, List<String> aliveReaders) {
		boolean thereAreChanges = false;
        //if amount of readers have changed
		if (readersStatus.size() < (deadReaders.size() + aliveReaders.size())) {
			
			Set<String> matchingReaders = readersStatus.keySet();
			List<String> extraReaders = new ArrayList<String>();
			
			//less active readers in configuration file than readers in lists, removing the one from the list 
			//as we don't want more re connections to that one. So find the extra one in dead or alive readers
			for (String deadReader : deadReaders) {
				if (! matchingReaders.contains(deadReader)) {
					extraReaders.add(deadReader);
				} 
			}
			for (String aliveReader : aliveReaders) {
				if (! matchingReaders.contains(aliveReader)) {
					extraReaders.add(aliveReader);
				} 
			}
			
			if (extraReaders.size() != 0) {
				for (String extraReader : extraReaders) {
					if (deadReaders.remove(extraReader)) {
						thereAreChanges = true;
					}
					if (aliveReaders.remove(extraReader)) {
						thereAreChanges = true;
					}
				}
			}
		}
		return thereAreChanges;
	}


	/**
     * Receives a report of one reader and refreshes the lists in case is necessary.
	 */
	private boolean updateReadersList(List<String> deadReaders, List<String> aliveReaders, String readerId, HeartbeatReport status) {
		
		boolean statusChanged = false;
		if (! status.isAlive()) {
			//if reader is dead add it to the collection of dead ones and remove it from the other 
			if (! deadReaders.contains(readerId)) {
				deadReaders.add(readerId);
				statusChanged = true;
			}
			if (aliveReaders.contains(readerId)) {
				aliveReaders.remove(readerId);
				statusChanged = true;
			}
		} else {
			//reader is alive
			if (deadReaders.contains(readerId)) {
				deadReaders.remove(readerId);
				statusChanged = true;
			}
			if (! aliveReaders.contains(readerId)) {
				aliveReaders.add(readerId);
				statusChanged = true;
			}
		}
		return statusChanged;
	}

	@Override
	public void stop() {
		this.readersReconnector.stop();
		this.stop = true;
	}
}
