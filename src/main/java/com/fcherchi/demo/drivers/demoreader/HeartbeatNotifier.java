package com.fcherchi.demo.drivers.demoreader;

import com.fcherchi.demo.drivers.rfidreader.HeartbeatListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Simulates the activity of a physical reader (it sends heartbeats).
 *
 * In the real life a reader can answer to a command GetTemperature with its own internal temperature.
 * @see com.fcherchi.demo.drivers.rfidreader.commands.impl.GetTemperatureCommand
 *
 * This notifier, simplifies a lot and just calls the listener with a method sending a fake temperature.
 * This way, the watchdog is happy, and the application sees the reader as "alive"
 *
 * At any moment where this HeartbeatNotifier would stop calling the listener, the ReaderStatusChecker will notify the
 * Watchdog, and the latter will try a reconnection. (In the real life the reconnection will typically fail n times or forever
 * that's way the reconnector attempts less frequently every time. In this example the reconnection immediately restart
 * the activity)
 *
 */
@Scope("prototype")
@Component
public class HeartbeatNotifier {

    private String readerId;
    private volatile boolean stop;
    private HeartbeatListener heartbeatListener;

    private static final int MIN_TEMP = 25;
    private static final int MAX_TEMP = 35;

    @Value("${readers.heartbeatInterval}")
    private int heartbeatInterval;

    public void initialise(String readerId, HeartbeatListener heartbeatListener) {

        this.readerId = readerId;
        this.heartbeatListener = heartbeatListener;
    }

    @Async
    public void start() {
        this.stop = false;
        while (! this.stop) {
            sendHeartbeat();
            try {
                Thread.sleep(this.heartbeatInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendHeartbeat() {
        this.heartbeatListener.readerIsAlive(this.readerId, getRandomTemp());
    }

    public void stop() {
        this.stop = true;
    }

    private int getRandomTemp() {
        Random r = new Random();
        return r.nextInt(MAX_TEMP - MIN_TEMP) + MIN_TEMP;
    }

}
