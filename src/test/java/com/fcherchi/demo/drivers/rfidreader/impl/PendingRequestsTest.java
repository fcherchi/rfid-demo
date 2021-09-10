package com.fcherchi.demo.drivers.rfidreader.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fcherchi.demo.drivers.rfidreader.impl.PendingRequests;
import com.fcherchi.demo.drivers.rfidreader.impl.TimeoutNotificationListener;
import org.junit.Test;
import org.mockito.Mockito;

import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetTemperatureCommand;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetTimeCommand;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.SetExtendedResultFlag;

public class PendingRequestsTest {

	@Test
	public void testAddNewRequest() throws Exception {
		
		PendingRequests requests = createInstance();
		
		Instant now = Instant.now();
		requests.addNewRequest(GetTimeCommand.RESPONSE_AS_SHORT, now);
		Instant later = now.plusMillis(2000);
		
		TimeoutNotificationListener listener = Mockito.mock(TimeoutNotificationListener.class);
		requests.checkTimeouts(listener , later);
		
		Mockito.verify(listener).timeoutOccurred(GetTimeCommand.RESPONSE_AS_SHORT);
	}

	
	/**
	 * @return
	 */
	private PendingRequests createInstance() {
		
		List<Short> commands = new ArrayList<Short>();
		commands.add(GetTimeCommand.RESPONSE_AS_SHORT);
		commands.add(GetTemperatureCommand.RESPONSE_AS_SHORT);
		commands.add(SetExtendedResultFlag.RESPONSE_AS_SHORT);
		
		PendingRequests requests = new PendingRequests("R1", 1000, commands);
		
		return requests;
	}
	
}
