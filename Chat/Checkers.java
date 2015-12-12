package Chat;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This panel lets two users play checkers against each other. Red always starts
 * the game. If a player can jump an opponent's piece, then the player must
 * jump. When a player can make no more moves, the game ends.
 *
 * The class has a main() routine that lets it be run as a stand-alone
 * application.
 *
 * @author Jessica, Nanxi, Prachi
 * @version May 25, 2015
 * @author Period: 2
 * @author Assignment: CHECKERS
 *
 * @author Sources: David Eck
 */
public class Checkers extends JPanel {

	/**
	 * Main routine makes it possible to run Checkers as a stand-alone
	 * application. Opens a window showing a Checkers panel; the program ends
	 * when the user closes the window.
	 *
	 * @param args
	 *            null array
	 */
	public static void main(String[] args) {
		JFrame window = new JFrame("Checkers");
		Checkers content = new Checkers();

		window.setContentPane(content);
		window.pack();
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation((screensize.width - window.getWidth()) / 2,
				(screensize.height - window.getHeight()) / 2);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);
		window.setVisible(true);
	}

	/**
	 * Button for starting a new game.
	 */
	static JButton newGameButton;

	/**
	 * GUI used to connect networking threads
	 */
	static ChatGui gui;

	/**
	 * Button that a player can use to end the game by resigning.
	 */
	static JButton resignButton;

	/**
	 * Label for displaying messages to the user.
	 */
	static JLabel message;

	/**
	 * Display of incoming messages
	 */
	static JScrollPane incoming_;

	/** Input field for outgoing messages */
	static JScrollPane outgoing_;

	// //////////////////////

	/**
	 * Default port to connect to on remote hosts
	 */
	public static final int DEFAULT_PORT = 1337;

	/**
	 * port to connect to on remote hosts
	 */
	static int port = DEFAULT_PORT;

	/** String that stores IP Address inputed by users **/
	static String ipAddress;

	/** Object that performs all networking and IO */
	protected static ChatConnectionHandler networker;

	/** Data model for connections list */
	protected static DefaultListModel connModel;

	/** List of active connections */
	protected static JList connections;

	/** Boolean to control MouseListener */
	static boolean active;

	/**
	 * The constructor creates the Board (which in turn creates and manages the
	 * buttons and message label), adds all the components, and sets the bounds
	 * of the components. A null layout is used. (This is the only thing that is
	 * done in the main Checkers class.)
	 */
	public Checkers() {
		setLayout(null); // I will do the layout myself.
		setPreferredSize(new Dimension(720, 450));

		setBackground(new Color(0, 150, 0)); // Dark green background.

		active = true;

		/* Create the components and add them to the panel. */

		Board board = new Board(); // Note: The constructor for the
									// board also creates the buttons
									// and label.

		add(board);
		add(newGameButton);
		add(resignButton);
		add(message);
		add(incoming_);
		add(outgoing_);

		/*
		 * Set the position and size of each component by calling its
		 * setBounds() method.
		 */

		board.setBounds(20, 20, 325, 325);
		newGameButton.setBounds(60, 390, 120, 30);
		resignButton.setBounds(180, 390, 120, 30);
		message.setBounds(0, 350, 350, 30);
		incoming_.setBounds(400, 10, 300, 320);
		outgoing_.setBounds(400, 330, 300, 100);

	} // end constructor

	/**
	 * Button to begin new game after 1 player resigns
	 *
	 * @return the newGameButton
	 */
	static JButton getNewGameButton() {
		return newGameButton;
	}

	/**
	 * Button for player to resign and end game
	 *
	 * @return the resignButton
	 */
	static JButton getResignButton() {
		return resignButton;
	}

	/**
	 * Return message displayed at bottom of screen
	 *
	 * @return message
	 */
	static JLabel getMessage() {
		return message;
	}

	/**
	 * This panel displays a 160-by-160 checkerboard pattern with a 2-pixel
	 * black border. It is assumed that the size of the panel is set to exactly
	 * 164-by-164 pixels. This class does the work of letting the users play
	 * checkers, and it displays the checkerboard.
	 *
	 * @author Jessica, Nanxi, Prachi
	 * @version May 25, 2015
	 * @author Period: 2
	 * @author Assignment: CHECKERS
	 *
	 * @author Sources: David Eck
	 */
	public static class Board extends JPanel implements ActionListener,
			MouseListener, ChatDisplay, KeyListener {

		/**
		 * The data for the checkers board is kept here. This board is also
		 * responsible for generating lists of legal moves.
		 */
		CheckersData board;

		/**
		 * indicates if a game is currently in progress
		 */
		boolean gameInProgress;

		/* The next three variables are valid only when the game is in progress. */

		/**
		 * indicates whose turn it is now. Possible values are CheckersData.RED
		 * and CheckersData.BLACK
		 */
		int currentPlayer;

		/**
		 * If the current player has selected a piece to move, these give the
		 * row and column containing that piece. If no piece is yet selected,
		 * then selectedRow is -1.
		 */
		int selectedRow, selectedCol; //

		/**
		 * An array containing the legal moves for the current player
		 */
		CheckersMove[] legalMoves;

		/** Object that performs all networking and IO */
		protected ChatConnectionHandler networker;

		/** Display of incoming messages */
		protected JTextArea incoming;

		/** Input field for outgoing messages */
		protected JTextField outgoing;

		/**
		 * Constructor. Create the buttons and label. Listens for mouse clicks
		 * and for clicks on the buttons. Create the board and start the first
		 * game.
		 */
		Board() {
			setBackground(Color.BLACK);
			addMouseListener(this);
			addKeyListener(this);
			resignButton = new JButton("Resign");
			resignButton.addActionListener(this);
			newGameButton = new JButton("New Game");
			newGameButton.addActionListener(this);
			incoming_ = (JScrollPane) makeMessageDisplay();
			outgoing_ = (JScrollPane) makeEntryForm();
			message = new JLabel("", JLabel.CENTER);
			message.setFont(new Font("Serif", Font.BOLD, 14));
			message.setForeground(Color.green);
			board = new CheckersData();
			doNewGame();

			/**
			 * Uses input from user to set up connection between ports.
			 */
			Scanner scan = new Scanner(System.in);
			connModel = new DefaultListModel();
			connections = new JList(connModel);

			System.out.print("Enter IP address: ");
			ipAddress = scan.nextLine();

			System.out.print("Enter listen port: ");
			port = scan.nextInt();

			// create a chat networking object to peform I/O
			networker = new ChatConnectionHandler(this, port);

			System.out.print("Enter talk port: ");
			port = scan.nextInt();

			connect();

		}

		/**
		 * Helper method for the constructor which creates the portion of the
		 * screen dealing with displaying incoming messages.
		 *
		 * @return Component The message display component
		 */
		protected Component makeMessageDisplay() {
			incoming = new JTextArea();
			incoming.addKeyListener(this);
			incoming.setEditable(false);

			JScrollPane scroll = new JScrollPane(incoming);
			scroll.setBorder(BorderFactory
					.createTitledBorder("Incoming Messages"));
			return scroll;
		}

		/**
		 * Helper method for the constructor which creates the portion of the
		 * screen dealing with inputting new outgoing messages.
		 *
		 * @return Component The chat composition component
		 */
		protected Component makeEntryForm() {
			outgoing = new JTextField();
			outgoing.addKeyListener(this);
			outgoing.setEditable(true);

			JScrollPane scroll = new JScrollPane(outgoing);
			scroll.setBorder(BorderFactory
					.createTitledBorder("Outgoing Messages"));
			return scroll;
		}

		/**
		 * Respond to user's click on one of the two buttons.
		 *
		 * @evt action event that is performed
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent evt) {
			Object src = evt.getSource();
			if (src == newGameButton) {
				doNewGame(); // tells other user to also begin a
								// new game
				networker.send("doNewGame");
			} else if (src == resignButton) {
				networker.send("doResign"); // tells other user that this user
											// has resigned
				doResign();
			}
		}

		/**
		 * Start a new game
		 */
		void doNewGame() {
			if (gameInProgress == true) {
				// This should not be possible, but it doesn't hurt to check.
				message.setText("Finish the current game first!");
				return;
			}
			board.setUpGame(); // Set up the pieces.
			currentPlayer = CheckersData.RED; // RED moves first.
			legalMoves = board.getLegalMoves(CheckersData.RED); // Get RED's
																// legal
																// moves.
			selectedRow = -1; // RED has not yet selected a piece to move.
			message.setText("Red:  Make your move.");
			gameInProgress = true;
			newGameButton.setEnabled(false);
			resignButton.setEnabled(true);
			repaint();
		}

		/**
		 * Current player resigns. Game ends. Opponent wins.
		 */
		void doResign() {
			if (gameInProgress == false) { // Should be impossible.
				message.setText("There is no game in progress!");
				return;
			}
			if (currentPlayer == CheckersData.RED)
				gameOver("RED resigns.  BLACK wins.");
			else
				gameOver("BLACK resigns.  RED wins.");
		}

		/**
		 * The game ends. The parameter, str, is displayed as a message to the
		 * user. The states of the buttons are adjusted so players can start a
		 * new game. This method is called when the game ends at any point in
		 * this class.
		 *
		 * @param str
		 *            Message is set to this string
		 */
		void gameOver(String str) {
			message.setText(str);
			newGameButton.setEnabled(true);
			resignButton.setEnabled(false);
			gameInProgress = false;
		}

		/**
		 * This method receives messages sent from the other player and calls
		 * the appropriate methods. If the message is "doNewGame," the doNewGame
		 * method is called; if the message is "doResign," the doResign method
		 * is called. If the message is row/column coordinates, the string is
		 * parsed to obtain the coordinates that are entered into the
		 * doClickSquare method's parameters. If the message does not meet any
		 * of those criteria, an error message is generated.
		 *
		 * @see Chat.ChatDisplay#chatMessage(Chat.SocketName, java.lang.String)
		 * @param name
		 *            The socket the message came from
		 * @param message
		 *            The chat message received
		 */
		@Override
		public void chatMessage(Chat.SocketName name, String message) {
			int space = -1;
			// checks if message is telling user to start new game
			if (message.equals("doNewGame"))
				doNewGame();
			// checks if message is telling user that other user has resigned
			else if (message.equals("doResign")) {
				doResign();
			}
			// checks if message is coordinates of square clicked by user
			else if (message.substring(0, 1).equals("*")
					&& (space = message.indexOf(",")) > -1) {
				int r = Integer.parseInt(message.substring(1, space));
				int c = Integer.parseInt(message.substring(space + 1));
				doClickSquare(r, c, false);
			}
			// prints to incoming if message is none of the above
			else {
				incoming.append("Incoming" + ": " + message + '\n');
			}
		}

		/**
		 * This is called by mousePressed() when a player clicks on the square
		 * in the specified row and col. It has already been checked that a game
		 * is, in fact, in progress.
		 *
		 * @param row
		 *            The row position clicked on
		 * @param col
		 *            The col position clicked on
		 * @param local
		 *            If it's currently the user's turn
		 */
		public void doClickSquare(int row, int col, boolean local) {

			if (local) // sends coordinates if move is by the current user
			{
				// send message
				networker.send("*" + row + "," + col);
				// send("["+ row + "," + col + "]");
			}

			/*
			 * If the player clicked on one of the pieces that the player can
			 * move, mark this row and col as selected and return. (This might
			 * change a previous selection.) Reset the message, in case it was
			 * previously displaying an error message.
			 */

			for (int i = 0; i < legalMoves.length; i++)
				if (legalMoves[i].fromRow == row
						&& legalMoves[i].fromCol == col) {
					selectedRow = row;
					selectedCol = col;
					if (currentPlayer == CheckersData.RED)
						message.setText("RED:  Make your move.");
					else
						message.setText("BLACK:  Make your move.");
					repaint();
					return;
				}

			/*
			 * If no piece has been selected to be moved, the user must first
			 * select a piece. Show an error message and return.
			 */

			if (selectedRow < 0) {
				message.setText("Click the piece you want to move.");
				return;
			}

			/*
			 * If the user clicked on a square where the selected piece can be
			 * legally moved, then make the move and return.
			 */

			for (int i = 0; i < legalMoves.length; i++)
				if (legalMoves[i].fromRow == selectedRow
						&& legalMoves[i].fromCol == selectedCol
						&& legalMoves[i].toRow == row
						&& legalMoves[i].toCol == col) {
					doMakeMove(legalMoves[i], local);
					return;
				}

			/*
			 * If we get to this point, there is a piece selected, and the
			 * square where the user just clicked is not one where that piece
			 * can be legally moved. Show an error message.
			 */

			message.setText("Click the square you want to move to.");

		} // end doClickSquare()

		/**
		 * This is called when the current player has chosen the specified move.
		 * Make the move, and then either end or continue the game
		 * appropriately.
		 *
		 * @param move
		 *            The move to be made
		 * @param local
		 *            If it's currently the user's turn
		 */
		void doMakeMove(CheckersMove move, boolean local) {

			board.makeMove(move);

			/*
			 * If the move was a jump, it's possible that the player has another
			 * jump. Check for legal jumps starting from the square that the
			 * player just moved to. If there are any, the player must jump. The
			 * same player continues moving.
			 */

			if (move.isJump()) {
				legalMoves = board.getLegalJumpsFrom(currentPlayer, move.toRow,
						move.toCol);
				if (legalMoves != null) {
					if (currentPlayer == CheckersData.RED)
						message.setText("RED:  You must continue jumping.");
					else
						message.setText("BLACK:  You must continue jumping.");
					selectedRow = move.toRow; // Since only one piece can be
												// moved, select it.
					selectedCol = move.toCol;
					repaint();
					return;
				}
			}

			/*
			 * The current player's turn is ended, so change to the other
			 * player. Get that player's legal moves. If the player has no legal
			 * moves, then the game ends.
			 */

			if (currentPlayer == CheckersData.RED) {
				currentPlayer = CheckersData.BLACK;
				legalMoves = board.getLegalMoves(currentPlayer);
				if (legalMoves == null)
					gameOver("BLACK has no moves.  RED wins.");
				else if (legalMoves[0].isJump())
					message.setText("BLACK:  Make your move.  You must jump.");
				else
					message.setText("BLACK:  Make your move.");
			} else {
				currentPlayer = CheckersData.RED;
				legalMoves = board.getLegalMoves(currentPlayer);
				if (legalMoves == null)
					gameOver("RED has no moves.  BLACK wins.");
				else if (legalMoves[0].isJump())
					message.setText("RED:  Make your move.  You must jump.");
				else
					message.setText("RED:  Make your move.");
			}

			/*
			 * Set selectedRow = -1 to record that the player has not yet
			 * selected a piece to move.
			 */

			selectedRow = -1;

			/*
			 * As a courtesy to the user, if all legal moves use the same piece,
			 * then select that piece automatically so the user won't have to
			 * click on it to select it.
			 */

			if (legalMoves != null) {
				boolean sameStartSquare = true;
				for (int i = 1; i < legalMoves.length; i++)
					if (legalMoves[i].fromRow != legalMoves[0].fromRow
							|| legalMoves[i].fromCol != legalMoves[0].fromCol) {
						sameStartSquare = false;
						break;
					}
				if (sameStartSquare) {
					selectedRow = legalMoves[0].fromRow;
					selectedCol = legalMoves[0].fromCol;
				}
			}

			/* Make sure the board is redrawn in its new state. */

			repaint();

			/*
			 * Checks if the move was made by this user- if so, sets active to
			 * false so that this user cannot move. Else, sets active to true,
			 * indicating that it is now this user's turn.
			 */
			if (local)
				active = false;
			else
				active = true;

		} // end doMakeMove();

		/**
		 * Draw a checkerboard pattern in gray and lightGray. Draw the checkers.
		 * If a game is in progress, hilite the legal moves.
		 *
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 * @param g
		 *            Graphics object
		 */
		public void paintComponent(Graphics g) {

			/* Turn on antialiasing to get nicer ovals. */

			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			/* Draw a two-pixel black border around the edges of the canvas. */

			g.setColor(Color.black);
			g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
			g.drawRect(1, 1, getSize().width - 3, getSize().height - 3);

			/* Draw the squares of the checkerboard and the checkers. */

			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					if (row % 2 == col % 2)
						g.setColor(Color.LIGHT_GRAY);
					else
						g.setColor(Color.GRAY);
					g.fillRect(2 + col * 40, 2 + row * 40, 40, 40);
					switch (board.pieceAt(row, col)) {
					case CheckersData.RED:
						g.setColor(Color.RED);
						g.fillOval(6 + col * 40, 6 + row * 40, 30, 30);
						break;
					case CheckersData.BLACK:
						g.setColor(Color.BLACK);
						g.fillOval(6 + col * 40, 6 + row * 40, 30, 30);
						break;
					case CheckersData.RED_KING:
						g.setColor(Color.RED);
						g.fillOval(6 + col * 40, 6 + row * 40, 30, 30);
						g.setColor(Color.WHITE);
						g.drawString("K", 14 + col * 40, 38 + row * 40);
						break;
					case CheckersData.BLACK_KING:
						g.setColor(Color.BLACK);
						g.fillOval(6 + col * 40, 6 + row * 40, 30, 30);
						g.setColor(Color.WHITE);
						g.drawString("K", 14 + col * 40, 38 + row * 40);
						break;
					}
				}
			}

			/*
			 * If a game is in progress, hilite the legal moves. Note that
			 * legalMoves is never null while a game is in progress.
			 */

			if (gameInProgress) {
				/*
				 * First, draw a 2-pixel cyan border around the pieces that can
				 * be moved.
				 */
				g.setColor(Color.cyan);
				for (int i = 0; i < legalMoves.length; i++) {
					g.drawRect(2 + legalMoves[i].fromCol * 40,
							2 + legalMoves[i].fromRow * 40, 38, 38);
					g.drawRect(3 + legalMoves[i].fromCol * 40,
							3 + legalMoves[i].fromRow * 40, 38, 38);
				}
				/*
				 * If a piece is selected for moving (i.e. if selectedRow >= 0),
				 * then draw a 2-pixel white border around that piece and draw
				 * green borders around each square that that piece can be moved
				 * to.
				 */
				if (selectedRow >= 0) {
					g.setColor(Color.white);
					g.drawRect(2 + selectedCol * 40, 2 + selectedRow * 40, 38,
							38);
					g.drawRect(3 + selectedCol * 40, 3 + selectedRow * 40, 38,
							38);
					g.setColor(Color.green);
					for (int i = 0; i < legalMoves.length; i++) {
						if (legalMoves[i].fromCol == selectedCol
								&& legalMoves[i].fromRow == selectedRow) {
							g.drawRect(2 + legalMoves[i].toCol * 40,
									2 + legalMoves[i].toRow * 40, 38, 38);
							g.drawRect(3 + legalMoves[i].toCol * 40,
									3 + legalMoves[i].toRow * 40, 38, 38);
						}
					}
				}
			}

		} // end paintComponent()

		/**
		 * Respond to a user click on the board. If no game is in progress, show
		 * an error message. Otherwise, find the row and column that the user
		 * clicked and call doClickSquare() to handle it.
		 *
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 * @param evt
		 *            The event generated by the mouse
		 */
		public void mousePressed(MouseEvent evt) {
			if (active) {
				if (gameInProgress == false)
					message.setText("Click \"New Game\" to start a new game.");
				else {
					int col = (evt.getX() - 2) / 40;
					int row = (evt.getY() - 2) / 40;
					if (col >= 0 && col < 8 && row >= 0 && row < 8)
						doClickSquare(row, col, true);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent evt) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent evt) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent evt) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent evt) {
		}

		/**
		 * Display a received chat message. The socket where the message
		 * originated is provided in case you wish to display that information
		 * with the message.
		 *
		 * @param message
		 *            The chat message received
		 */
		@Override
		public void statusMessage(String message) {
			System.out.println(message);
		}

		/**
		 * Helper method to read inputs from GUI components and create a new
		 * socket connection.
		 */
		protected void connect() {
			try {
				SocketName sock = new SocketName(ipAddress, port + "", "port_"
						+ port);

				if (connModel.contains(sock)) {
					statusMessage("Cannot connect to " + sock
							+ ": already connected");
				} else {
					networker.connect(sock);
					statusMessage("Connected to " + sock);
				}
			} catch (IllegalArgumentException iae) {
				statusMessage("Cannot connect: " + iae.getMessage());
			}

		}

		/**
		 * Helper method to read inputs from GUI components and destroy an
		 * existing socket connection.
		 */
		protected void disconnect() {
			int index = connections.getSelectedIndex();
			if (index > -1) {
				SocketName dead = (SocketName) (connModel.elementAt(index));

				networker.disconnect(dead);
			}
		}

		/**
		 * @see ChatDisplay#createSocket
		 */
		public synchronized void createSocket(SocketName name) {
			connModel.addElement(name);
		}

		/**
		 * @see ChatDisplay#destroySocket
		 */
		public void destroySocket(SocketName name) {
			if (connModel.contains(name)) {
				connModel.removeElement(name);
			}
		}

		/**
		 * KeyEvent handler method, used to deal with keyboard input on
		 * components that have registered with this class.
		 *
		 * For the most part, this method listens for the "return" key to be
		 * hit, checks which form element generated the event, and takes the
		 * appropriate action (creating a new message or connection).
		 *
		 * @param e
		 *            The event to handle
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				// find the component that generated the event
				Component source = e.getComponent();

				if (source == outgoing) {
					send();
				}

			}

		}

		/**
		 * Event handling method required by the KeyEvent interface. This method
		 * is empty, as we don't deal with key release events at all.
		 *
		 * @param e
		 *            The key released event (ignored)
		 */
		@Override
		public void keyReleased(KeyEvent e) {

		}

		/**
		 * Event handling method required by the KeyEvent interface. This method
		 * is empty, as we don't deal with key typed events at all.
		 *
		 * @param e
		 *            The key typed event (ignored)
		 */
		@Override
		public void keyTyped(KeyEvent e) {

		}

		/**
		 * Helper method to send a message to all remote sockets.
		 */
		public void send() {
			networker.send(outgoing.getText());

			incoming.append("Me: ");
			incoming.append(outgoing.getText());
			incoming.append("\n");

			outgoing.setText("");
		}

	} // end class Board

}