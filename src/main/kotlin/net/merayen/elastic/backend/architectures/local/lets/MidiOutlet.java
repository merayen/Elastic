package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.logicnodes.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MidiOutlet extends Outlet {
	public static class MidiFrame extends ArrayList<short[]> {}

	/**
	 * Only owner of the outlet should modify this.
	 */
	public final Map<Integer, MidiFrame> midi = new HashMap<>();

	public MidiOutlet(int buffer_size) {
		super(buffer_size);
	}

	public Format getFormat() {
		return Format.MIDI;
	}

	@Override
	public Class<? extends Inlet> getInletClass() {
		return MidiInlet.class;
	}

	@Override
	public void reset() {
		super.reset();
		midi.clear();
	}

	/**
	 * Write midi to outlet.
	 * midi parameter is not copied, so don't change it after pushing it.
	 */
	public void addMidi(int position, short[] midiPacket) {
		addMidi(position, new short[][]{midiPacket});
	}

	/**
	 * Write midi to outlet.
	 * midi parameter is not copied, so don't change it after pushing it.
	 */
	public void addMidi(int position, short[][] midiPackets) {
		if(position < 0)
			throw new RuntimeException("Negative write position not allowed");

		if(position >= buffer_size)
			throw new RuntimeException("Write position out of bound. Only 0 - buffer_size-1 is allowed. Got " + position);

		if (!midi.containsKey(position))
			midi.put(position, new MidiFrame());

		midi.get(position).addAll(Arrays.asList(midiPackets));
	}

	@Override
	public void forwardFromOutlet(Outlet source) {
		if (satisfied())
			throw new RuntimeException("MidiOutlet already written to");

		if (!source.satisfied())
			throw new RuntimeException("Outlet must be pushed to, to be forwarded");

		if (!(source instanceof MidiOutlet))
			throw new RuntimeException("Source must be an MidiOutlet, or it can not be forwarded");

		MidiOutlet outlet = (MidiOutlet)source;

		for(Map.Entry<Integer, MidiFrame> midiEntry : outlet.midi.entrySet())
			midi.put(midiEntry.getKey(), midiEntry.getValue());

		push();
	}
}