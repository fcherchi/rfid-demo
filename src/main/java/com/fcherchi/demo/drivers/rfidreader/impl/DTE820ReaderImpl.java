/**
 * Just for demo purposes


 */

package com.fcherchi.demo.drivers.rfidreader.impl;

import com.fcherchi.demo.drivers.exception.DriverException;
import com.fcherchi.demo.drivers.rfidreader.*;
import com.fcherchi.demo.drivers.rfidreader.commands.impl.*;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.AntennaGain;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.AntennaPortPower;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.gpio.IOData;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.gpio.InputValues;
import com.fcherchi.demo.events.impl.SynchroTagReadServiceImpl;
import com.fcherchi.demo.events.impl.TagFromDifferentCompanyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a reader. It is being instantiated at runtime based on the configuration file.
 *
 * The reader has two ways of working. Either activated by GPIO or "always ON".
 *
 * In production, it works with GPIO, but for lab operations, debugging, and due to legacy requirements it is
 * still possible to operate it as "always on".
 *
 * In GPIO mode the protocol works as follows:
 * 1- PLC sends 1 (first input).
 * 2- Reader starts reading. If reader gets a tag, continues, otherwise conveyor belt will be block and manual intervention is needed.
 * 3- After the reader has read a tag, the reader sends a 1 (first output). Meaning that the conveyor belt can start again.
 * 4- The PLC sets back a 0 in the first input of the GPIO.
 *
 * @author Fernando
 */
public class DTE820ReaderImpl implements DTE820Reader, GPIOSignalListener, ReaderListener {

    public static final byte[] END_OF_MSG_TOKEN = {(byte) 0xAA, (byte) 0XCC};
    public static final int ANTENNA_PORT = 1;
    public static final int TRUE = 1;
    public static final Byte[] SUPPORTED_COMM_PROFILES = {0, 2, 4, 5, 7, 9, 10, 11, 25};
    /**
     * The logger
     */
    final Logger logger = (Logger) LoggerFactory.getLogger(DTE820ReaderImpl.class);

    /**
     * Transport layer
     */
    SocketManager socketManager;

    /**
     * Parse the messages from the reader
     */
    DTE820MessageParser msgParser;

    /**
     * Notifies responses and validate common errors
     */
    private DTE820CommandsProxy commandsProxy;

    /**
     * The reader name or ID
     */
    private String readerId;

    /**
     * To notify the reader is alive
     */
    private HeartbeatListener heartbeatListener;

    /**
     * Executes periodically a command in the reader to check if it is alive
     */
    DTE820HeartbeatExecutor heartbeatExecutor;

    /**
     * A tool to execute the commands in a synchronous way (it might give
     * timeouts)
     */
    SynchronisedCommandsExecutor synchronisedCommandsExecutor;

    /**
     * The reader configuration
     */
    private ReaderConfig config;

    /**
     * As this bean is built dynamically the App Context is used to create the
     * composed beans
     */
    @Autowired
    ApplicationContext applicationContext;

    /**
     * Timeout configuration of the readers
     */
    @Value("${readers.timeout}")
    int timeoutReaders;

    /**
     * Trigger Input number
     */
    @Value("${readers.gpio.input:0}")
    int inputNo = 0;

    /**
     * Trigger Input number
     */
    @Value("${readers.gpio.output:0}")
    int onputNo = 0;

    /**
     * Thread safe Flag
     */
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * Thread safe IsOutputON
     */
    private AtomicBoolean isOutputOn = new AtomicBoolean(false);

    /**
     * When the reader works with GPIO trigger mode it uses the watcher to
     * listen to the GPIO signal changes
     */
    private DTE820GPIOWatcher gpioWatcher;

    /** Used to filter tag numbers with different company prefix.*/
    @Autowired
    private SynchroTagReadServiceImpl synchroTagReadServiceImpl;

    /** Flag to prevent reentries in connect. */
    private AtomicBoolean isReconnecting;


    public DTE820ReaderImpl(ReaderConfig config, ReaderListener listener, HeartbeatListener heartbeatListener) {

        this.config = config;
        this.readerId = config.getId();
        this.heartbeatListener = heartbeatListener;
        this.commandsProxy = new DTE820CommandsProxy(listener, heartbeatListener, config.getId(), this, this);
        this.msgParser = new DTE820MessageParser(config.getId());
        this.isOutputOn = new AtomicBoolean(false);
        this.isReconnecting = new AtomicBoolean(false);
    }

