package com.fcherchi.demo.drivers.rfidreader.impl;
        
import com.fcherchi.demo.drivers.rfidreader.HeartbeatListener;
import com.fcherchi.demo.drivers.rfidreader.ReaderConfig;
import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.drivers.rfidreader.SynchronisedCommandsExecutor;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.time.Instant;




@RunWith(MockitoJUnitRunner.class)
public class DTE820ReaderImplTest {

	final Logger logger = (Logger) LoggerFactory.getLogger(DTE820ReaderImplTest.class);
	
	@Mock
	ReaderListener listener;

	@Mock
	HeartbeatListener heartbeatListener;
	
	@Mock
	SynchronisedCommandsExecutor synchroCommandExecutor;
	
	@Mock
    DTE820GPIOWatcher gpioWatcher;
	
	@InjectMocks
    DTE820ReaderImpl reader = new DTE820ReaderImpl(getReaderConfig(), listener, heartbeatListener);
	
	@Mock
    SocketManager socketManager;
	
	@Mock
	SocketFactory socketFactory;
	
	@Mock
	Socket mockSocket;
	
	@Before
	public void initMocks() throws IOException {
		MockitoAnnotations.initMocks(this);
		reader.msgParser = new DTE820MessageParser("R1");
		this.socketManager.setSocketFactory(this.socketFactory);
		
		Mockito.when(this.synchroCommandExecutor.executeCommand(Mockito.eq(SetExtendedResultFlag.getInstance()), Mockito.eq(new byte[] {TagReportType.RF_0TRA})))
			.thenReturn(new byte[] {0});
		Mockito.when(this.synchroCommandExecutor.executeCommand(Mockito.eq(SetPortPowerCommand.getInstance()), Mockito.any())).thenReturn(new byte[] {0,1});
		Mockito.when(this.synchroCommandExecutor.executeCommand(Mockito.eq(SetMode.getInstance()), Mockito.any())).thenReturn(new byte[] {0});
		Mockito.when(this.synchroCommandExecutor.executeCommand(Mockito.eq(SetParameterById.getInstance()), Mockito.any())).thenReturn(new byte[] {0});
		Mockito.when(this.synchroCommandExecutor.executeCommand(Mockito.eq(SetProfile.getInstance()), Mockito.any())).thenReturn(new byte[] {0});
		Mockito.when(this.synchroCommandExecutor.executeCommand(Mockito.eq(SetTimeCommand.getInstance()), Mockito.any())).thenReturn(new byte[] {0});
		Mockito.when(this.synchroCommandExecutor.executeCommand(Mockito.eq(SetIOCardHwConfig.getInstance()), Mockito.any())).thenReturn(new byte[] {0});
		Mockito.when(this.synchroCommandExecutor.executeCommand(Mockito.eq(GetTemperatureCommand.getInstance()), Mockito.any())).thenReturn(new byte[] {0,30});
		Mockito.when(this.synchroCommandExecutor.executeCommand(Mockito.eq(SetCableLossAndAntennaGain.getInstance()), Mockito.any())).thenReturn(new byte[] {0,30});
		Mockito.when(this.synchroCommandExecutor.executeCommand(Mockito.eq(GetIOCardHwConfig.getInstance()), Mockito.eq(new byte[] { (byte) 0x00 })))
			.thenReturn(new byte[] {0,0,7});
		
		
		Mockito.when(this.socketFactory.createSocket(Mockito.anyString(), Mockito.anyInt())).thenReturn(mockSocket);
	}
	
	/**
	 * @return
	 */
	private ReaderConfig getReaderConfig() {

		ReaderConfig config = new ReaderConfig("id", "ip");
		config.setEnabled(true);
		config.setPort(100);
		config.setUseGPIOTrigger(false);
		
		return config;
	}

	
	@Test
	public void testConnect() throws Exception {
		
		reader.connect();
		Mockito.verify(socketManager).connect();
	}

	@Test
	public void testGetDateTimeInvocation() throws Exception {
		
		
		byte[] dateInfo = {(byte)0xBC, (byte)0x05, (byte)0x57, (byte)0x56};
		Mockito.when(this.synchroCommandExecutor.executeCommand(Mockito.any(GetTimeCommand.class))).thenReturn(dateInfo);
		Instant time = reader.getDateTime();
		Instant expected = Instant.parse("2015-11-26T13:14:36Z");
		Assert.assertEquals(expected, time);
	
	}
	
	
}
