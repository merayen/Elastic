package net.merayen.elastic.uinodes.list.output_1;

import net.merayen.elastic.ui.UIObject;

class VU extends UIObject {
	float width = 100;
	float bar_height = 10;

	private float[] vu = new float[0];

	private long last_draw = System.currentTimeMillis();

	@Override
	protected void onDraw() {
		super.onDraw();

		float meter_width = width - 4;
		int channels = vu.length;

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

		decrease();

		for(int i = 0; i < vu.length; i++)
			vu[i] = Math.max(0, vu[i] - 0.01f);
	}

	void updateVU(float[] data) {
		if(data.length != vu.length)
			vu = new float[data.length];

		for(int i = 0; i < data.length; i++)
			if(data[i] > vu[i])
				vu[i] = Math.min(1, data[i]);
	}

	float getHeight() {
		return vu.length * bar_height;
	}

	private void decrease() {
		float delta = (System.currentTimeMillis() - last_draw) / 1000f;

		for(int i = 0; i < vu.length; i++)
			vu[i] = Math.max(0, vu[i] - 0.1f * delta);

		last_draw = System.currentTimeMillis();
	}
}
