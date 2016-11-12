package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.nodes.Format;

public class AudioOutlet extends Outlet {
	public final float[] audio;

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
