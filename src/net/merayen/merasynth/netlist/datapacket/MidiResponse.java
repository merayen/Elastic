package net.merayen.merasynth.netlist.datapacket;

/*
 * Contains raw MIDI data.
 * Can contain multiple MIDI-packets, and they can be synchronous.
 */
public class MidiResponse extends ResponsePacket {
	/*
	 * MIDI data.
	 * Format: midi[packet no][MIDI data offset]
	 */
	public short[][] midi;

	/*
	 * Offset in sample count for each MIDI packet.
	 * Must be the same length as MIDI packets in midi-variable.
	 */
	public int[] offset;

	public int getSize() {
		int result = 0;

		if(midi != null)
			for(int i = 0; i < midi.length; i++)
				if(midi[i] != null)
					result += midi[i].length * 2;

		if(offset != null)
			result += offset.length * 4;

		return result + size;
	}
}
