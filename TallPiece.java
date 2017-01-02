import java.util.ArrayList;

/**
 * Class representing the tall pieces. See the PlayPiece class before this documentation.
 * 
 * @author Matt Stone
 * @version 1/1/17
 *
 */
public class TallPiece extends PlayPiece {
	/**
	 * Basic construction that initializes the piece to a specified x(column) and y(row) position.
	 * 
	 * @param x
	 *            column value of position
	 * @param y
	 *            row value of position
	 */
	public TallPiece(int x, int y) {
		super(x, y);
		representsPoints = new ArrayList<>();
		representsPoints.add(new Point(this.topLeftPoint));
		representsPoints.add(new Point(this.topLeftPoint.x, this.topLeftPoint.y + 1));
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
		ArrayList<Point> ret = representsPoints;
		int x = this.topLeftPoint.x;
		int y = this.topLeftPoint.y;
		representsPoints.get(0).setXY(x, y);
		representsPoints.get(1).setXY(x, y + 1);
		return ret;
	}
}
