package net.merayen.elastic.ui;

public class Rect {
	public float x1 = 0, y1 = 0, x2 = 0, y2 = 0;

	public Rect(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public Rect(Rect r) {
		x1 = r.x1;
		y1 = r.y1;
		x2 = r.x2;
		y2 = r.y2;
	}

	public Rect() {

	}

	public String toString() {
		return String.format(
				"[Rect(x1=%f, y1=%f, x2=%f, y2=%f)]",
				x1, y1, x2, y2
		);
	}

	/*
	 * Clip this rectangle with another rectangle.
	 */
	public void clip(Rect r) {
		clip(r.x1, r.y1, r.x2, r.y2);
	}

	public void clip(float x1, float y1, float x2, float y2) {
		this.x1 = Math.max(this.x1, x1);
		this.y1 = Math.max(this.y1, y1);
		this.x2 = Math.min(this.x2, x2);
		this.y2 = Math.min(this.y2, y2);
	}

	public void enlarge(Rect r) {
		enlarge(r.x1, r.y1, r.x2, r.y2);
	}

	public void enlarge(float x1, float y1, float x2, float y2) {
		this.x1 = Math.min(this.x1, x1);
		this.y1 = Math.min(this.y1, y1);
		this.x2 = Math.max(this.x2, x2);
		this.y2 = Math.max(this.y2, y2);
	}

	public Rect copy() {
		return new Rect(x1, y1, x2, y2);
	}

	public float getWidth() {
		return x2 - x1;
	}

	public float getHeight() {
		return y2 - y1;
	}
}
