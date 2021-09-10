package com.fcherchi.demo.readers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fcherchi.demo.readers.impl.RFIDReaderFactoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fcherchi.demo.drivers.rfidreader.DTE820Reader;
import com.fcherchi.demo.drivers.rfidreader.HeartbeatListener;
import com.fcherchi.demo.drivers.rfidreader.ReaderConfig;
import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;

@Ignore
public class RFIDReaderFactoryTest {
	
	
	RFIDReaderFactoryImpl factory;
	
	@Before
	public void init() {
		factory = new RFIDReaderFactoryImpl();
	}

	@Test
	public void testReaderConstruction() {
		
		Map<String, Object> config = createMap();
		factory.instantiateReaders(config, new ReaderListener() {
			
			@Override
			public void onUnknownResponseReceived(String readerId, byte[] data) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean onTagRead(String readerId, TagReport tagReport) {
				// TODO Auto-generated method stub
				return true;
			}
		}, new HeartbeatListener() {
			
			@Override
			public void readerIsAlive(String readerId, int internalTemperature) {
				// TODO Auto-generated method stub
				
			}
		});
		Map<String, DTE820Reader> readers = factory.getReaders();
		
		Assert.assertEquals(2, readers.size());
		Assert.assertEquals("theId: 1", readers.get(0).getReaderId());
	}
	
	/**
	 * 
	 * "readers":[
	 {
      "ip": "192.168.255.25",
      "id": "Filling 1",
      "power":"0x44",
      "rssi":"12"
	 },
	 {
      "ip": "192.168.255.26",
      "id": "Filling 2",
      "power":"0x44",
      "rssi":"26"
	 }]
	 * 
	 * 
	 * 
	 * Creates the map
	 * @return
	 */
	private Map<String, Object> createMap() {
		
		Map<String, Object> res = new HashMap<String, Object>();
		List<Map<String, String>>list = new ArrayList<Map<String, String>>();
		
		list.add(getReaderMap(1));
		list.add(getReaderMap(2));
		res.put(ReaderConfig.READERS, list);
		
		return res;
	}

	/**
	 * @param i
	 * @return
	 */
	private Map<String, String> getReaderMap(int i) {
		
		Map<String, String> res = new HashMap<String, String>();
		res.put(ReaderConfig.IP, "theIp: " + i);
		res.put(ReaderConfig.ID, "theId: " + i);
		res.put(ReaderConfig.POWER, "0x44");
		res.put(ReaderConfig.ANTENNA_GAIN, "26");
		res.put(ReaderConfig.RSSI_THRESHOLD, "0");
		res.put(ReaderConfig.PORT, "4007");
		
		return res;
	}
	
}
