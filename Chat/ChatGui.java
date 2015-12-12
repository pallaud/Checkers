package Chat;

//import info.gridworld.grid.Location;

import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.*;
import java.awt.event.*;

/**
 * ChatGui creates the graphical user interface for the SimpleChat program. This
 * class fulfills two important roles. First, it creates all of the GUI
 * components necessary for entering text and options to connect to other hosts.
 * Second, it acts as a displayer of messages received from the networking
 * threads, and presents these messages to the user.
 *
 * $Id: ChatGui.java
 *
 * @author Jessica, Nanxi, Prachi
 * @version May 25, 2015
 * @author Period: 2
 * @author Assignment: CHECKERS
 *
 * @author Sources: Simple Chat
 */
public class ChatGui extends JPanel implements ChatDisplay, ActionListener,
		KeyListener {
	/** Default port to connect to on remote hosts */
	public static final int DEFAULT_PORT = 1337;

	/** Object that performs all networking and IO */
	protected ChatConnectionHandler networker;

	/** Display of incoming messages */
	protected JTextArea incoming;

	/** Input field for outgoing messages */
	protected JTextField outgoing;

	/** List of active connections */
	protected JList connections;

	/** Data model for connections list */
	protected DefaultListModel connModel;

	/** Input field for new host connections */
	protected JTextField host;

	/** Input field for port to connect to */
	protected JTextField port;

	/** Input field for name to assign to new connection */
	protected JTextField nickname;

	/** Button to push when adding a new connection */
	protected JButton connect;

	/** Button to push when removing an existing connection */
	protected JButton disconnect;

	/**
	 * Constructor. Creates a Component object that may be added to any
	 * Swing-compatible layout.
	 * 
	 * This constructor creates the basic layout of all the major components of
	 * the chat program: text display and entry, connection display, and a form
	 * to connect to new hosts.
	 */
	public ChatGui() {

		int port = 0;

		while (port < 1024 || port > 65535) {

			try {
				port = Integer.parseInt(JOptionPane.showInputDialog(this,
						"Enter a port to listen on (1025-65535)", new Integer(
								DEFAULT_PORT)));
			} catch (Exception e) {
			}
		}

		// create a chat networking object to peform I/O
		networker = new ChatConnectionHandler(this, port);
	}

	/**
	 * Helper method for the constructor which creates the portion of the screen
	 * dealing with displaying incoming messages.
	 * 
	 * @return Component The message display component
	 */
	protected Component makeMessageDisplay() {
		incoming = new JTextArea();
		incoming.addKeyListener(this);
		incoming.setEditable(false);

		JScrollPane scroll = new JScrollPane(incoming);
		scroll.setBorder(BorderFactory.createTitledBorder("Incoming Messages"));
		return scroll;
	}

	/**
	 * Helper method for the constructor which creates the portion of the screen
	 * dealing with inputting new outgoing messages.
	 * 
	 * @return Component The chat composition component
	 */
	protected Component makeEntryForm() {
		outgoing = new JTextField();
		outgoing.addKeyListener(this);

		JScrollPane scroll = new JScrollPane(outgoing);
		scroll.setBorder(BorderFactory.createTitledBorder("Outgoing Messages"));
		return scroll;
	}

	/**
	 * Helper method for the constructor which creates the portion of the screen
	 * dealing with displaying existing connections.
	 * 
	 * @return Component The existing connection display component
	 */
	protected Component makeConnectionDisplay() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Connected Clients"));

		connModel = new DefaultListModel();
		connections = new JList(connModel);
		JScrollPane scroll = new JScrollPane(connections);

		panel.add(scroll);

		disconnect = new JButton("Disconnect");
		disconnect.addActionListener(this);
		panel.add(disconnect);

		return panel;
	}

	/**
	 * Helper method for the constructor which creates the portion of the screen
	 * dealing with creating new connections.
	 * 
	 * @return Component The new connection entry component
	 */
	protected Component makeConnectionForm() {

		host = new JTextField(15); // enough space for a dotted quad address
		host.addKeyListener(this);
		JLabel hostL = new JLabel("Host or IP");
		hostL.setLabelFor(host);

		port = new JTextField(5); // enough space for a large port number
		port.addKeyListener(this);
		JLabel portL = new JLabel("Port");
		portL.setLabelFor(port);

		nickname = new JTextField(15); // same as host
		nickname.addKeyListener(this);
		JLabel nicknameL = new JLabel("Nickname");
		nicknameL.setLabelFor(nickname);

		connect = new JButton("Add");
		connect.addActionListener(this);

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Add Connection"));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);

		c.gridx = 0;
		c.gridy = 0;
		panel.add(hostL, c);

		c.gridx = 0;
		c.gridy = 1;
		panel.add(portL, c);

		c.gridx = 0;
		c.gridy = 2;
		panel.add(nicknameL, c);

		c.gridwidth = 2;

		c.gridx = 1;
		c.gridy = 0;
		panel.add(host, c);

		c.gridx = 1;
		c.gridy = 2;
		panel.add(nickname, c);

		c.gridwidth = 1;

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.99;
		panel.add(port, c);
		c.weightx = 0.01;

		c.gridx = 2;
		c.gridy = 3;
		panel.add(connect, c);

		return panel;
	}

	/**
	 * KeyEvent handler method, used to deal with keyboard input on components
	 * that have registered with this class.
	 * 
	 * For the most part, this method listens for the "return" key to be hit,
	 * checks which form element generated the event, and takes the appropriate
	 * action (creating a new message or connection).
	 * 
	 * @param e
	 *            The event to handle
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			// find the component that generated the event
			Component source = e.getComponent();

			if (source == outgoing) {
				send();
			} else if (source == host || source == port || source == nickname) {
				connect();
			}
		}
	}

	/**
	 * Event handling method required by the KeyEvent interface. This method is
	 * empty, as we don't deal with key release events at all.
	 * 
	 * @param e
	 *            The key released event (ignored)
	 */
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * Event handling method required by the KeyEvent interface. This method is
	 * empty, as we don't deal with key typed events at all.
	 * 
	 * @param e
	 *            The key typed event (ignored)
	 */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * ActionEvent handler method, used to deal with action events from
	 * components that have registered with this class.
	 * 
	 * For the most part, this method listens for button presses, checks which
	 * element generated the event, and takes the appropriate action (creating
	 * or deleting a connection).
	 * 
	 * @param e
	 *            The event to handle
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == connect) {
			connect();
		} else if (source == disconnect) {
			disconnect();
		}
	}

	/**
	 * Helper method to read inputs from GUI components and create a new socket
	 * connection.
	 */
	protected void connect() {
		try {
			SocketName sock = new SocketName(host.getText(), port.getText(),
					nickname.getText());

			if (connModel.contains(sock)) {
				statusMessage("Cannot connect to " + sock
						+ ": already connected");
			} else {
				networker.connect(sock);
			}

			host.setText("");
			port.setText("");
			nickname.setText("");
		} catch (IllegalArgumentException iae) {
			statusMessage("Cannot connect: " + iae.getMessage());
		}

	}

	/**
	 * Helper method to read inputs from GUI components and destroy an existing
	 * socket connection.
	 */
	protected void disconnect() {
		int index = connections.getSelectedIndex();
		if (index > -1) {
			SocketName dead = (SocketName) (connModel.elementAt(index));

			networker.disconnect(dead);
		}
	}

	/**
	 * Helper method to send a message to all remote sockets.
	 */
	protected void send() {
		networker.send(outgoing.getText());

		incoming.append("Me: ");
		incoming.append(outgoing.getText());
		incoming.append("\n");

		outgoing.setText("");
	}

	/**
	 * Displays a status message to the user. This could be in the main chat
	 * window, the console, or another area where the user can see it.
	 * 
	 * @param message
	 *            The message to display
	 */
	public void statusMessage(String message) {
		System.err.println(message);
	}

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
	public void chatMessage(SocketName name, String message) {

		incoming.append(name.getName());
		incoming.append(": ");
		incoming.append(message);

	}

	/**
	 * Note the creation of a new socket and display it to the user. This allows
	 * the user to see which sockets are currently connected and (possibly)
	 * disconnect them.
	 * 
	 * @param name
	 *            The socket that was just created
	 */
	public synchronized void createSocket(SocketName name) {
		connModel.addElement(name);
	}

	/**
	 * Note the destruction of an existing socket and remove it from the user's
	 * display.
	 * 
	 * @param name
	 *            The socket that was just destroyed
	 */
	public void destroySocket(SocketName name) {
		if (connModel.contains(name)) {
			connModel.removeElement(name);
		}
	}

}
