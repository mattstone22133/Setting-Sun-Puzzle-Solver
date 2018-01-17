package enigma.engine;


import java.util.ArrayList;

/**
 * Class that represents a configuration of the game board.
 * 
 * Piece locations are stored based on their top left coordinate. All pieces are stored in a single
 * long value, which is called the dataVector. this is done by bit wise operations. Pieces of the
 * same type are stored by a type of sorting so that redundant configurations will produce the same
 * data vector (see PointCompare class documentation for more information). The state acts as a
 * singly linked list to other states. A valid solution of the game is expected to be a list of
 * these states.
 * 
 * 
 * @author Matt Stone
 * @version 1/1/17
 *
 */
public class State {
	// Static fields used in extracting values from the bit vector
	public static int sunIndex = 9;
	public static int wideIndex = 8;
	public static int tall1Index = 7;
	public static int tall2Index = 6;
	public static int tall3Index = 5;
	public static int tall4Index = 4;
	public static int small1Index = 3;
	public static int small2Index = 2;
	public static int small3Index = 1;
	public static int small4Index = 0;

	// static helper points to prevent object creation for every generation of state
	private static ArrayList<Point> sortHelperPoints;
	private static PointCompare comparer = new PointCompare();

	// The data vector
	long dataVector = 0;

	// Link to next node
	State linkedNode;

	/**
	 * Constructor for generation of state based on point locations. The points represent the
	 * top-left coordinate.
	 * 
	 * @param sunPnt
	 * @param widePnt
	 * @param tall1
	 * @param tall2
	 * @param tall3
	 * @param tall4
	 * @param smallPoint1
	 * @param smallPoint2
	 * @param smallPoint3
	 * @param smallPoint4
	 */
	public State(Point sunPnt, Point widePnt, Point tall1, Point tall2, Point tall3, Point tall4, Point smallPoint1, Point smallPoint2, Point smallPoint3, Point smallPoint4) {
		// instance
		storePointInDataVector(sunPnt); // 9
		storePointInDataVector(widePnt); // 8
		storePointInDataVector(tall1); // 7
		storePointInDataVector(tall2); // 6
		storePointInDataVector(tall3); // 5
		storePointInDataVector(tall4); // 4
		storePointInDataVector(smallPoint1); // 3
		storePointInDataVector(smallPoint2); // 2
		storePointInDataVector(smallPoint3); // 1
		storePointInDataVector(smallPoint4); // 0

	}

	/**
	 * Copy Constructor but with specified state to be linked.
	 * 
	 * @notice: does not do a shallow copy on linked state, instead the state to be linked is
	 *          specified
	 * 
	 * @param other
	 *            the values to be copied.
	 * @param stateToLink
	 *            the state that should be linked (this value is not copied)
	 */
	public State(State other, State stateToLink) {
		linkedNode = stateToLink;
		this.dataVector = other.dataVector;
	}

	/**
	 * Pure copy constructor with shallow copy on reference to the last node.
	 * 
	 * @param other
	 */
	public State(State other) {
		this.linkedNode = other.linkedNode;
		this.dataVector = other.dataVector;
	}

	/**
	 * A function that acts like an assignment operator. I created it to prevent creation of a new
	 * object by simply updating the the fields of the current state.
	 * 
	 * @param other
	 *            the point to get values from.
	 */
	public void copyDataFrom(State other) {
		this.linkedNode = other.linkedNode;
		this.dataVector = other.dataVector;
	}

	/**
	 * Function that uses the data vector to look up the location of a piece at a specified index.
	 * It then either creates a point or uses a point provided to store the data and then returns
	 * the updated/new point.
	 * 
	 * @param index
	 *            the index in the data vector from which to get data.
	 * @param optionalStoragePoint
	 *            an optional point to be updated.
	 * @return returns the updated optional point or a new point with the data extracted from the
	 *         bit vector.
	 */
	private Point getPointAtIndex(int index, Point optionalStoragePoint) {
		long dataVectorCopy = dataVector;
		dataVectorCopy >>>= 6 * index; // unsigned shift

		int y = (int) (dataVectorCopy & 7); // 7 is binary for 111
		int x = (int) (dataVectorCopy >>> 3) & 7; // 7 is used to only collect the 3 bits at lower
													// order

		if (optionalStoragePoint == null) {
			return new Point(x, y);
		} else {
			optionalStoragePoint.x = x;
			optionalStoragePoint.y = y;
			return optionalStoragePoint;
		}
	}

