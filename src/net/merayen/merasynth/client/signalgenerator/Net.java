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
	public Net(Supervisor supervisor) {
		super(supervisor);
	}

	protected void onDump(JSONObject state) {
		state.put("test", 1337);
	}

	protected void onRestore(JSONObject state) {

	}

	protected void onReceive(String port, DataPacket dp) {
		if(port.equals("output")) {
			if(dp instanceof AudioRequest) {
				AudioRequest request = (AudioRequest)dp;
				AudioResponse ar = new AudioResponse();
				ar.sample_offset = 0;
				ar.sample_rate = request.sample_request_rate;
				ar.samples = new float[1024];
				send("output", ar);
			}
		}
	}

	public double onUpdate() {
		// TODO lag et sinussignal
		//send("output", new DataPacket(1337));
		return DONE;
	}
}
