package net.merayen.elastic.backend.mix.datatypes;

import java.util.List;

public class Midi extends DataType {
	public final short[/* sample index */][/* midi packet no */][/* midi data array */] midi;

	public Midi(short[][][] midi) {
		this.midi = midi;
	}

	static Midi mix(int samples, List<DataType> midi) { // Untested
		short[][][] out = new short[samples][][];

		// Count total number of midi packets
		int midi_packet_count = 0;
		for(DataType m : midi)
			for(short[][] midi_sample : ((Midi)m).midi) // for each sample
				midi_packet_count += midi_sample.length;

		// XXX We don't care about timing for midi for now. Just send everything at once, though we should perhaps have a thread that feeds the device with correct timings? Or is this supported elsewhere?
		out[0] = new short[midi_packet_count][];

		int i = 0;
		for(DataType m : midi)
			for(short[][] midi_sample : ((Midi)m).midi) // for each sample
				for(short[] midi_packet : midi_sample) // for each midi_packet in this sample
					out[0][i++] = midi_packet;

		return new Midi(out);
	}
}
