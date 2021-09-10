//package com.fcherchi.demo.events.impl;

//@RunWith(MockitoJUnitRunner.class)
//public class EventProducerImplTest {
//
//	@Mock
//	private TagEventAccumulatorImpl tagFiltering;
//
//	@Mock
//	private ConfigurationProvider configProvider;
//
//	@Mock
//	private TagEventAccumulator tagEventAccumulator;
//
//	@Mock
//	private EventRepository eventRepository;
//
//	@Mock
//	private AntennaServices antennaServices;
//
//	@Mock
//	private BarcodeServices barcodeServices;
//
//	@Mock
//	private GenerateEventTrigger generateEventTrigger;
//
//	@Mock
//	private EventConfigurationProvider eventConfigProvider;
//
//	@InjectMocks
//	private EventPersistorImpl eventPersistor;
//
//
//	@InjectMocks
//	private EventProducerImpl producer;
//
//	@Before
//	public void setup() {
//		Map<String, Object> configMap = new HashMap<String, Object>();
//
//		List<Map<String, Object>> listOfConfig = new ArrayList<Map<String, Object>>();
//
//		Map<String, Object> r1 = new HashMap<String, Object>();
//		r1.put(EventConfiguration.COMPUTATION_TIME, 1000);
//		r1.put(EventConfiguration.COUNT_THRESHOLD, 10);
//		r1.put(EventConfiguration.IS_FILLING_STATION, true);
//		r1.put(EventConfiguration.READER_ID, "R1");
//
//		Map<String, Object> o1 = new HashMap<String, Object>();
//		o1.put(EventConfiguration.COMPUTATION_TIME, 1000);
//		o1.put(EventConfiguration.COUNT_THRESHOLD, 10);
//		o1.put(EventConfiguration.IS_FILLING_STATION, false);
//		o1.put(EventConfiguration.READER_ID, "O1");
//
//		listOfConfig.add(r1);
//		listOfConfig.add(o1);
//
//
//		configMap.put(EventConfiguration.EVENTS, listOfConfig);
//
//		Mockito.when(this.configProvider.getConfigurationMap(Mockito.anyString())).thenReturn(configMap);
//
//
//		Antenna antenna = new Antenna();
//		antenna.setId(1L);
//		antenna.setProductId(1L);
//		Mockito.when(this.antennaServices.findAntennaByBusinessId("R1")).thenReturn(antenna);
//
//		Antenna antenna2 = new Antenna();
//		antenna2.setId(2L);
//		antenna2.setProductId(2L);
//		Mockito.when(this.antennaServices.findAntennaByBusinessId("R2")).thenReturn(antenna2);
//
//		this.producer.eventPersistor = this.eventPersistor;
//		this.producer.initialisation();
//	}
//
//	@Test
//	public void testTagReadArrived() throws Exception {
//
//		ExtendedTagReport report = new ExtendedTagReport("R1", getTagReport("EPC1"), Instant.now(), Instant.now());
//
//		Mockito.when(this.tagEventAccumulator.getBestReport("EPC1", "R1", true)).thenReturn(report);
//		this.producer.tagReadArrived("R1", "EPC1", true);
//
//		Mockito.verify(this.generateEventTrigger).addEvent(report);
//	}
//
//
//
//	private ExtendedTagReport getExtendedTagReport(TagReport tagReport, String readerId) {
//
//		ExtendedTagReport report = new ExtendedTagReport(readerId, tagReport, Instant.now(), Instant.now());
//
//		return report;
//	}
//
//
//	private TagReport getTagReport(String tagEPC) {
//
//		TagReport tagReport = new TagReport();
//		tagReport.setAntennaPort((byte)1);
//		tagReport.setComingMessage(true);
//		tagReport.setCount(0);
//		tagReport.setExtendedResultFlag((byte)7);
//		tagReport.setRssi((byte)55);
//		tagReport.setTagEpc(tagEPC);
//		tagReport.setTimestamp(Instant.now());
//
//		return tagReport;
//
//	}
//
//	@Test
//	public void testTriggerEventGeneration() throws Exception {
//
//		ExtendedTagReport reportR1 = getExtendedTagReport(getTagReport("EPC1"), "R1");
//		ExtendedTagReport reportR2 = getExtendedTagReport(getTagReport("EPC2"), "R2");
//
//		Mockito.when(this.tagEventAccumulator.getBestReport("EPC1", "R1", true)).thenReturn(reportR1);
//		Mockito.when(this.tagEventAccumulator.getBestReport("EPC2", "R2", true)).thenReturn(reportR2);
//
//
//		this.producer.triggerEventGeneration(reportR1, false);
//		this.producer.triggerEventGeneration(reportR2, false);
//		this.producer.triggerEventGeneration(reportR1, false);
//
//		Mockito.verify(this.eventRepository).findLastNotPrintedEventByTag("EPC1");
//		Mockito.verify(this.eventRepository).findLastNotPrintedEventByTag("EPC2");
//	}
//
//	@Test
//	public void testTriggerEventGenerationWithSeveralInMemory() throws Exception {
//
//		ExtendedTagReport report1 = getExtendedTagReport(getTagReport("EPC1"), "R1");
//		ExtendedTagReport report2 = getExtendedTagReport(getTagReport("EPC2"), "R1");
//		ExtendedTagReport report3 = getExtendedTagReport(getTagReport("EPC3"), "R1");
//		ExtendedTagReport report4 = getExtendedTagReport(getTagReport("EPC4"), "R1");
//		ExtendedTagReport report5 = getExtendedTagReport(getTagReport("EPC5"), "R1");
//		ExtendedTagReport report6 = getExtendedTagReport(getTagReport("EPC6"), "R1");
//
//
//		Mockito.when(this.tagEventAccumulator.getBestReport("EPC1", "R1", true)).thenReturn(report1);
//		Mockito.when(this.tagEventAccumulator.getBestReport("EPC2", "R1", true)).thenReturn(report2);
//		Mockito.when(this.tagEventAccumulator.getBestReport("EPC3", "R1", true)).thenReturn(report3);
//		Mockito.when(this.tagEventAccumulator.getBestReport("EPC4", "R1", true)).thenReturn(report4);
//		Mockito.when(this.tagEventAccumulator.getBestReport("EPC5", "R1", true)).thenReturn(report5);
//		Mockito.when(this.tagEventAccumulator.getBestReport("EPC6", "R1", true)).thenReturn(report6);
//
//
//		this.producer.triggerEventGeneration(report1, false);
//		this.producer.triggerEventGeneration(report2, false);
//		this.producer.triggerEventGeneration(report3, false);
//		this.producer.triggerEventGeneration(report4, false);
//		this.producer.triggerEventGeneration(report5, false);
//		this.producer.triggerEventGeneration(report6, false);
//		//repetition
//		this.producer.triggerEventGeneration(report2, false);
//
//		Mockito.verify(this.eventRepository).findLastNotPrintedEventByTag("EPC1");
//		Mockito.verify(this.eventRepository).findLastNotPrintedEventByTag("EPC2");
//		Mockito.verify(this.eventRepository).findLastNotPrintedEventByTag("EPC3");
//	}
//
//
//}
