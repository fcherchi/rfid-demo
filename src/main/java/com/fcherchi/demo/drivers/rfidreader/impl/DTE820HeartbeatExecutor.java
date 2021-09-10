/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import com.fcherchi.demo.drivers.rfidreader.commands.impl.GetTemperatureCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

/**
 *
 * @author Fernando
 */
public class DTE820HeartbeatExecutor {

    @Value("${readers.heartbeatInterval}")
    int heartbeatInterval;

    /**
     * The logger
     */
    final Logger logger = (Logger) LoggerFactory.getLogger(DTE820HeartbeatExecutor.class);

    /** Parses the messages */
    private DTE820MessageParser msgParser;

    /**
     * Access to the communication channel with the reader.
     */
    private SocketManager socketManager;

    /** Flag to stop the heartbeat */
    private volatile boolean stop;

    public DTE820HeartbeatExecutor(DTE820MessageParser msgParser, SocketManager socketManager) {
        this.msgParser = msgParser;
        this.socketManager = socketManager;
    }

    /**
     * keeps sending the message GetTemperature
     */
    @Async
    public void keepAliveLoop() {
        this.stop = false;
        while (!stop) {

            byte[] fullCmd = msgParser.getFullFrameCommand(GetTemperatureCommand.COMMAND_ID);
            socketManager.send(fullCmd);

            try {
                Thread.sleep(heartbeatInterval);
            } catch (InterruptedException e) {
                logger.error("Error in heartbeat sleep.", e);
            }
        }
    }

    /**
     * Stops the reader to gracefully finish the application.
     */
    public void stop() {
        this.stop = true;
    }

}
