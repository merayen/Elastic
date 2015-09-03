package net.merayen.merasynth.ui;

public class Rect {
	public float x = 0, y = 0, width = 0, height = 0;
	
	public Rect(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rect(Rect r) {
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
	}
	
	public Rect() {
		
	}
	
	public String toString() {
		return String.format(
				"[Rect(x=%f, y=%f, width=%f, height=%f)]",
				x, y, width, height
		);
	}

	/*
	 * Clip this rectangle with another rectangle.
	 */
	public void clip(Rect r) {
		x = Math.max(r.x, x);
		y = Math.max(r.y, y);
		width = Math.min(r.width, width);
		height = Math.min(r.height, height);
	}

	public void enlarge(Rect r) {
		x = Math.min(r.x, x);
		y = Math.min(r.y, y);
		width = Math.max(r.width, width);
		height = Math.max(r.height, height);
	}

	public void enlarge(float x, float y, float width, float height) {
		this.x = Math.min(this.x, x);
		this.y = Math.min(this.y, y);
		this.width = Math.max(this.width, width);
		this.height = Math.max(this.height, height);
	}
}
