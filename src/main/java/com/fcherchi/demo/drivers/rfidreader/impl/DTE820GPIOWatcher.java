/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fcherchi.demo.drivers.rfidreader.commands.impl.GPIOGetIOData;

/**
 * It watches for GPIO signals.
 *
 * In production, the reader is activated by a sensor and the communication
 * between the reader and the sensor occurs via GPIO signals.
 *
 * This artifact polls the reader to react if a GPIO signal has been sent (from the sensor).
 *
 * There is one watcher per reader (scope prototype and being constructed depending on the
 * configuration file of the readers).
 *
 * @author Fernando
 *
 */
@Component
@Scope(value = "prototype")
public class DTE820GPIOWatcher {

	@Value("${readers.gpioWatchInterval:1000}")
	int gpioWatchInterval;

	/** Parser for the messages of the reader */
	private DTE820MessageParser msgParser;

	/** Socket access */
	private SocketManager socketManager;

	/** Flag to start or stop the loop */
	private volatile boolean stop;

	/** Flag to avoid re-entrance */
	private volatile boolean isRunning;

	/** The logger */
	final Logger logger = (Logger) LoggerFactory.getLogger(DTE820GPIOWatcher.class);

	/** The reader id */
	private String readerId;

	/**
	 * Creates the watcher. Call watchIOSignal to start loop.
	 * 
	 * @param msgParser
	 * @param socketManager
	 */
	public DTE820GPIOWatcher(String readerId, DTE820MessageParser msgParser, SocketManager socketManager) {
		this.msgParser = msgParser;
		this.socketManager = socketManager;
		this.readerId = readerId;
	}

	/**
	 * Asks the status of IO signals
	 */
	@Async
	public void watchIOSignalLoop() {

		if (!this.isRunning) {
			this.stop = false;
			while (!stop) {

				GPIOGetIOData cmd = GPIOGetIOData.getInstance();
				// card number is always 0
				//cannot use synchronous executor as we are in a different thread
				this.socketManager.send(this.msgParser.getFullFrameCommand(cmd.getCommand(), cmd.getParamsForCalling(0)));
				
				try {
					Thread.sleep(this.gpioWatchInterval);
				} catch (InterruptedException e) {
					this.logger.error("Error in GPIO Watcher sleep.", e);
				}
			}
			this.isRunning = false;
		}
	}

	/**
	 * Stops watching.
	 */
	public void stop() {
		if (this.isRunning) {
			this.stop = true;
		}
	}

}
