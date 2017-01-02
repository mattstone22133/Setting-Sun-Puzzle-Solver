import java.util.ArrayList;

/**
 * Class representing the sun piece. See the PlayPiece class before this documentation.
 * 
 * @author Matt Stone
 * @version 1/1/17
 *
 */
public class SunPiece extends PlayPiece {

	/**
	 * Basic construction that initializes the piece to a specified x(column) and y(row) position.
	 * 
	 * @param x
	 *            column value of position
	 * @param y
	 *            row value of position
	 */
	public SunPiece(int x, int y) {
		super(x, y);
		representsPoints = new ArrayList<>();
		representsPoints.add(new Point(this.topLeftPoint));
		representsPoints.add(new Point(this.topLeftPoint.x + 1, this.topLeftPoint.y));
		representsPoints.add(new Point(this.topLeftPoint.x, this.topLeftPoint.y + 1));
		representsPoints.add(new Point(this.topLeftPoint.x + 1, this.topLeftPoint.y + 1));
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
		ret.get(0).setXY(x, y);
		ret.get(1).setXY(x + 1, y);
		ret.get(2).setXY(x, y + 1);
		ret.get(3).setXY(x + 1, y + 1);
		return ret;
	}

}
