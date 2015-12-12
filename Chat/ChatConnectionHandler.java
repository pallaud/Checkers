package Chat;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * ChatConnectionHandler is in charge of listening for connections from and
 * initating connections to remote hosts. Because it must wait for new
 * connections, the class is designed as a thread, with a main loop that simply
 * blocks while waiting for new connections to arrive.
 *
 * The class also contains methods to initiate outgoing connections in response
 * to commands from the main class.
 *
 * Once a socket has been created (either receving or outgoing), this class
 * attaches two new threads to it: one for reading data from the socket, and one
 * for writing new data out to it. These threads are implemented using the
 * ChatSender and ChatReceiver classes.
 *
 * $Id: ChatConnectionHandler.java
 *
*  @author  Jessica, Nanxi, Prachi
 *  @version May 25, 2015
 *  @author  Period: 2
 *  @author  Assignment: CHECKERS
 *
 *  @author  Sources: Simple Chat
 */
public class ChatConnectionHandler extends Thread {
	/** Object to send display notifications to */
	protected ChatDisplay display;

	/** Server socket to listen on */
	protected ServerSocket serverSocket;

	/** Collection of sending threads */
	protected Map senders;

	/** Collection of receiving threads */
	protected Map receivers;

	/** Count of incoming connections (used for unique naming) */
	protected int count = 1;

	/**
	 * Constructor. Creates a new thread listening on the given port. Also,
	 * remembers the display object, which it passes on to other threads as they
	 * are created.
	 * 
	 * @param cd
	 *            The display object to pass on to child threads
	 * @param p
	 *            The port to bind to and listen on
	 */
	public ChatConnectionHandler(ChatDisplay cd, int p) {
		super("ChatConnectionHandler-" + p);

		senders = new HashMap();
		receivers = new HashMap();

		display = cd;

		try {
			serverSocket = new ServerSocket(p);
			start();
		} catch (IOException e) {
			System.err.println("Could not listen on port " + p);
		}
	}

	/**
	 * Helper method for main spawn method. This version deduces a SocketName
	 * object from data available from the socket, and can be used for
	 * connections where the address is not known ahead of time (e.g., incoming
	 * connections).
	 * 
	 * @param sock
	 *            The Socket to spawn listeners for
	 * 
	 * @see spwan(SocketName, Socket)
	 */
	protected synchronized void spawn(Socket sock) {
		SocketName name = new SocketName(sock.getInetAddress().toString(),
				sock.getPort(), "Incoming " + count);
		count++;
		spawn(name, sock);
	}

	/**
	 * Helper method that creates two new threads to listen on the given socket.
	 * Also, stores the threads in a data structure so they can be easily
	 * referenced later (when they need to have messages sent to them, or when
	 * they need to be killed off).
	 * 
	 * @param name
	 *            The name of the socket
	 * @param sock
	 *            The Socket to spawn listeners for
	 */
	protected void spawn(SocketName name, Socket sock) {
		ChatSender cs = new ChatSender(display, name, sock);
		ChatReceiver cr = new ChatReceiver(display, name, sock);

		senders.put(name, cs);
		receivers.put(name, cr);

		display.createSocket(name);
	}

	/**
	 * Main thread method. Should simply contain a loop that waits for incoming
	 * connections and spawn()s new threads for them as appropriate.
	 */
	public void run() {

		// listen until we quit
		try {

			while (true) {
				Socket s = serverSocket.accept();
				spawn(s);
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Create a new outgoing connection socket with the parameters provided in
	 * the socket name.
	 * 
	 * Also, notify the display class of the new socket once it's created.
	 * 
	 * @param name
	 *            The name and connection parameters of the socket to create
	 */
	public void connect(SocketName name) {
		try {
			Socket sock = new Socket(name.getHost(), name.getPort());
			spawn(name, sock);
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + name.getHost());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Destroy an existing connection socket with the parameters provided in the
	 * socket name.
	 * 
	 * Also, notify the display class that the socket has been destroyed.
	 * 
	 * @param name
	 *            The name and connection parameters of the socket to destroy
	 */
	public void disconnect(SocketName name) {
		System.out.println("Disconnecting from '" + name + "'");

		ChatSender cs = (ChatSender) senders.remove(name);
		ChatReceiver cr = (ChatReceiver) receivers.remove(name);

		cs.kill();
		cr.kill();

		display.destroySocket(name);
	}

	/**
	 * Iterate over all socket sending threads and tell them to send the given
	 * data.
	 * 
	 * @param s
	 *            The data to send
	 */
	public void send(String s) {
		Iterator iter = senders.keySet().iterator();

		while (iter.hasNext()) {
			ChatSender cs = (ChatSender) senders.get(iter.next());
			cs.send(s);
		}
	}

}
