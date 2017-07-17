package net.merayen.elastic.ui;

public class TranslationData {
	public float x = 0, y = 0; // Object's origin (relative to parent)
	public float scale_x = 1, scale_y = 1; // Scales the content INSIDE, not ourself
	public Rect clip;

	public TranslationData() {

	}

	public TranslationData(TranslationData t) {
		x = t.x;
		y = t.y;
		scale_x = t.scale_x;
		scale_y = t.scale_y;
	}

	public void translate(TranslationData td) {
		x = x + td.x / scale_x;
		y = y + td.y / scale_y;

		if(td.clip != null) {
			if(clip == null)
				clip = new Rect(
					x + td.clip.x1 / scale_x,
					y + td.clip.y1 / scale_y,
					x + td.clip.x2 / scale_x,
					y + td.clip.y2 / scale_y
				);
			else
				clip.clip(
					x + td.clip.x1 / scale_x,
					y + td.clip.y1 / scale_y,
					x + td.clip.x2 / scale_x,
					y + td.clip.y2 / scale_y
				);
		}

		scale_x *= td.scale_x;
		scale_y *= td.scale_y;
	}

	public String toString() {
		return String.format(
			"X=%f, Y=%f, scale_x=%f, scale_y=%f, clip=%s",
			x, y, scale_x, scale_y, clip
		);
	}
}
