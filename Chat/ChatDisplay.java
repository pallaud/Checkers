package Chat;

/**
 * Interface defining the callback methods that must be supported for the socket
 * threads to send data back to. In the default implementation, the ChatGui
 * class implements this interface, and you do not need to write an implementing
 * class of your own.
 *
 * If you change ChatSender or ChatReceiver to call new methods on the display
 * object, they must be added to this interface.
 *
 * UNLESS YOU IMPLEMENT NEW FUNCTIONALITY, YOU DO NOT NEED TO EDIT THIS FILE.
 *
 * $Id: ChatDisplay.java
 *
 * @author Jessica, Nanxi, Prachi
 * @version May 25, 2015
 * @author Period: 2
 * @author Assignment: CHECKERS
 *
 * @author Sources: Simple Chat
 */
public interface ChatDisplay {
	/**
	 * Displays a status message to the user. This could be in the main chat
	 * window, the console, or another area where the user can see it.
	 * 
	 * @param message
	 *            The message to display
	 */
	public void statusMessage(String message);

	/**
	 * Display a received chat message. The socket where the message originated
	 * is provided in case you wish to display that information with the
	 * message.
	 * 
	 * @param name
	 *            The socket the message came from
	 * @param message
	 *            The chat message received
	 */
	public void chatMessage(SocketName name, String message);

	/**
	 * Note the creation of a new socket and display it to the user. This allows
	 * the user to see which sockets are currently connected and (possibly)
	 * disconnect them.
	 * 
	 * @param name
	 *            The socket that was just created
	 */
	public void createSocket(SocketName name);

	/**
	 * Note the destruction of an existing socket and remove it from the user's
	 * display.
	 * 
	 * @param name
	 *            The socket that was just destroyed
	 */
	public void destroySocket(SocketName name);

}
