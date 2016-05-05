package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

/**
 * Sent to backend to request creation of this node
 */
public class CreateNodeMessage extends Postmaster.Message {

	public final String name;
	public final Integer version;

	public CreateNodeMessage(String name, Integer version) {
		this.name = name;
		this.version = version;
	}
}
