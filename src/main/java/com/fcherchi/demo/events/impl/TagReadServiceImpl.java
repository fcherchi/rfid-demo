/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events.impl;

import com.fcherchi.demo.config.file.ConfigurationException;
import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;
import com.fcherchi.demo.events.EventProducer;
import com.fcherchi.demo.events.TagReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Fernando
 * Part of the production code but not used in the Demo. Substituted by the Client notificator.
 */

//@Component
public class TagReadServiceImpl implements TagReadService, ReaderListener {

	final Logger logger = (Logger) LoggerFactory.getLogger(TagReadServiceImpl.class);

	@Value("${tag.companyCode}")
	String companyCode; 
	
	@Value("${tag.itemReference}")
	String itemReference; 
	
	@Autowired
	TagEventAccumulator tagAccumulator;
	
	@Autowired
	EventProducer eventProducer;
	
	private long companyCodeAsLong;
	
	private long itemReferenceAsLong;
	
	
	@PostConstruct
	public void initialisation() {
		try {
			this.companyCodeAsLong = Long.parseLong(this.companyCode);
			this.itemReferenceAsLong = Long.parseLong(this.itemReference);
		} catch (Exception e) {
			throw new ConfigurationException("Error parsing configuration.", e);
		}
	}
	
	@PreDestroy
	public void cleanUp() {
		
	}
	
	@Override
	public void onUnknownResponseReceived(String readerId, byte[] data) {
		this.logger.debug("[{}] - Unexpected message received from reader.");
	}

	@Override
	public boolean onTagRead(String readerId, TagReport tagReport) {
		boolean isTagAccepted = false;
		
		this.logger.debug("[{}] - Tag received {}", readerId, tagReport);
		Sgtin96 epcItemTag = null;
		try {
			epcItemTag = new Sgtin96(tagReport.getTagEpc());
			
		} catch (Exception e){
			this.logger.debug("[{}] - Tag is not parseable as epc. {}", e.getMessage());
		}
		if (epcItemTag != null) {
			if (checkCompanyCodeAndItemReference(epcItemTag.getCompanyPrefix(), epcItemTag.getItemReference())) {
				this.tagAccumulator.addReadTag(readerId, tagReport);
				this.eventProducer.tagReadArrived(readerId, tagReport.getTagEpc(), tagReport.isComingMessage());
				isTagAccepted = true;
			}
		}
		return isTagAccepted;
	}

	private boolean checkCompanyCodeAndItemReference(long companyPrefix, long itemReference) {
		
		return companyPrefix == this.companyCodeAsLong && itemReference == this.itemReferenceAsLong;
	}
}
