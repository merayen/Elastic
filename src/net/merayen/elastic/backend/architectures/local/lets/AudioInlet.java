package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.nodes.Format;

public class AudioInlet extends Inlet {
	public AudioInlet(AudioOutlet outlet) {
		super(outlet);
	}

	public Format getFormat() {
		return Format.AUDIO;
	}
}
