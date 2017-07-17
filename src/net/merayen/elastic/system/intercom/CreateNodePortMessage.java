package net.merayen.elastic.system.intercom;

import net.merayen.elastic.backend.logicnodes.Format;

public class CreateNodePortMessage extends NetListMessage {
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

	public String toString() {
		return super.toString() + String.format(" (node_id=%s, port=%s, output=%b)", node_id, port, output);
	}
}
