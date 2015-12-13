package net.merayen.merasynth.netlist.datapacket;

/*
 * Returned by audio nodes, but also graph nodes.
 * Length of all channels MUST BE EQUAL.
 */
public class AudioResponse extends ResponsePacket {
	public float[] samples;

	/*
	 * Channel number
	 */
	public short[] channels; // Channel numbers

	public int getSize() {
		return (channels == null ? 0 : 2 * channels.length) + (samples == null ? 0 : 4 * samples.length);
	}
}
