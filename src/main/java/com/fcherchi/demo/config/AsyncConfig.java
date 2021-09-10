/**
 * Just for demo purposes


 */

package com.fcherchi.demo.config;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author Fernando
 *
 */
@Configuration
@EnableAsync
public class AsyncConfig {

	

	/** The logger */
	final Logger logger = (Logger) LoggerFactory.getLogger(AsyncConfig.class);

	/**
	 * @see org.springframework.scheduling.annotation.AsyncConfigurer#getAsyncExecutor()
	 */
	@Bean
	public Executor taskExecutor() {
		
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(85);
        executor.setMaxPoolSize(200);
        executor.setThreadNamePrefix("DEMO-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        return executor;
	}

	

}
