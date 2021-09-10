/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events.impl;

import com.fcherchi.demo.config.file.ConfigurationException;
import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;
import com.fcherchi.demo.events.EventConfigurationProvider;
import com.fcherchi.demo.events.EventProducer;
import com.fcherchi.demo.events.ExtendedTagReport;
import com.fcherchi.demo.events.TagReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Service that process a tag read from a synchronous mode (Triggered by the GPIO)
 * @author Fernando
 *
 */
@Component
public class SynchroTagReadServiceImpl implements TagReadService {

	/** The logger */
	final Logger logger = (Logger) LoggerFactory.getLogger(TagReadServiceImpl.class);

	@Value("${tag.companyCode}")
	String companyCode; 
	
	@Value("${tag.itemReference}")
	String itemReference; 
	
	@Autowired
	private EventConfigurationProvider configProvider;

    /** Keep a copy as long to improve performance */
	private long companyCodeAsLong;

    /** Keep a copy as long to improve performance */
	private long itemReferenceAsLong;

	@Autowired
	private EventProducer eventProducer;
	
	@PostConstruct
	public void initialisation() {
		try {
			this.companyCodeAsLong = Long.parseLong(this.companyCode);
			this.itemReferenceAsLong = Long.parseLong(this.itemReference);
		} catch (Exception e) {
			throw new ConfigurationException("Error parsing configuration.", e);
		}
	}


    /**
     * Triggered when a tag was read.
     * @param readerId
     * @param tagReport
     * @return
     */
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
			boolean isTagOfThisCompany = checkCompanyCodeAndItemReference(epcItemTag.getCompanyPrefix(), epcItemTag.getItemReference());
			
			if (isTagOfThisCompany) {
				
				EventConfiguration readerConfig = this.configProvider.getConfigForReader(readerId);
				ExtendedTagReport extendedReport = new ExtendedTagReport(readerId, tagReport, tagReport.getTimestamp(), tagReport.getTimestamp());
				
				this.eventProducer.triggerEventGeneration(extendedReport, readerConfig.isFillingStation());
				isTagAccepted = true;
			} else {
				throw new TagFromDifferentCompanyException();
			}
			
		}
		return isTagAccepted;
	}

	/**
     * Checks if the tag belongs to the same company
	 * @param companyPrefix
	 * @param itemReference
	 * @return
	 */
	private boolean checkCompanyCodeAndItemReference(long companyPrefix, long itemReference) {
		
		return companyPrefix == this.companyCodeAsLong && itemReference == this.itemReferenceAsLong;
	}
}
