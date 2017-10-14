package net.merayen.elastic.ui.objects.components.midiroll;

import net.merayen.elastic.ui.UIObject;

public class PianoNet extends UIObject {
	private float width = 100, height = 100; // TODO to be dynamic when notes are written
	float octave_width = 5 * 7;

	private final boolean[] BLACK_TANGENTS = new boolean[]{false,true,false,true,false,false,true,false,true,false,true,false};

	private final int octave_count;

	public PianoNet(int octave_count) {
		this.octave_count = octave_count;
	}

	@Override
	protected void onDraw() {
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
