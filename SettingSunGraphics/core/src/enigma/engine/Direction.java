package enigma.engine;

/**
 * This enumeration defines the move directions that a play piece can take. Enumeration values are
 * used as arguments to a function for calculating the resulting move coordinates.
 * 
 * @author Matt Stone
 * @version 1/1/2017
 */
public enum Direction {
	UP, DOWN, LEFT, RIGHT, STAY;

	// save a quick lookup reference in static storage; this allows recycling of objects in memory. 
	static Direction[] directionValues = Direction.values();
}
