/**
 * Just for demo purposes


 */

package com.fcherchi.demo.config.file;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Access to read and write the configuration file.
 *
 * @author Fernando
 *
 */
@Component
public class ConfigurationProvider {

	/** The logger */
	final Logger logger = (Logger) LoggerFactory.getLogger(ConfigurationProvider.class);

	/** The path containing the files */
	@Value("${json.config.path}")
	private String configPath;

	/** The listeners to notify config changes */
	private HashMap<String, List<ConfigurationChangesListener>> watchingListeners;
	
	private final Object fileLocker = new Object();

	/** Component able to notify changes in a file */
	@Autowired
	FileChangedWatcher changeListener;

	/** Flag to initialise only once the file watcher */
	private boolean initialised = false;

	/**
	 * Reads the configuration file
	 * 
	 * @param configFile
	 * @return
	 */
	public Map<String, Object> getConfigurationMap(String configFile) {
		return getMapFromFile(configFile);
	}

	/**
	 * Writes the given value to a file (and subsequently triggers the
	 * notification)
	 * 
	 * @param value
	 * @param configFile
	 */
	public void writeMapToFile(HashMap<String, Object> value, String configFile) {

		Path file = Paths.get(configPath, configFile);
		writeMapToFile(value, file);
	}

	/**
	 * Starts listening for changes
	 * 
	 * @param fileToWatch
	 * @param listener
	 */
	public void listenToChanges(String fileToWatch, ConfigurationChangesListener listener) {

		if (!this.initialised) {
			this.initialise();
			this.initialised = true;
		}

		List<ConfigurationChangesListener> list;
		if (!this.watchingListeners.containsKey(fileToWatch)) {
			list = new ArrayList<>();
			this.watchingListeners.put(fileToWatch, list);
		} else {
			list = this.watchingListeners.get(fileToWatch);
		}
		list.add(listener);
	}

	private Map<String, Object> getMapFromFile(String configFile) {
		try {
			Path file = Paths.get(configPath, configFile);
			Map<String, Object> map = readFileToMap(file);
			return map;
		} catch (Exception e) {
			throw new ConfigurationException("Error reading configuration", e);
		}
	}

	/**
	 * initialise components
	 */
	private void initialise() {

		if (this.watchingListeners == null) {
			changeListener.listenForChanges((isANewFile, fileName) -> notifyChange(isANewFile, fileName));
			this.watchingListeners = new HashMap<>();
		}
		Path path = Paths.get(configPath).toAbsolutePath();
		this.logger.info("Initialised configuration provider from path: '{}'. File being watched {}", path.toString());
	}

	private void notifyChange(boolean isANewFile, Path fileName) {

		String fileStr = fileName.getFileName().toString();
		if (this.watchingListeners.containsKey(fileStr)) {

			List<ConfigurationChangesListener> list = this.watchingListeners.get(fileStr);
			for (ConfigurationChangesListener listener : list) {
				try {
					listener.onConfigurationChanged(readFileToMap(fileName));
				} catch (Exception e) {
					throw new ConfigurationException("Error trying to notify configuration change", e);
				}

			}
		}
	}

	private void writeMapToFile(HashMap<String, Object> map, Path fileName) {

		try {
			synchronized(this.fileLocker) 
			{
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
				objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
				objectMapper.writeValue(fileName.toFile(), map);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}

	}

	private Map<String, Object> readFileToMap(Path fileName) {

		Path fullPath = Paths.get(configPath, fileName.toFile().getName());
		try {
			synchronized (this.fileLocker) {
				TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {
				};
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
				return objectMapper.readValue(fullPath.toFile(), typeRef);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		} 
	}

}
