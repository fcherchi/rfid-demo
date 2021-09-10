/**
 * Just for demo purposes


 */

package com.fcherchi.demo.config.file;

import java.nio.file.Path;

/**
 * @author Fernando
 *
 */
public interface FileChangedListener {
	
	/**
	 * Invoked when a new file is created, or a file has been modified
	 * @param isANewFile If true, it is a new file. Else it is an update
	 * @param fileName The path of the modified/created file.
	 */
	void fileChanged(boolean isANewFile, Path fileName);
	
}
