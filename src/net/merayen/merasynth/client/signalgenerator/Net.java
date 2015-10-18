package net.merayen.merasynth.client.signalgenerator;

import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.AudioRequest;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;

import org.json.simple.JSONObject;

public class Net extends Node {
	/*
	 * Genererer sinuslyd
	 */
	private double pos = 0;
	private float frequency = 1000f;

	public Net(Supervisor supervisor) {
		super(supervisor);
	}

	protected void onReceive(String port, DataPacket dp) {
		if(port.equals("output")) {
			if(dp instanceof AudioRequest) {
				AudioRequest request = (AudioRequest)dp;
				AudioResponse response = new AudioResponse();
				response.sample_offset = 0;
				response.sample_rate = request.sample_rate;
				response.channels = 1;
				response.samples = generate(request.sample_rate, request.sample_count);
				send("output", response);
			}
		}
	}

	public double onUpdate() {
		// Doesn't process anything, unless explicitly asked for data
		return DONE;
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency;
	}

	private float[] generate(int sample_rate, int sample_request_count) {
		if(frequency <= 0.09)
			throw new RuntimeException(String.format("Invalid frequency: %d Hz", frequency));

		float[] r = new float[sample_request_count]; // TODO use shared buffer

		for(int i = 0; i < r.length; i++)
			r[i] = (float)Math.sin(pos += ((Math.PI * 2.0) * frequency) / sample_rate);

		pos %= Math.PI*2*1000;

		return r;
	}
}
