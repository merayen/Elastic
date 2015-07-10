package net.merayen.merasynth.system;

import org.json.simple.JSONObject;

import net.merayen.merasynth.NodeSystem;

public class Restoration {
	/*
	 * Handles restoring a NodeSystem from a dump.
	 */
	private NodeSystem node_system;

	public Restoration(NodeSystem node_system, JSONObject dump) {
		this.node_system = node_system;
	}
}
