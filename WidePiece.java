import java.util.ArrayList;

public class WidePiece extends PlayPiece {

	public WidePiece(int x, int y) {
		super(x, y);
		representsPoints = new ArrayList<>();
		representsPoints.add(new Point(this.topLeftPoint));
		representsPoints.add(new Point(this.topLeftPoint.x + 1, this.topLeftPoint.y));
	}

	@Override
	protected ArrayList<Point> buildPoints() {
		//create a safe collection of objects
		ArrayList<Point> ret = representsPoints;
		int x = this.topLeftPoint.x;
		int y = this.topLeftPoint.y;
		ret.get(0).setXY(x, y);
		ret.get(1).setXY(x + 1, y);
		return ret;
	}
}
