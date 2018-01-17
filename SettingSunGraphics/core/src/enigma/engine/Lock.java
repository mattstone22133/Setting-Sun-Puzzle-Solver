package enigma.engine;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * @author Matt Stone
 * @deprecated this class was an attempt to prevent cheating by jumping over pieces. 1.a better way
 *             to implement preventing jumping over pieces is to use the SettingSun game solver
 *             class object. have this object check to see if the move is a valid move given the
 *             current board. This can be checked when the piece "snaps" into position. If it is not
 *             a valid move, then it simply snaps to the original location.
 * 
 *             2(**) perhaps an even better implementation. As the piece is being dragged,
 *             constantly check the coordinate it evaluates to. if this coordinate changes from the
 *             last evaluated coordinate, then ask the game solver if this is a valid move. If it is
 *             not a valid move, then snap the piece back to the location before the move was made.
 *             this would prevent having to do path finding to figure out if a valid sequence of
 *             moves can be used (in making L shapes) because every transition would be a single
 *             step and therefore an easy look up on the boolean table.
 * 
 *             I coded up a method for checking this in the SettingSun class, but it hasn't been
 *             used in the rising sun version.
 */
public class Lock {
	public float minX;
	public float maxX;

	public float minY;
	public float maxY;

	public boolean useMinX = false;
	public boolean useMinY = false;

	public boolean useMaxX = false;
	public boolean useMaxY = false;

	public void attemptUnlock(Sprite sprite) {
		// check x
		if (useMinX && sprite.getX() > minX) {
			useMinX = false;
		}
		if (useMaxX && sprite.getX() < maxX) {
			useMaxX = false;
		}
		// check y
		if (useMinY && sprite.getY() > minY) {
			useMinY = false;
		}
		if (useMaxY && sprite.getY() < maxY) {
			useMaxY = false;
		}
	}

	public void lockMaxX(float x) {
		maxX = x;
		useMaxX = true;
	}

	public void lockMinX(float x) {
		minX = x;
		useMinX = true;
	}

	public void lockMaxY(float y) {
		maxY = y;
		useMaxY = true;
	}

	public void lockMinY(float y) {
		minY = y;
		useMinY = true;
	}

	public void unlockAll() {
		useMinX = false;
		useMaxX = false;
		useMinY = false;
		useMaxY = false;
	}

	public boolean spriteLocationValid(Sprite piece) {
		float currMinX = piece.getX();
		float currMinY = piece.getY();
		// float currMaxX = currMinX + piece.getWidth() * piece.getScaleX();
		// float currMaxY = currMinY + piece.getHeight() * piece.getScaleY();

		if (useMinX && currMinX < minX) {
			return false;
		}
		// if(useMaxX && currMaxX > maxX){
		if (useMaxX && currMinX > maxX) {
			return false;
		}
		if (useMinY && currMinY < minY) {
			return false;
		}
		// if (useMaxY && currMaxY > maxY) {
		if (useMaxY && currMinY > maxY) {
			return false;
		}

		return true;
	}

	public void lockX(float newX, float origX) {
		if (newX > origX) {
			// test if max is already being used
			if (useMaxX) {
			} else {
				lockMaxX(newX);
			}
		} else {
			if (useMinX) {
			} else {
				lockMinX(newX);
			}
		}
	}

	public void lockY(float newY, float origY) {
		if (newY > origY) {
			if (useMaxY) {
			} else {
				lockMaxY(newY);
			}
		} else {
			if (useMinY) {
			} else {
				lockMinY(newY);
			}
		}
	}
}
