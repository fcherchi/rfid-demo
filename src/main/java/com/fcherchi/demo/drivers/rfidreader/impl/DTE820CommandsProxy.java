/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import com.fcherchi.demo.drivers.rfidreader.GPIOSignalListener;
import com.fcherchi.demo.drivers.rfidreader.HeartbeatListener;
import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.gpio.IOData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fcherchi.demo.drivers.rfidreader.commands.impl.AsyncStopCommand;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GPIOGetIOData;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GPIOSetOutput;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetCableLossAndAntennaGain;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetEPCAsyncResponse;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetIOCardHwConfig;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetParameterById;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetPortPowerCommand;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetTemperatureCommand;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetTimeCommand;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.SaveConfigurationCommand;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.SetCableLossAndAntennaGain;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.SetExtendedResultFlag;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.SetIOCardHwConfig;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.SetMode;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.SetParameterById;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.SetPortPowerCommand;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.SetProfile;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.SetTimeCommand;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.StartInventoryCommand;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.SyncGetEPC;

/**
 * @author Fernando
 *
 */

public class DTE820CommandsProxy {
	
	
	/** The main listener for the reader functions */
	private ReaderListener listener;
	
	/** The listener for the heartbeat */
	private HeartbeatListener hearbeatListener;
	
	/** Logger */
	final Logger logger = (Logger) LoggerFactory.getLogger(DTE820CommandsProxy.class);

	/** Id of the reader */
	private String readerId;

	/** GPIO Signal Listener */
	private GPIOSignalListener gpioListener;

	/** Listener for the "synchronous" EPC tag (used when GPIO trigger is enabled) */
	private ReaderListener synchronousListener;
	
	
	/**
	 * Constructor 
	 * 
	 */
	public DTE820CommandsProxy(ReaderListener listener, HeartbeatListener heartbeatListener, String readerId, GPIOSignalListener gpioListener, 
			ReaderListener synchronousListener) {
		this.listener = listener;
		this.readerId = readerId;
		this.hearbeatListener = heartbeatListener;
		this.gpioListener = gpioListener;
		this.synchronousListener = synchronousListener;
	}
	
	

	/**
	 * @param responseId
	 * @param data
	 */
	public void treatResponse(short responseId, byte[] data) {
		
		logger.debug("{} - {}", this.readerId, (String.format("Received response with the code 0x%04X", responseId)));
		switch (responseId) {
			case GetTimeCommand.RESPONSE_AS_SHORT:
			case SetTimeCommand.RESPONSE_AS_SHORT:
			case SetExtendedResultFlag.RESPONSE_AS_SHORT:
			case GetPortPowerCommand.RESPONSE_AS_SHORT:
			case SetPortPowerCommand.RESPONSE_AS_SHORT:
			case SetCableLossAndAntennaGain.RESPONSE_AS_SHORT:
			case GetCableLossAndAntennaGain.RESPONSE_AS_SHORT:
			case SaveConfigurationCommand.RESPONSE_AS_SHORT:
			case SetMode.RESPONSE_AS_SHORT:
			case GetParameterById.RESPONSE_AS_SHORT:
			case SetProfile.RESPONSE_AS_SHORT:
			case SetParameterById.RESPONSE_AS_SHORT:
			case GetIOCardHwConfig.RESPONSE_AS_SHORT:
			case SetIOCardHwConfig.RESPONSE_AS_SHORT:
			case GPIOSetOutput.RESPONSE_AS_SHORT:
				//synchronous responses are accepted but no action taken
				this.logger.debug("[{}] - Synchronous message received {} ", this.readerId, ByteUtils.getHexString(data));
			break;
			case SyncGetEPC.RESPONSE_AS_SHORT:
				this.logger.debug("[{}] - Synchronous message received {} ", this.readerId, ByteUtils.getHexString(data));
				notifyNewSynchronousTag(data);
				break;
			case StartInventoryCommand.RESPONSE_AS_SHORT:
				notifyStartInventoryResponse(data);
			break;
			case GetEPCAsyncResponse.RESPONSE_AS_SHORT:
				notifyNewTag(data);
				break;
			case AsyncStopCommand.RESPONSE_AS_SHORT:
				notifyReaderStopped(data);
				break;
			case GetTemperatureCommand.RESPONSE_AS_SHORT:
				notifyGetTempResponse(data);
				break;
			case GPIOGetIOData.RESPONSE_AS_SHORT:
				notifyGetIOData(data);
				break;
			default:
				invokeUnkwownCommand(responseId, data);
				
		}
	}

	


	/**
	 * @param data
	 */
	private void notifyGetIOData(byte[] data) {
		if (this.gpioListener == null) {
			this.logger.warn("GPIO Data received but no listener attached.");
		} else {
			IOData ioData = GPIOGetIOData.getInstance().parseResponse(this.readerId, data);
			this.gpioListener.signalsReceived(ioData);
		}
	}



	private void notifyGetTempResponse(byte[] data) {
		
		if (this.hearbeatListener != null) {
			Integer temp = GetTemperatureCommand.getInstance().parseResponse(this.readerId, data);
			this.hearbeatListener.readerIsAlive(this.readerId, temp);
		}
	}



	


	/**
	 * @param data
	 */
	private void notifyReaderStopped(byte[] data) {
		
		AsyncStopCommand.getInstance().parseResponse(this.readerId, data);
		this.logger.info("{} - Reader has been stoped successfully", this.readerId);
	}



	/**
	 * @param data
	 */
	private void notifyStartInventoryResponse(byte[] data) {
		
		StartInventoryCommand.getInstance().parseResponse(this.readerId, data);
		logger.debug("{} - StartInventoryCommand executed successfully.", this.readerId);
	}

	
	private void notifyNewSynchronousTag(byte[] data) {
		
		TagReport tagReport = SyncGetEPC.getInstance().parseResponse(this.readerId, data);
		if (tagReport != null) {
			logger.debug("[{}] New Tag received. '{}'", this.readerId, ByteUtils.getHexString(data));
			if (this.synchronousListener != null) {
				this.synchronousListener.onTagRead(this.readerId, tagReport);
			} else {
				logger.warn("[{}] - Tag read but no listener attached", this.readerId);
			}
		}
	}
	
	private void notifyNewTag(byte[] data) {
		
		TagReport tagReport = GetEPCAsyncResponse.getInstance().parseResponse(this.readerId, data);
		logger.debug("[{}] New Tag received. '{}'", this.readerId, ByteUtils.getHexString(data));
		if (this.listener != null) {
			this.listener.onTagRead(this.readerId, tagReport);
		} else {
			logger.warn("[{}] - Tag read but no listener attached", this.readerId);
		}
	}

	/**
	 * @param data
	 * @param responseId 
	 */
	private void invokeUnkwownCommand(short responseId, byte[] data) {
		
		logger.warn("{} - Received command without a proper proxy method. ResponseID '{}', Data: {}",
				this.readerId, String.format("0x%04x", responseId), ByteUtils.getHexString(data));
		if (this.listener != null) {
			this.listener.onUnknownResponseReceived(this.readerId, data);
		} 
	}
}
