package net.merayen.elastic.system.intercom;

import net.merayen.elastic.backend.nodes.Format;
import net.merayen.elastic.util.Postmaster;

public class CreateNodePortMessage extends Postmaster.Message {
	public final String node_id;
	public final String port;
	public final boolean output;
	public final Format[] format;
	public final String chain_ident; // TODO rename to chain_ident

	public CreateNodePortMessage(String node_id, String port, boolean output, Format[] format, String chain_ident) {
		this.node_id = node_id;
		this.port = port;
		this.output = output;
		this.format = format;
		this.chain_ident = chain_ident;
	}
}
