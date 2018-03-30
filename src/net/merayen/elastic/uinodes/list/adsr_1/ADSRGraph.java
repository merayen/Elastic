package net.merayen.elastic.uinodes.list.adsr_1;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;

public class ADSRGraph extends UIObject {
	public float width = 50;
	public float height = 50;

	public float attack_time = 1;
	public float decay_time = 1;
	public float sustain_value = 0.5f;// 0..1
	public float release_time = 1;

	@Override
	public void onInit() {}

	@Override
	public void onDraw(Draw draw) {
		draw.setColor(20, 20, 50);
		draw.fillRect(0, 0, width, height);

		drawSectorLines(draw);

		super.onDraw(draw);
	}

	private void drawSectorLines(Draw draw) {
		float[] widths = getSectorWidths();

		draw.setColor(50, 50, 100);
		draw.setStroke(2f);

		draw.line(widths[0], 0, widths[0], height);
		draw.line(widths[1], 0, widths[1], height);
		draw.line(widths[2], 0, widths[2], height);

		float sustain_height = height * 0.1f + (1 - sustain_value) * height * 0.9f;
		draw.setColor(200, 150, 0);
		draw.line(0, height, widths[0], height * 0.1f);
		draw.line(widths[0], height * 0.1f, widths[1], sustain_height);
		draw.line(widths[1], sustain_height, widths[2], sustain_height);
		draw.line(widths[2], sustain_height, width, height);
	}

	private float[] getSectorWidths() {
		final float SUSTAIN_PERCENTAGE = 0.25f;
		float total = attack_time + decay_time + release_time;
		total += total * SUSTAIN_PERCENTAGE; // Sustain block 25%
		total = Math.max(0.1f, total);

		float[] r = new float[3];

		r[0] = (attack_time / total) * width;
		r[1] = r[0] + (decay_time / total) * width;
		r[2] = r[1] + ((total - total / (1 + SUSTAIN_PERCENTAGE)) / total) * width;

		return r;
	}
}
