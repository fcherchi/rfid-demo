package com.fcherchi.demo.drivers.demoreader;

import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.drivers.rfidreader.impl.TagReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Random;

/**
 * Simulates the activity the tag reads.
 *
 */
@Scope("prototype")
@Component
public class DummyReaderTagSingulator {

    private String readerId;
    private volatile boolean stop;
    private ReaderListener tagListener;
    private static Logger logger = LoggerFactory.getLogger(DummyReaderTagSingulator.class);

    private static int MIN_DELAY = 2000;
    private static int MAX_DELAY = 4000;

    private static int MIN_SERIAL = 100;
    private static int MAX_SERIAL = 999;

    public void initialise(String readerId, ReaderListener tagListener) {

        this.readerId = readerId;
        this.tagListener = tagListener;
    }

    /**
     * Starts sending tags.
     */
    @Async
    public void start() {
        this.stop = false;

        while (! this.stop) {
            sendTag();
            try {
                Thread.sleep(getRandomDelay());
            } catch (InterruptedException e) {
                logger.error("Error in sleep. " + readerId, e);
            }
        }

    }

    /**
     * Notifies the listener as if a tag would have been read.
     */
    private void sendTag() {
        this.tagListener.onTagRead(readerId, getTagReport());
    }

    /**
     * Gets the tag report pojo.
     * @return
     */
    private TagReport getTagReport() {
        TagReport tagReport = new TagReport();
        tagReport.setAntennaPort((byte)1);
        tagReport.setComingMessage(false);
        tagReport.setCount(1);
        tagReport.setRssi((byte)23);
        tagReport.setTagEpc("302400000000028000000" + getRandomSerial());
        tagReport.setTimestamp(Instant.now());

        return tagReport;
    }

    /**
     * Delay between constants min and max.
     * @return
     */
    private int getRandomDelay() {
        Random r = new Random();
        return r.nextInt(MAX_DELAY - MIN_DELAY) + MIN_DELAY;
    }

    /**
     * Gets a random part of an epc code.
     * @return
     */
    private int getRandomSerial() {
        Random r = new Random();
        return r.nextInt(MAX_SERIAL - MIN_SERIAL) + MIN_SERIAL;
    }

    /**
     * Stops the sending of tags.
     */
    public void stop() {
        this.stop = true;
    }
}
