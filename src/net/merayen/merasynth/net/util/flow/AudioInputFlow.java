package net.merayen.merasynth.net.util.flow;

import net.merayen.merasynth.buffer.AudioCircularBuffer;
import net.merayen.merasynth.netlist.Port;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;

/*
 * Class that abstracts away sending requests and responses of data for a single port.
 * Takes care if for example two nodes are connected to the input of one node,
 * and for example, the nodes request audio in different times.
 * Then this node will automatically buffer the audio and make it available for
 * the slower node.
 * 
 * TODO automatically delete "dead" channels (that are used for voices), to reduce amount
 * of data sent back and forth.
 */
class AudioInputFlow {
	public static interface IHandler {
		public void onReceive();
	}

	private final int BUFFER_SIZE = 44100 * 10; // Count of samples for each channel

	private Port port;
	private IHandler handler;
	public final AudioCircularBuffer buffer = new AudioCircularBuffer(BUFFER_SIZE); // Multi-channel, floating point, circular audio buffer

	public AudioInputFlow(IHandler handler) {
		this.handler = handler;
	}

	public void handle(DataPacket dp) {
		if(dp instanceof AudioResponse) {
			AudioResponse ar = (AudioResponse)dp;
			int channel_sample_count = ar.samples.length / ar.channels.length;
			for(int i = 0; i < ar.channels.length; i++)
				buffer.write(ar.channels[i], i * channel_sample_count, (i + 1) * channel_sample_count, ar.samples);;
			handler.onReceive();
		}
	}

	public int available() {
		return buffer.available();
	}
}
