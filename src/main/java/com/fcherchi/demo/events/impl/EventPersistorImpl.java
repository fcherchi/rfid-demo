/**
 * Just for demo purposes


 */

package com.fcherchi.demo.events.impl;

import com.fcherchi.demo.events.ExtendedTagReport;
import com.fcherchi.demo.events.EventPersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Fernando
 *
 */
@Component
public class EventPersistorImpl implements EventPersistor {
	


	final Logger logger = (Logger) LoggerFactory.getLogger(EventPersistorImpl.class);

	@Override
	public boolean persistEvent(ExtendedTagReport tagReport, boolean isFillingStation) {
		logger.info("PERSISTING..... {}", tagReport);

		// code removed.
		return true;
	}
	


	

}
