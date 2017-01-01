import java.util.ArrayList;

public class TallPiece extends PlayPiece {
	public TallPiece(int x, int y){
		super(x, y);
		representsPoints = new ArrayList<>();
		representsPoints.add(new Point(this.topLeftPoint));
		representsPoints.add(new Point(this.topLeftPoint.x, this.topLeftPoint.y + 1));
	}

	@Override
	protected ArrayList<Point> buildPoints() {
		//create a safe collection of objects
		ArrayList<Point> ret = representsPoints;
		int x = this.topLeftPoint.x;
		int y = this.topLeftPoint.y;
		representsPoints.get(0).setXY(x, y);
		representsPoints.get(1).setXY(x, y + 1);
		return ret;
	}
}
