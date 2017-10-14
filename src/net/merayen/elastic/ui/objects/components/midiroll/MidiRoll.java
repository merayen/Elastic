package net.merayen.elastic.ui.objects.components.midiroll;

import net.merayen.elastic.ui.UIObject;

public class MidiRoll extends UIObject {
	public interface Handler {
		public void onDown(int tangent_no);
		public void onUp(int tangent_no);
	}

	public float width = 100;
	public float height = 100;

	private Piano piano;
	PianoNet net;

	private final int OCTAVE_COUNT = 8;

	private Handler handler;

	public MidiRoll(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onInit() {
		net = new PianoNet(OCTAVE_COUNT);
		add(net);

		piano = new Piano(OCTAVE_COUNT, new Piano.Handler() {
			@Override
			public void onUp(int tangent_no) {
				handler.onUp(tangent_no);
			}

			@Override
			public void onDown(int tangent_no) {
				handler.onDown(tangent_no);
			}
		});
		add(piano);
	}

	@Override
	protected void onUpdate() {
		//net.width = width;
	}

	@Override
	protected void onDraw() {
		draw.setColor(50, 50, 100);
		draw.fillRect(0, 0, width, height);
	}

	@Override
	public float getWidth() {
		return net.getWidth();
	}
	
	@Override
	public float getHeight() {
		return net.getHeight();
	}
}
