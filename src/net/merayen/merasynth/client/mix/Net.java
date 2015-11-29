package net.merayen.merasynth.client.mix;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.merayen.merasynth.buffer.AudioCircularBuffer;
import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.util.flow.AudioFlowHelper;

public class Net extends Node {
	/*
	 * Genererer sinuslyd
	 */
	AudioFlowHelper audio_flow_helper;
	double test;
	float fac_value = 0.5f; // TODO Get this from UINode
	Mixer mixer;

	public Net(Supervisor supervisor) {
		super(supervisor);

		audio_flow_helper = new AudioFlowHelper(this, new AudioFlowHelper.IHandler() {

			@Override
			public void onReceive(String port_name) {
				System.out.printf("Got answer from %s: Available=%s\n", port_name, audio_flow_helper.available(port_name));

				// This is VERY experimental. Need to decide how nodes should react when a node on the left does not respond in time
				mixer.mix(fac_value);
			}

			@Override
			public void onRequest(String port_name, int request_sample_count) {
				// Passes on the request to our input nodes
				audio_flow_helper.request("input_a", request_sample_count);
				audio_flow_helper.request("input_b", request_sample_count);
				audio_flow_helper.request("fac", request_sample_count);
			}
		});

		mixer = new Mixer(audio_flow_helper);
	}

	@Override
	protected void onCreatePort(String port_name) {
		System.out.printf("%s: %s\n", port_name, getPort(port_name));
		if(port_name.equals("input_a"))
			audio_flow_helper.addInput(this.getPort("input_a"));
		else if(port_name.equals("input_b"))
			audio_flow_helper.addInput(this.getPort("input_b"));
		else if(port_name.equals("fac"))
			audio_flow_helper.addInput(this.getPort("fac"));
		else if(port_name.equals("output"))
			audio_flow_helper.addOutput(this.getPort("output"));
	}

	protected void onReceive(String port_name, DataPacket dp) {
		audio_flow_helper.handle(port_name, dp);
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
		return 0.2;
	}
}
