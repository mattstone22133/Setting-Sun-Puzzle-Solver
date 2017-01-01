public class Point {
	public int x;
	public int y;
	
	//normal constructor
	Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	//copy constructor
	Point(Point other){
		this.x = other.x;
		this.y = other.y;
	}

	public void copyPoint(Point other) {
		this.x = other.x;
		this.y = other.y;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Point other){
		return this.x == other.x && this.y == other.y;
	}
}
