package net.merayen.merasynth.netlist.datapacket;

/*
 * Contains raw MIDI data.
 * Can contain multiple MIDI-packets, and they can be synchronous.
 */
public class MidiResponse extends DataPacket {
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

	/*
	 * Which channel each midi packet is designated to.
	 * Note that this is not MIDI channel, but our internal channel.
	 * Must be the same length as MIDI packets in midi-variable.
	 */
	public short[] channels;

	public int getSize() {
		int size = 0;

		if(midi != null)
			for(int i = 0; i < midi.length; i++)
				if(midi[i] != null)
					size += midi[i].length * 2;

		if(offset != null)
			size += offset.length * 4;

		size += 4;

		return size;
	}
}