    @PostConstruct
    public void initialisation() {

        // create transient instances of composed components, all these beans
        // have been declared in ApplicationConfig
        this.socketManager = this.applicationContext.getBean(SocketManager.class, config.getId(), config.getIp(), config.getPort(),
                END_OF_MSG_TOKEN);
        this.heartbeatExecutor = this.applicationContext.getBean(DTE820HeartbeatExecutor.class, this.msgParser, this.socketManager);
        this.synchronisedCommandsExecutor = this.applicationContext.getBean(SynchronisedCommandsExecutor.class, config.getId(), this.socketManager,
                timeoutReaders);

        this.gpioWatcher = this.applicationContext.getBean(DTE820GPIOWatcher.class, config.getId(), this.msgParser, this.socketManager);

    }

    @Override
    public Instant getDateTime() {
        if (checkEnabled()) {
            byte[] data = this.synchronisedCommandsExecutor.executeCommand(GetTimeCommand.getInstance());
            Instant res = GetTimeCommand.getInstance().parseResponse(readerId, data);
            return res;
        }
        return null;
    }


    @Override
    public AntennaPortPower getPortPower(int antennaPort) {

        if (checkEnabled()) {
            byte[] data = this.synchronisedCommandsExecutor.executeCommand(GetPortPowerCommand.getInstance(), new byte[]{(byte) antennaPort});
            AntennaPortPower res = GetPortPowerCommand.getInstance().parseResponse(readerId, data);
            return res;
        }
        return null;
    }


    @Override
    public void setPortPower(int antennaPort, int power) {
        if (checkEnabled()) {
            if (power < 0x44) {
                logger.warn("[{}] - Power setting lower than minimum. Value provided is {}. Minimum value will be set {}.", this.readerId, power, 0x44);
                power = 0x44;
            }
            if (power > 0x84) {
                logger.warn("[{}] - Power setting higher than maximum. Value provided is {}. Max value will be set {}.", this.readerId, power, 0x84);
                power = 0x84;
            }
            SetPortPowerCommand cmd = SetPortPowerCommand.getInstance();
            byte[] data = this.synchronisedCommandsExecutor.executeCommand(cmd, new byte[]{(byte) antennaPort, (byte) power});
            cmd.parseResponse(readerId, data);
            this.config.setPower(power);
        }
    }

    @Override
    public Integer getParamById(int paramId) {

        // intentionally removed
        return null;
    }

    @Override
    public void setParamById(int paramId, int paramValue) {

        // intentionally removed
    }

    @Override
    public void saveConfiguration() {
        if (checkEnabled()) {
            SaveConfigurationCommand cmd = SaveConfigurationCommand.getInstance();
            byte[] res = this.synchronisedCommandsExecutor.executeCommand(cmd);
            cmd.parseResponse(readerId, res);
        }
    }

    @Override
    public AntennaGain getAntennaGain(int antennaPort) {

        if (checkEnabled()) {
            GetCableLossAndAntennaGain cmd = GetCableLossAndAntennaGain.getInstance();
            byte[] data = this.synchronisedCommandsExecutor.executeCommand(cmd, new byte[]{(byte) antennaPort});
            AntennaGain res = cmd.parseResponse(readerId, data);
            return res;
        }
        return null;
    }

    public IOData getGPIOData() {
        if (checkEnabled()) {
            GPIOGetIOData cmd = GPIOGetIOData.getInstance();
            byte[] data = this.synchronisedCommandsExecutor.executeCommand(cmd, new byte[]{(byte) 0x00});
            IOData res = cmd.parseResponse(readerId, data);
            return res;
        }
        return null;
    }

    /**
     * Gets the configuration of the IO Card. Should be 7 (RFID)
     *
     * @return
     */
    public int getIOCardHwConfig() {
        if (checkEnabled()) {
            GetIOCardHwConfig cmd = GetIOCardHwConfig.getInstance();
            byte[] res = this.synchronisedCommandsExecutor.executeCommand(cmd, new byte[]{(byte) 0x00});
            return cmd.parseResponse(this.readerId, res);
        }
        return -1;
    }

