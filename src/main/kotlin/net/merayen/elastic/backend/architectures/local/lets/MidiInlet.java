package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.logicnodes.Format;

public class MidiInlet extends Inlet {
	public MidiOutlet outlet;
	private int lastReadPosition = -1;

	public MidiInlet(Outlet outlet) {
		super(outlet);
		this.outlet = (MidiOutlet)outlet;
	}

	public Format getFormat() {
		return Format.MIDI;
	}

	public MidiOutlet.MidiFrame getNextMidiFrame(int maxFramePosition) {
		if(outlet.midi.size() > lastReadPosition + 1 && outlet.midi.get(lastReadPosition + 1).framePosition <= maxFramePosition)
			return outlet.midi.get(++lastReadPosition);

		return null;
	}

	@Override
	public void reset(int sample_offset) {
		super.reset(sample_offset);
		lastReadPosition = -1;
	}
}
