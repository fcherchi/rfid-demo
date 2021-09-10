package com.fcherchi.demo.config.file;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationProviderTest {
	
	private FileChangedWatcher changeListener;
	
	@InjectMocks
	private ConfigurationProvider configurationProvider;

	@Before
	public void initMocks() {
		changeListener = Mockito.mock(FileChangedWatcher.class);
		
	}
	
	@Test
	public void testListenToChanges() throws Exception {
//		
//		configurationProvider.listenToChanges("testFile", new ConfigurationChangesListener() {
//			
//			@Override
//			public void onConfigurationChanged(HashMap<String, Object> map) {
//				
//			}
//		});
//		
//		configurationProvider.initialise();
	}

}
