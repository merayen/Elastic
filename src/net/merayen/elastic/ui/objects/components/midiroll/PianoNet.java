package net.merayen.elastic.ui.objects.components.midiroll;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;

public class PianoNet extends UIObject {
	private float height = 100; // TODO to be dynamic when notes are written
	public float width = 100;
	float octave_width = 5 * 7;

	private final boolean[] BLACK_TANGENTS = new boolean[]{false,true,false,true,false,false,true,false,true,false,true,false};

	private final int octave_count;

	public PianoNet(int octave_count) {
		this.octave_count = octave_count;
	}

	@Override
	public void onDraw(Draw draw) {
		float y = 0;

		draw.setStroke(0.5f);

		int pos = 0;
		for(int i = 0; i < octave_count * 12; i++) {
			int b = BLACK_TANGENTS[pos] ? 1 : 0;

			draw.setColor(50 - b * 20, 50 - b * 20, 50 - b * 20);

			draw.fillRect(0, y, width, octave_width / 12);

			draw.setColor(0, 0, 0);
			draw.rect(0, y, width, octave_width / 12);

			y += octave_width / 12;
			pos++;
			pos %= 12;
		}

		height = y;
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}
}
