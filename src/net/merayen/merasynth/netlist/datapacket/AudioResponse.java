package net.merayen.merasynth.netlist.datapacket;

public class AudioResponse extends DataPacket {
	public long sample_offset;
	public float[] samples;
	public int channels;
	public int sample_rate;
}
