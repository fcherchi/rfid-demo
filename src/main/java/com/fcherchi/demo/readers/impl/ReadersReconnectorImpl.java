/**
 * Just for demo purposes


 */

package com.fcherchi.demo.readers.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fcherchi.demo.readers.ReaderReconnector;
import com.fcherchi.demo.readers.WatchdogListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ReadersReconnectorImpl implements ReaderReconnector {

    private static final int ITERATIONS_WITHOUT_RETRIES = 3;
    private static final int ITERATIONS_WITH_NORMAL_RATE = 15;
    private static final int ITERATIONS_WITH_MEDIUM_RATE = 40;
    private static final int STEPS_AT_NORMAL_RATE = 2;
    private static final int STEPS_AT_MEDIUM_RATE = 10;
    private static final int STEPS_AT_SLOW_RATE = 100;

    /** Holds the list of readers which are dead and have to be reconnected.
     * The Key is the reader id, the value is the counter of reconnection attempts already made */
	private ConcurrentHashMap<String, Integer> readers = new ConcurrentHashMap<String, Integer>(20, 0.9f, 2);

    /** Interval of the loop for reconnections. Not all iterations of the loop will try to reconnect)*/
	private static final int MIN_INTERVAL = 10000;

	/** The logger */
	final Logger logger = (Logger) LoggerFactory.getLogger(ReadersReconnectorImpl.class);

	/** Flag to stop the loop. */
    private volatile boolean stop;

    @Override
	public void updateListOfDeadReaders(List<String> deadReaders, List<String> aliveReaders) {

        //This method is invoked when a change in the status of the readers has been detected.
        //The goal is to update the list of dead readers.

		//first remove the list of alive readers from those to be reconnected
        removeAliveReaders(aliveReaders);

        //When a reader has been turned off at runtime by configuration, even if it is dead, reconnections have
        //to be suspended.
        removeDeadReaderIfTheyAreTurnedOff(deadReaders);

        //now add dead readers if they are not present
        addNewDeadReaders(deadReaders);

    }

    @Override
    @Async
    public void startReconnectorLoop(WatchdogListener listener) {

        this.stop = false;

        while (!stop) {
            // foreach dead reader
            for (Entry<String, Integer> deadReader : this.readers.entrySet()) {
                incrementCounter(deadReader);
                if (mustTryConnection(deadReader.getValue())) {
                    invokeReconnection(listener, deadReader);
                }
            }
            try {
                Thread.sleep(MIN_INTERVAL);
            } catch (Exception e) {
                logger.error("Sleep Error in reconnector loop.", e);
            }
        }
    }

    /**
     * Adds new dead readers to be reconnected.
     * @param deadReaders
     */
    private void addNewDeadReaders(List<String> deadReaders) {
        for (String deadReader : deadReaders) {
            this.logger.debug("Adding reader {} to the list of dead readers.", deadReader);
            //the collection contains the counter of iterations
            this.readers.putIfAbsent(deadReader, 0);
        }
    }

    /**
     * Remove dead readers in case they have been turned off by demand by configuration.
     * @param deadReaders
     */
    private void removeDeadReaderIfTheyAreTurnedOff(List<String> deadReaders) {
        //if some readers have been turned off
        if (this.readers.size() > deadReaders.size()) {
            List<String> toPurge = getReadersToPurge(this.readers.keySet(), deadReaders);

            //remove readers from dead collection (they are turned off by the user)
            for (String readerId : toPurge) {
                this.readers.remove(readerId);
            }
        }
    }

    /**
     * Remove readers that are not anymore disconnected.
     * @param aliveReaders
     */
    private void removeAliveReaders(List<String> aliveReaders) {
        for (String aliveReader : aliveReaders) {
            Integer removed = this.readers.remove(aliveReader);
            if (removed != null) {
                this.logger.debug("Removing reader {} from the list of dead readers.", aliveReader);
            }
        }
    }

    /**
     * Calls the listener to retry a connection.
     * @param listener
     * @param entrySet
     */
    private void invokeReconnection(WatchdogListener listener, Entry<String, Integer> entrySet) {
        logger.debug("[{}] - Invoke of reconnection.", entrySet.getKey());
        if (listener != null) {
            listener.reconnectionRequired(entrySet.getKey());
        }
    }

    /**
     * Increments that controls the attempts to connect.
     * @param entrySet
     */
    private void incrementCounter(Entry<String, Integer> entrySet) {
        if (entrySet.getValue() < Integer.MAX_VALUE) {
            entrySet.setValue(entrySet.getValue() + 1);
        }
        logger.debug("[{}] - Reader reported dead. Iteration #{}", entrySet.getKey(), entrySet.getValue());
    }

    /**
     * Gets the list of readers which have been restored and have to be removed from dead list.
     * @param readersBeingReconnected
     * @param deadReaders
     * @return
     */
    private List<String> getReadersToPurge(Set<String> readersBeingReconnected, List<String> deadReaders) {
		
		List<String> res = new ArrayList<String>();
		readersBeingReconnected.forEach(readerId -> {
			if (! deadReaders.contains(readerId)) {
				res.add(readerId);
			}
		});
		
		return res;
	}

	/**
     * According to the iteration number, returns if it is time to reconnect.
	 * @param iteration
	 * @return True if it is time to attempt a connection.
	 */
	private boolean mustTryConnection(int iteration) {
		
		//the goal is to not to retry so often when several attempts have been made
		//the first 3 attemps, will not retry (give time for the system to startup)
		if (iteration < ITERATIONS_WITHOUT_RETRIES) {
			//here it will enter with iteration 0..2
			return false;
		}
		if (iteration < ITERATIONS_WITH_NORMAL_RATE) {
			//Here it will enter with iteration 3..40
            //Retry every 2 attempts.
			//in iteration 6, 8, 10, 12, 14
			return iteration % STEPS_AT_NORMAL_RATE == 0;
		}
		//between 15 and 40 attempts, only retry every 10 steps
		if (iteration < ITERATIONS_WITH_MEDIUM_RATE) {
			//here it will enter with iteration 10, 20, 30
			return iteration % STEPS_AT_MEDIUM_RATE == 0;
		}
		
		//after 40 attempts, retry every 100 * MIN_INTERVAL
		return iteration % STEPS_AT_SLOW_RATE == 0;
	}

	@Override
	public void stop() {
		this.stop = true;
	}

}
