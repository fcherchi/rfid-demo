package com.fcherchi.demo.drivers.rfidreader.impl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import com.fcherchi.demo.drivers.rfidreader.impl.SocketManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.fcherchi.demo.drivers.exception.DriverException;

@RunWith(MockitoJUnitRunner.class)
public class SocketManagerTest {

	@Mock
	private Socket socket;

	@Mock
	private SocketFactory socketFactory;

	@Mock
	private InputStream inputStream;
	
	@Mock
	private OutputStream outputStream;
	
	
	//Intentionally not annotated as inject mocks
	SocketManager socketManager = new SocketManager("r1", "anyIp", 1000);
	
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		
		initManualMocks();
	}


	/**
	 * 
	 */
	private void initManualMocks() {
		try {
			Mockito.when(this.socketFactory.createSocket(Mockito.anyString(), Mockito.anyInt())).thenReturn(this.socket);
			Mockito.when(this.socket.getInputStream()).thenReturn(this.inputStream);
			Mockito.when(this.socket.getOutputStream()).thenReturn(this.outputStream);
			
			this.socketManager.setSocketFactory(socketFactory);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testConnect() throws Exception {

		this.socketManager.connect();
		Mockito.verify(this.socketFactory).createSocket("anyIp", 1000);
		Mockito.verify(this.socket).getInputStream();
		Mockito.verify(this.socket).getOutputStream();
		
	}

	@Test
	public void testDisconnect() throws Exception {
		
		//test that disconnect when not connected is ignored (no exception)
		this.socketManager.disconnect();
		
		//test now the proper disconnection
		this.socketManager.connect();
		this.socketManager.disconnect();
		Mockito.verify(this.socket).close();
	}


	@Test
	public void testSend() throws Exception {
		
		byte[] cmd = { 0x01, 0x02 };
		//tests that send without connection throws an exception
		try {
			this.socketManager.send(cmd);
			Assert.fail("We should not get here. Exception was expected");
		} catch (DriverException e) {
			//expected
			Assert.assertTrue("Expected exception when sending without connection.", true);
		}
		
		//tests that sends with connection actually sends the data
		this.socketManager.connect();
		this.socketManager.send(cmd);
		
		Mockito.verify(this.outputStream).write(cmd);
	}


	@Test
	public void testWaitForResponse() throws Exception {
		
		//test that send without connection throws an exception
		try {
			this.socketManager.waitForResponse(1000);
			Assert.fail("We should not get here. Exception was expected");
		} catch (DriverException e) {
			//expected
			Assert.assertTrue("Expected exception when sending without connection.", true);
		}
		
		//tests that response is sent
		this.socketManager.connect();
		Mockito.when(this.inputStream.available()).thenReturn(1);
		//send first 1, then 2, then EOF
		Mockito.when(this.inputStream.read()).thenReturn(1).thenReturn(2).thenThrow(new EOFException());
		byte[] response = this.socketManager.waitForResponse(100);
		
		Assert.assertEquals(1, response[0]);
		Assert.assertEquals(2, response[1]);
	}
	
	@Test
	public void testWaitForResponseWithTerminationToken() throws Exception {
		
		byte[] eof = {(byte)0xAA, (byte)0xCC};
		this.socketManager = new SocketManager("r1", "anyIp", 200, eof);
		initManualMocks();
		//test that send without connection throws an exception
		try {
			this.socketManager.waitForResponse(100);
			Assert.fail("We should not get here. Exception was expected");
		} catch (DriverException e) {
			//expected
			Assert.assertTrue("Expected exception when sending without connection.", true);
		}
		
		//tests that response is sent
		this.socketManager.connect();
		Mockito.when(this.inputStream.available()).thenReturn(1);
		//send first 1, then 2, then termination token
		Mockito.when(this.inputStream.read()).thenReturn(1).thenReturn(2).thenReturn(0xAA).thenReturn(0xCC);
		byte[] response = this.socketManager.waitForResponse(100);
		
		//just check that the loop inside id finished (reponse is obtained)
		Assert.assertEquals(1, response[0]);
		Assert.assertEquals(2, response[1]);
		Assert.assertEquals((byte)0xAA, response[2]);
		Assert.assertEquals((byte)0xCC, response[3]);
	}

}