    /**
     * RFID = 7
     *
     * @param cardType
     */
    private void setIOCardHwConfig(int cardType) {

        SetIOCardHwConfig cmd = SetIOCardHwConfig.getInstance();

        byte[] res = this.synchronisedCommandsExecutor.executeCommand(cmd, cmd.getParameterBytes((byte) 0));
        cmd.parseResponse(this.readerId, res);

    }

    /**
     * Sets the antenna gain power
     *
     * @see DTE820Reader#setAntennaGain(int,
     * int)
     */
    @Override
    public void setAntennaGain(int antennaPort, int antennaGain) {

        // intentionally removed
    }

    /**
     * Sets the date time into the reader system clock
     *
     * @param datetime
     */
    public void setDatetime(Instant datetime) {
        if (checkEnabled()) {
            SetTimeCommand cmd = SetTimeCommand.getInstance();
            byte[] params = cmd.getParameterBytes(Instant.now());
            byte[] res = this.synchronisedCommandsExecutor.executeCommand(cmd, params);
            cmd.parseResponse(readerId, res);
        }
    }


    private void callStartReading() {
        if (this.isRunning.compareAndSet(false, true) && checkEnabled()) {
            byte[] fullCmd = this.msgParser.getFullFrameCommand(StartInventoryCommand.COMMAND_ID);
            this.socketManager.send(fullCmd);
        }
    }


