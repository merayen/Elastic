package net.merayen.merasynth.client.signalgenerator;

import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.AudioRequest;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.util.flow.AudioFlowHelper;
import net.merayen.merasynth.netlist.util.flow.MidiFlowHelper;

import org.json.simple.JSONObject;

public class Net extends Node {
	private enum FrequencyInputMode {
		MIDI,
		Audio
	}

	private double pos = 0;
	private float frequency = 1000f;
	private int sample_rate = 44100; // TODO Set by event
	private FrequencyInputMode freq_input_mode = FrequencyInputMode.MIDI;

	private final AudioFlowHelper audio_flow_helper;
	private final MidiFlowHelper midi_flow_helper;

	public Net(Supervisor supervisor) {
		super(supervisor);

		audio_flow_helper = new AudioFlowHelper(this, new AudioFlowHelper.IHandler() {

			@Override
			public void onRequest(String port_name, int request_sample_count) {
				//audio_flow_helper.request("frequency", request_sample_count);
				// Only sends data on first channel for noe
				// TODO Send on multiple channels as given on the input port
				// TODO read data from "frequency" (if connected) and generate data dependent on that
				if(port_name.equals("output"))
					audio_flow_helper.send("output", new short[]{0}, generate(request_sample_count)); // Synchronous for now
			}

			@Override
			public void onReceive(String port_name) {
				// TODO Generate 

				System.out.printf("Data in: %d\n", audio_flow_helper.available("frequency"));
			}
		});

		midi_flow_helper = new MidiFlowHelper(this, new MidiFlowHelper.IHandler() {

			@Override
			public void onReceive(String port_name) {
				if(port_name.equals("frequency"))
					;//System.out.printf("Got MIDI data: %s\n", midi_flow_helper.available());
			}

			@Override
			public void onRequest(String port_name, int request_sample_count) {
				// We do not output MIDI data
			}
			
		});
	}

	@Override
	protected void onCreatePort(String port_name) {
		if(port_name.equals("frequency"))
			audio_flow_helper.addInput(this.getPort(port_name));
		
		if(port_name.equals("output"))
			audio_flow_helper.addOutput(this.getPort(port_name));
	}

	protected void onReceive(String port_name, DataPacket dp) {
		if(port_name.equals("output")) {
			if(dp instanceof AudioRequest) { // TODO use AudioFlowHelper for sending?
				AudioRequest request = (AudioRequest)dp;
				
				
			}
		}

		audio_flow_helper.handle(port_name, dp);
	}

	public double onUpdate() {
		// Doesn't process anything, unless explicitly asked for data
		return DONE;
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency;
	}

	private float[] generate(int sample_request_count) {
		if(frequency <= 0.09)
			throw new RuntimeException(String.format("Invalid frequency: %d Hz", frequency));

		float[] r = new float[sample_request_count]; // TODO use shared buffer

		for(int i = 0; i < r.length; i++)
			r[i] = (float)Math.sin(pos += ((Math.PI * 2.0) * frequency) / sample_rate);

		pos %= Math.PI*2*1000;

		return r;
	}
}
