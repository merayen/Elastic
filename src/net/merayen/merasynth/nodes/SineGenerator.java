package net.merayen.merasynth.nodes;

import net.merayen.merasynth.netlist.*;

import org.json.simple.JSONObject;

public class SineGenerator extends Node {
	/*
	 * Genererer sinuslyd
	 */
	
	public SineGenerator(Supervisor supervisor) {
		super(supervisor);
		addPort(new Port(this, "output"));
	}
	
	protected void freezeState(JSONObject state) {
		state.put("test", 1337);
	}
	
	public double update() {
		// TODO lag et sinussignal
		send("output", new DataPacket(1337));
		System.out.println("Updating");
		return DONE;
	}
}
