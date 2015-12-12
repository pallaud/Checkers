package Chat;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import org.junit.*;

import Chat.ChatGui;

/**
 * Tests for the CheckersData, CheckersMove, and Checkers classes
 *
 * @author Jessica, Nanxi, Prachi
 * @version May 4, 2015
 * @author Period: 2
 * @author Assignment: FINAL_PROJECT_CHECKERS
 *
 * @author Sources: none
 */
public class JUtestCheckers {
	// Jessica IP Address: 192.168.0.16
	// School comp: 172.18.240.56

	// CheckersData Test Class
	/**
	 * Numerical representation of an empty piece on the board
	 */
	int EMPTY = 0;

	/**
	 * Numerical representation of a red piece on the board
	 */
	int RED = 1;

	/**
	 * Numerical representation of a red king piece on the board
	 */
	int RED_KING = 2;

	/**
	 * Numerical representation of a black piece on the board
	 */
	int BLACK = 3;

	/**
	 * Numerical representation of a black king piece on the board
	 */
	int BLACK_KING = 4;

	/**
	 * Tests the constructor and method setUpGame() in the class CheckersData
	 * Compares each piece on the board to the expected value
	 */
	@Test
	public void checkersDataConstructorAndSetUpGame() {
		CheckersData d = new CheckersData();

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if (row % 2 == col % 2) {
					if (row < 3)
						assertEquals(d.pieceAt(row, col), BLACK);
					else if (row > 4)
						assertEquals(d.pieceAt(row, col), RED);
					else
						assertEquals(d.pieceAt(row, col), EMPTY);
				} else {
					assertEquals(d.pieceAt(row, col), EMPTY);
				}
			}
		}
	}

	/**
	 * Tests the method pieceAt() in the class CheckersData
	 */
	@Test
	public void checkersDataPieceAt() {
		CheckersData d = new CheckersData();
		assertEquals(d.pieceAt(6, 4), RED);
	}

	/**
	 * Tests the method setPieceAt() in the class CheckersData ensures that the
	 * appropriate change to the location is made.
	 */
	@Test
	public void checkersDataSetPieceAt() {
		CheckersData d = new CheckersData();
		d.setPieceAt(6, 4, BLACK);
		assertEquals(d.pieceAt(6, 4), BLACK);
	}

	/**
	 * Tests the method makeMove() in the class CheckersData Condition: the move
	 * is a jump, so tests that the piece that was jumped over is removed
	 */
	@Test
	public void checkersDataMakeMoveCaseA() {
		CheckersData d = new CheckersData();
		CheckersMove m = new CheckersMove(0, 1, 2, 3); // legal jump
		d.makeMove(m);
		assertEquals(d.pieceAt(1, 2), EMPTY);
	}

	/**
	 * Tests the method makeMove() in the class CheckersData Condition: The red
	 * piece is at the last row on the black side, so test that it becomes a red
	 * king
	 */
	@Test
	public void checkersDataMakeMoveCaseB() {
		CheckersData d = new CheckersData();
		CheckersMove m = new CheckersMove(6, 4, 0, 0); // not legal jump
		d.makeMove(m);
		assertEquals(d.pieceAt(0, 0), RED_KING);
	}

	/**
	 * Tests the method makeMove() in the class CheckersData Condition: The
	 * black piece is at the last row on the red side, so test that it becomes a
	 * black king
	 */
	@Test
	public void checkersDataMakeMoveCaseC() {
		CheckersData d = new CheckersData();
		CheckersMove m = new CheckersMove(2, 2, 7, 0); // not legal jump
		d.makeMove(m);
		assertEquals(d.pieceAt(7, 0), BLACK_KING);
	}

	/**
	 * Tests the method getLegalMoves() in the class CheckersData
	 */
	@Test
	public void checkersDataGetLegalMoves() {
		CheckersData d = new CheckersData();

		// Condition: not a legal player, test that there are no legal moves
		assertNull(d.getLegalMoves(EMPTY));

		// Condition: no possible moves, so getLegalMoves() should return null
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				d.setPieceAt(row, col, RED);
			}
		}
		assertNull(d.getLegalMoves(RED));

		d.setUpGame();// reset

		// Condition: no possible jump, only moves

		// Case: RED
		// Possible moves for red at initial setup
		CheckersMove[] alpha = new CheckersMove[7];
		alpha[0] = new CheckersMove(5, 1, 4, 2);
		alpha[1] = new CheckersMove(5, 1, 4, 0);
		alpha[2] = new CheckersMove(5, 3, 4, 4);
		alpha[3] = new CheckersMove(5, 3, 4, 2);
		alpha[4] = new CheckersMove(5, 5, 4, 6);
		alpha[5] = new CheckersMove(5, 5, 4, 4);
		alpha[6] = new CheckersMove(5, 7, 4, 6);

		CheckersMove[] testA = d.getLegalMoves(RED);
		for (int i = 0; i < testA.length; i++) {
			assertTrue(testA[i].equals(alpha[i]));
		}

		// Case: BLACK
		// Possible moves for red at initial setup
		CheckersMove[] beta = new CheckersMove[7];
		beta[0] = new CheckersMove(2, 0, 3, 1);
		beta[1] = new CheckersMove(2, 2, 3, 3);
		beta[2] = new CheckersMove(2, 2, 3, 1);
		beta[3] = new CheckersMove(2, 4, 3, 5);
		beta[4] = new CheckersMove(2, 4, 3, 3);
		beta[5] = new CheckersMove(2, 6, 3, 7);
		beta[6] = new CheckersMove(2, 6, 3, 5);

		CheckersMove[] testB = d.getLegalMoves(BLACK);
		for (int i = 0; i < testB.length; i++) {
			assertTrue(testB[i].equals(beta[i]));
		}

		// Condition: there are legal Jumps
		d.setPieceAt(4, 2, BLACK);

		CheckersMove[] gamma = new CheckersMove[7];
		gamma[0] = new CheckersMove(5, 1, 3, 3);
		gamma[1] = new CheckersMove(5, 3, 3, 1);

		CheckersMove[] testC = d.getLegalMoves(RED);
		for (int i = 0; i < testC.length; i++) {
			assertTrue(testC[i].equals(gamma[i]));
		}
	}

	/**
	 * Tests the method getLegalJumpsFrom() in the class CheckersData
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void checkersDataGetLegalJumpsFrom() {
		CheckersData d = new CheckersData();

		// Condition: empty piece, so no legal jumps, so returns null
		assertNull(d.getLegalJumpsFrom(EMPTY, 0, 0));

		// Condition: such a piece does not exist at that location, so returns
		// null
		assertNull(d.getLegalJumpsFrom(RED, 0, 0));

		// Condition: test diagonal lower right jump
		d.setPieceAt(3, 3, RED);
		CheckersMove[] alpha = new CheckersMove[1];
		CheckersMove a = new CheckersMove(2, 2, 4, 4);
		alpha[0] = a;
		CheckersMove[] testA = d.getLegalJumpsFrom(BLACK, 2, 2);
		assertTrue(alpha[0].equals(testA[0]));

		d.setUpGame();// reset

		// Condition: test diagonal lower left jump
		d.setPieceAt(3, 1, RED);
		CheckersMove[] beta = new CheckersMove[1];

		CheckersMove b = new CheckersMove(2, 2, 4, 0);
		beta[0] = b;
		CheckersMove[] testB = d.getLegalJumpsFrom(BLACK, 2, 2);
		assertTrue(beta[0].equals(testB[0]));

		d.setUpGame(); // reset

		// Condition: test diagonal upper right jump
		d.setPieceAt(4, 4, BLACK);
		CheckersMove[] gamma = new CheckersMove[1];
		CheckersMove g = new CheckersMove(5, 3, 3, 5);
		gamma[0] = g;
		CheckersMove[] testC = d.getLegalJumpsFrom(RED, 5, 3);
		assertTrue(gamma[0].equals(testC[0]));

		d.setUpGame(); // reset

		// Condition: test diagonal upper left jump
		d.setPieceAt(4, 4, BLACK);
		CheckersMove[] delta = new CheckersMove[1];
		CheckersMove de = new CheckersMove(5, 5, 3, 3);
		delta[0] = de;
		CheckersMove[] testD = d.getLegalJumpsFrom(RED, 5, 5);
		assertTrue(delta[0].equals(testD[0]));
	}

	/**
	 * Tests the method canJump() in the class CheckersData
	 */
	@Test
	public void checkersDataCanJump() {
		CheckersData d = new CheckersData();

		assertFalse(d.canJump(RED_KING, 0, 0, 1, 1, -1, 2)); // Condition:
																// off the
																// board

		assertFalse(d.canJump(RED_KING, 0, 0, 1, 1, 8, 2)); // Condition:
															// off the board

		assertFalse(d.canJump(RED_KING, 0, 0, 1, 1, 3, -1)); // Condition:
																// off the
																// board

		assertFalse(d.canJump(RED_KING, 0, 0, 1, 1, 3, 8)); // Condition:
															// off the board

		assertFalse(d.canJump(RED_KING, 0, 0, 1, 1, 0, 0)); // Condition:
															// location
															// already
															// contains a
															// piece.

		assertFalse(d.canJump(RED, 5, 1, 1, 1, 6, 1)); // Condition: Regular
														// red piece can only
														// move up.

		assertFalse(d.canJump(RED, 5, 1, 2, 1, 6, 1)); // Condition: There
														// is no black piece
														// to for a red piece
														// to jump.

		assertTrue(d.canJump(RED, 5, 1, 2, 0, 4, 1)); // legal red jump

		assertFalse(d.canJump(BLACK, 1, 1, 1, 1, 0, 1)); // Condition:
															// Regular black
															// piece can only
															// move down.

		assertFalse(d.canJump(BLACK, 5, 1, 1, 2, 6, 1)); // Condition: There
															// is no red piece
															// to for a black
															// piece to jump.

		assertTrue(d.canJump(BLACK, 1, 1, 5, 1, 4, 1)); // legal black jump
	}

	/**
	 * Tests the method canMove() in the class CheckersData
	 */
	@Test
	public void checkersDataCanMove() {
		CheckersData d = new CheckersData();
		assertFalse(d.canMove(RED, 0, 0, -1, 0)); // Condition: off the
													// board
		assertFalse(d.canMove(RED, 0, 0, 8, 0)); // Condition: off the board
		assertFalse(d.canMove(RED, 0, 0, 0, -1)); // Condition: off the
													// board
		assertFalse(d.canMove(RED, 0, 0, 0, 8)); // Condition: off the board

		assertFalse(d.canMove(RED, 0, 0, 0, 2)); // Condition: location
													// already contains a
													// piece.

		assertFalse(d.canMove(RED, 6, 0, 7, 0)); // Condition: red piece can
													// only move up
		assertTrue(d.canMove(RED, 6, 0, 5, 0)); // legal move

		assertFalse(d.canMove(BLACK, 2, 0, 1, 0)); // Condition: black piece
													// only moves down
		assertTrue(d.canMove(BLACK, 2, 2, 3, 3)); // legal move
	}

	// CheckersMove Test Class
	/**
	 * Tests the constructor in the class CheckersMove tests that assignments
	 * worked
	 */
	@Test
	public void checkersMoveConstructor() {
		CheckersMove m = new CheckersMove(0, 1, 2, 3);
		assertEquals(m.fromRow, 0);
		assertEquals(m.fromCol, 1);
		assertEquals(m.toRow, 2);
		assertEquals(m.toCol, 3);
	}

	/**
	 * Tests the method isJump() in the class CheckersMove
	 */
	@Test
	public void checkersMoveIsJump() {
		CheckersMove m = new CheckersMove(0, 1, 2, 3); // Condition: fromRow -
														// toRow == 2: VALID
		assertTrue(m.isJump());

		CheckersMove o = new CheckersMove(2, 1, 0, 3); // Condition: fromRow -
														// toRow == 2: VALID
		assertTrue(m.isJump());

		CheckersMove p = new CheckersMove(0, 1, 3, 3); // Condition: fromRow -
														// toRow == -3: INVALID
		assertFalse(p.isJump());
	}

	/**
	 * Tests the method equals() in the class CheckersMove asserts that fromRow,
	 * fromCol, toRow, toCol are equivalent
	 */
	@Test
	public void checkersMoveEquals() {
		CheckersMove m = new CheckersMove(0, 1, 3, 3);
		CheckersMove n = new CheckersMove(0, 1, 3, 3);
		CheckersMove o = new CheckersMove(0, 1, 3, 4);
		assertTrue(m.equals(n));
		assertFalse(m.equals(o));
	}

	/**
	 * Tests the method toString() in the class CheckersMove.
	 */
	@Test
	public void checkersMovetoString() {
		CheckersMove m = new CheckersMove(0, 1, 3, 3);
		String s = m.toString();
		assertTrue(s.equals("0, 1, 3, 3"));
	}

	// Checkers Class Tests

	/**
	 * Tests main method in class Checkers
	 */
	@Test
	public void checkersMainMethod() {
		// nothing to test
	}

	/**
	 * Tests the constructor and the methods getNewGameButton(),
	 * getResignButton(), getMessage() in the class Checkers
	 */
	@Test
	public void checkersConstructorAndGetterMethods() {

		Checkers c = new Checkers();
		assertNull(c.getLayout());

		assertEquals(c.getBackground(), new Color(0, 150, 0));
		assertEquals(c.getPreferredSize(), new Dimension(720, 450));

		assertTrue(c.active);

		JButton testA = new JButton();
		testA.setBounds(60, 390, 120, 30);

		JButton testB = new JButton();
		testB.setBounds(180, 390, 120, 30);

		assertEquals(c.getNewGameButton().getBounds(), testA.getBounds());
		assertEquals(c.getResignButton().getBounds(), testB.getBounds());

		JLabel testMessage = new JLabel();
		testMessage.setBounds(0, 350, 350, 30);

		assertEquals(c.getMessage().getBounds(), testMessage.getBounds());

		// tests everything except board methods in Checkers Constructor
	}

	// Checkers.Board Class Tests
	/**
	 * Tests the constructor in the Checkers.Board class
	 */
	@Test
	public void boardConstructor() {
		Checkers.Board b = new Checkers.Board();

		assertEquals(b.getBackground(), Color.BLACK);
		assertEquals(Checkers.getResignButton().getText(), "Resign");
		assertEquals(Checkers.getNewGameButton().getText(), "New Game");
		assertEquals(Checkers.getMessage().getText(), "Red:  Make your move.");
		assertEquals(Checkers.getMessage().getHorizontalAlignment(),
				JLabel.CENTER);
		assertEquals(Checkers.getMessage().getFont(), new Font("Serif",
				Font.BOLD, 14));
		assertEquals(Checkers.getMessage().getForeground(), Color.green);
		// doNewGame is tested separately

		// assertTrue(Checkers.connModel.equals(new DefaultListModel()));
		// assertTrue(Checkers.connections.equals(new JList(new
		// DefaultListModel())));

		// assertEquals(Checkers.ipAddress, "172.18.240.56"); //when tested on
		// school comp
		// assertEquals(Checkers.port, 1339); //using 1338 as listener, 1339 as
		// talk port

		// assertTrue(Checkers.networker.equals( new ChatConnectionHandler(b,
		// 1337) ));
	}

	/**
	 * Tests the method actionPerformed() in the Checkers.Board class
	 */
	@Test
	public void boardActionPerformed() {
		Checkers.Board b = new Checkers.Board();
		ActionEvent a = new ActionEvent(Checkers.newGameButton, 45, "test"); // source
																				// is
																				// newGameButton
		b.actionPerformed(a);
		assertEquals(Checkers.getMessage().getText(),
				"Finish the current game first!"); // check that correct
													// decision
													// path is followed

		ActionEvent c = new ActionEvent(Checkers.resignButton, 45, "test");
		// source is resignButton
		b.actionPerformed(c);
		assertFalse(b.gameInProgress); // check that correct decision path is
										// followed
	}

	/**
	 * Tests the method doNewGame() in the Checkers.Board class In this
	 * scenario, the current game has not yet been terminated
	 */
	@Test
	public void boardDoNewGameA() {
		Checkers.Board b = new Checkers.Board();
		b.gameInProgress = true;
		b.doNewGame();
		assertEquals(Checkers.getMessage().getText(),
				"Finish the current game first!");
	}

	/**
	 * Tests the method doNewGame() in the Checkers.Board class In this
	 * scenario, the current game has ended. A new board is set up accordingly.
	 */
	@Test
	public void boardDoNewGameB() {
		Checkers.Board b = new Checkers.Board();
		b.gameInProgress = false;
		b.doNewGame();

		assertEquals(b.currentPlayer, RED);
		CheckersMove[] redMoves = new CheckersMove[7];
		redMoves[0] = new CheckersMove(5, 1, 4, 2);
		redMoves[1] = new CheckersMove(5, 1, 4, 0);
		redMoves[2] = new CheckersMove(5, 3, 4, 4);
		redMoves[3] = new CheckersMove(5, 3, 4, 2);
		redMoves[4] = new CheckersMove(5, 5, 4, 6);
		redMoves[5] = new CheckersMove(5, 5, 4, 4);
		redMoves[6] = new CheckersMove(5, 7, 4, 6);

		CheckersMove[] testA = b.legalMoves;
		for (int i = 0; i < testA.length; i++) {
			assertTrue(testA[i].equals(redMoves[i]));
		}

		assertEquals(b.selectedRow, -1);
		assertEquals(Checkers.getMessage().getText(), "Red:  Make your move.");
		assertTrue(b.gameInProgress);
		assertFalse(Checkers.getNewGameButton().isEnabled());
		assertTrue(Checkers.getResignButton().isEnabled());
		// repaint() not tested
	}

	/**
	 * Tests doResign() method in the Checkers.Board class. Condition is that
	 * there is no game in progress.
	 */
	@Test
	public void boardDoResignA() {
		Checkers.Board b = new Checkers.Board();
		b.gameInProgress = false;
		b.doResign();
		assertEquals(Checkers.getMessage().getText(),
				"There is no game in progress!");
	}

	/**
	 * Tests Board.doResign() method in the case of black winning in the
	 * Checkers.Board class. Also executes and tests the Game Over method
	 */
	@Test
	public void boardDoResignBlackWinsAndGameOver() {
		Checkers.Board b = new Checkers.Board();
		b.gameInProgress = true;
		b.currentPlayer = RED;
		b.doResign();
		assertEquals(Checkers.getMessage().getText(),
				"RED resigns.  BLACK wins.");
		assertTrue(Checkers.newGameButton.isEnabled());
		assertFalse(Checkers.resignButton.isEnabled());
		assertFalse(b.gameInProgress);
	}

	/**
	 * Tests doResign() method in the case of red winning in the Checkers.Board
	 * class. Also executes and tests the Game Over method
	 */
	@Test
	public void boardDoResignRedWinsAndGameOver() {
		Checkers.Board b = new Checkers.Board();
		b.gameInProgress = true;
		b.currentPlayer = BLACK;
		b.doResign();
		assertEquals(Checkers.getMessage().getText(),
				"BLACK resigns.  RED wins.");
		assertTrue(Checkers.newGameButton.isEnabled());
		assertFalse(Checkers.resignButton.isEnabled());
		assertFalse(b.gameInProgress);
	}

	/**
	 * Tests doClickSquare() method in the Checkers.Board class.
	 */
	@Test
	public void boardDoClickSquare() {
		Checkers.Board b = new Checkers.Board();
		b.currentPlayer = RED;

		for (int row = 3; row < 5; row++) {
			for (int col = 0; col < 8; col++) {
				b.board.setPieceAt(row, col, BLACK);
			}
		}
		b.board.setPieceAt(4, 0, EMPTY); // only possible space to move in
		b.selectedRow = -1;
		b.legalMoves = b.board.getLegalMoves(RED);

		// Condition: player clicked on one of the pieces that the player can
		// move, mark this row and col as selected and return. (This might
		// change a previous selection.) Reset the message
		b.doClickSquare(5, 1, true);

		assertEquals(Checkers.getMessage().getText(), "RED:  Make your move.");
		assertEquals(b.selectedRow, 5);
		assertEquals(b.selectedCol, 1);

		b.currentPlayer = BLACK;
		b.doClickSquare(5, 1, true);
		assertEquals(Checkers.getMessage().getText(), "BLACK:  Make your move.");

		// Condition: no piece has been selected to be moved
		b.selectedRow = -1;
		b.doClickSquare(5, 0, true);
		assertEquals(Checkers.getMessage().getText(),
				"Click the piece you want to move.");

		// Condition: If the user clicked on a square where the selected piece
		// can be
		// legally moved, then make the move and return.
		b.selectedRow = 5;
		b.selectedCol = 1;
		b.doClickSquare(4, 0, true);
		assertEquals(Checkers.getMessage().getText(), "RED:  Make your move.");

		// Condition: there is a piece selected, and the
		// square where the user just clicked is not one where that piece
		// can be legally moved.
		b.selectedRow = 5;
		b.doClickSquare(4, 2, true);
		assertEquals(Checkers.getMessage().getText(),
				"Click the square you want to move to.");
	}

	/**
	 * Tests chatMessage() method in the Checkers.Board class. WILL NEED TO BE
	 * UPDATED
	 */
	@Test
	public void boardChatMessage() {
		Checkers.Board b = new Checkers.Board();
		SocketName s = new SocketName("a", 1025, "b");

		// Condition: message is doNewGame
		b.chatMessage(s, "doNewGame");
		assertEquals(Checkers.getMessage().getText(),
				"Finish the current game first!"); // asserts that doNewGame was
													// called
		// Condition: message is doResign
		b.chatMessage(s, "doResign");
		assertFalse(b.gameInProgress); // asserts that doResign was called

		// Condition: message is coordinates
		// setUp for doClickSquare execution
		for (int row = 3; row < 5; row++) {
			for (int col = 0; col < 8; col++) {
				b.board.setPieceAt(row, col, BLACK);
			}
		}
		b.board.setPieceAt(4, 0, EMPTY);
		b.selectedRow = -1;
		b.legalMoves = b.board.getLegalMoves(RED);
		// end

		b.chatMessage(s, "*5,1");
		assertEquals(Checkers.getMessage().getText(), "RED:  Make your move.");
		assertEquals(b.selectedRow, 5);
		assertEquals(b.selectedCol, 1);

		// Condition: message is else
		// Tested through user acceptance
	}

	/**
	 * Tests doMakeMove() method in the Checkers.Board class.
	 */
	@Test
	public void boardDoMakeMoveA() {
		// Condition: legal jump, has one more move, red player

		Checkers.Board b = new Checkers.Board();
		b.currentPlayer = RED;
		b.board.setPieceAt(4, 2, BLACK);
		b.board.setPieceAt(1, 3, EMPTY);
		CheckersMove m = new CheckersMove(5, 3, 3, 1); // legal jump, 2 in a
		// row
		CheckersMove n = new CheckersMove(3, 1, 1, 3);

		b.doMakeMove(m, true);
		assertEquals(Checkers.getMessage().getText(),
				"RED:  You must continue jumping.");
		assertTrue(b.legalMoves[0].equals(n));
		assertEquals(b.selectedRow, 3);
		assertEquals(b.selectedCol, 1);

		b.board.setUpGame(); // reload board

		// Condition: legal jump, has one more move, black player

		b.currentPlayer = BLACK;
		b.board.setPieceAt(3, 5, RED);
		b.board.setPieceAt(6, 4, EMPTY);
		m = new CheckersMove(2, 4, 4, 6); // legal jump, 2 in a row
		n = new CheckersMove(4, 6, 6, 4);

		b.doMakeMove(m, true);
		assertEquals(Checkers.getMessage().getText(),
				"BLACK:  You must continue jumping.");
		assertTrue(b.legalMoves[0].equals(n));
		assertEquals(b.selectedRow, 4);
		assertEquals(b.selectedCol, 6);
	}

	/**
	 * Tests doMakeMove() method in the Checkers.Board class.
	 */
	@Test
	public void boardDoMakeMoveB() {
		// Condition: no legal jump, no legal moves. intially RED player.
		Checkers.Board b = new Checkers.Board();
		b.currentPlayer = RED;
		CheckersMove m = new CheckersMove(0, 0, 1, 1); // not legal jump
		// no possible moves for BLACK
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				b.board.setPieceAt(row, col, RED);
			}
		}

		b.doMakeMove(m, true);
		assertEquals(b.currentPlayer, BLACK);
		assertNull(b.legalMoves);
		assertEquals(Checkers.getMessage().getText(),
				"BLACK has no moves.  RED wins.");
		assertEquals(b.selectedRow, -1);

		b.board.setUpGame(); // reload board

		// Condition: no legal jump, only legal move is jump. intially RED
		// player.
		b.currentPlayer = RED;
		m = new CheckersMove(0, 0, 1, 1); // not legal jump
		for (int row = 3; row < 5; row++) {
			for (int col = 0; col < 8; col++) {
				b.board.setPieceAt(row, col, RED);
			}
		}
		b.board.setPieceAt(4, 4, EMPTY);

		b.doMakeMove(m, true);
		assertEquals(Checkers.getMessage().getText(),
				"BLACK:  Make your move.  You must jump.");

		b.board.setUpGame(); // reload board

		// Condition: no legal jump, legal moves are all moves. intially RED
		// player.
		b.currentPlayer = RED;
		m = new CheckersMove(0, 0, 1, 1); // not legal jump
		for (int row = 3; row < 5; row++) {
			for (int col = 0; col < 8; col++) {
				b.board.setPieceAt(row, col, RED);
			}
		}
		b.board.setPieceAt(3, 3, EMPTY);

		b.doMakeMove(m, true);
		assertEquals(Checkers.getMessage().getText(), "BLACK:  Make your move.");
	}

	/**
	 * Tests doMakeMove() method in the Checkers.Board class.
	 */
	@Test
	public void boardDoMakeMoveC() {
		// Condition: no legal jump, no legal moves. intially BLACK player.
		Checkers.Board b = new Checkers.Board();
		b.currentPlayer = BLACK;
		CheckersMove m = new CheckersMove(0, 0, 1, 1); // not legal jump
		// no possible moves for RED
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				b.board.setPieceAt(row, col, BLACK);
			}
		}

		b.doMakeMove(m, true);
		assertEquals(b.currentPlayer, RED);
		assertNull(b.legalMoves);
		assertEquals(Checkers.getMessage().getText(),
				"RED has no moves.  BLACK wins.");
		assertEquals(b.selectedRow, -1);

		b.board.setUpGame(); // reload board

		// Condition: no legal jump, only legal move is jump. intially BLACK
		// player.
		b.currentPlayer = BLACK;
		m = new CheckersMove(0, 0, 1, 1); // not legal jump
		for (int row = 3; row < 5; row++) {
			for (int col = 0; col < 8; col++) {
				b.board.setPieceAt(row, col, BLACK);
			}
		}
		b.board.setPieceAt(3, 3, EMPTY);

		b.doMakeMove(m, true);
		assertEquals(Checkers.getMessage().getText(),
				"RED:  Make your move.  You must jump.");

		b.board.setUpGame(); // reload board

		// Condition: no legal jump, legal moves are all moves. intially BLACK
		// player.
		b.currentPlayer = BLACK;
		m = new CheckersMove(0, 0, 1, 1); // not legal jump
		for (int row = 3; row < 5; row++) {
			for (int col = 0; col < 8; col++) {
				b.board.setPieceAt(row, col, BLACK);
			}
		}
		b.board.setPieceAt(4, 4, EMPTY);

		b.doMakeMove(m, true);
		assertEquals(Checkers.getMessage().getText(), "RED:  Make your move.");
	}

	/**
	 * Tests doMakeMove() method in the Checkers.Board class.
	 */
	@Test
	public void boardDoMakeMoveD() {
		// Condition: no legal jump, multiple legal moves
		Checkers.Board b = new Checkers.Board();
		b.currentPlayer = BLACK;
		CheckersMove m = new CheckersMove(0, 0, 1, 1); // not legal jump
		for (int row = 3; row < 5; row++) {
			for (int col = 0; col < 8; col++) {
				b.board.setPieceAt(row, col, BLACK);
			}
		}
		b.board.setPieceAt(4, 2, EMPTY); // possible Move 1
		b.board.setPieceAt(4, 4, EMPTY); // possible Move 2

		b.doMakeMove(m, true);
		assertEquals(b.selectedRow, -1);

		b.board.setUpGame(); // reload board

		// Condition: no legal jump, only one legal move. courtesy.
		b.currentPlayer = BLACK;
		m = new CheckersMove(0, 0, 1, 1); // not legal jump
		for (int row = 3; row < 5; row++) {
			for (int col = 0; col < 8; col++) {
				b.board.setPieceAt(row, col, BLACK);
			}
		}
		b.board.setPieceAt(4, 0, EMPTY); // only possible space to move in

		// courtesy select
		b.doMakeMove(m, true);
		assertEquals(b.selectedRow, 5);
		assertEquals(b.selectedCol, 1);
		assertFalse(Checkers.active);

		b.board.setUpGame(); // reload board

		// Condition: no legal jump, only one legal move. courtesy.
		b.currentPlayer = BLACK;
		m = new CheckersMove(0, 0, 1, 1); // not legal jump
		for (int row = 3; row < 5; row++) {
			for (int col = 0; col < 8; col++) {
				b.board.setPieceAt(row, col, BLACK);
			}
		}
		b.board.setPieceAt(4, 0, EMPTY); // only possible space to move in

		// courtesy select
		b.doMakeMove(m, false);
		assertEquals(b.selectedRow, 5);
		assertEquals(b.selectedCol, 1);
		assertTrue(Checkers.active);

	}

	/**
	 * Tests paintComponent() method in the Checkers.Board class.
	 */
	@Test
	public void boardPaintComponent() {

		// Graphics g = new Graphics();
		// ^cannot instantiate so there's really no point of testing this method
	}

	/**
	 * Tests mousePressed() method in the Checkers.Board class.
	 */
	@Test
	public void boardMousePressed() {
		Checkers.Board b = new Checkers.Board();
		MouseEvent m = new MouseEvent(b, 1, 1, 1, 162, 82, 1, false, 1);

		// Condition: no game in progress
		Checkers.active = true;
		b.gameInProgress = false;
		b.mousePressed(m);
		assertEquals(Checkers.getMessage().getText(),
				"Click \"New Game\" to start a new game.");

		// Condition: game in progress, users's turn, doClickSquare
		b.gameInProgress = true;
		Checkers.active = true;
		b.mousePressed(m);
		assertEquals(Checkers.getMessage().getText(),
				"Click the piece you want to move.");
	}

	/**
	 * Tests connect() method in the Checkers.Board class.
	 */
	@Test
	public void boardConnect() {
		Checkers.Board b = new Checkers.Board();

		SocketName s = new SocketName(Checkers.ipAddress, Checkers.port + "",
				"port_" + Checkers.port);
		try {
			// Condition: if (connModel.contains(sock)
			Checkers.connModel.addElement(s);

			// Check to see printed: "Cannot connect to " + sock +
			// ": already connected"
			b.connect();
		} catch (Exception e) {
		}

		Checkers.connModel.removeElement(s);
		try {
			// Condition: if (!connModel.contains(sock)
			b.connect();
			// Check to see printed: "Connected to "
		} catch (Exception e) {
		}
	}

	/**
	 * Tests disconnect() method in the Checkers.Board class.
	 */
	@Test
	public void boardDisconnect() {
		Checkers.Board b = new Checkers.Board();
		assertNotNull(b);
		// System.out.println(Checkers.connections.getSelectedIndex());
		// incomplete
	}

	/**
	 * Tests createSocket() method in the Checkers.Board class.
	 */
	@Test
	public void boardCreateSocket() {
		SocketName s = new SocketName("PortA", 1024, "nameA");
		Checkers.Board b = new Checkers.Board();
		b.createSocket(s);
		// Socket s should be in connModel
		assertTrue(Checkers.connModel.contains(s));
	}

	/**
	 * Tests destroySocket() method in the Checkers.Board class.
	 */
	@Test
	public void boardDestroySocket() {
		SocketName s = new SocketName("PortA", 1024, "nameA");
		Checkers.Board b = new Checkers.Board();
		Checkers.connModel.addElement(s);
		b.destroySocket(s);
		// socket s should no longer be in connModel
		assertTrue(!Checkers.connModel.contains(s));
	}
}
