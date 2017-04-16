package net.merayen.elastic.ui.objects.components.midiroll;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.UIClip;

public class MidiRoll extends UIObject {
	public interface Handler {
		public void onDown(int tangent_no);
		public void onUp(int tangent_no);
	}

	public float width = 100;
	public float height = 100;

	private Piano piano;
	private PianoNet net;
	private UIClip clip;

	private final int OCTAVE_COUNT = 8;

	private Handler handler;

	public MidiRoll(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onInit() {
		clip = new UIClip();
		add(clip);

		net = new PianoNet(OCTAVE_COUNT);
		clip.add(net);

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
		clip.add(piano);
	}

	@Override
	protected void onUpdate() {
		clip.width = width;
		clip.height = height;
		net.width = width;
		net.height = height;
	}

	@Override
	protected void onDraw() {
		draw.setColor(50, 50, 100);
		draw.fillRect(0, 0, width, height);
	}
}
