package net.merayen.elastic.ui;

public class TranslationData {
	public float x = 0, y = 0; // Object's origin (relative to parent)
	public float scaleX = 1, scaleY = 1; // Scales the content INSIDE, not ourself
	public Rect clip;
	public Color color = new Color(); // TODO implement color and alpha filtering when drawing, so that we can translate colors on children objects

	public TranslationData() {}

	public TranslationData(TranslationData t) {
		x = t.x;
		y = t.y;
		scaleX = t.scaleX;
		scaleY = t.scaleY;
		clip = t.clip;
		color = t.color;
	}

	public void translate(TranslationData td) {
		x = x + td.x / scaleX;
		y = y + td.y / scaleY;

		if(td.clip != null) {
			if(clip == null)
				clip = new Rect(
					x + td.clip.getX1() / scaleX,
					y + td.clip.getY1() / scaleY,
					x + td.clip.getX2() / scaleX,
					y + td.clip.getY2() / scaleY
				);
			else
				clip.clip(
					x + td.clip.getX1() / scaleX,
					y + td.clip.getY1() / scaleY,
					x + td.clip.getX2() / scaleX,
					y + td.clip.getY2() / scaleY
				);
		}

		scaleX *= td.scaleX;
		scaleY *= td.scaleY;
	}

	public String toString() {
		return String.format(
			"X=%f, Y=%f, scaleX=%f, scaleY=%f, clip=%s",
			x, y, scaleX, scaleY, clip
		);
	}
}
