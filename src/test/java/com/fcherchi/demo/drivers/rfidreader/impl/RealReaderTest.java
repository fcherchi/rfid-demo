/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fcherchi.demo.config.ApplicationConfig;
import com.fcherchi.demo.drivers.rfidreader.DTE820Reader;
import com.fcherchi.demo.drivers.rfidreader.HeartbeatListener;
import com.fcherchi.demo.drivers.rfidreader.ReaderConfig;
import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.AntennaGain;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.AntennaPortPower;
import com.fcherchi.demo.readers.impl.RFIDReaderFactoryImpl;

/**
 * @author Fernando
 *
 */


@Configuration
@PropertySource("classpath:config/application.properties")
class TestConfig {
	
}

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {ApplicationConfig.class, TestConfig.class})
public class RealReaderTest {

	private static final String READER_IP = "192.168.255.26";
	private static final String READER_ID = "R1";

	final Logger logger = (Logger) LoggerFactory.getLogger(RealReaderTest.class);
	
	
	@Autowired
    RFIDReaderFactoryImpl readerFactory;
	
	ReaderListener listener = new ReaderListener() {
		
		
		@Override
		public boolean onTagRead(String readerId, TagReport tagReport) {
			logger.debug("{} - {}", readerId, tagReport);
			return true;
		}

		@Override
		public void onUnknownResponseReceived(String readerId, byte[] data) {
			logger.debug("{}", data);
		}

		
	};



	private HeartbeatListener heartbeatListener = new HeartbeatListener() {

		@Override
		public void readerIsAlive(String readerId, int internalTemperature) {
			logger.debug("Reader '{}' is alive. Internal temperature: {} C", readerId, internalTemperature);
		}
		
	
	};



	private ReaderConfig readerConfig;



	private DTE820HeartbeatExecutor heartbeatExecutor;
	
	@Before
	public void initConfig() {
		this.readerConfig = new ReaderConfig(READER_ID, READER_IP);
		this.readerConfig.setPort(4007);
		this.readerConfig.setEnabled(true);
		
		
	}

	@Ignore
	@Test
	public void testGetDate(){

		DTE820Reader reader = new DTE820ReaderImpl(this.readerConfig, this.listener, this.heartbeatListener);
		
		reader.connect();
		
		Instant res = reader.getDateTime();
		logger.debug("################## {}", res);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			reader.disconnect();
		}
	}
	
	@Ignore
	@Test
	public void testStartReading(){
		
		DTE820Reader reader = new DTE820ReaderImpl(this.readerConfig, this.listener, this.heartbeatListener);
		try {
			reader.connect();
			
			//reader.setPortPower(1, 0x84);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {			// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			AntennaPortPower portPower = reader.getPortPower(1);
			logger.debug("########## {}", portPower);
			
			reader.setPortPower(1, 0x22);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			logger.error("Error reading.", e);
			throw e;
		}
		finally {
			try {
				reader.disconnect(); 
			} catch (Exception e) {
				logger.error("Error closing reader. ", e);
			}
		}
	}
	
	@Ignore
	@Test
	public void testGetAntennaGain(){
		
		DTE820Reader reader = new DTE820ReaderImpl(this.readerConfig, this.listener, this.heartbeatListener);
		try {
			reader.connect();
			AntennaGain antennaGain = reader.getAntennaGain(1);
			
			logger.debug("########## {}", antennaGain);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		finally {
			reader.disconnect();
		}
	}
	
	
	@Test
	public void testGetAndSetIOCardHwConfigData() throws Exception{
		
		try {
			DTE820ReaderImpl reader;
			reader = (DTE820ReaderImpl) this.readerFactory.getReaderById("Filling 1");
			//reader.callStopReading();
			
			int ioCardHwConfig = reader.getIOCardHwConfig();
			//Assert.assertEquals(0, ioCardHwConfig);
			
			reader.setModeToGPIOTrigger();
			
			ioCardHwConfig = reader.getIOCardHwConfig();
			Assert.assertEquals(7, ioCardHwConfig);
			
			
			
			
		}
		catch (Exception e) {
			logger.info(e.getMessage());
			throw e;
		}
	}
	
}
