/**
 * Just for demo purposes
 */

package com.fcherchi.demo.drivers.rfidreader;

import com.fcherchi.demo.drivers.rfidreader.commands.Command;

/**
 * The executor of synchronized commands.
 * @author Fernando
 */
public interface SynchronisedCommandsExecutor {

    /**
     * Starts the loop for checking for timeouts.
     */
    void checkTimeouts();

    /**
     * Executes a command with the given params.
     * @param cmd The command to be executed.
     * @param params The bytes to be passed as parameters.
     * @return A synchronous response of the executed command.
     */
	byte[] executeCommand(Command<?> cmd, byte[] params);

	/**
     * Response received. Call to put the response out of the waited responsed.
	 * @param responseId
	 * @param data 
	 */
	void responseReceived(short responseId, byte[] data);

	/**
     * Executes the given command with no parameters.
	 * @param cmd The command to execute.
	 */
	byte[] executeCommand(Command<?> cmd);

	/**
	 * Stops the loop to check for timeout. Call this to exit the application gracefully.
	 */
	void stopTimeoutChecker();

}