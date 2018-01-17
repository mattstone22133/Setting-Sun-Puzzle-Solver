package enigma.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * This class provides the functionality to solve the puzzle. The puzzle is solved using a breadth
 * first search style of algorithm that employees memoization to have realistic run times for
 * solving the puzzle.
 * 
 * To summarize how the algorithm works:
 * 
 * 1. There is a state class which acts as a linked list. The state class saves a configuration of
 * the game in a "long" data vector. the state class is a singly linked list that can point to
 * previous states.
 * 
 * 2. There is a boolean 2d array that represents the game board. Positions marked as true represent
 * that location being occupied by a piece.
 * 
 * 3. There is a balanced binary search tree that state's keys (the data vector is the key) are
 * inserted to when they are found to be a valid configuration. This provides memoization and allows
 * the program to look up if a state has already been visited (and it is therefore discarded)
 * 
 * 4. The breadth first algorithm works by first loading the start state into a queue. There is then
 * a while loop that operates as long as there are states in the queue. As a state is pulled out
 * from the queue, each piece is checked for new valid moves (the boolean array from 3. is used in
 * this validation). If a valid move is found, the new resulting state is checked to be the winning
 * state and is also checked to see if the state has already been encountered. If it is a winning
 * state, the function (solvePuzzle) returns a linked list of the states required to transition to
 * to find the solution.
 * 
 * Note: there were many optimizations made to reduce object creations, but what is above is the
 * general idea. e.g. new states are only created if it is a state that will go into the queue (or a
 * winning state).
 * 
 * 
 * 
 * 
 * @author Matt Stone
 * @version 1/1/17
 *
 */
public class SettingSun {

	private final int row = 5;
	private final int col = 4;
	private boolean[][] board = new boolean[row][col];
	private Queue<State> stateQueue = new LinkedList<State>();
	// private BSTNode memo; //TODO remove this from github
	private TreeSet<Long> memo;
	private State winState = null;

	// pieces
	SunPiece sun;
	WidePiece wide;
	TallPiece tall1;
	TallPiece tall2;
	TallPiece tall3;
	TallPiece tall4;
	SmallPiece small1;
	SmallPiece small2;
	SmallPiece small3;
	SmallPiece small4;
	private Point winPoint = new Point(1, 3);

	// other
	int iterationCount;
	private State tempState;

	/**
	 * Standard constructor that sets the piece objects to the starting state of the board.f
	 */
	public SettingSun() {
		sun = new SunPiece(1, 0);
		wide = new WidePiece(1, 2);
		tall1 = new TallPiece(0, 0);
		tall2 = new TallPiece(3, 0);
		tall3 = new TallPiece(0, 3);
		tall4 = new TallPiece(3, 3);
		small1 = new SmallPiece(1, 3);
		small2 = new SmallPiece(1, 4);
		small3 = new SmallPiece(2, 3);
		small4 = new SmallPiece(2, 4);

		tall1.setID(1);
		tall2.setID(2);
		tall3.setID(3);
		tall4.setID(4);

		small1.setID(1);
		small2.setID(2);
		small3.setID(3);
		small4.setID(4);
	}

	/**
	 * Find the solution to the problem and returns the solution in the form of a singly linked
	 * list.
	 * 
	 * @return returns a linked list of states to find the solution to the problem.
	 */
	public State solvePuzzle() {
		clearFields(); // TODO add this to github
		loadStartStateIntoQueue();
		iterationCount = 0;
		while (true) {
			iterationCount++;

			// check if a search found a win state
			if (winState != null) {
				break;
			}

			// check if queue has any states left to check
			if (stateQueue.size() <= 0) {
				// no solution found
				System.out.println("No Solution Found. Max iteration:" + iterationCount);
				break;
			}

			// load a state from the queue
			State currState = stateQueue.poll();
			updatePieceFields(currState);
			loadPiecesToBoolBoard(currState);

			// add valid state permutations, check for win state, check for state in map
			addValidStates(sun, currState);
			addValidStates(wide, currState);
			addValidStates(small1, currState);
			addValidStates(small2, currState);
			addValidStates(small3, currState);
			addValidStates(small4, currState);
			addValidStates(tall1, currState);
			addValidStates(tall2, currState);
			addValidStates(tall3, currState);
			addValidStates(tall4, currState);
		}

		// reverse win state linked list (currently in backwards order)
		reverseStates(winState, null);

		// dump the queue (for garbage collector)
		while (!stateQueue.isEmpty()) {
			stateQueue.poll();
		}

		return winState;
	}

