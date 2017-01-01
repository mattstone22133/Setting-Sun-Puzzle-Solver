import java.util.ArrayList;

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

	// static helper points to prevent object creation for every generation state
	private static ArrayList<Point> sortHelperPoints;
	private static PointCompare comparer = new PointCompare();

	// The data vector
	long dataVector = 0;

	// Link to next node
	State linkedNode;

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

	public State(State other, State stateToLink) {
		linkedNode = stateToLink;
		this.dataVector = other.dataVector;
	}
	
	public State(State other){
		this.linkedNode = other.linkedNode;
		this.dataVector = other.dataVector;		
	}
	
	public void assignmentOperator(State other){
		this.linkedNode = other.linkedNode;
		this.dataVector = other.dataVector;
	}

	private Point getPointAtIndex(int index, Point optionalStoragePoint) {
		long dataVectorCopy = dataVector;
		dataVectorCopy >>>= 6 * index; // unsigned shift

		int y = (int) (dataVectorCopy & 7); // 7 is binary for 111
		int x = (int) (dataVectorCopy >>> 3) & 7; // 7 is used to only collect the 3 bits at lower order

		if (optionalStoragePoint == null) {
			return new Point(x, y);
		} else {
			optionalStoragePoint.x = x;
			optionalStoragePoint.y = y;
			return optionalStoragePoint;
		}
	}

	public Point getSunPoint() {
		return getPointAtIndex(State.sunIndex, null);
	}

	public Point getWidePoint() {
		return getPointAtIndex(State.wideIndex, null);
	}

	public Point getTall1() {
		return getPointAtIndex(State.tall1Index, null);

	}

	public Point getTall2() {
		return getPointAtIndex(State.tall2Index, null);
	}

	public Point getTall3() {
		return getPointAtIndex(State.tall3Index, null);
	}

	public Point getTall4() {
		return getPointAtIndex(State.tall4Index, null);
	}

	public Point getSmall1() {
		return getPointAtIndex(State.small1Index, null);
	}

	public Point getSmall2() {
		return getPointAtIndex(State.small2Index, null);
	}

	public Point getSmall3() {
		return getPointAtIndex(State.small3Index, null);
	}

	public Point getSmall4() {
		return getPointAtIndex(State.small4Index, null);
	}

	private void storePointInDataVector(Point toStore) {
		dataVector <<= 6; // shift 6 bits over to make room to store data

		// x and y should take up no more than 3 bits each (6 bits total)
		long toMerge = 0;
		toMerge = toStore.x;
		toMerge <<= 3;
		toMerge |= toStore.y;
		dataVector |= toMerge;
	}

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

	public void setSunPoint(Point newPoint) {
		storePointInDataVectorAt(State.sunIndex, newPoint);
	}

	public void setWidePoint(Point newPoint) {
		storePointInDataVectorAt(State.wideIndex, newPoint);
	}

	public void setTall1Point(Point newPoint) {
		storePointInDataVectorAt(State.tall1Index, newPoint);
		sortIndices(State.tall1Index, State.tall2Index, State.tall3Index, State.tall4Index);
	}

	public void setTall2Point(Point newPoint) {
		storePointInDataVectorAt(State.tall2Index, newPoint);	
		sortIndices(State.tall1Index, State.tall2Index, State.tall3Index, State.tall4Index);

	}

	public void setTall3Point(Point newPoint) {
		storePointInDataVectorAt(State.tall3Index, newPoint);
		sortIndices(State.tall1Index, State.tall2Index, State.tall3Index, State.tall4Index);

	}

	public void setTall4Point(Point newPoint) {
		storePointInDataVectorAt(State.tall4Index, newPoint);
		sortIndices(State.tall1Index, State.tall2Index, State.tall3Index, State.tall4Index);

	}

	public void setSmall1Point(Point newPoint) {
		storePointInDataVectorAt(State.small1Index, newPoint);
		sortIndices(State.small1Index, State.small2Index, State.small3Index, State.small4Index);

	}

	public void setSmall2Point(Point newPoint) {
		storePointInDataVectorAt(State.small2Index, newPoint);
		sortIndices(State.small1Index, State.small2Index, State.small3Index, State.small4Index);

	}

	public void setSmall3Point(Point newPoint) {
		storePointInDataVectorAt(State.small3Index, newPoint);
		sortIndices(State.small1Index, State.small2Index, State.small3Index, State.small4Index);

	}

	public void setSmall4Point(Point newPoint) {
		storePointInDataVectorAt(State.small4Index, newPoint);
		sortIndices(State.small1Index, State.small2Index, State.small3Index, State.small4Index);

	}

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
