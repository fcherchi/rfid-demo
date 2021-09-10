/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import com.fcherchi.demo.drivers.rfidreader.SynchronisedCommandsExecutor;
import com.fcherchi.demo.drivers.rfidreader.commands.Command;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Executor of synchronous commands. Throws timeouts if necessary.
 *
 * @author Fernando
 *
 */
public class SynchronisedCommandsExecutorImpl implements SynchronisedCommandsExecutor, TimeoutNotificationListener {

	private PendingRequests pendingRequests;

	final Logger logger = (Logger) LoggerFactory.getLogger(SynchronisedCommandsExecutorImpl.class);
	List<Short> commands = new ArrayList<Short>();

	AtomicBoolean timeout = new AtomicBoolean(false);
	AtomicBoolean response = new AtomicBoolean(false);
	private String readerId;
	private SocketManager socketManager;

	private DTE820MessageParser msgParser;

	private byte[] responseData;

	private volatile boolean stop;

	public SynchronisedCommandsExecutorImpl(String readerId, SocketManager socketManager, int readersTimeout) {

		// "synchronous" commands should be watched in case timeout
        // TODO this can be improved by create an annotation (something like @SynchronousCommand) and use it in the commands.
		this.commands =
        Arrays.asList(
                SetExtendedResultFlag.RESPONSE_AS_SHORT,
                GetTimeCommand.RESPONSE_AS_SHORT,
                SetTimeCommand.RESPONSE_AS_SHORT,
		        GetPortPowerCommand.RESPONSE_AS_SHORT,
		        SetPortPowerCommand.RESPONSE_AS_SHORT,
		        SetCableLossAndAntennaGain.RESPONSE_AS_SHORT,
		        GetCableLossAndAntennaGain.RESPONSE_AS_SHORT,
		        SaveConfigurationCommand.RESPONSE_AS_SHORT,
		        SetMode.RESPONSE_AS_SHORT,
		        GetParameterById.RESPONSE_AS_SHORT,
		        SetParameterById.RESPONSE_AS_SHORT,
		        SetProfile.RESPONSE_AS_SHORT,
		        GPIOGetIOData.RESPONSE_AS_SHORT,
		        GetIOCardHwConfig.RESPONSE_AS_SHORT,
		        SetIOCardHwConfig.RESPONSE_AS_SHORT,
		        SyncGetEPC.RESPONSE_AS_SHORT);
		
		this.readerId = readerId;
		this.socketManager = socketManager;
		this.msgParser = new DTE820MessageParser(readerId);
		
		this.pendingRequests = new PendingRequests(readerId, readersTimeout, commands);
	}
	
	@Override
	@Async
	public void checkTimeouts() {
		this.stop = false;
		while (! stop) {
			this.pendingRequests.checkTimeouts(this, Instant.now());
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				logger.error("[{}] - Error waiting in the timeout checker thread", this.readerId);
			}
		}
		//to unlock waiting
		this.timeout.set(true);
	}

	@Override
	public byte[] executeCommand(Command<?> cmd, byte[] params) {

		//synchonised as we don't want conflicts between different threads..
		//if two threads calls a command the second one has to wait
		if (!this.commands.contains(cmd.getResponseAsShort())) {
			throw new RuntimeException(String.format("Synchronous command not supported. 0x%02X", cmd.getResponseAsShort()));
		}

		this.timeout.set(false);
		this.response.set(false);

		byte[] fullCmd = this.msgParser.getFullFrameCommand(cmd.getCommand(), params);
		this.socketManager.send(fullCmd);
		String cmdDescription = String.format("0x%02X", cmd.getResponseAsShort());

		this.pendingRequests.addNewRequest(cmd.getResponseAsShort(), Instant.now());
		
		this.logger.debug("[{}] / {} / {} - Executed Synchro command with ID {}",
				this.readerId, 
				Thread.currentThread().getName(),
				System.identityHashCode(this),
				cmdDescription);

		while (!timeout.get() && !response.get()) {

			// waiting
			logger.debug("[{}] - Waiting response for command {}", this.readerId, cmdDescription);
			try {
				Thread.sleep(200);

			} catch (InterruptedException e) {
			}

		}
		if (this.timeout.get()) {
			this.logger.error(String.format("[%s] - Timeout after execution of command %s.", this.readerId, cmdDescription));
			return this.responseData;
		} else {
			return this.responseData;
		}
	}
	
	@Override
	public void responseReceived(short responseId, byte[] data) {

		// if the response is in the list of pending requests
		if (this.pendingRequests.responseReceived(responseId)) {
			synchronized (this.response) {
				this.responseData = data;
				this.response.set(true);
			}
		}
	}

	@Override
	public void timeoutOccurred(Short responseId) {

		this.logger.debug("Timed out {}", String.format("0x%02X", responseId));
		this.timeout.set(true);
	}

	@Override
	public byte[] executeCommand(Command<?> cmd) {
		return executeCommand(cmd, null);
	}

	@Override
	public void stopTimeoutChecker() {
		this.stop = true;
	}
}
