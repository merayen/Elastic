package net.merayen.merasynth.netlist.util.flow;

import net.merayen.merasynth.buffer.MidiCircularBuffer;
import net.merayen.merasynth.buffer.ObjectCircularBuffer;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.MidiResponse;

/*
 * Buffers incoming MIDI data that can then be read from the node having this port.
 */
public class MidiInputFlow {
	public static interface IHandler {
		public void onReceive();
	}

	private long current_sample_position;

	private IHandler handler;
	private MidiCircularBuffer buffer = new MidiCircularBuffer();

	public MidiInputFlow(IHandler handler) {
		this.handler = handler;
	}

	public void handle(DataPacket dp) {
		if(dp instanceof MidiResponse) {
			MidiResponse mr = (MidiResponse)dp;

			for(int i = 0; i < mr.midi.length; i++) { // TODO
				current_sample_position += mr.offset[i];
				//MidiPacket mp = new MidiPacket();
				//mp.midi = mr.midi[i];
				
			}
			handler.onReceive();
		}
	}

	/*
	 * Gets all the samples from the buffer.
	 * TODO Retrieve only for the requested sample_count range
	 */
	//public short[][] read() {
		//short[][] result = new short[buffer.available()][];
	//}

	
	//public void write(short[] midi, int sample_count)
}
