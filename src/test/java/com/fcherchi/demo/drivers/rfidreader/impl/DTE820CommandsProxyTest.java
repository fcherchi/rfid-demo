package com.fcherchi.demo.drivers.rfidreader.impl;

import com.fcherchi.demo.drivers.rfidreader.impl.DTE820CommandsProxy;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fcherchi.demo.drivers.rfidreader.ReaderListener;

public class DTE820CommandsProxyTest {

	@Mock
	ReaderListener listener;
	
	@InjectMocks
    DTE820CommandsProxy proxy;
	
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	

	/**
	 * @return
	 */
	private byte[] getStartInventoryFrameResponse() {
		// 0xAA 0xBB 0x01 0x01 0x11 0x81 0x00 0xAA 0xCC 
		return new byte[] {0x00};
	}

	/**
	 * @return
	 */
	private byte[] getDateTimeFrameResponse() {
		// AA BB 01 01 26 80 E4 C9 4D 56 AA CC
		return new byte[] {0x29, (byte) 0xCA, 0x4D, 0x56};
		
	}

}
