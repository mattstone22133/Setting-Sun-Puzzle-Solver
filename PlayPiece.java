import java.util.ArrayList;

public abstract class PlayPiece {
	protected Point topLeftPoint;
	private int ID;

	// represents the last calculated points. These references change with each call to getCoordinates.
	// these points are recycled to reduce object creation.
	public ArrayList<Point> representsPoints;
	
	public PlayPiece(int x, int y) {
		topLeftPoint = new Point(x, y);
		setID(1);
	}

	/**
	 * Abstract method that sets the "representsPoints" field to prevent creating a large number of objects.
	 * 
	 * @return returns the points the representing the space occupied by the current piece.
	 */
	abstract protected ArrayList<Point> buildPoints();

	/**
	 * This method recycles internal points. Therefore, the returned points from the first call are not valid after the second call.
	 * Why is it done this way if it is generally bad OOP practice? It is done for the specific reason
	 * of keeping object creation down during breadth first search. This should improve performance and theoretically
	 * reduce the chance for using all available heap space. 
	 * 
	 * @param direction
	 *            the direction for that the piece will be moved
	 * @return returns the board coordinates after the applied move direction
	 * 
	 * @invariant assumes that buildPoints has been implemented to update the represented points based on the current topLeftPoint
	 * 
	 * @warning subsequent calls invalidate references obtained in previous calls (see above).  It is possible to introduce a null pointer
	 * exception if the returned container is modified. This method is only expected to be used internally for the project.
	 */
	public ArrayList<Point> getCoordinatePoints(Direction direction) {
		ArrayList<Point> buildPoints = buildPoints();
		switch (direction) {
		case UP:
			for (Point pnt : buildPoints) {
				pnt.y -= 1;
			}
			break;
		case DOWN:
			for (Point pnt : buildPoints) {
				pnt.y += 1;
			}
			break;
		case LEFT:
			for (Point pnt : buildPoints) {
				pnt.x -= 1;
			}
			break;
		case RIGHT:
			for (Point pnt : buildPoints) {
				pnt.x += 1;
			}
			break;
		case STAY:
			break;
		}
		return buildPoints;
	}
	
	void setTopLeftPointToValues(Point other){
		topLeftPoint.x = other.x;
		topLeftPoint.y = other.y;
	}

	public int getID() {
		return ID;
	}

	public void setID(int paramID) {
		if (paramID < 0)
			paramID *= -1;
		if (paramID == 0)
			paramID = 1;
		this.ID = paramID % 5;	//trim off any higher numbers
	}
	

}
