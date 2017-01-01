import java.util.ArrayList;

public class SunPiece extends PlayPiece {

	public SunPiece(int x, int y) {
		super(x, y);
		representsPoints = new ArrayList<>();
		representsPoints.add(new Point(this.topLeftPoint));
		representsPoints.add(new Point(this.topLeftPoint.x + 1, this.topLeftPoint.y));
		representsPoints.add(new Point(this.topLeftPoint.x, this.topLeftPoint.y + 1));
		representsPoints.add(new Point(this.topLeftPoint.x + 1 , this.topLeftPoint.y + 1));	
	}

	@Override
	protected ArrayList<Point> buildPoints() {
		//create a safe collection of objects
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
