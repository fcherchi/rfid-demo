/**
 * Just for demo purposes


 */

package com.fcherchi.demo.config.file;

import java.util.Map;

/**
 * Notifies the configuration changes.
 * @author Fernando
 *
 */
public interface ConfigurationChangesListener {

	/**
     * Invoked when a config file has changed
	 * @param map The configuration as a map.
	 */
	void onConfigurationChanged(Map<String, Object> map);

}
