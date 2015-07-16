package net.merayen.merasynth.netlist.datapacket;

public class AudioResponse extends DataPacket {
	public long sample_offset;
	public float[] samples; // Size: channels * sample_rate * samples.length * 4
	public int channels; // How many channels
	public int sample_rate;

	public int getSize() {
		return 8 + (samples == null ? 0 : 4 * samples.length) + 4 + 4;
	}
}
