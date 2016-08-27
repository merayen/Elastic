package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.nodes.Format;

public class MidiInlet extends Inlet {
	public MidiInlet(Outlet outlet) {
		super(outlet);
	}

	public Format getFormat() {
		return Format.MIDI;
	}
}
