package net.merayen.merasynth.netlist.datapacket;

public class AudioResponse extends DataPacket {
	/*
	 * Returned by audio nodes, but also graph nodes.
	 */
	public long sample_offset;
	public float[] samples; // Byte size: channels * samples.length * 4
	public int channels; // How many channels
	public int sample_rate;

	public int getSize() {
		return 8 + (samples == null ? 0 : 4 * samples.length) + 4 + 4;
	}
}
