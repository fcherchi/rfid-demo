package com.fcherchi.demo.drivers.demoreader;

import com.fcherchi.demo.drivers.rfidreader.DTE820Reader;
import com.fcherchi.demo.drivers.rfidreader.HeartbeatListener;
import com.fcherchi.demo.drivers.rfidreader.ReaderConfig;
import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.AntennaGain;
import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.AntennaPortPower;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.time.Instant;

/**
 * A reader that simulates the behavior of the physical readers.
 * It includes a method to simulate a network disconnection (setCable)
 *
 */
public class DummyReaderImpl implements DTE820Reader {

    private static final Logger logger = LoggerFactory.getLogger(DummyReaderImpl.class);
    private final String readerId;

    private final ReaderConfig config;
    private final HeartbeatListener heartbeatListener;
    private final ReaderListener tagListener;


    @Autowired
    private DummyReaderTagSingulator tagSingulator;

    @Autowired
    private HeartbeatNotifier heartbeatNotifier;

    public DummyReaderImpl(ReaderConfig config, ReaderListener tagListener, HeartbeatListener heartbeatListener) {

        this.config = config;
        this.readerId = config.getId();
        this.heartbeatListener = heartbeatListener;
        this.tagListener = tagListener;
    }

    @PostConstruct
    public void initialisation() {

        // create transient instances of composed components, all these beans
        // have been declared in ApplicationConfig
        this.tagSingulator.initialise(this.readerId, this.tagListener);
        this.heartbeatNotifier.initialise(this.readerId, this.heartbeatListener);
    }

    @Override
    public void connect() {
        if (checkEnabled()) {
            LoggerWriter.logInfo(logger, readerId, "Connected.");
            start();
        }
    }

    @Override
    public Instant getDateTime() {
        return Instant.now();
    }

    @Override
    public AntennaPortPower getPortPower(int antennaPort) {
        return null;
    }

    @Override
    public void setPortPower(int antennaPort, int power) {
        LoggerWriter.logInfo(logger, readerId, "Port power set at {}", power);
    }



    @Override
    public void disconnect() {
        if (checkEnabled()) {
            LoggerWriter.logInfo(logger, readerId, "Disconnected");
            stop();
        }
    }

    @Override
    public AntennaGain getAntennaGain(int antennaPort) {
        return new AntennaGain(1, 1, 10);
    }

    @Override
    public void saveConfiguration() {

    }

    @Override
    public void setAntennaGain(int antennaPort, int antennaGain) {

    }

    @Override
    public String getReaderId() {
        return this.readerId;
    }

    @Override
    public Integer getParamById(int paramId) {
        return null;
    }

    @Override
    public void setParamById(int paramId, int paramValue) {

    }

    /**
     * A method to simulate bad cable behavior (Stops the activity of the reader unexpectedly)
     * @param isOn
     */
    public void setCable(boolean isOn) {

        if (checkEnabled()) {
            if (isOn) {
                this.heartbeatNotifier.start();
            } else {
                LoggerWriter.logInfo(this.logger, readerId, "Cable pulled from web.");

                this.tagSingulator.stop();
                this.heartbeatNotifier.stop();
            }
        }
    }

    @Override
    public void reconfigure(ReaderConfig readerConfig) {
        if (this.config.getAntennaGain() != readerConfig.getAntennaGain()) {
            this.logger.info("[{}] - New configuration parameter received. Antenna gain: {}.", readerConfig.getId(), readerConfig.getAntennaGain());
        }
        if (this.config.getPower() != readerConfig.getPower()) {
            this.logger.info("[{}] - New configuration parameter received. Power: {}.", readerConfig.getId(), readerConfig.getPower());
        }
        if (this.config.getRssiThreshold() != readerConfig.getRssiThreshold()) {
            this.logger.info("[{}] - New configuration parameter received. RSSI Threshold: {}.", readerConfig.getId(), readerConfig.getRssiThreshold());
        }
        if (this.config.getCommunicationProfile() != readerConfig.getCommunicationProfile()) {
            this.logger.info("[{}] - New configuration parameter received. Communication Profile: {}.", readerConfig.getId(),
                    readerConfig.getCommunicationProfile());
        }
        if (this.config.getUseGPIOTrigger() != readerConfig.getUseGPIOTrigger()) {
            this.logger.info("[{}] - New configuration parameter received. Use GPIO Trigger: {}.", readerConfig.getId(), readerConfig.getUseGPIOTrigger());
        }


        // if configuration changes
        if (this.config.getIsEnabled() != readerConfig.getIsEnabled()) {
            // if we have to connect
            if (readerConfig.getIsEnabled()) {
                this.config.setEnabled(readerConfig.getIsEnabled());
                start();
            } else {
                stop();
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
     * Starts the composed beans activity.
     */
    private void start() {
        this.heartbeatNotifier.start();
        this.tagSingulator.start();
    }

    /**
     * Stops the composed beans activity
     */
    private void stop() {
        this.heartbeatNotifier.stop();
        this.tagSingulator.stop();
    }

    /**
     * Gives access to the isEnabled property (printed in the monitor view)
     * @return
     */
    public boolean isEnabled() {
        return this.config.getIsEnabled();
    }

    @Override
    public void setModeToGPIOTrigger() {

    }

    @Override
    public void setModeToAlwaysOn() {

    }

    /**
     * Returns true if the reader is enabled. False otherwise.
     * @return
     */
    private boolean checkEnabled() {
        if (!this.config.getIsEnabled()) {
            this.logger.info("[{}] - Reader is not enabled.", this.readerId);
            return false;
        }
        return true;
    }


}