	private void clearFields() {
		winState = null;
		while (stateQueue.size() != 0) {
			stateQueue.poll();
		}
		if (memo != null) {
			memo.removeAll(memo);
		}

	}

	/**
	 * This takes a linked list of states and reverses the direction of links.
	 * 
	 * This was a weird way to implement reversing the list, but I wanted to see if I could get it
	 * working. it works by using pointers to the current node, and the node before it. It saves the
	 * currents node's link, then changes the link to the node before the current node.
	 * 
	 * With the link between the current and its original link broken, this method is called
	 * recursively to set the next node to link back to the current node.
	 * 
	 * @param current
	 *            the current node being checked (will become "before" on next call)
	 * @param before
	 *            the node before the current node.
	 */
	private void reverseStates(State current, State before) {
		// reversing a linked list
		State next = current.linkedNode;
		current.linkedNode = before;

		// base case check
		if (next == null) {
			winState = current;
		} else {
			reverseStates(next, current);
		}
	}

	/**
	 * This function checks a given polymorphic piece in a current state for all possible moves it
	 * can make. Then, it check if those moves result in a valid new state based on the current
	 * state of the board.
	 * 
	 * @param piece
	 *            the piece to have all moves checked
	 * @param currState
	 *            the state used to determine if moves are valid.
	 */
	private void addValidStates(PlayPiece piece, State currState) {
		// if win state has been found, simply return control so loop can end.
		if (winState != null) {
			return;
		}

		// remove current points from board (coordinate of self piece should not count as collision)
		removePointsIntoBooleanBoard(piece.getCoordinatePoints(Direction.STAY));

		// for every direction (other than stay direction)
		for (int i = 0; i < 4; ++i) {
			ArrayList<Point> coordsAfterMove = piece.getCoordinatePoints(Direction.directionValues[i]);

			// check if the generated points are a valid state
			if (pointsAvailableAndValid(coordsAfterMove)) {

				// set up temporary state and see look up if it has already been visited
				State newState = tempState;
				tempState.copyDataFrom(currState);
				updateStateForPieceAndCoords(piece, coordsAfterMove, newState);

				// if state hasn't been visited, add it to the queue
				if (!memo.contains(newState.dataVector)) {
					memo.add(newState.dataVector);
					if (isWinState(newState)) {
						// set win state and break out of loop. No need to keep searching.
						System.out.println("Solution Found.");
						winState = new State(newState, currState);
						break;
					} else {
						stateQueue.add(new State(newState, currState));
					}
				}

			}

		}

		// add back piece's location to the boolean board (so other pieces have accurate board)
		loadPointsIntoBooleanBoard(piece.getCoordinatePoints(Direction.STAY));
	}

	/**
	 * Method that determines if the passed state is in the winning configuration.
	 * 
	 * @param state
	 *            the state to be checked
	 * @return if the state is in the winning position.
	 */
	private boolean isWinState(State state) {
		return state.getSunPoint().equals(winPoint);
	}

	/**
	 * This method takes a polymorphic pointer to a play piece and updates a state's internal data
	 * based on the result of moving that piece. Calling this method should update the given state's
	 * data to represent the result of moving a piece to a new location.
	 * 
	 * 
	 * @param piece
	 *            the piece that was or is to be moved.
	 * @param coordsAfterMove
	 *            the calculated coordinates that the piece occupies after the move
	 * @param state
	 *            the state where the data should be stored.
	 */
	private void updateStateForPieceAndCoords(PlayPiece piece, ArrayList<Point> coordsAfterMove, State state) {
		if (piece instanceof SunPiece) {
			state.setSunPoint(coordsAfterMove.get(0));
		} else if (piece instanceof WidePiece) {
			state.setWidePoint(coordsAfterMove.get(0));
		} else if (piece instanceof TallPiece) {
			switch (piece.getID()) {
			case 1:
				state.setTall1Point(coordsAfterMove.get(0));
				break;
			case 2:
				state.setTall2Point(coordsAfterMove.get(0));
				break;
			case 3:
				state.setTall3Point(coordsAfterMove.get(0));
				break;
			case 4:
				state.setTall4Point(coordsAfterMove.get(0));
				break;
			default:
				throw new RuntimeException("invalid id on a tall piece");
			}
		} else if (piece instanceof SmallPiece) {
			switch (piece.getID()) {
			case 1:
				state.setSmall1Point(coordsAfterMove.get(0));
				break;
			case 2:
				state.setSmall2Point(coordsAfterMove.get(0));
				break;
			case 3:
				state.setSmall3Point(coordsAfterMove.get(0));
				break;
			case 4:
				state.setSmall4Point(coordsAfterMove.get(0));
				break;
			default:
				throw new RuntimeException("invalid id on a small piece");
			}
		}
	}

