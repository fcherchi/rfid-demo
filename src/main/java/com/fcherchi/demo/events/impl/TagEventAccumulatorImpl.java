/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events.impl;

import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;
import com.fcherchi.demo.events.ExtendedTagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Groups the repetitive events of the same tag and antenna into only one event
 * with an accumulated count.
 * 
 * @author Fernando
 *
 */
@Component
public class TagEventAccumulatorImpl implements TagEventAccumulator {

	

	/** The logger */
	final Logger logger = (Logger) LoggerFactory.getLogger(TagEventAccumulatorImpl.class);

	/**
	 * The 'tag reads' grouped by tag epc The first map contains all tag reads
	 * from different readers. The key is the Tag EPC code. The inner map
	 * contains all the tag read per reader. The key is the ReaderId
	 */
	private Map<String, Map<String, ExtendedTagReport>> reads = Collections.synchronizedMap(new HashMap<String, Map<String, ExtendedTagReport>>());

	@Override
	public void addReadTag(String readerId, TagReport tagReport) {

		// if this is the first time this tag is read in any reader, stores it
		// as it is
		if (!this.reads.containsKey(tagReport.getTagEpc())) {
			initialiseInnerMap(this.reads, readerId, tagReport);
		} else {
			
			Map<String, ExtendedTagReport> readsPerReader = this.reads.get(tagReport.getTagEpc());
			// if this is the first time this tag is read on this reader, create
			// a new row
			
			if (!readsPerReader.containsKey(readerId)) {
				addFirstReadForThisReader(readerId, tagReport, readsPerReader);
			} else {
				
				// the tag read already exist for that reader. The Tag counter, RSSI and timestamps should be updated
				updateAccumulatedParams(readerId, tagReport, readsPerReader);
			}
		}
	}

	@Override
	public void purgeTagEpc(String tagEpc) {
		this.reads.remove(tagEpc);
		this.logger.debug("Removed tag from accumulator. Remaining {}.", this.reads.size());
	}

	@Override
	public ExtendedTagReport getBestReport(String tagEpc, String currentReaderId, boolean isFillingStation) {

		// intentionally removed
		return null;
	}

	/**
	 * Updates counter or RSSI (max power signal) if necessary.
	 * @param readerId
	 * @param tagReport
	 * @param readsPerReader
	 */
	private void updateAccumulatedParams(String readerId, TagReport tagReport, Map<String, ExtendedTagReport> readsPerReader) {
		
		ExtendedTagReport alreadyExistentTagReport = readsPerReader.get(readerId);
		
		if (tagReport.isComingMessage()) {
			computeRSSI(alreadyExistentTagReport.getTagReport(), tagReport);
		} else {
			incrementTagCounter(alreadyExistentTagReport.getTagReport(), tagReport);
		}
		alreadyExistentTagReport.getTagReport().setComingMessage(tagReport.isComingMessage());
		alreadyExistentTagReport.setLastSeen(tagReport.getTimestamp());
	}

	/**
     * Creates the initial read for the given reader.
	 * @param readerId
	 * @param tagReport
	 * @param readsPerReader
	 */
	private void addFirstReadForThisReader(String readerId, TagReport tagReport, Map<String, ExtendedTagReport> readsPerReader) {
		
		ExtendedTagReport extendedTagReport = new ExtendedTagReport(readerId, tagReport, tagReport.getTimestamp(), null);
		readsPerReader.put(readerId, extendedTagReport);
	}

	/**
     * Get the best tag report based on count (1st criterion) and RSSI (2nd criterion)
	 * @param bestTagReport
	 * @param comparedOne
	 * @return
	 */
	private ExtendedTagReport getTheBest(ExtendedTagReport bestTagReport, ExtendedTagReport comparedOne) {
		// first time
		ExtendedTagReport res;
		if (bestTagReport == null) {
			res = comparedOne;
			return res;
		}
		// bestTagReport is not null here
		if (comparedOne.getTagReport().getCount() > bestTagReport.getTagReport().getCount()) {
			res = comparedOne;
		} else if (comparedOne.getTagReport().getCount() == bestTagReport.getTagReport().getCount()) {
			// get the best RSSI
			res = getTheBestRSSI(bestTagReport, comparedOne);
		} else {
			res = bestTagReport;
		}
		return res;
	}


	/**
     * Gets the best tag report based on RSSI.
	 * @param bestTagReport
	 * @param comparedOne
	 * @return
	 */
	private ExtendedTagReport getTheBestRSSI(ExtendedTagReport bestTagReport, ExtendedTagReport comparedOne) {
		if (bestTagReport.getTagReport().getRssi() > comparedOne.getTagReport().getRssi()) {
			return bestTagReport;
		} else {
			return comparedOne;
		}

	}

	/**
     * Sets the rssi in the oldReport if it is stronger than current one.
	 * @param oldReport
	 * @param newReport
	 */
	private void computeRSSI(TagReport oldReport, TagReport newReport) {

		if (oldReport.getRssi() < newReport.getRssi()) {
			oldReport.setRssi((byte) newReport.getRssi());
		}

	}

	/**
	 * Increments the counter.
	 * @param oldTagReport
	 * @param newTagReport
	 */
	private void incrementTagCounter(TagReport oldTagReport, TagReport newTagReport) {
		oldTagReport.setCount(oldTagReport.getCount() + newTagReport.getCount());
	}

	/**
	 * Creates the inner map for this reader and tag
	 * 
	 * @param reads
	 * @param readerId
	 * @param tag
	 */
	private void initialiseInnerMap(Map<String, Map<String, ExtendedTagReport>> reads, String readerId, TagReport tag) {

		Map<String, ExtendedTagReport> innerMap = Collections.synchronizedMap(new HashMap<String, ExtendedTagReport>());
		ExtendedTagReport extendedReport = new ExtendedTagReport(readerId, tag, tag.getTimestamp(), null);
		// key of inner map is the antenna id
		innerMap.put(readerId, extendedReport);
		reads.put(tag.getTagEpc(), innerMap);
	}


}
