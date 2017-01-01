import java.util.Comparator;

public class PointCompare implements Comparator<Point> {

	@Override
	public int compare(Point first, Point second) {
		if(first.x > second.x) {
			return 1;
		} else if (first.x < second.x){
			return -1;
		} else {
			//points have same x value
			if(first.y > second.y){
				return 1;
			} else if (first.y < second.y){
				return -1;
			} else {
				return 0;
			}
		}
	}
}
