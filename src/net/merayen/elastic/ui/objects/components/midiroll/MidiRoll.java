package net.merayen.elastic.ui.objects.components.midiroll;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.UIClip;

public class MidiRoll extends UIObject {
	public float width = 100;
	public float height = 100;

	private Piano piano;
	private PianoNet net;
	private UIClip clip;

	@Override
	protected void onInit() {
		clip = new UIClip();
		add(clip);

		net = new PianoNet();
		clip.add(net);

		piano = new Piano();
		clip.add(piano);
	}

	@Override
	protected void onUpdate() {
		clip.width = width;
		clip.height = height;
		piano.height = height;
		net.width = width;
		net.height = height;
	}

	@Override
	protected void onDraw() {
		draw.setColor(50, 50, 100);
		draw.fillRect(0, 0, width, height);
	}
}
