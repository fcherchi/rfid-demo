package com.fcherchi.demo.events.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.fcherchi.demo.config.file.ConfigurationProvider;
import com.fcherchi.demo.config.file.FileChangedWatcher;
import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;
import com.fcherchi.demo.events.EventConfigurationProvider;
import com.fcherchi.demo.events.ExtendedTagReport;
import com.fcherchi.demo.events.GenerateEventListener;
import com.fcherchi.demo.events.GenerateEventTrigger;

@Configuration
@EnableAsync
@PropertySource("classpath:config/application.properties")
class TestConfig {
	@Bean
	FileChangedWatcher fileChangedWatcher() {
		FileChangedWatcher mock = Mockito.mock(FileChangedWatcher.class);
		return mock;
	}
	
	@Bean
	EventConfigurationProvider eventConfigProvider() {
		EventConfigurationProvider mock = Mockito.mock(EventConfigurationProviderImpl.class);
		
		EventConfiguration r1Config = new EventConfiguration();
		r1Config.setFillingStation(true);
		r1Config.setReaderId("R1");
		
		Mockito.when(mock.getConfigForReader("R1")).thenReturn(r1Config);
		
		return mock;
	}
}

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestConfig.class,
		ConfigurationProvider.class,
		GenerateEventTriggerImpl.class, TagEventAccumulatorImpl.class })

public class GenerateEventTriggerImplTest {

	@Autowired
	private GenerateEventTrigger eventTrigger;

	/** The logger */
	final Logger logger = (Logger) LoggerFactory.getLogger(GenerateEventTriggerImplTest.class);

	private GenerateEventListener eventListener = Mockito.mock(GenerateEventListener.class);

	@Before
	public void setUp() throws Exception {
		this.eventTrigger.initialise( this.eventListener);
	}

	@Test
	public void testAddEvent() throws Exception {

		this.logger.debug("Starting the watch");
		this.eventTrigger.startWatching();
		try {
			this.eventTrigger.addEvent(getExtendedTagReportComing("30240010A04CA000000002BE"));
			this.eventTrigger.addEvent(getExtendedTagReportGoing("30240010A04CA000000002BE", 33));
			this.eventTrigger.addEvent(getExtendedTagReportComing("30240010A04CA000000002BE"));
			this.eventTrigger.addEvent(getExtendedTagReportGoing("30240010A04CA000000002BE", 109));
			Thread.sleep(450);
			
			Mockito.verify(this.eventListener).triggerEventGeneration(Mockito.any(ExtendedTagReport.class), Mockito.eq(true));
			
		} finally {
			this.eventTrigger.stop();
		}
	}

	private Map<String, Object> getEventsConfig() {
		// "events" : [
		// {
		// "readerId" : "Filling 1",
		// "computationTimeMs" : 10000,
		// "countThreshold": 50,
		// "isFillingStation" : true
		// }]

		Map<String, Object> res = new HashMap<String, Object>();

		List<Map<String, Object>> listOfConfig = new ArrayList<Map<String, Object>>();
		Map<String, Object> oneReaderConfig = new HashMap<String, Object>();
		oneReaderConfig.put(EventConfiguration.READER_ID, "R1");
		oneReaderConfig.put(EventConfiguration.COMPUTATION_TIME, 100);
		oneReaderConfig.put(EventConfiguration.COUNT_THRESHOLD, 10);
		oneReaderConfig.put(EventConfiguration.IS_FILLING_STATION, true);

		listOfConfig.add(oneReaderConfig);

		res.put(EventConfiguration.EVENTS, listOfConfig);
		return res;
	}

	private ExtendedTagReport getExtendedTagReportComing(String tag) {

		TagReport report = TagReportUtils.createTagReportComing(tag);

		ExtendedTagReport res = new ExtendedTagReport("R1", report, Instant.now(), Instant.now());
		return res;
	}

	private ExtendedTagReport getExtendedTagReportGoing(String tag, int count) {

		TagReport report = TagReportUtils.createTagReportGoing(tag, count);

		ExtendedTagReport res = new ExtendedTagReport("R1", report, Instant.now(), Instant.now());
		return res;
	}

}
