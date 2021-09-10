package com.fcherchi.demo.drivers.demoreader;

import org.slf4j.Logger;

/**
 * Just to write always in the same way to the logger.
 */
public class LoggerWriter {


    public static void logInfo(Logger logger, String readerId, String message, Object... params) {
        String str = "[" + readerId + "] - " + message;
        logger.info(str, params);
    }

}