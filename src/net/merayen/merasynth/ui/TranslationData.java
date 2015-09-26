package net.merayen.merasynth.ui;

public class TranslationData {
	public float x = 0, y = 0; // Object's origin (relative to parent)
	public float scale_x = 1, scale_y = 1; // 
	public boolean visible = true;
	public Rect clip;

	public TranslationData() {

	}

	public TranslationData(TranslationData t) {
		x = t.x;
		y = t.y;
		scale_x = t.scale_x;
		scale_y = t.scale_y;
		visible = t.visible;
	}

	public void translate(TranslationData td) {
		x += td.x;
		y += td.y;
		scale_x *= td.scale_x;
		scale_y *= td.scale_y;
		visible = td.visible;

		if(td.clip != null) {
			if(clip == null) // TODO apply scaling below?
				clip = new Rect(
					(x + td.clip.x1) * scale_x,
					(y + td.clip.y1) * scale_y,
					(x + td.clip.x2) * scale_x,
					(y + td.clip.y2) * scale_y
				);
			else
				clip.clip(
					(x + td.clip.x1) * scale_x,
					(y + td.clip.y1) * scale_y,
					(x + td.clip.x2) * scale_x,
					(y + td.clip.y2) * scale_y
				);
		}
	}

	public String toString() {
		return String.format(
			"X=%f, Y=%f, scale_x=%f, scale_y=%f",
			x, y, scale_x, scale_y
		);
	}
}