	/**
	 * Gets a point representing the sun piece.
	 * 
	 * @return the point representing the sun.
	 */
	public Point getSunPoint() {
		return getPointAtIndex(State.sunIndex, null);
	}

	/**
	 * Gets a point representing the wide piece.
	 * 
	 * @return the point representing wide piece.
	 */
	public Point getWidePoint() {
		return getPointAtIndex(State.wideIndex, null);
	}

	/**
	 * Gets a point representing the first tall piece.
	 * 
	 * @return the point representing the first tall piece.
	 */
	public Point getTall1() {
		return getPointAtIndex(State.tall1Index, null);

	}

	/**
	 * Gets a point representing the second tall piece.
	 * 
	 * @return the point representing the second tall piece
	 */
	public Point getTall2() {
		return getPointAtIndex(State.tall2Index, null);
	}

	/**
	 * Gets a point representing the third tall piece.
	 * 
	 * @return the point representing the third tall piece
	 */
	public Point getTall3() {
		return getPointAtIndex(State.tall3Index, null);
	}

	/**
	 * Gets a point representing the fourth tall piece.
	 * 
	 * @return the point representing the fourth tall piece.
	 */
	public Point getTall4() {
		return getPointAtIndex(State.tall4Index, null);
	}

	/**
	 * Gets a point representing the first small piece.
	 * 
	 * @return the point representing the first small piece
	 */
	public Point getSmall1() {
		return getPointAtIndex(State.small1Index, null);
	}

	/**
	 * Gets a point representing the second small piece.
	 * 
	 * @return the point representing the second small piece
	 */
	public Point getSmall2() {
		return getPointAtIndex(State.small2Index, null);
	}

	/**
	 * Gets a point representing the third small piece.
	 * 
	 * @return the point representing the third small piece
	 */
	public Point getSmall3() {
		return getPointAtIndex(State.small3Index, null);
	}

	/**
	 * Gets a point representing the fourth small piece.
	 * 
	 * @return the point representing the fourth small piece
	 */
	public Point getSmall4() {
		return getPointAtIndex(State.small4Index, null);
	}

	/**
	 * A method that simply pushes a point's data into the data vector. It does not specify an
	 * index, rather it pushes all data currently in the data vector to the left by 6 bits.
	 * 
	 * @param toStore
	 *            the point containing the data to store.
	 */
	private void storePointInDataVector(Point toStore) {
		dataVector <<= 6; // shift 6 bits over to make room to store data

		// x and y should take up no more than 3 bits each (6 bits total)
		long toMerge = 0; // value hold data before it is put in vector.
		toMerge = toStore.x;
		toMerge <<= 3; // shift over by 3 to load y data.
		toMerge |= toStore.y; // bitwise 'or' in the data for y.
		dataVector |= toMerge; // bitwise 'or' in the data to the data vector.
	}

	/**
	 * Stores a point's data at a specified index.
	 * 
	 * @param index
	 *            the index where the data should be stored (there are static variables defining
	 *            this indices).
	 * @param toStore
	 *            the point containing the data to store.
	 */
	private void storePointInDataVectorAt(int index, Point toStore) {
		// clear location in data vector
		long mask = 63; // 0b0111111 (6 ones)
		mask <<= 6 * index; // position mask
		mask = ~mask; // invert mask so that 0s are position to clear index
		dataVector &= mask; // clear out position

		// convert point into bits
		long toMerge = 0;
		toMerge = toStore.x; // note x and y are at most 3 bits of data
		toMerge <<= 3;
		toMerge |= toStore.y;
		toMerge <<= 6 * index; // position value to be stored
		dataVector |= toMerge; // load value into data vector
	}

	/**
	 * Updates the data vector with data representing the new location of the Sun point.
	 * 
	 * @param newPoint
	 *            the point data to use for the update of the data vector.
	 */
	public void setSunPoint(Point newPoint) {
		storePointInDataVectorAt(State.sunIndex, newPoint);
	}

	/**
	 * Updates the data vector with data representing the new location of the Wide Point.
	 * 
	 * @param newPoint
	 *            the point data to use for the update of the data vector.
	 */
	public void setWidePoint(Point newPoint) {
		storePointInDataVectorAt(State.wideIndex, newPoint);
	}

