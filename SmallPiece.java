import java.util.ArrayList;

/**
 * Class representing the small yellow pieces. See the PlayPiece class before this documentation.
 * 
 * @author Matt Stone
 * @version 1/1/17
 *
 */
public class SmallPiece extends PlayPiece {
	/**
	 * Basic construction that initializes the piece to a specified x(column) and y(row) position.
	 * 
	 * @param x
	 *            column value of position
	 * @param y
	 *            row value of position
	 */
	public SmallPiece(int x, int y) {
		super(x, y);
		representsPoints = new ArrayList<>();
		representsPoints.add(new Point(this.topLeftPoint));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see PlayPiece#buildPoints() populates the representsPoints fields of this class with the
	 * appropriate value for the given position of the piece.
	 */
	@Override
	protected ArrayList<Point> buildPoints() {
		// create a safe collection of objects
		representsPoints.get(0).setXY(this.topLeftPoint.x, this.topLeftPoint.y);
		return representsPoints;
	}
}
