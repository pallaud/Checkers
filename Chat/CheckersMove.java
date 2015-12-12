package Chat;

/**
 * A CheckersMove object represents a move in the game of Checkers. It holds the
 * row and column of the piece that is to be moved and the row and column of the
 * square to which it is to be moved. (This class makes no guarantee that the
 * move is legal.)
 *
 * @author Jessica, Nanxi, Prachi
 * @version May 25, 2015
 * @author Period: 2
 * @author Assignment: CHECKERS
 *
 * @author Sources: David Eck
 */
public class CheckersMove {

	/**
	 * Row position of piece to be moved.
	 */
	int fromRow;

	/**
	 * Column position of piece to be moved.
	 */
	int fromCol;

	/**
	 * Row position of where the piece is moved to.
	 */
	int toRow;

	/**
	 * Col position of where the piece is moved to.
	 */
	int toCol;

	/**
	 * Constructor. Just set the values of the instance variables.
	 * 
	 * @param r1
	 *            is set as the row of the piece to be moved
	 * @param c1
	 *            is set as the col of the piece to be moved
	 * @param r2
	 *            is set as the row of where the piece is moved to
	 * @param c2
	 *            is set as the col of where the piece is moved to
	 */
	public CheckersMove(int r1, int c1, int r2, int c2) {
		fromRow = r1;
		fromCol = c1;
		toRow = r2;
		toCol = c2;
	}

	/**
	 * Test whether this move is a jump. It is assumed that the move is legal.
	 * In a jump, the piece moves two rows. (In a regular move, it only moves
	 * one row.)
	 * 
	 * @return whether the move is a jump
	 */
	boolean isJump() {

		return (fromRow - toRow == 2 || fromRow - toRow == -2);
	}

	/**
	 * Test whether this instance of a CheckersMove is equal to another
	 * CheckersMove instance
	 * 
	 * @param c
	 *            the CheckersMove instance the current instance is being
	 *            compared to
	 * @return if the current instance of CheckersMove is equal to c
	 */
	boolean equals(CheckersMove c) {
		if (this.fromRow == c.fromRow && this.fromCol == c.fromCol
				&& this.toRow == c.toRow && this.toCol == c.toCol) {
			return true;
		}
		return false;

	}

	/**
	 * Returns the current instance of CheckersMove in a string format.
	 * 
	 * @return the current instance of CheckersMove in a string format
	 */
	public String toString() {
		return ("" + fromRow + ", " + fromCol + ", " + toRow + ", " + toCol);
	}
} // end class CheckersMove.