	/**
	 * This method checks that an ArrayList of points are valid positions on the current boolean
	 * board.
	 * 
	 * First, it checks that the points are within the array's range.
	 * 
	 * Second, it checks whether those positions are already occupied by some piece.
	 * 
	 * @param coordsAfterMove
	 *            the ArrayList of points that represent every position that will be occupied on the
	 *            board.
	 * @return returns whether every point in the collection are available(i.e. valid) and within
	 *         the boolean array range.
	 */
	private boolean pointsAvailableAndValid(ArrayList<Point> coordsAfterMove) {
		for (Point pnt : coordsAfterMove) {
			// check that point is in range, if any point is out of range return false.
			if (pnt.x < 0 || pnt.x >= board[0].length || pnt.y < 0 || pnt.y >= board.length) {
				return false;
			}

			// check if space is available (true value represents something already at position)
			if (board[pnt.y][pnt.x]) {
				return false;
			}
		}

		// if this point is reached, all points were valid locations on the board.
		return true;
	}

	/**
	 * Updates the fields of this object based on the positions saved in the parameter currState.
	 * 
	 * This function is used to load the data of a state into the fields of this object.
	 * 
	 * @param currState
	 *            the state to load data from.
	 */
	private void updatePieceFields(State currState) {
		sun.setTopLeftPointToValues(currState.getSunPoint());
		wide.setTopLeftPointToValues(currState.getWidePoint());
		tall1.setTopLeftPointToValues(currState.getTall1());
		tall2.setTopLeftPointToValues(currState.getTall2());
		tall3.setTopLeftPointToValues(currState.getTall3());
		tall4.setTopLeftPointToValues(currState.getTall4());
		small1.setTopLeftPointToValues(currState.getSmall1());
		small2.setTopLeftPointToValues(currState.getSmall2());
		small3.setTopLeftPointToValues(currState.getSmall3());
		small4.setTopLeftPointToValues(currState.getSmall4());
	}

	/**
	 * This method updates field that is a boolean array representing positions of pieces.
	 * 
	 * @param currState
	 *            represents current state. It is the state to load data from
	 */
	private void loadPiecesToBoolBoard(State currState) {
		resetBoard();
		loadPointsIntoBooleanBoard(sun.getCoordinatePoints(Direction.STAY));
		loadPointsIntoBooleanBoard(wide.getCoordinatePoints(Direction.STAY));
		loadPointsIntoBooleanBoard(tall1.getCoordinatePoints(Direction.STAY));
		loadPointsIntoBooleanBoard(tall2.getCoordinatePoints(Direction.STAY));
		loadPointsIntoBooleanBoard(tall3.getCoordinatePoints(Direction.STAY));
		loadPointsIntoBooleanBoard(tall4.getCoordinatePoints(Direction.STAY));
		loadPointsIntoBooleanBoard(small1.getCoordinatePoints(Direction.STAY));
		loadPointsIntoBooleanBoard(small2.getCoordinatePoints(Direction.STAY));
		loadPointsIntoBooleanBoard(small3.getCoordinatePoints(Direction.STAY));
		loadPointsIntoBooleanBoard(small4.getCoordinatePoints(Direction.STAY));
	}

	/**
	 * Updates the boolean board using a container of points. Points locations add "true" values to
	 * the 2d boolean array that represents the game board at the specified point positions.
	 * 
	 * Note: y represents rows, x represents columns.
	 * 
	 * @param coordinatePoints
	 *            container of points to make false on the game board.
	 */
	private void loadPointsIntoBooleanBoard(ArrayList<Point> coordinatePoints) {
		for (Point pnt : coordinatePoints) {
			int x = pnt.x;
			int y = pnt.y;

			// [row = y][column = x]
			board[y][x] = true;
		}

	}

