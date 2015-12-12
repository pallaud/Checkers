package Chat;

import java.net.Socket;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Thread class that deals with sending data from the program to a remote host.
 * You may assume that the socket is already connected in the constructor of
 * this class. From there, your thread should wait for input from the main
 * program, and send that input out over the socket.
 *
 * If the socket is closed, the thread should terminate.
 *
 * The thread should also have a method that causes it to terminate, so the main
 * program can kill the thread at any time.
 *
 * $Id: ChatSender.java
 *
 * @author Jessica, Nanxi, Prachi
 * @version May 25, 2015
 * @author Period: 2
 * @author Assignment: CHECKERS
 *
 * @author Sources: Simple Chat
 */
public class ChatSender extends Thread {
	/** Object to send display notifications to */
	protected ChatDisplay display;

	/** Name of the socket we're connected over */
	protected SocketName name;

	/** Socket used to communicate with remote host */
	protected Socket socket;

	/** Writer to use when sending information over the socket */
	protected PrintWriter output;

	/** Whether this sender should continue sending, or if it's done */
	protected boolean alive = false;

	/**
	 * Constructor. The given Socket is already connected and ready for use.
	 * 
	 * @param cd
	 *            The ChatDisplay object that displays any error messages
	 * @param n
	 *            The name of the socket this thread goes with
	 * @param sock
	 *            The socket to send data out to
	 */
	public ChatSender(ChatDisplay cd, SocketName n, Socket sock) {
		super("ChatSender-" + sock.getInetAddress() + ":" + sock.getPort());
		display = cd;
		name = n;
		socket = sock;

		try {
			output = new PrintWriter(socket.getOutputStream(), true);
			alive = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		start();
	}

	/**
	 * Send the given string out over the socket belonging to this thread.
	 * 
	 * @param s
	 *            The data to send over the socket
	 */
	public void send(String s) {
		try {
			output.println(s);
		} catch (Exception e) {
			alive = false;
			e.printStackTrace();
		}
	}

	/**
	 * Stop waiting for input and terminate this thread.
	 */
	public void kill() {
		alive = false;
	}

	/**
	 * Main thread execution loop. This method should contain a tight loop that
	 * calls sleep() and checks the state of the thread to see if it should
	 * still be running. The argument to sleep() should be at least 1 second.
	 */
	public void run() {

		// basically, wait to die...
		while (alive) {

			try {
				sleep(1000);
			} catch (InterruptedException ie) {
			} catch (Exception e) {
				alive = false;

				e.printStackTrace();
			}
		}

		try {
			// clean up the sockets
			output.close();
			socket.close();
		} catch (Exception e) {
		}

		System.out.println(getName() + " terminating");
	}

}
