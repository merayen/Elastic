package net.merayen.merasynth.client.delay;

import net.merayen.merasynth.buffer.AudioCircularBuffer;
import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.DataPacket;

public class Net extends Node {
	int sample_delay = 0;
	AudioCircularBuffer buffer;
	int SAMPLE_RATE = 44100; // TODO get sample rate from event

	public Net(Supervisor supervisor) {
		super(supervisor);

		/*audio_flow_helper = new AudioFlowHelper(this, new AudioFlowHelper.IHandler() {

			@Override
			public void onReceive(String port_name) {
				System.out.printf("Got answer from %s: Available=%s\n", port_name, audio_flow_helper.available(port_name));
			}

			@Override
			public void onRequest(String port_name, int request_sample_count) {
				// Passes on the request to our input nodes
				audio_flow_helper.request("input_a", request_sample_count);
				audio_flow_helper.request("input_b", request_sample_count);
				audio_flow_helper.request("fac", request_sample_count);
			}
		});*/
	}

	@Override
	protected void onCreatePort(String port_name) {
		/*System.out.printf("%s: %s\n", port_name, getPort(port_name));
		if(port_name.equals("input"))
			audio_flow_helper.addInput(this.getPort("input"));
		else if(port_name.equals("output"))
			audio_flow_helper.addOutput(this.getPort("output"));*/
	}

	protected void onReceive(String port_name, DataPacket dp) {
		//audio_flow_helper.handle(port_name, dp);
	}

	public double onUpdate() {
		/*if(test < System.currentTimeMillis()) {
			try {
				audio_flow_helper.request("input_a", 64);
				audio_flow_helper.request("input_b", 64);
				System.out.println("Requested");
			} catch (AudioFlowHelper.PortNotFound e) {
				// Meh
			}
			test = System.currentTimeMillis() + 1000;
		}*/
		return DONE;
	}

	public void changeDelay(float seconds) {
		sample_delay = (int)(seconds * SAMPLE_RATE);
		if(sample_delay > 0 ) {
			if(buffer == null || buffer.getSize() != sample_delay) {
				System.out.println(sample_delay);
				buffer = new AudioCircularBuffer(sample_delay);
			}
		}else {
			System.out.println("Inverted delay");
			buffer = null;
		}
		
	}
}