	/**
	 * Uses a container of points to remove "true" values from the 2d boolean array (ie make false)
	 * that represents the game board.
	 * 
	 * @param coordinatePoints
	 *            container of points to make false on the game board.
	 */
	private void removePointsIntoBooleanBoard(ArrayList<Point> coordinatePoints) {
		for (Point pnt : coordinatePoints) {
			int x = pnt.x;
			int y = pnt.y;
			board[y][x] = false;
		}
	}

	/**
	 * Resets the boolean array board to all false values.
	 */
	private void resetBoard() {
		for (int i = 0; i < board.length; ++i) {
			for (int j = 0; j < board[i].length; ++j) {
				board[i][j] = false;
			}
		}
	}

	/**
	 * Loads the first state into the state queue for breadth first search.
	 * 
	 * The start state is determined by the positions of the piece fields. To change the start
	 * state, update the fields of this object to represent the state the search should start on.
	 * Doing so will allow finding a solution from any given configuration of the board.
	 */
	private void loadStartStateIntoQueue() {
		State firstState = new State(sun.topLeftPoint, wide.topLeftPoint, tall1.topLeftPoint, tall2.topLeftPoint, tall3.topLeftPoint, tall4.topLeftPoint, small1.topLeftPoint, small2.topLeftPoint, small3.topLeftPoint, small4.topLeftPoint);
		tempState = new State(firstState, null);

		memo = new TreeSet<Long>();
		stateQueue.add(firstState);
	}

	/**
	 * A simple method that prints a state. This is used in printing the steps to get to a solution.
	 * the sun piece is represented as 'S'. the tall pieces are represented as two 'T's on top of
	 * each other. the wide piece is represented as two 'W's beside each other. the small square
	 * pieces are represented by a single 's'.
	 * 
	 * @param state
	 *            the state to print.
	 */
	public void printState(State state) {
		// an array that will contain all the characters to print.
		char printBoard[][] = new char[5][4];

		// set the points to a background value (value is ' ' for now)
		for (int i = 0; i < printBoard.length; ++i) {
			for (int j = 0; j < printBoard[i].length; ++j) {
				printBoard[i][j] = ' ';
			}
		}

		// update pieces to draw on board
		updatePieceFields(state);
		addCharsToArray('S', printBoard, sun.getCoordinatePoints(Direction.STAY));
		addCharsToArray('W', printBoard, wide.getCoordinatePoints(Direction.STAY));
		addCharsToArray('T', printBoard, tall1.getCoordinatePoints(Direction.STAY));
		addCharsToArray('T', printBoard, tall2.getCoordinatePoints(Direction.STAY));
		addCharsToArray('T', printBoard, tall3.getCoordinatePoints(Direction.STAY));
		addCharsToArray('T', printBoard, tall4.getCoordinatePoints(Direction.STAY));
		addCharsToArray('s', printBoard, small1.getCoordinatePoints(Direction.STAY));
		addCharsToArray('s', printBoard, small2.getCoordinatePoints(Direction.STAY));
		addCharsToArray('s', printBoard, small3.getCoordinatePoints(Direction.STAY));
		addCharsToArray('s', printBoard, small4.getCoordinatePoints(Direction.STAY));

		// print out the now configured 2d character array
		for (int i = 0; i < printBoard.length; ++i) {
			for (int j = 0; j < printBoard[i].length; ++j) {
				System.out.print(printBoard[i][j] + " ");
			}
			System.out.println();
		}

	}

	/**
	 * Updates a 2d character array with letters representing game pieces.
	 * 
	 * @invariant assumes that points are correctly generated for board size and will not produce
	 *            array out of bounds
	 * @param charToUse
	 *            the character to place on the board.
	 * @param board
	 *            the instance of the board to update
	 * @param locations
	 *            the locations to change on the board
	 */
	public void addCharsToArray(char charToUse, char[][] board, ArrayList<Point> locations) {
		for (Point pnt : locations) {
			board[pnt.y][pnt.x] = charToUse;
		}

	}

