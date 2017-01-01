import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * @author Matt
 *
 */
public class SettingSun {

	private final int row = 5;
	private final int col = 4;
	private boolean[][] board = new boolean[row][col];
	private Queue<State> stateQueue = new LinkedList<State>();
	// private BSTNode memo;
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
	int iteration;
	private State tempState;

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

	public State solvePuzzle() {
		loadStartStateIntoQueue();
		iteration = 0;
		while (true) {
			iteration++;

			// check if a search found a win state
			if (winState != null) {
				break;
			}

			// check if queue has any states left to check
			if (stateQueue.size() <= 0) {
				// no solution found
				System.out.println("No Solution Found. Max iteration:" + iteration);
				break;
			}

			State currState = stateQueue.poll();
			updatePieceStates(currState);
			loadPiecesToBoard(currState);

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

		// reverse win state
		reverseStates(winState, null);
		
		//TODO DUMP QUEUE FOR GARBAGE COLLECTOR?

		return winState;
	}

	private void reverseStates(State current, State before) {
		// //reversing a linked list
		State next = current.linkedNode;
		current.linkedNode = before;

		if (next == null) {
			winState = current;
		} else {
			reverseStates(next, current);
		}
	}

	private void addValidStates(PlayPiece piece, State currState) {
		// if win state has been found, simply return control so loop can end.
		if (winState != null) {
			return;
		}

		// remove current points from board (because coordinate so same piece should not count as collision)
		removePointsIntoBooleanBoard(piece.getCoordinatePoints(Direction.STAY));

		// for every direction (other than stay direction)
		for (int i = 0; i < 4; ++i) {
			ArrayList<Point> coordsAfterMove = piece.getCoordinatePoints(Direction.directionValues[i]);

			// check if the generated points are a valid state
			if (pointsAvailableAndValid(coordsAfterMove)) {
				// generate a state and see look up if it has already been visited
//				State newState = new State(currState, currState); // TODO: change this to just a data vector
				State newState = tempState;
				tempState.assignmentOperator(currState);
				updateStateForPieceAndCoords(piece, coordsAfterMove, newState);

				// if state hasn't been visited, add it to the queue
				// if (memo.find(newState.dataVector) == null) { //BSTnode version
				// memo.insert(newState.dataVector); //BSTnode version
				if (!memo.contains(newState.dataVector)) {
					memo.add(newState.dataVector);
					if (isWinState(newState)) {
						// set win state and break out of loop. No need to keep searching.
						System.out.println("Solution Found");
						winState = newState;
						break;
					} else {
						stateQueue.add(new State(newState, currState));
					}
				}

			}

		}

		// add back the pieces location to the boolean coordinate (so that other pieces will have accurate board)
		loadPointsIntoBooleanBoard(piece.getCoordinatePoints(Direction.STAY));
	}

	private boolean isWinState(State newState) {
		return newState.getSunPoint().equals(winPoint);
	}

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

	private void updatePieceStates(State currState) {
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

	private void loadPiecesToBoard(State currState) {
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
	 * Uses a container of points to add "true" values from the 2d boolean array that represents the game board.
	 * 
	 * @param coordinatePoints
	 *            container of points to make false on the game board.
	 */
	private void loadPointsIntoBooleanBoard(ArrayList<Point> coordinatePoints) {
		for (Point pnt : coordinatePoints) {
			int x = pnt.x;
			int y = pnt.y;
			board[y][x] = true;
		}

	}

	/**
	 * Uses a container of points to remove "true" values from the 2d boolean array (ie make false) that represents the game board.
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

	private void resetBoard() {
		for (int i = 0; i < board.length; ++i) {
			for (int j = 0; j < board[i].length; ++j) {
				board[i][j] = false;
			}
		}
	}

	private void loadStartStateIntoQueue() {
		State firstState = new State(sun.topLeftPoint, wide.topLeftPoint, tall1.topLeftPoint, tall2.topLeftPoint, tall3.topLeftPoint, tall4.topLeftPoint, small1.topLeftPoint, small2.topLeftPoint, small3.topLeftPoint, small4.topLeftPoint);
		tempState = new State(firstState, null);
		
		memo = new TreeSet<Long>();
		stateQueue.add(firstState);
	}

	public void printState(State state) {
		char printBoard[][] = new char[5][4];

		// set the points to a background value
		for (int i = 0; i < printBoard.length; ++i) {
			for (int j = 0; j < printBoard[i].length; ++j) {
				printBoard[i][j] = ' ';
			}
		}

		// update pieces to draw on board
		updatePieceStates(state);
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

		for(int i = 0; i < printBoard.length; ++i){
			for(int j = 0; j < printBoard[i].length; ++j){
				System.out.print(printBoard[i][j] + " ");
			}
			System.out.println();
		}

	}

	/**
	 * Updates a 2d character array with letters representing game pieces.
	 * 
	 * @invariant assumes that points are correctly generated for board size and will not produce arry out of bounds
	 * 
	 * 
	 * @param charToUse
	 *            the character to place on the board.
	 * @param board
	 *            the instance of the board to update
	 * @param locations
	 *            the locations to change on the board
	 */
	public void addCharsToArray(char charToUse, char[][] board, ArrayList<Point> locations) {
		for (Point pnt : locations){
			board[pnt.y][pnt.x] = charToUse;
		}

	}

	public static void main(String[] args) {
		SettingSun game = new SettingSun();
		State sequence = game.solvePuzzle();

		Scanner kb = new Scanner(System.in);
		
		// print off each state
		State iter = sequence;
		while (iter != null) {
			game.printState(iter);
			iter = iter.linkedNode;
			kb.nextLine();	//pause until user presses enter
			
		}
		kb.close();
	}

}