	/**
	 * Updates the data vector with data representing the new location of the first tall point.
	 * 
	 * @param newPoint
	 *            the point data to use for the update of the data vector.
	 */
	public void setTall1Point(Point newPoint) {
		storePointInDataVectorAt(State.tall1Index, newPoint);
		sortIndices(State.tall1Index, State.tall2Index, State.tall3Index, State.tall4Index);
	}

	/**
	 * Updates the data vector with data representing the new location of the second tall point.
	 * 
	 * @param newPoint
	 *            the point data to use for the update of the data vector.
	 */
	public void setTall2Point(Point newPoint) {
		storePointInDataVectorAt(State.tall2Index, newPoint);
		sortIndices(State.tall1Index, State.tall2Index, State.tall3Index, State.tall4Index);

	}

	/**
	 * Updates the data vector with data representing the new location of the third tall point.
	 * 
	 * @param newPoint
	 *            the point data to use for the update of the data vector.
	 */
	public void setTall3Point(Point newPoint) {
		storePointInDataVectorAt(State.tall3Index, newPoint);
		sortIndices(State.tall1Index, State.tall2Index, State.tall3Index, State.tall4Index);

	}

	/**
	 * Updates the data vector with data representing the new location of the fourth tall point
	 * 
	 * @param newPoint
	 *            the point data to use for the update of the data vector.
	 */
	public void setTall4Point(Point newPoint) {
		storePointInDataVectorAt(State.tall4Index, newPoint);
		sortIndices(State.tall1Index, State.tall2Index, State.tall3Index, State.tall4Index);

	}

	/**
	 * Updates the data vector with data representing the new location of the first small point
	 * 
	 * @param newPoint
	 *            the point data to use for the update of the data vector.
	 */
	public void setSmall1Point(Point newPoint) {
		storePointInDataVectorAt(State.small1Index, newPoint);
		sortIndices(State.small1Index, State.small2Index, State.small3Index, State.small4Index);

	}

	/**
	 * Updates the data vector with data representing the new location of the second small point.
	 * 
	 * @param newPoint
	 *            the point data to use for the update of the data vector.
	 */
	public void setSmall2Point(Point newPoint) {
		storePointInDataVectorAt(State.small2Index, newPoint);
		sortIndices(State.small1Index, State.small2Index, State.small3Index, State.small4Index);

	}

	/**
	 * Updates the data vector with data representing the new location of the third small point
	 * 
	 * @param newPoint
	 *            the point data to use for the update of the data vector.
	 */
	public void setSmall3Point(Point newPoint) {
		storePointInDataVectorAt(State.small3Index, newPoint);
		sortIndices(State.small1Index, State.small2Index, State.small3Index, State.small4Index);

	}

	/**
	 * Updates the data vector with data representing the new location of the fourth small point.
	 * 
	 * @param newPoint
	 *            the point data to use for the update of the data vector.
	 */
	public void setSmall4Point(Point newPoint) {
		storePointInDataVectorAt(State.small4Index, newPoint);
		sortIndices(State.small1Index, State.small2Index, State.small3Index, State.small4Index);

	}

	/**
	 * A method that takes in 4 indices for the data vector and sorts them as defined in the
	 * PointCompare class.
	 * 
	 * The sorting works first by x value, then by y value if the x values are the same.
	 * 
	 * This is used to ensure that all permutations of the same state are stored in the same vector.
	 * 
	 * @param first
	 *            index
	 * @param second
	 *            index
	 * @param third
	 *            index
	 * @param fourth
	 *            index
	 */
	private void sortIndices(int first, int second, int third, int fourth) {
		// single time static initialization
		if (State.sortHelperPoints == null) {
			sortHelperPoints = new ArrayList<Point>();
			for (int i = 0; i < 4; ++i) {
				sortHelperPoints.add(new Point(0, 0));
			}
		}

		// uses global static point variables to prevent repeated object creation
		getPointAtIndex(first, State.sortHelperPoints.get(0));
		getPointAtIndex(second, State.sortHelperPoints.get(1));
		getPointAtIndex(third, State.sortHelperPoints.get(2));
		getPointAtIndex(fourth, State.sortHelperPoints.get(3));

		// sort the points
		sortHelperPoints.sort(comparer);

		// right the data back to the indices in sorted order
		storePointInDataVectorAt(first, sortHelperPoints.get(0));
		storePointInDataVectorAt(second, sortHelperPoints.get(1));
		storePointInDataVectorAt(third, sortHelperPoints.get(2));
		storePointInDataVectorAt(fourth, sortHelperPoints.get(3));
	}

}
