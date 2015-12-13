package net.merayen.merasynth.netlist.datapacket;

/*
 * Request audio from node from the left.
 * How many samples that is requested to process is set on DataPacket.sample_count.
 * Requested node needs to respond with exact samples or more.
 * Set this on sample_count (inherited)
 */
public class AudioRequest extends RequestPacket {}
