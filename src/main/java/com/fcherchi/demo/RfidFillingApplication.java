package com.fcherchi.demo;

import com.fcherchi.demo.fileutils.FileCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class RfidFillingApplication {

	// The logger
	final static Logger logger = (Logger) LoggerFactory.getLogger(RfidFillingApplication.class);

	public static void main(String[] args) throws IOException {

		logger.info("Working Directory: {}", System.getProperty("user.dir"));
		for (String string : args) {
			logger.info("Received Arg: {}", string);
		}
		copyFilesIfDoesNotExist();
		SpringApplication.run(RfidFillingApplication.class, args);
	}

	private static void copyFilesIfDoesNotExist() throws IOException {

		File configFolder = new File("config");
		if (!configFolder.exists()) {
			configFolder.mkdir();
		}
		FileCopier.copyFile("config/application.properties");
		FileCopier.copyFile("config/logback.xml");

		logger.info("Finding classpath resource.");

	}

}
