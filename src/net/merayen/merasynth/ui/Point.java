package net.merayen.merasynth.ui;

public class Point {
	public float x;
	public float y;

	public Point() {
		this.x = 0f;
		this.y = 0f;
	}

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return String.format(
				"[Point(x=%f, y=%f)]",
				x, y
		);
	}
}
