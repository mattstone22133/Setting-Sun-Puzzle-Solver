package enigma.engine;

/**
 * A simple helper class that represents a point defined by an x and y value.
 * 
 * In the puzzle game, x values represent columns and y values represent rows on the game board.
 * 
 * @author Matt Stone
 * @version 1/1/2017
 *
 */
public class Point {
	public int x;
	public int y;

	/**
	 * Normal constructor.
	 * 
	 * @param x
	 *            the x value to set for the point.
	 * @param y
	 *            the y value to set for the point.
	 */
	Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * The copy constructor.
	 * 
	 * @param other
	 *            is a reference to copy data from
	 */
	Point(Point other) {
		this.x = other.x;
		this.y = other.y;
	}

	/**
	 * This sets the object's fields to match another object. It is similar to an assignment
	 * operator overload.
	 * 
	 * @param other
	 *            the point from which to get the copy values.
	 */
	public void copyPoint(Point other) {
		this.x = other.x;
		this.y = other.y;
	}

	/**
	 * A simple setter provided for changing two fields at once for convenience.
	 * 
	 * @param x
	 *            the x value of the point.
	 * @param y
	 *            the y value of the point
	 */
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * An equals method that determines if the two points represent the same location.
	 * 
	 * @param other
	 *            the point to check against.
	 * @return returns whether the points represent the same location.
	 */
	public boolean equals(Point other) {
		return this.x == other.x && this.y == other.y;
	}
	
	public String toString(){
		return String.format("%d %d", x, y);
	}
}
