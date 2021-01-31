package net.merayen.elastic.system.intercom;

import net.merayen.elastic.backend.logicnodes.Format;

public class CreateNodePortMessage extends NetListMessage implements NodeMessage {
	public final String node_id;
	public final String port;
	public final boolean output;
	public final Format format;

	public CreateNodePortMessage(String node_id, String port, boolean output, Format format) {
		this.node_id = node_id;
		this.port = port;
		this.output = output;
		this.format = format; // Only for output-ports
	}

	/**
	 * Shortcut to create an input port.
	 */
	public CreateNodePortMessage(String node_id, String port) {
		this.node_id = node_id;
		this.port = port;
		this.output = false;
		this.format = null;
	}

	/**
	 * Shortcut to create an output port.
	 */
	public CreateNodePortMessage(String node_id, String port, Format format) {
		this.node_id = node_id;
		this.port = port;
		this.output = true;
		this.format = format;
	}

	public String toString() {
		return super.toString() + String.format(" (nodeId=%s, port=%s, output=%b)", node_id, port, output);
	}

	@Override
	public String getNodeId() {
		return node_id;
	}
}