	/**
	 * A main to test finding the solution from the start state of the game.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		// create a game object and solve the puzzle from the start state.
		SettingSun game = new SettingSun();
		State sequence = game.solvePuzzle();

		Scanner kb = new Scanner(System.in);
		System.out.println("Press enter to see the next state.\n");

		// print off each state
		State iter = sequence;
		while (iter != null) {
			game.printState(iter);
			iter = iter.linkedNode;
			kb.nextLine(); // pause until user presses enter

		}
		kb.close();
	}

	/**
	 * Added of libgdx to calculate positions
	 * 
	 * @return state that is currently represented by the game state.
	 */
	public State getCurrentState() {
		return new State(sun.topLeftPoint, wide.topLeftPoint, tall1.topLeftPoint, tall2.topLeftPoint, tall3.topLeftPoint, tall4.topLeftPoint, small1.topLeftPoint, small2.topLeftPoint, small3.topLeftPoint, small4.topLeftPoint);

	}

	// methods added for libgdx TODO
	public boolean isPointValid(Point pnt, PieceType type) {

		return false;
	}

	public void setSunPoint(Point pointToSet) {
		sun.setTopLeftPointToValues(pointToSet);
	}

	public void setWidePoint(Point pointToSet) {
		wide.setTopLeftPointToValues(pointToSet);
	}

	public void setSmall1Point(Point pointToSet) {
		small1.setTopLeftPointToValues(pointToSet);
	}

	public void setSmall2Point(Point pointToSet) {
		small2.setTopLeftPointToValues(pointToSet);
	}

	public void setSmall3Point(Point pointToSet) {
		small3.setTopLeftPointToValues(pointToSet);
	}

	public void setSmall4Point(Point pointToSet) {
		small4.setTopLeftPointToValues(pointToSet);
	}

	public void setTall1Point(Point pointToSet) {
		tall1.setTopLeftPointToValues(pointToSet);
	}

	public void setTall2Point(Point pointToSet) {
		tall2.setTopLeftPointToValues(pointToSet);
	}

	public void setTall3Point(Point pointToSet) {
		tall3.setTopLeftPointToValues(pointToSet);
	}

	public void setTall4Point(Point pointToSet) {
		tall4.setTopLeftPointToValues(pointToSet);
	}

	private ArrayList<PlayPiece> allPieces = new ArrayList<PlayPiece>();
	private void addPiecesIfNeeded() {
		if (allPieces.size() == 0) {
			allPieces.add(sun);
			allPieces.add(wide);
			allPieces.add(small1);
			allPieces.add(small2);
			allPieces.add(small3);
			allPieces.add(small4);
			allPieces.add(tall1);
			allPieces.add(tall2);
			allPieces.add(tall3);
			allPieces.add(tall4);
		}
	}

	/**
	 * Method that checks if a 1 step point transition is valid
	 * 
	 * @untested
	 * 
	 * @param originalPoint
	 *            the starting position of the point
	 * @param newPoint
	 *            the point to transition to (point being tested)
	 * @return whether the point produces a valid transition
	 */
	public boolean isValidTransition(Point originalPoint, Point newPoint) {
		addPiecesIfNeeded(); // decided not to change constructor simply for this method, 
		boolean result = false;

		// find the piece
		PlayPiece targetPiece = null;
		for (PlayPiece piece : allPieces) {
			if (piece.topLeftPoint.equals(originalPoint)) {
				targetPiece = piece;
				break;
			}
		}

		if (targetPiece == null) {
			// could not find the piece at the original point
			return false;
		}
		
		//update the game board for current pieces
		loadPiecesToBoolBoard(null);//TODO MAY THROW NULL POINTER

		// remove the piece from boolean board
		removePointsIntoBooleanBoard(targetPiece.getCoordinatePoints(Direction.STAY));

		// assumes no diagonal transitions
		if (originalPoint.x != newPoint.x && originalPoint.y == newPoint.y) {
			// assumes transition is only of 1 space
			if (originalPoint.x < newPoint.x) {
				// test if move to right has collisions
				result = pointsAvailableAndValid(targetPiece.getCoordinatePoints(Direction.RIGHT));
			} else {
				result = pointsAvailableAndValid(targetPiece.getCoordinatePoints(Direction.LEFT));
			}
		} else if (originalPoint.y != newPoint.y && originalPoint.x == newPoint.x) {
			if (originalPoint.y < newPoint.y) {
				result = pointsAvailableAndValid(targetPiece.getCoordinatePoints(Direction.DOWN));
			} else {
				result = pointsAvailableAndValid(targetPiece.getCoordinatePoints(Direction.UP));
			}
		}

		// restore boolean board
		loadPointsIntoBooleanBoard(targetPiece.getCoordinatePoints(Direction.STAY));
		return result;
	}
}
