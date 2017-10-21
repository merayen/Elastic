package net.merayen.elastic.uinodes.list.output_1;

import net.merayen.elastic.ui.UIObject;

class VU extends UIObject {
	float width = 100;
	float bar_height = 10;

	private float[] vu = new float[0];
	private float[] offset = new float[0];

	private long last_draw = System.currentTimeMillis();

	@Override
	protected void onDraw() {
		super.onDraw();

		float meter_width = width - 4;
		int channels = vu.length;

		for(int i = 0; i < channels; i++) {
			float v = (float)Math.pow(vu[i], 0.5);

			draw.setColor(0, 0, 0);
			draw.fillRect(0, 1 + i * bar_height, 100, bar_height - 2);

			draw.setColor(0, 255, 0);
			draw.fillRect(2, 2 + i * bar_height, 0 + Math.min(0.7f, v) * meter_width, bar_height - 4);

			if(v > 0.7f) {
				draw.setColor(255, 200, 0);
				draw.fillRect(0.7f * meter_width, 2 + i * bar_height, 0 + Math.min(0.9f, v) * meter_width - meter_width * 0.7f, bar_height - 4);
			}

			if(v > 0.9) {
				draw.setColor(255, 0, 0);
				draw.fillRect(0.9f * meter_width, 2 + i * bar_height, 0 + Math.min(1f, v) * meter_width - meter_width * 0.9f, bar_height - 4);
			}

			if(offset.length > i) {
				float w = Math.min(1, Math.max(-1, offset[i] / 1000));
				draw.setColor(255, 255, 255);
				draw.line(meter_width / 2 + (w * meter_width / 2), bar_height * 0.9f, meter_width / 2 + (w * meter_width / 2), bar_height);
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

	void updateOffset(float[] data) {
		offset = data;
	}

	public float getHeight() {
		return vu.length * bar_height;
	}

	private void decrease() {
		float delta = (System.currentTimeMillis() - last_draw) / 1000f;

		for(int i = 0; i < vu.length; i++)
			vu[i] = Math.max(0, vu[i] - 0.1f * delta);

		//for(int i = 0; i < offset.length; i++)
		//	offset[i] /= 1.1f * delta;

		last_draw = System.currentTimeMillis();
	}
}
