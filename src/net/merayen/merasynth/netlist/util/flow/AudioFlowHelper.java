package net.merayen.merasynth.netlist.util.flow;

import java.util.HashMap;

import net.merayen.merasynth.buffer.AudioCircularBuffer;
import net.merayen.merasynth.netlist.datapacket.AudioRequest;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.process.AbstractProcessor;

/*
 * Helps managing flowing of audio.
 */
public class AudioFlowHelper implements IFlowHelper {
	public static class PortNotFound extends RuntimeException {
		public PortNotFound(String port_name) {
			super(port_name);
		}
	}

	private final IHandler handler;
	private final AbstractProcessor processor;
	private final HashMap<String,AudioInputFlow> inputs = new HashMap<>();
	private final HashMap<String,AudioOutputFlow> outputs = new HashMap<>();

	public AudioFlowHelper(AbstractProcessor processor, IHandler handler) {
		this.processor = processor;
		this.handler = handler;
	}

	public void addInput(String port_name) {
		AudioFlowHelper self = this;
		inputs.put(port_name, new AudioInputFlow(new AudioInputFlow.IHandler() {
			@Override
			public void onReceive() {
				self.handler.onReceive(port_name);
			}
		}));
	}

	public void addOutput(String port_name) {
		outputs.put(port_name, new AudioOutputFlow());
	}

	/*
	 * Sends data on an output port.
	 */
	public void send(String port_name, short[] channels, float[] samples) {
		if(outputs.get(port_name) == null)
			throw new PortNotFound(port_name);

		AudioResponse response = new AudioResponse();
		response.channels = channels;
		response.samples = samples;
		response.sample_count = response.samples.length;
		processor.send(port_name, response);
	}

	public void request(String port_name, int sample_count) {
		if(inputs.get(port_name) == null)
			throw new PortNotFound(port_name);

		AudioRequest ar = new AudioRequest();
		ar.sample_count = sample_count;
		processor.send(port_name, ar);
	}

	/*
	 * Handles receiving of DataPacket.
	 * Call this in your onReceive()-function.
	 */
	public void handle(String port_name, DataPacket dp) {
		if(dp instanceof AudioResponse) {
			AudioInputFlow aif = inputs.get(port_name);

			if(aif != null)
				aif.handle((AudioResponse)dp);

		} else if(dp instanceof AudioRequest) {
			AudioRequest ar = (AudioRequest)dp;

			if(outputs.get(port_name) != null)
				handler.onRequest(port_name, ar.sample_count);
		}
	}

	/*
	 * Returns channels available on a port.
	 */
	public AudioCircularBuffer getInputBuffer(String port_name) {
		AudioInputFlow aif = inputs.get(port_name);
		if(aif == null)
			throw new PortNotFound(port_name);

		return aif.buffer;
	}

	/*
	 * Returns how many samples are available on a certain port.
	 */
	public int available(String port_name) {
		AudioInputFlow aif = inputs.get(port_name);
		if(aif == null)
			throw new PortNotFound(port_name);

		return aif.available();
	}
}
