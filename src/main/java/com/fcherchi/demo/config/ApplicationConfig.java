

package com.fcherchi.demo.config;

import com.fcherchi.demo.drivers.demoreader.DummyReaderImpl;
import com.fcherchi.demo.drivers.rfidreader.DTE820Reader;
import com.fcherchi.demo.drivers.rfidreader.HeartbeatListener;
import com.fcherchi.demo.drivers.rfidreader.ReaderConfig;
import com.fcherchi.demo.drivers.rfidreader.ReaderListener;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820MessageParser;
import com.fcherchi.demo.drivers.rfidreader.impl.SocketManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.fcherchi.demo.drivers.rfidreader.SynchronisedCommandsExecutor;
import com.fcherchi.demo.drivers.rfidreader.impl.DTE820HeartbeatExecutor;
import com.fcherchi.demo.drivers.rfidreader.impl.SynchronisedCommandsExecutorImpl;

@Configuration
@ComponentScan("com.fcherchi.demo")
public class ApplicationConfig {

	// The logger
	final Logger logger = (Logger) LoggerFactory.getLogger(ApplicationConfig.class);


	@Value("${use.ssl:false}")
	private boolean useSSL;


	@Value("${connector.port:8080}")
	private Integer port;



	// readers creation will be dynamic based on configuration. see RfidReadersManagerImpl.instantiateReaders

//	@Bean
//	@Scope(value = "prototype")
//	DTE820Reader dte820Reader(ReaderConfig config, ReaderListener listener, HeartbeatListener heartbeatListener) {
//		return new DTE820ReaderImpl(config, listener, heartbeatListener);
//	}

	@Bean
	@Scope(value = "prototype")
    DTE820Reader dte820Reader(ReaderConfig config, ReaderListener listener, HeartbeatListener heartbeatListener) {
		return new DummyReaderImpl(config, listener, heartbeatListener);
	}

	@Bean
	@Scope(value = "prototype")
	SynchronisedCommandsExecutor synchroCommandExecutor(String readerId, SocketManager socketManager, int readersTimeout) {
		return new SynchronisedCommandsExecutorImpl(readerId, socketManager, readersTimeout);
	}

	@Bean
	@Scope(value = "prototype")
	DTE820HeartbeatExecutor heatbeatExecutor(DTE820MessageParser msgParser, SocketManager socketManager) {
		return new DTE820HeartbeatExecutor(msgParser, socketManager);
	}

	@Bean
	@Scope(value = "prototype")
	SocketManager socketManager(String readerId, String ip, int port, byte[] endOfFrame) {
		return new SocketManager(readerId, ip, port, endOfFrame);
	}
}
