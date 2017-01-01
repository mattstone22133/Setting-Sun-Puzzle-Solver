import java.util.ArrayList;

public class SmallPiece extends PlayPiece {
	public SmallPiece(int x, int y){
		super(x, y);
		representsPoints = new ArrayList<>();
		representsPoints.add(new Point(this.topLeftPoint));
	}

	@Override
	protected ArrayList<Point> buildPoints() {
		//create a safe collection of objects
		representsPoints.get(0).setXY(this.topLeftPoint.x, this.topLeftPoint.y);
		return representsPoints;
	}
}
