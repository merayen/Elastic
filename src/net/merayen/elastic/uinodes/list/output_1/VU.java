package net.merayen.elastic.uinodes.list.output_1;

import net.merayen.elastic.ui.UIObject;

class VU extends UIObject {
	float width = 100;
	float bar_height = 10;

	float[] vu;

	@Override
	protected void onDraw() {
		super.onDraw();

		float meter_width = width - 4;
		int channels = 0;

		if(vu != null)
			channels = vu.length + 5;

		if(vu != null) {
			for(int i = 0; i < channels; i++) {
				draw.setColor(0, 0, 0);
				draw.fillRect(0, 1 + i * bar_height, 100, bar_height - 2);

				draw.setColor(0, 255, 0);
				draw.fillRect(2, 2 + i * bar_height, 0 + Math.min(0.7f, vu[0]) * meter_width, bar_height - 4);

				if(vu[0] > 0.7f) {
					draw.setColor(255, 200, 0);
					draw.fillRect(0.7f * meter_width, 2 + i * bar_height, 0 + Math.min(0.9f, vu[0]) * meter_width - meter_width * 0.7f, bar_height - 4);
				}

				if(vu[0] > 0.9) {
					draw.setColor(255, 0, 0);
					draw.fillRect(0.9f * meter_width, 2 + i * bar_height, 0 + Math.min(1f, vu[0]) * meter_width - meter_width * 0.9f, bar_height - 4);
				}
			}
		}
	}
}