    private void callStopReading() {

        if (this.isRunning.compareAndSet(true, false)) {

            byte[] fullCmd = this.msgParser.getFullFrameCommand(AsyncStopCommand.COMMAND_ID);
            this.socketManager.send(fullCmd);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new DriverException(this.readerId, "Error stopping reader.", e);
            }
        }
    }

    /**
     * Tries to connect to the reader using the given ip and port number
     */
    @Override
    public void connect() {
        if (checkEnabled()) {
            doConnect();
        }
    }

    /**
     * @return the readerId
     */
    @Override
    public String getReaderId() {
        return readerId;
    }

    /**
     * @see DTE820Reader#reconfigure(ReaderConfig)
     */
    @Override
    public void reconfigure(ReaderConfig readerConfig) {

        if (this.config.getIsEnabled()) {
            if (this.config.getAntennaGain() != readerConfig.getAntennaGain()) {
                this.logger.debug("[{}] - New configuration parameter received. Antenna gain: {}.", readerConfig.getId(), readerConfig.getAntennaGain());
                setAntennaGain(1, readerConfig.getAntennaGain());
            }
            if (this.config.getPower() != readerConfig.getPower()) {
                this.logger.debug("[{}] - New configuration parameter received. Power: {}.", readerConfig.getId(), readerConfig.getPower());
                setPortPower(1, readerConfig.getPower());
            }
            if (this.config.getRssiThreshold() != readerConfig.getRssiThreshold()) {
                this.logger.debug("[{}] - New configuration parameter received. RSSI Threshold: {}.", readerConfig.getId(), readerConfig.getRssiThreshold());
                setParamById(ParameterId.RSSI_THRESHOLD, readerConfig.getRssiThreshold());
            }
            if (this.config.getCommunicationProfile() != readerConfig.getCommunicationProfile()) {
                this.logger.debug("[{}] - New configuration parameter received. Communication Profile: {}.", readerConfig.getId(),
                        readerConfig.getCommunicationProfile());
                setProfile(readerConfig.getCommunicationProfile());
            }
            if (this.config.getUseGPIOTrigger() != readerConfig.getUseGPIOTrigger()) {
                this.logger.debug("[{}] - New configuration parameter received. Use GPIO Trigger: {}.", readerConfig.getId(), readerConfig.getUseGPIOTrigger());
                setUseGPIOTrigger(readerConfig.getUseGPIOTrigger());
            }
        }
        // if configuration changes
        if (this.config.getIsEnabled() != readerConfig.getIsEnabled()) {
            // if we have to connect
            if (readerConfig.getIsEnabled()) {
                this.config.setEnabled(readerConfig.getIsEnabled());
                this.doConnect();
            } else {
                this.doDisconnect();
                this.config.setEnabled(readerConfig.getIsEnabled());
            }
        }
        // refreshing memory
        this.config.setAntennaGain(readerConfig.getAntennaGain());
        this.config.setPower(readerConfig.getPower());
        this.config.setRssiThreshold(readerConfig.getRssiThreshold());
        this.config.setCommuncationProfile(readerConfig.getCommunicationProfile());
        this.config.setUseGPIOTrigger(readerConfig.getUseGPIOTrigger());
    }

    /**
     * Sets the mode to use GPIO Trigger
     *
     * @see DTE820Reader#setModeToGPIOTrigger()
     */
    @Override
    public void setModeToGPIOTrigger() {
        this.setUseGPIOTrigger(true);
    }

    @Override
    public void setModeToAlwaysOn() {
        this.setUseGPIOTrigger(false);
    }


    /**
     * Toggles the GPIO operation mode.
     * @param useTrigger If true, the reader will work with GPIO as trigger (it will be set on when
     *                   GPIO input is received). If false, operation is constant reading.
     */
    private void setUseGPIOTrigger(Boolean useTrigger) {

        // intentionally removed

    }

    /**
     * This parameter has to be set in order to have GPIO operation on/off.
     * After setting, it will restart connection (otherwise the reader ignores the setting)
     * @param cardType
     */
    private void setCardMode(int cardType) {

        //sends commands to the reader
        setIOCardHwConfig(cardType);

        try {
            this.isReconnecting.set(true);
            // reconnection is required if we change the IOCardHWConfig
            this.doDisconnect();
            Thread.sleep(200);
            this.doConnect();
            Thread.sleep(200);
        } catch (Exception e) {
            logger.error("[{}] - Error restarting connection after calling setIOCardHwConfig.", this.readerId);
        } finally {
            this.isReconnecting.set(false);
        }
    }

    @Override
    public void disconnect() {
        if (checkEnabled()) {
            doDisconnect();
        }
    }

    /**
     * Checks if the reader is enabled (has not been disabled by settings)
     * @return True if reader is enabled. Otherwise false.
     */
    private boolean checkEnabled() {
        if (!this.config.getIsEnabled()) {
            this.logger.info("[{}] - Reader is not enabled.", this.readerId);
            return false;
        }
        return true;
    }

    /**
     * Performs the connection .
     */
    private void doConnect() {

        this.synchronisedCommandsExecutor.checkTimeouts();
        this.socketManager.connect();

        // after connection wait for communications from the reader
        this.socketManager.waitForResponseAsync((byte[] res) -> parseAsyncResponse(res));

        // give some time to the reader for start listening...
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            logger.error("Error in sleep.", e);
        }

        // sends the initial configuration
        initialiseReader();
        if (this.heartbeatListener != null && this.heartbeatExecutor != null) {
            this.heartbeatExecutor.keepAliveLoop();
        } else {
            this.logger.warn("[{}] - No heartbeat listener has been provided. Heartbeat disabled.", this.readerId);
        }

        //avoiding recurse
        if (!this.isReconnecting.get()) {
            if (this.config.getUseGPIOTrigger()) {
                // wait for a GPIO signal (see method signal received)
                this.setUseGPIOTrigger(true);
            } else {
                // only if is autonomous mode (always ON)
                this.setUseGPIOTrigger(false);
                callStartReading();
            }
        }
    }

    /**
     * Performs disconnection.
     */
    private void doDisconnect() {
        logger.info("[{}] - Disconnection invoked.", this.readerId);

        if (this.config.getUseGPIOTrigger()) {
            this.gpioWatcher.stop();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                this.logger.error("Error stopping GPIO watcher.", e);
            }
        } else {
            this.callStopReading();
        }
        this.socketManager.cancelWaitForResponseAsync();
        this.synchronisedCommandsExecutor.stopTimeoutChecker();
        this.heartbeatExecutor.stop();
        this.isRunning.set(false);
        this.socketManager.disconnect();
    }

    /**
     * Sets the initial config.
     */
    private void initialiseReader() {

        // set the config
        logger.debug("[{}] - Initialising reader {}", this.readerId, this.config.toString());
        byte[] res = this.synchronisedCommandsExecutor.executeCommand(SetExtendedResultFlag.getInstance(), new byte[]{TagReportType.RF_0TRA});
        // throws exceptions if error
        SetExtendedResultFlag.getInstance().parseResponse(readerId, res);

        res = this.synchronisedCommandsExecutor.executeCommand(SetMode.getInstance(), new byte[]{0});
        // throws exceptions if error
        SetMode.getInstance().parseResponse(readerId, res);

        //these readers have only one antenna.
        setAntennaGain(ANTENNA_PORT, this.config.getAntennaGain());
        setPortPower(ANTENNA_PORT, this.config.getPower());
        setParamById(ParameterId.RSSI_THRESHOLD, this.config.getRssiThreshold());
        setProfile(this.config.getCommunicationProfile());

        setParamById(ParameterId.FILTER_DUPLICATES, TRUE);

        setDatetime(Instant.now());
        this.logger.debug("[{}] - Reader finished its initialisation.", this.readerId);
    }

    /**
     * Sets the communication profile. Normally 11.
     * @param communicationProfile
     */
    private void setProfile(int communicationProfile) {
        if (checkEnabled()) {
            SetProfile cmd = SetProfile.getInstance();

            List<Byte> supportedProfiles = Arrays.asList(SUPPORTED_COMM_PROFILES);

            if (!supportedProfiles.contains((byte) communicationProfile)) {
                this.logger.warn("[{}] - Communication profile {} is not supported. Assuming default 11.", this.readerId, communicationProfile);
                communicationProfile = 11;
            }

            byte[] params = new byte[]{(byte) communicationProfile};
            byte[] response = this.synchronisedCommandsExecutor.executeCommand(cmd, params);
            cmd.parseResponse(readerId, response);
            this.config.setCommuncationProfile(communicationProfile);
        }
    }

    /**
     * Treats the response received from the reader.
     * @param response
     */
    private void parseAsyncResponse(byte[] response) {

        // throws its own exceptions
        this.msgParser.parseMessage(response);
        this.synchronisedCommandsExecutor.responseReceived(this.msgParser.getResponseId(), this.msgParser.getData());
        this.commandsProxy.treatResponse(this.msgParser.getResponseId(), this.msgParser.getData());
    }

    @Override
    public void signalsReceived(IOData payload) {

        //removed intentionally

    }

    /**
     * returns true if the signal received from GPIO is on.
     * @param inputs
     * @return
     */
    private boolean isTriggerOn(InputValues inputs) {

        switch (this.inputNo) {
            case 0:
                return inputs.getLogical().getValueOne();
            case 1:
                return inputs.getLogical().getValueTwo();
            case 2:
                return inputs.getLogical().getValueThree();
            default:
                throw new DriverException(this.readerId, "GPIO input not valid: " + this.inputNo);
        }
    }

    @Override
    public void onUnknownResponseReceived(String readerId, byte[] data) {
        this.logger.warn("[{}] - Unknown response received", readerId, data);
    }

    @Override
    public boolean onTagRead(String readerId, TagReport tagReport) {

        // This method is only invoked in the mode UseGPIOSignal
        boolean tagHasBeenAccepted = false;
        boolean tagIsBeingIgnored = false;
        try {
            tagHasBeenAccepted = this.synchroTagReadServiceImpl.onTagRead(readerId, tagReport);

        } catch (TagFromDifferentCompanyException e) {
            tagIsBeingIgnored = true;
        }

        //
        // after tag has been read, if tag is a tag of the same company, send signal to GPIO
        // to set output1 to 1
        // isOutputOn avoids re entries when a tag was read
        if (!tagIsBeingIgnored && this.isOutputOn.compareAndSet(false, true)) {

            // with this command the signal is sent for the PLC
            setOutput(true);

        }
        return tagHasBeenAccepted;
    }

    /**
     * Writes to the GPIO output.
     * @param isOn
     */
    private void setOutput(boolean isOn) {

        // set output to ON
        GPIOSetOutput cmd = GPIOSetOutput.getInstance();

        this.logger.info("[{}] - Setting value of output {} to {}", this.readerId, this.onputNo + 1, isOn);
        // cannot use synchronous executor because this is called from a
        // different thread
        this.socketManager.send(this.msgParser.getFullFrameCommand(cmd.getCommand(), cmd.getParameterBytes(0, this.onputNo, isOn)));
    }

}
