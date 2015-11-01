package net.merayen.merasynth.netlist.util.flow;

import java.util.ArrayList;

import net.merayen.merasynth.netlist.Port;
import net.merayen.merasynth.netlist.datapacket.AudioRequest;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;

/*
 * Class that abstracts away sending requests and responses of data.
 * Takes care if for example two nodes are connected to the input of one node,
 * and for example, the nodes request audio in different times.
 * Then this node will automatically buffer the audio and make it available for
 * the slower node.
 */
public class AudioFlowHelper {
	public static interface IHandler {
		public void onAudioReceive(String port);
	}

	private ArrayList<Port> input_ports = new ArrayList<>();
	private ArrayList<Port> output_ports = new ArrayList<>();
	private IHandler handler;
	private float[] buffer;
	private int sample_rate;

	public AudioFlowHelper(IHandler handler) {
		this.handler = handler;
	}

	public void addInputPort(Port port) {
		input_ports.add(port);
	}

	public void addOutputPort(Port port) {
		output_ports.add(port);
	}

	public void requestAudio(Port port, int sample_count) {
		if(!input_ports.contains(port))
			throw new RuntimeException(String.format("Port %s is not registered", port.name));

		
	}

	public void sendAudio(Port port, float[] data, int channels) {
		if(!output_ports.contains(port))
			throw new RuntimeException(String.format("Port %s is not registered", port.name));

		
	}

	public void handle(Port port, DataPacket dp) {
		if(dp instanceof AudioRequest && output_ports.contains(port))
			;

		if(dp instanceof AudioResponse && input_ports.contains(port))
			;
	}
	/*
	 * Returns the sample rate.
	 * The sample rate is automatically set by the requesting node.
	 * Use setSampleRate() if you are a output node.
	 */
	public int getSampleRate() {
		return sample_rate;
	}

	/*
	 * Set the sample_rate explicitly.
	 * Do this only if your node is an output node.
	 * Nodes that gets requested should not do this, as they have to obey
	 * the requested sample rate.
	 */
	public void setSampleRate(int sample_rate) {
		this.sample_rate = sample_rate;
	}
}
