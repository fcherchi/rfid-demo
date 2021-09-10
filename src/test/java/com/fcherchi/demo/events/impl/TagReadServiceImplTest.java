package com.fcherchi.demo.events.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;
import com.fcherchi.demo.events.EventProducer;

@RunWith(MockitoJUnitRunner.class)
public class TagReadServiceImplTest {
	@Mock
	private TagEventAccumulatorImpl tagFiltering;
	
	@Mock
	private EventProducer eventProducer;
	
	
	@InjectMocks
	private TagReadServiceImpl tagReadServiceImpl;
	
	@Before
	public void setup() {
		
	}

	@Test
	public void testOnTagRead() throws Exception {
		
		this.tagReadServiceImpl.companyCode = "8716901";
		this.tagReadServiceImpl.itemReference = "0";
		
		TagReport tagReport = TagReportUtils.createTagReportComing("30240010A04CA000000002BE");
		
		this.tagReadServiceImpl.initialisation();
		this.tagReadServiceImpl.onTagRead("R1", tagReport);
		Mockito.verify(this.tagFiltering).addReadTag("R1", tagReport);
		
		
		tagReport = TagReportUtils.createTagReportComing("30240000A04CA000000002BE");
		this.tagReadServiceImpl.onTagRead("R1", tagReport);
		Mockito.verify(this.tagFiltering, Mockito.never()).addReadTag("R1", tagReport);
	}
	
	

}
