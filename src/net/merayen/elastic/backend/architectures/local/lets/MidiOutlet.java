package net.merayen.elastic.backend.architectures.local.lets;

import net.merayen.elastic.backend.logicnodes.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MidiOutlet extends Outlet {
	public static class MidiFrame extends ArrayList<short[]> {
		public final int framePosition;

		private MidiFrame(int framePosition) {
			this.framePosition = framePosition;
		}
	}

	/**
	 * Format: midi[<sample offset>][<midi-packet no>][<midi-data>]
	 */
	final List<MidiFrame> midi = new ArrayList<>();

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
	public void reset(int sample_offset) {
		super.reset(sample_offset);
		midi.clear();
	}

	/**
	 * Write midi to outlet.
	 * midi parameter is not copied, so don't change it after pushing it.
	 */
	public void putMidi(int position, short[] midiPacket) {
		putMidi(position, new short[][]{midiPacket});
	}

	/**
	 * Write midi to outlet.
	 * midi parameter is not copied, so don't change it after pushing it.
	 */
	public void putMidi(int position, short[][] midiPackets) {
		MidiFrame last = midi.isEmpty() ? null : midi.get(midi.size() - 1);

		if(position < 0)
			throw new RuntimeException("Negative write position not allowed");

		if(position >= buffer_size)
			throw new RuntimeException("Write position out of bound. Only 0 - buffer_size-1 is allowed");

		if(position < written)
			throw new RuntimeException("Wrote midi to a previous frame after announcing. Another node may already have read");

		if (last == null || last.framePosition != position)
			midi.add(last = new MidiFrame(position));

		last.addAll(Arrays.asList(midiPackets));
	}

	@Override
	public void forwardFromOutlet(Outlet source) {
		MidiOutlet outlet = (MidiOutlet)source;

		int stop = outlet.written;

		if(outlet.midi.size() < midi.size())
			throw new RuntimeException("Can not forward midi, perhaps already forwarded from another port?");

		int i = -1;
		for(MidiFrame midiFrame : new ArrayList<>(outlet.midi)) {
			i++;

			if(midiFrame.framePosition >= stop)
				break; // In case someone added midiFrame while we are in this function

			if(midi.size() > i) {
				if(midi.get(i) != midiFrame)
					throw new RuntimeException("Can not forward midi, perhaps already forwarded from another port?");
			} else {
				midi.add(midiFrame);
			}
		}

		written = stop;
		push();
	}
}