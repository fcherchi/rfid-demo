/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events.impl;

import com.fcherchi.demo.events.*;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventProducerImpl implements GenerateEventListener, EventProducer, EventConfigurationChangesListener {


	private static int LAST_READS_QUEUE_SIZE = 5;

	final Logger logger = (Logger) LoggerFactory.getLogger(EventProducerImpl.class);

	@Autowired
	private TagEventAccumulator tagEventAccumulator;

	@Autowired
    EventPersistor eventPersistor;

//	@Autowired
//	private AntennaServices antennaServices;

	@Autowired
	private GenerateEventTrigger generateEventTrigger;

	@Autowired
	EventConfigurationProvider eventConfigProvider;

	/** The last notified reports per reader id. */
	private Map<String, Queue<ExtendedTagReport>> lastNotifiedReports;

	private Map<String, Boolean> antennasConfig;


	@PostConstruct
	void initialisation() {

		this.lastNotifiedReports = new ConcurrentHashMap<String, Queue<ExtendedTagReport>>(32, 0.9f, 2);
		this.generateEventTrigger.initialise(this);
		this.eventConfigProvider.addEventConfigurationChangeListener(this);
		Map<String, EventConfiguration> map = this.eventConfigProvider.getConfigurationMap();
		this.antennasConfig = this.getAntennaConfigMap(map);
	//	this.antennaServices.updateAntennasInDatabase(getAntennaConfigMap(map));

		this.generateEventTrigger.startWatching();
	}

	@Override
	@PreDestroy
	public void cleanUp() {
		this.generateEventTrigger.stop();
	}


	@Override
	public void tagReadArrived(String readerId, String tagEpc, boolean isComing) {

		boolean isFillingStation = getIsFillingStation(readerId);

		ExtendedTagReport bestReport = this.tagEventAccumulator.getBestReport(tagEpc, readerId, isFillingStation);
		if (bestReport != null) {
			this.generateEventTrigger.addEvent(bestReport);
		}
	}

	/**
	 * @param readerId
	 * @return
	 */
	private boolean getIsFillingStation(String readerId) {

		boolean isFilling = true;

		if (this.antennasConfig == null || this.antennasConfig.get(readerId) == null) {
			logger.warn("[{}] - Tag received but configuration cannot be retrieved. Asuming is a filling station.", readerId);
		} else {
			isFilling = this.antennasConfig.get(readerId) != null ? this.antennasConfig.get(readerId) : true;
		}

		return isFilling;
	}

	@Override
	public void triggerEventGeneration(ExtendedTagReport tagReport, boolean isFillingStation) {

		// if is the first time the event is being generated
		// this is to avoid notifying several times the same event. It should
		// check if
		// the same epc code has been received from the same antenna
		if (!isARepetitionOf(tagReport)) {

			// initialising list inside the map. The list contains only last
			// limited number of reads
			if (!this.lastNotifiedReports.containsKey(tagReport.getReaderId())) {
				this.lastNotifiedReports.put(tagReport.getReaderId(), new CircularFifoQueue<ExtendedTagReport>(LAST_READS_QUEUE_SIZE));
			}
			// cannot be null thanks to previous if
			Queue<ExtendedTagReport> lastNotifiedReportsForThisReader = this.lastNotifiedReports.get(tagReport.getReaderId());
			// FIFO, only x will be there
			synchronized (lastNotifiedReportsForThisReader) {
				lastNotifiedReportsForThisReader.add(tagReport);
			}

			boolean printingWasOK = this.eventPersistor.persistEvent(tagReport, isFillingStation);
			if (!printingWasOK) {
				this.lastNotifiedReports.remove(tagReport.getReaderId());
			}
		}
	}


	/**
	 * Returns true if two elements are equal
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	private boolean areEqual(ExtendedTagReport one, ExtendedTagReport two) {

		// avoiding NPE
		String readerIdOne = getReaderId(one);
		String readerIdTwo = getReaderId(two);

		String epcCodeOne = getEpcCode(one);
		String epcCodeTwo = getEpcCode(two);

		boolean repetition = readerIdOne.equals(readerIdTwo) && epcCodeOne.equals(epcCodeTwo);
		return repetition;
	}

	/**
	 * Gets the EPC code or empty string if any null;
	 * 
	 * @param report
	 * @return
	 */
	private String getEpcCode(ExtendedTagReport report) {

		String res = "";

		if (report != null && report.getTagReport() != null && report.getTagReport().getTagEpc() != null) {
			res = report.getTagReport().getTagEpc();
		}

		return res;
	}

	/**
	 * Returns the reader id or empty string if there is any null
	 * 
	 * @param report
	 * @return
	 */
	private String getReaderId(ExtendedTagReport report) {
		String res = "";

		if (report != null && report.getReaderId() != null) {
			res = report.getReaderId();
		}

		return res;
	}

	/**
	 * @param tagReport
	 * @return
	 */
	private boolean isARepetitionOf(ExtendedTagReport tagReport) {

		Queue<ExtendedTagReport> queue = this.lastNotifiedReports.get(tagReport.getReaderId());
		if (queue != null) {
			Iterator<ExtendedTagReport> iterator = queue.iterator();
			while (iterator.hasNext()) {
				boolean equals = areEqual(tagReport, iterator.next());
				if (equals) {
					// avoid loop iterations
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Extracts the ReaderId and the IsFillingStation values from the event
	 * configuration
	 * 
	 * @param configMap
	 * @return
	 */
	private Map<String, Boolean> getAntennaConfigMap(Map<String, EventConfiguration> configMap) {
		Map<String, Boolean> eventConfigMap = Collections.synchronizedMap(new LinkedHashMap<String, Boolean>());
		
		configMap.forEach((key, value) -> {
			eventConfigMap.put((String) value.getReaderId(), value.isFillingStation());
		});
		
		return eventConfigMap;
	}

	@Override
	public void eventConfigurationChanged(Map<String, EventConfiguration> newConfig) {
		this.antennasConfig = getAntennaConfigMap(newConfig);
	//	this.antennaServices.updateAntennasInDatabase(getAntennaConfigMap(newConfig));
	}

}
