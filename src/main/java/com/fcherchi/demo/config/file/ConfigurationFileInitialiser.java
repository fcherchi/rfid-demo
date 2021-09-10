/**
 * Just for demo purposes


 */

package com.fcherchi.demo.config.file;

import com.fcherchi.demo.fileutils.FileCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Initialises the configuration file. Creates directories if needed.
 * Copy an initial configuration file if it does not exist.
 * @author Fernando
 *
 */
@Component
public class ConfigurationFileInitialiser {

	final Logger logger = (Logger) LoggerFactory.getLogger(ConfigurationFileInitialiser.class);

	@PostConstruct
	public void initialise() {

		try {
			// if dir containing config files does not exist
			// creates it
			File destDir = new File("json-config");
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			//TODO remove hardcoded values
            FileCopier.copyFile("json-config/antennas.json");
            FileCopier.copyFile("json-config/events.json");

		} catch (Exception e) {
			throw new ConfigurationException("Error copying configuration files to json-config", e);
		}
	}
}
