package Chat;

import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Thread class that deals with receiving data from a remote host and relaying
 * it back to the main chat program. You may assume that the socket is already
 * connected in the constructor of this class. From there, your thread should
 * wait for input to come in over the socket, read that data, and send it back
 * to the display.
 *
 * If the socket is closed, the thread should terminate.
 *
 * The thread should also have a method that causes it to terminate, so the main
 * program can kill the thread at any time. It should attempt to close the
 * socket before exiting.
 *
 * $Id: ChatReceiver.java
 */
public class ChatReceiver extends Thread {
	/** Object to send display notifications to */
	protected ChatDisplay display;

	/** Name of the socket we're connected over */
	protected SocketName name;

	/** Socket used to communicate with remote host */
	protected Socket socket;

	/** Reader to grab input from over the wire */
	protected BufferedReader input;

	/**
	 * Constructor. The given Socket is already connected and ready for use.
	 * 
	 * @param cd
	 *            The ChatDisplay object that displays any error messages
	 * @param n
	 *            The name of the socket this thread goes with
	 * @param sock
	 *            The socket to receive data from
	 */
	public ChatReceiver(ChatDisplay cd, SocketName n, Socket sock) {
		super("ChatReceiver-" + sock.getInetAddress() + ":" + sock.getPort());
		display = cd;
		name = n;
		socket = sock;

		try {
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

		} catch (IOException e) {
			e.printStackTrace();
		}

		start();
	}

	/**
	 * Stop waiting for input and terminate this thread.
	 * 
	 * You should also attempt to close the socket before exiting.
	 */
	public void kill() {
		try {
			socket.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Main thread execution loop. This method should contain a tight loop that
	 * attempts to read data from the socket, and relays it to to main display
	 * class. When an exception occurs, the thread should close the socket,
	 * clean up, and exit its loop.
	 */
	public void run() {

		try {
			String line;

			while ((line = input.readLine()) != null) {
				display.chatMessage(name, line);
			}
		} catch (Exception e) {
		}

		try {
			// clean up the sockets
			display.destroySocket(name);
			input.close();
			socket.close();
		} catch (Exception e) {
		}

		System.out.println(getName() + " terminating");
	}

}
