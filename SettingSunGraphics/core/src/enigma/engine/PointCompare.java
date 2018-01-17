package enigma.engine;

import java.util.Comparator;

/**
 * Class that provides an object for comparing two points; this is provided to make use of the java
 * collection sorting function.
 * 
 * Why is this class necessary for the problem? Board configuration states are contained in a data
 * vector where information is inserted using bitwise operations. Because there are redundant pieces
 * on the board (ie 4 tall pieces, and 4 small pieces), there are certain configurations of the
 * board that actually represent the same state configuration (duplicate permutations). For example,
 * swapping the first tall piece with the 4th tall piece is the same configuration as far as the
 * player is concerned. Without recognizing this fact, there are over 1 trillion potential board
 * states. Attempting to account for these duplicate states reduces the total number of possible
 * states to around 36,000 by my calculations.
 * 
 * The way I account for this is to sort the similar piece positions before they are stored in the
 * data vector. The positions are sorted first by differing the x values, then by the y values if x
 * values are the same. This ensures that different permutations of the same state will always be
 * stored in the data vector in only one way.
 * 
 * Since the states already encountered are stored in a balanced binary search tree for look up
 * (like the hashing concept), this sorting greatly improves the speed the program runs because
 * there is a single key for the many permutations of how similar pieces are positioned. While we're
 * not using a hash table, the principle is the same. Therefore, the creation of the data vector can
 * be thought of has a hash function, and this sorting provided here is a component of that
 * function.
 * 
 * 
 * @author Matt Stone
 * @version 1/1/2017
 *
 */
public class PointCompare implements Comparator<Point> {

	@Override
	public int compare(Point first, Point second) {
		if (first.x > second.x) {
			return 1;
		} else if (first.x < second.x) {
			return -1;
		} else {
			// points have same x value
			if (first.y > second.y) {
				return 1;
			} else if (first.y < second.y) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
