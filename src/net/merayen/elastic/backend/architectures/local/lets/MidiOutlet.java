package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.logicnodes.Format;

public class MidiOutlet extends Outlet {
	/**
	 * Format: midi[<sample offset>][<midi-packet no>][<midi-data>]
	 */
	public final short[][][] midi;

	public MidiOutlet(int buffer_size) {
		super(buffer_size);
		midi = new short[buffer_size][][];
	}

	public Format getFormat() {
		return Format.MIDI;
	}

	@Override
	public Class<? extends Inlet> getInletClass() {
		return MidiInlet.class;
	}
}
