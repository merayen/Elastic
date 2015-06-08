package net.merayen.merasynth.ui;

public class Rect {
	public float x = 0, y = 0, width = 0, height = 0;
	
	public Rect(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Rect() {
		
	}
	
	public String toString() {
		return String.format(
				"[Rect(x=%f, y=%f, width=%f, height=%f)]",
				x, y, width, height
		);
	}
}
