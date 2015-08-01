package net.merayen.merasynth.netlist.datapacket;

/*
 * Contains raw midi data.
 * TODO decide if we want sequencing data to be MIDI, or if we roll our own
 */
public class Midi extends DataPacket {
	public int getSize() {
		return 0;
	}
}
