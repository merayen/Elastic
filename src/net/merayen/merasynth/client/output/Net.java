package net.merayen.merasynth.client.output;

import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.AudioRequest;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;

import org.json.simple.JSONObject;

public class Net extends Node {
	/*
	 * Genererer sinuslyd
	 */
	private int buffer_size = 1024;
	private int sample_rate = 44100;
	private long noe;

	public Net(Supervisor supervisor) {
		super(supervisor);
	}

	protected void onDump(JSONObject state) {
		state.put("test", 1337);
	}

	protected void onRestore(JSONObject state) {

	}

	protected void onReceive(String port, DataPacket dp) {
		if(port.equals("input")) {
			if(dp instanceof AudioResponse) {
				//System.out.printf("Got audio response! %s\n", ((AudioResponse)dp).samples);
				noe++;
				testRequest(); // Flooding
				if((noe % 1000000) == 0)
					System.out.println(noe);
			}
		}
	}

	public double onUpdate() {
		// TODO lag et sinussignal
		//send("output", new DataPacket(1337));
		//System.out.println("Updating output");
		return DONE;
	}

	// Functions called from GlueNode
	public void testRequest() {
		AudioRequest ar = new AudioRequest();
		ar.sample_offset = 0;
		ar.sample_request_count = buffer_size;
		ar.sample_request_rate = sample_rate;
		this.send("input", ar);
	}
}
