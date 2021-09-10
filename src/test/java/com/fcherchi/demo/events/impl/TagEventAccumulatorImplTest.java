package com.fcherchi.demo.events.impl;

public class TagEventAccumulatorImplTest {

  //  Commented out because of production code removed

//	private TagEventAccumulator accumulator = new TagEventAccumulatorImpl();

//    @Test
//	public void testAdd2ReadsIncreaseQty() throws Exception {
//
//		//after two going reads of the same tag and reader, the quantity should be added
//		accumulator.addReadTag("R1", getGoingReport("EPC1", 10));
//		accumulator.addReadTag("R1", getGoingReport("EPC1", 20));
//
//		TagReport bestReport = accumulator.getBestReport("EPC1", "R1", true).getTagReport();
//		Assert.assertEquals(30, bestReport.getCount());
//	}
//
//    @Ignore
//	@Test
//	public void testAddReadsGetsTheBestRSSI() throws Exception {
//
//		//after two coming reads of the same tag and reader, the higher RSSI should be kept
//		accumulator.addReadTag("R1", getComingReport("EPC1", 40));
//		accumulator.addReadTag("R1", getComingReport("EPC1", 20));
//		accumulator.addReadTag("R1", getComingReport("EPC1", 60));
//
//		ExtendedTagReport bestReport = accumulator.getBestReport("EPC1", "R1", true);
//		Assert.assertEquals(60, bestReport.getTagReport().getRssi());
//	}
//    @Ignore
//	@Test
//	public void testAddReadsDifferentReaderGetsTheBestRSSI() throws Exception {
//
//		//after two coming reads of the same tag and reader, the higher RSSI should be kept
//		accumulator.addReadTag("R1", getComingReport("EPC1", 40));
//		accumulator.addReadTag("R2", getComingReport("EPC1", 60));
//		accumulator.addReadTag("R1", getComingReport("EPC1", 20));
//
//		ExtendedTagReport bestReport = accumulator.getBestReport("EPC1", "R1", true);
//		Assert.assertEquals(60, bestReport.getTagReport().getRssi());
//		Assert.assertEquals("R2", bestReport.getReaderId());
//
//	}
//
//    @Ignore
//	@Test
//	public void testAddReadsDifferentReaderGetsTheBestCount() throws Exception {
//
//		//after two coming reads of the same tag and reader, the higher RSSI should be kept
//		accumulator.addReadTag("R1", getGoingReport("EPC1", 20));
//		accumulator.addReadTag("R2", getGoingReport("EPC1", 60));
//		accumulator.addReadTag("R1", getGoingReport("EPC1", 20));
//
//		ExtendedTagReport bestReport = accumulator.getBestReport("EPC1", "R1", true);
//		Assert.assertEquals(60, bestReport.getTagReport().getCount());
//		Assert.assertEquals("R2", bestReport.getReaderId());
//	}
//
//    @Ignore
//	@Test
//	public void testAddReadsDifferentReaderGetsThePrinterCount() throws Exception {
//
//		//after two coming reads of the same tag and reader, the higher RSSI should be kept
//		accumulator.addReadTag("R1", getGoingReport("EPC1", 20));
//		accumulator.addReadTag("R2", getGoingReport("EPC1", 60));
//		accumulator.addReadTag("R1", getGoingReport("EPC1", 30));
//		accumulator.addReadTag("R3", getGoingReport("EPC1", 10));
//
//		//competitions are not relevant here, we expect to have the printer event
//		//(printer or output events) they don't compete. they are just persisted
//		ExtendedTagReport bestReport = accumulator.getBestReport("EPC1", "R3", false);
//		Assert.assertEquals(10, bestReport.getTagReport().getCount());
//		Assert.assertEquals("R3", bestReport.getReaderId());
//	}
//    @Ignore
//	@Test
//	public void testAddReadsComingAndGoingGetsTheTimestamps() throws Exception {
//
//		//after two coming reads of the same tag and reader, the higher RSSI should be kept
//		TagReport comingReport = getComingReport("EPC1", 20);
//		accumulator.addReadTag("R1", comingReport);
//
//		Thread.sleep(100);
//
//		TagReport goingReport = getGoingReport("EPC1", 60);
//		accumulator.addReadTag("R1", goingReport);
//
//		ExtendedTagReport bestReport = accumulator.getBestReport("EPC1", "R1", true);
//
//		Assert.assertEquals(true, bestReport.getFirstSeen().isBefore(bestReport.getLastSeen()));
//		Assert.assertEquals(comingReport.getTimestamp(), bestReport.getFirstSeen());
//		Assert.assertEquals(goingReport.getTimestamp(), bestReport.getLastSeen());
//	}
//
//
//
//
//	private TagReport getComingReport(String tagEpc, int rssi) {
//		return this.getTagReport(tagEpc, true, 0, rssi);
//	}
//
//	private TagReport getGoingReport(String tagEpc, int count) {
//		return this.getTagReport(tagEpc, false, count, 0);
//	}
//
//	/**
//	 *
//	 * @param tagEpc
//	 * @param isComingMessage
//	 * @param count
//	 * @param rssi
//	 * @return
//	 */
//	private TagReport getTagReport(String tagEpc, boolean isComingMessage, int count, int rssi) {
//
//		TagReport res = new TagReport();
//		res.setAntennaPort((byte) 1);
//		res.setComingMessage(isComingMessage);
//		res.setCount(count);
//		res.setExtendedResultFlag((byte) 7);
//		res.setRssi((byte)rssi);
//		res.setTagEpc(tagEpc);
//		res.setTimestamp(Instant.now());
//
//		return res;
//	}

}
