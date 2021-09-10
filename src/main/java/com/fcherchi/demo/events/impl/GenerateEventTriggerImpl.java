/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fcherchi.demo.events.GenerateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fcherchi.demo.config.file.ConfigurationException;
import com.fcherchi.demo.events.EventConfigurationProvider;
import com.fcherchi.demo.events.ExtendedTagReport;
import com.fcherchi.demo.events.GenerateEventTrigger;

/**
 * It checks if its time to generate an event (after the period of time has
 * elapsed)
 * 
 * @author Fernando
 */
@Component
public class GenerateEventTriggerImpl implements GenerateEventTrigger {

	
	/** Events older than one day are discarded from memory */
	private static final int MINUTES_TO_DISCARD_EVENT = 1440;
	
	/** Flag to stop the loop */
	private volatile boolean hasToStop;

	
	/** Listener to notify the events after the time has elapsed */
	private GenerateEventListener listener;

	/** The logger */
	final Logger logger = (Logger) LoggerFactory.getLogger(GenerateEventTriggerImpl.class);

	/** These are the events pending to be analysed */
	private Map<String, ExtendedTagReport> eventsWaitingToBeAnalysed;
	
	@Autowired
	TagEventAccumulator tagEventAccumulator;
	
	@Autowired
	EventConfigurationProvider eventConfigProvider;

	/**
	 * @see GenerateEventTrigger#initialise(java.util.HashMap,
	 *      GenerateEventListener)
	 */
	@Override
	public void initialise(GenerateEventListener listener) {
		
		this.eventsWaitingToBeAnalysed = Collections.synchronizedMap(new HashMap<String, ExtendedTagReport>());
		this.listener = listener;
	}


	/**
	 * @see GenerateEventTrigger#addEvent(ExtendedTagReport)
	 */
	@Override
	public void addEvent(ExtendedTagReport tagReport) {
		this.eventsWaitingToBeAnalysed.put(tagReport.getTagReport().getTagEpc(), tagReport);
		this.logger.debug("######################## Events added to be analysed. Count {}", this.eventsWaitingToBeAnalysed.size());
	}


	


	/**
	 * @see GenerateEventTrigger#startWatching()
	 */
	@Override
	@Async
	public void startWatching() {
		this.hasToStop = false;
		while (!this.hasToStop) {
			checkEvents();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				this.logger.error("Error in sleep", e);
			}
		}
	}
	
	
	/**
	 * @see GenerateEventTrigger#stop()
	 */
	@Override
	public void stop() {
		this.hasToStop = true;
	}


	/**
	 * @see GenerateEventTrigger#getStatusOfMemory()
	 */
	@Override
	public List<ExtendedTagReport> getStatusOfMemory() {
		
		List<ExtendedTagReport> res = new CopyOnWriteArrayList<>();
		synchronized (this.eventsWaitingToBeAnalysed) {
			this.eventsWaitingToBeAnalysed.forEach((tagEpc, tagReport) -> res.add(tagReport));
		}
		return res;
	}

	@Override
	public void removeEvent(String tagEpc) {
		this.eventsWaitingToBeAnalysed.remove(tagEpc);
	}


		/**
	 * 
	 */
	private void checkEvents() {

		List<String> eventsToBeNotified = new ArrayList<String>();
		List<String> eventsToBeDiscarded = new ArrayList<String>();

		synchronized (this.eventsWaitingToBeAnalysed) {
			// getting list of events to notify
			this.eventsWaitingToBeAnalysed.forEach((tagEpc, tagReport) -> {

				if (eventHasToBeGenerated(tagReport)) {

					eventsToBeNotified.add(tagReport.getTagReport().getTagEpc());
				}
				// this happens when an event is there for more than 
				// X time without resolution 
				if (eventHasToBeDiscarded(tagReport)) {
					eventsToBeDiscarded.add(tagReport.getTagReport().getTagEpc());
				}
			});

			// notify and remove from the list
			if (eventsToBeNotified.size() > 0) {
				eventsToBeNotified.forEach((epcTag) -> {

					ExtendedTagReport report = this.eventsWaitingToBeAnalysed.get(epcTag);
					ExtendedTagReport clone = new ExtendedTagReport(report.getReaderId(), report.getTagReport(), report.getFirstSeen(), report.getLastSeen());

					if (this.listener != null) {
						EventConfiguration config = this.eventConfigProvider.getConfigForReader(clone.getReaderId());
						this.listener.triggerEventGeneration(clone, config.isFillingStation());
					}
					this.eventsWaitingToBeAnalysed.remove(epcTag);
					this.tagEventAccumulator.purgeTagEpc(epcTag);
					this.logger.debug("######################## Event removed to be analysed. Count {}", this.eventsWaitingToBeAnalysed.size());
				});
			}
			// remove from the list
			if (eventsToBeDiscarded.size() > 0) {
				eventsToBeDiscarded.forEach(epcTag -> {
					this.eventsWaitingToBeAnalysed.remove(epcTag);
					this.tagEventAccumulator.purgeTagEpc(epcTag);
				});
			
			}
		}

	}

	/**
	 * @param tagReport
	 * @return
	 */
	private boolean eventHasToBeDiscarded(ExtendedTagReport tagReport) {
		Instant reportInstant = tagReport.getTagReport().getTimestamp();
		return reportInstant.isBefore(Instant.now().minus(MINUTES_TO_DISCARD_EVENT, ChronoUnit.MINUTES));
	}

	/** Computes if the event has to be generated. */
	private boolean eventHasToBeGenerated(ExtendedTagReport tagReport) {

		EventConfiguration eventConfiguration = this.eventConfigProvider.getConfigForReader(tagReport.getReaderId());
		if (eventConfiguration == null) {
			throw new ConfigurationException("Configuration not found for reader with id " + tagReport.getReaderId());
		}
		boolean res = false;
		if (eventConfiguration.isFillingStation()) {
			// when it is a filling station the event is generated when the
			// going event occurs
			res = tagReport.getLastSeen() != null && !tagReport.getTagReport().isComingMessage() && expired(tagReport) && countThresholdAccomplished(tagReport);
		} else {
			// if it is not a filling station, generate event immediately
			res = tagReport.getTagReport().isComingMessage();
		}

		return res;
	}

	/**
	 * @param tagReport
	 * @return
	 */
	private boolean countThresholdAccomplished(ExtendedTagReport tagReport) {
		EventConfiguration eventConfiguration = this.eventConfigProvider.getConfigForReader(tagReport.getReaderId());
		boolean res = eventConfiguration.getCountThreshold() < tagReport.getTagReport().getCount();
		return res;
	}

	/**
	 * @param tagReport
	 * @return
	 */
	private boolean expired(ExtendedTagReport tagReport) {
		EventConfiguration eventConfiguration = this.eventConfigProvider.getConfigForReader(tagReport.getReaderId());

		Instant timeout = tagReport.getLastSeen().plusMillis(eventConfiguration.getComputationTimeMs());
		boolean before = timeout.isBefore(Instant.now());
		return before;
	}


	
}
