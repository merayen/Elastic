package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.logicnodes.Format;

public class AudioOutlet extends Outlet {
	public final float[] audio; // Coded alternating channels
	public int channels; // Count of channels that this Outlet represents. Inlet must read this to correctly read the audio-data TODO

	public AudioOutlet(int buffer_size) {
		super(buffer_size);
		audio = new float[buffer_size];
	}

	public Format getFormat() {
		return Format.AUDIO;
	}

	public Class<? extends Inlet> getInletClass() {
		return AudioInlet.class;
	}
}
