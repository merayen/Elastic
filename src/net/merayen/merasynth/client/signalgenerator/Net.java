package net.merayen.merasynth.client.signalgenerator;

import net.merayen.merasynth.netlist.*;

import org.json.simple.JSONObject;

public class Net extends Node {
	/*
	 * Genererer sinuslyd
	 */
	
	public Net(Supervisor supervisor) {
		super(supervisor);
	}
	
	protected void onCreate() {
		addPort("output");
	}
	
	protected void onDump(JSONObject state) {
		state.put("test", 1337);
	}
	
	protected void onRestore(JSONObject state) {
		
	}
	
	public double update() {
		// TODO lag et sinussignal
		send("output", new DataPacket(1337));
		System.out.println("Updating");
		return DONE;
	}
}
