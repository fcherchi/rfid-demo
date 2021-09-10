/**
 * Just for demo purposes


 */

package com.fcherchi.demo.config.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * It watches for file changes using java.nio.WatchService and complemented with Async spring.
 * @author Fernando
 *
 */

@Component
public class FileChangedWatcher {

	final Logger logger = (Logger) LoggerFactory.getLogger(FileChangedWatcher.class);

	@Value("${json.config.path}")
	public String configPath;

	private WatchService watchService;

	private AtomicBoolean initialised = new AtomicBoolean(false);

	private volatile boolean stop;

    /**
     * Initialises the component. Registers to watch a certain directory.
     */
	@PostConstruct
	public void initialise() {

		Path watchedDir = Paths.get(configPath);

		try {
			FileSystem fileSystem = watchedDir.getFileSystem();
			WatchService watchService = fileSystem.newWatchService();
			//we listen to file creations and modification
            watchedDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

			this.watchService = watchService;
			this.initialised.set(true);
		} catch (Exception e) {
			throw new ConfigurationException("Error initialising WatchService.", e);
		}
	}

	/** Called when application will stop (So no pending threads are looping) */
	@PreDestroy
	public void stop() {
		try {
			this.stop = true;
			this.watchService.close();
		} catch (IOException e) {
			logger.error("Error finishing file's WatchService", e);
		}
	}

    /**
     * Start listening for changes of a file or directory.
     * @param listener
     */
	@SuppressWarnings("unchecked")
	@Async
	public void listenForChanges(FileChangedListener listener) {

		if (listener == null) {
			throw new IllegalArgumentException("Listener cannot be null");
		}
		this.logger.debug("Starting listening for changes in '{}'", this.configPath);
		this.stop = false;

		while (!stop) {

			WatchKey key;
			try {

				Thread.sleep(200);

				if (this.initialised.get()) {
					
					key = this.watchService.take();
					Kind<?> kind = null;
                    //poll the event from the queue (if any)
					for (WatchEvent<?> watchEvent : key.pollEvents()) {
                        treatEvent(listener, watchEvent);
                    }
					if (!key.reset()) {
						break;
					}
				}
			} catch (InterruptedException e) {
				logger.error("Error watching for changes.", e);
			} catch (ClosedWatchServiceException e) {
				logger.info("Closing watching watchService.");
				
			} catch (Exception e) {
				logger.error("Unknown error in watching watchService.", e);
			}
		}
	}

    private void treatEvent(FileChangedListener listener, WatchEvent<?> watchEvent) {
        Kind<?> kind;// Get the type of the event
        kind = watchEvent.kind();

        if (StandardWatchEventKinds.ENTRY_CREATE == kind) {
            // created
            Path fileName = ((WatchEvent<Path>) watchEvent).context();
            this.logger.debug("New file created '{}' in '{}'", fileName, configPath);
            listener.fileChanged(true, fileName);

        } else if (StandardWatchEventKinds.ENTRY_MODIFY == kind) {
            // modified
            Path fileName = ((WatchEvent<Path>) watchEvent).context();
            this.logger.debug("File '{}' modified in '{}'", fileName, configPath);
            listener.fileChanged(false, fileName);
        }
    }

}
