import java.util.ArrayList;

/**
 * This class provides an abstract super class with the base functionality of the game's play
 * pieces.
 * 
 * Provided Functionality: Play Pieces can be moved. This class calculates how a collection of
 * coordinates would change for any given move direction(enumeration) argument.
 * 
 * Required subclass functionality: Subclasses must define how many coordinates their piece occupies
 * and subclasses must create a method to populate an array list with points representing these
 * locations (ie buildPoints() method).
 * 
 * How this class is used in solving puzzle: It provides a superclass pointer variable. A
 * generic/polymorphic function can just operate on the play piece without ever knowing the
 * underlying subclass of the play piece. Most functions only require a list of points used in
 * determining if a move is valid.
 * 
 * @author Matt Stone
 * @version 1/1/2017
 *
 */
public abstract class PlayPiece {
	// topLeftPoint represents the base coordinate of the piece.
	protected Point topLeftPoint;

	// ID is used when there are multiple pieces of the same type. e.g. there are 4 tall pieces.
	private int ID;

	// Collection of points that represent the piece. References change with call to getCoordinates
	public ArrayList<Point> representsPoints;

	/**
	 * Basic constructor that sets the positions defining coordinate. It does not update all points
	 * that the piece may occupy.
	 * 
	 * @param x
	 *            column
	 * @param y
	 *            row
	 */
	public PlayPiece(int x, int y) {
		topLeftPoint = new Point(x, y);
		setID(1);
	}

	/**
	 * Abstract method that sets the "representsPoints" field. These objects are to be recycled to
	 * prevent creating a large number of objects.
	 * 
	 * @return returns the points the representing the space occupied by the current piece.
	 */
	abstract protected ArrayList<Point> buildPoints();

	/**
	 * Returns a "recycled" set of points that would occur for move direction provided.
	 * 
	 * This method recycles internal points. Therefore, the returned points from the first call are
	 * not valid after the second call. Why is it done this way if it is generally bad OOP practice?
	 * It is done for the specific reason of keeping object creation down during breadth first
	 * search. This should improve performance and theoretically reduce the chance for using all
	 * available heap space.
	 * 
	 * @param direction
	 *            the direction for that the piece will be moved
	 * @return an ArrayList representing the board coordinates after the applied move direction
	 * 
	 * @invariant assumes that buildPoints has been implemented to update the represented points
	 *            based on the current topLeftPoint
	 * 
	 * @warning subsequent calls invalidate references obtained in previous calls (see above). It is
	 *          possible to introduce a null pointer exception if the returned container is
	 *          modified. This method is only expected to be used internally for the project.
	 */
	public ArrayList<Point> getCoordinatePoints(Direction direction) {
		ArrayList<Point> buildPoints = buildPoints();
		switch (direction) {
		case UP:
			// To represent moving up, decrease the y(row) value for all points.
			for (Point pnt : buildPoints) {
				pnt.y -= 1;
			}
			break;
		case DOWN:
			// To represent going down, increase the y(row) value for all points.
			for (Point pnt : buildPoints) {
				pnt.y += 1;
			}
			break;
		case LEFT:
			// To represent going left, decrease the x(column) value for all points.
			for (Point pnt : buildPoints) {
				pnt.x -= 1;
			}
			break;
		case RIGHT:
			// To represent going right, increase the x(column) value for all points.
			for (Point pnt : buildPoints) {
				pnt.x += 1;
			}
			break;
		case STAY:
			// If direction is stay, then the correct coordinates were calculated by buildPoints();.
			break;
		}
		return buildPoints;
	}

	/**
	 * This method uses another point as reference and sets the current pieces definite coordinate
	 * (topLeftPoint) to match the other point.
	 * 
	 * @param other
	 *            represents a point to obtain values from
	 */
	void setTopLeftPointToValues(Point other) {
		topLeftPoint.x = other.x;
		topLeftPoint.y = other.y;
	}

	/**
	 * Returns the ID for this piece. This is used in differentiating pieces that are of the same
	 * type. e.g. There are 4 tall pieces. Each tall piece on the board has a different ID.
	 * 
	 * @return the id set for this piece
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Setter for the ID
	 * 
	 * This is used in differentiating pieces that are of the same type. e.g. There are 4 tall
	 * pieces. Each tall piece on the board has a different ID.
	 * 
	 * @param paramID
	 *            the new ID to be set. Valid range is 1-4 inclusive.
	 */
	public void setID(int paramID) {
		if (paramID < 0) paramID *= -1;
		if (paramID == 0) paramID = 1;
		this.ID = paramID % 5; // trim off any higher numbers
	}

}
