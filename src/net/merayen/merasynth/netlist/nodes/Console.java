package net.merayen.merasynth.netlist.nodes;

import net.merayen.merasynth.netlist.*;

import org.json.simple.JSONObject;

public class Console extends Node {
	/*
	 * Dustete konsoll som printer ut all møkka den får ut i konsoll.
	 */
	public Console(Supervisor supervisor) {
		super(supervisor);
		addPort("input");
	}
	
	protected void freezeState(JSONObject state) {
		
	}
	
	public double update() {
		DataPacket input = getPort("input").receive();
		if(input != null)
			print("Console node got input: %s", input.getData().toString());
		return DONE;
	}
}
