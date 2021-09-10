
package com.fcherchi.demo.drivers.rfidreader.impl;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.net.SocketFactory;

import com.fcherchi.demo.drivers.exception.DriverException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.net.DefaultSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.fcherchi.demo.drivers.rfidreader.SocketResponseListener;

/**
 * Communication protocol with the reader at its low level.
 * It uses a socket, and it divide the bytes into commands.
 * @author Fernando
 *
 */
public class SocketManager {

    private SocketFactory socketFactory;

	/** Standard logger. */
	private final Logger logger = (Logger) LoggerFactory.getLogger(SocketManager.class);

	/** The ip of the endpoint. */
	private String ip;

	/** The port of the endpoint. */
	private int port;

    /** Identifies the reader which is linked to this socket. */
	private String readerId;

    /** Instance of the communication channel. */
	private Socket socket;

    /** Reader from socket */
	private InputStream socketInput;

    /** Writer to socket */
	private OutputStream socketOutput;

    /** Bytes identifying the end of a command. */
	private byte[] endOfFrame = null;

	private volatile boolean aborted = false;

	public SocketManager(String readerId, String ip, Integer port) {
		this(readerId, ip, port, null);
	}

	public SocketManager(String readerId, String ip, Integer port, byte[] endOfFrame) {
		this.ip = ip;
		this.port = port;
		this.endOfFrame = endOfFrame;
		this.readerId = readerId;
		this.socketFactory = new DefaultSocketFactory();
	}

	/**
	 * Opens the socket to the endpoint.
	 */
	public void connect() {
		this.logger.debug("{} - Connecting to ip {}", this.readerId, this.ip);
		initialiseSocket();
	}

	/**
	 * Closes the socket
	 */
	public void disconnect() {
		if (this.socket != null) {
			try {
				this.socket.close();

			} catch (IOException e) {
				throw new DriverException(this.readerId, "Error closing socket", e);
			}
			this.socket = null;
		}
	}

	/**
	 * Sends the given command.
	 * 
	 * @param cmd
	 */
	public void send(byte[] cmd) {

		checkConnection();
		try {
			this.socketOutput.write(cmd);
		} catch (IOException e) {
			throw new DriverException(this.readerId, "Error writing to the socket", e);
		}
	}

	/**
	 * Waits a number of milliseconds to receive a response from the socket. If
	 * no connection, throws an exception If no response, returns null
	 * 
	 * @param timeoutMillis
	 */
	public byte[] waitForResponse(int timeoutMillis) {
		checkConnection();

		int attempts = timeoutMillis / 10;

		ArrayList<Byte> frame = new ArrayList<>();
		byte[] res = null;
		try {
			while (this.socketInput.available() == 0 && attempts > 0) {
				attempts--;
				Thread.sleep(10);
			}
			if (attempts > 0) {
				try {
					DataInputStream is = new DataInputStream(this.socketInput);
					while (true) {
						frame.add(is.readByte());
						verifyEOF(frame);
					}
				} catch (EOFException e) {
					// end of sending

					res = ArrayUtils.toPrimitive(frame.toArray(new Byte[frame.size()]));
					this.logger.debug("Received {}", ByteUtils.getHexString(res));
				}
			}

		} catch (IOException | InterruptedException e) {
			throw new DriverException(this.readerId, "Error reading information.", e);
		}

		return res;
	}

	/**
     * Stops the loop of waiting for async responses.
     */
	public void cancelWaitForResponseAsync() {
		this.aborted = true;
	}

    /**
     * Starts waiting for response asynchronously.
     * @param listener To notify the response.
     */
	@Async
	public void waitForResponseAsync(SocketResponseListener listener) {

		checkConnection();
		listenToSocketAsync(listener);
	}

	/**
	 * Occurs in a different thread, waits for socket messages
	 * 
	 * @param listener
	 */
	private void listenToSocketAsync(SocketResponseListener listener) {

		byte[] res;
		ArrayList<Byte> frame = new ArrayList<>();

		try {
			this.aborted = false;
			while (!this.aborted) {
				while (this.socketInput.available() == 0 && !this.aborted) {
					Thread.sleep(50);
				}
				if (!this.aborted) {
					try {
						DataInputStream is = new DataInputStream(this.socketInput);
						while (!this.aborted) {
							frame.add(is.readByte());
							verifyEOF(frame);
						}
					} catch (EOFException e) {
						// end of sending
						res = ArrayUtils.toPrimitive(frame.toArray(new Byte[frame.size()]));
						this.logger.debug("{} - Async response of command. Received {}", this.ip, ByteUtils.getHexString(res));
						frame.clear();
						listener.responseReceived(res);
					}
				}
			}

		} catch (SocketException e) {
			// stopped
			logger.debug("stopped?");

		} catch (IOException e) {
			logger.info("IOException while reading async responses from command. {}", e.getMessage());
			this.aborted = true;
		} catch (InterruptedException e) {
			throw new DriverException(this.readerId, "Error reading information.", e);
		}
	}

	/**
	 * Checks if the end of the frame is achieved. If so, throws an EOF
	 * exception
	 * 
	 * @throws EOFException
	 */
	private void verifyEOF(ArrayList<Byte> bytes) throws EOFException {

		if (this.endOfFrame != null && bytes.size() >= endOfFrame.length) {
    		boolean match = true;

			// checking the last digits of the frame if they match with the
			// termination tokens

			// the first digit to check is the first digit that should match the
			// first digit of the endOf Frame Array
			// thus if end of frame is {122, 123} and frame is {1,2,122,123} the
			// first digit to check is position 2
			int firstDigitToCheck = bytes.size() - endOfFrame.length;

			for (int i = firstDigitToCheck, j = 0; i < bytes.size(); i++, j++) {
				if (bytes.get(i) != endOfFrame[j]) {
					match = false;
					break;
				}
			}
			if (match) {
				throw new EOFException();
			}
		}
	}

	/**
	 * Throws an exception if socket is not connected
	 */
	private void checkConnection() {
		if (this.socket == null) {
			throw new DriverException(this.readerId, "Socket is not connected");
		}
	}

	/**
	 * Creates the instance of the socket
	 */
	private void initialiseSocket() {

		if (this.socket != null) {
			// need to clean up old socket
			try {
				this.socket.close();
				Thread.sleep(300);
				
			} catch (IOException e) {
				logger.info("[{}] - Error while restarting socket in the close command.", this.readerId);
			} catch (InterruptedException e) {
				logger.info("[{}] - Error in sleep closing socket in restart.", this.readerId);
			}
		}

		try {
			this.socket = this.socketFactory.createSocket(this.ip, this.port);
			this.socketInput = this.socket.getInputStream();
			this.socketOutput = this.socket.getOutputStream();

		} catch (IOException e) {
			throw new DriverException(this.readerId, "Error creating instance of Socket.", e);
		}
	}

	public void setSocketFactory(SocketFactory socketFactory) {
		this.socketFactory = socketFactory;
	}
}