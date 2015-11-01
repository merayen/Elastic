package net.merayen.merasynth.netlist.datapacket;

public class AudioRequest extends DataPacket {
	/*
	 * Never-ending sample counter.
	 */
	public long sample_offset;

	/*
	 * Represents where in the playback we are.
	 * Always zero if no playback controller is set.
	 * This one is read by nodes like sequencers.
	 */
	public long sample_offset_time;

	/*
	 * How many samples that is requested to process.
	 * Requested node needs to respond with exact samples or more.
	 * If not done, the requester might "starve" and mute parts might happen.
	 */
	public int sample_count;

	/*
	 * Sample rate that is expected that the requested node to respond in.
	 * This value can not be changed in one module, due to one node might
	 * have multiple other nodes connected to it, and then sample rate
	 * conversion and decision of which sample rate to actually process
	 * gets complex to figure out. We just drop this functionality. 
	 */
	public int sample_rate;

	public int getSize() {
		return 8 + 8 + 4 + 4;
	}
}
