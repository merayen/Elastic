package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

/**
 * Little early to introduce this one, but it is sent from a group
 * node (a node that contains nodes).
 * This message can contain several levels deep GroupNodeMessage.
 * Is contained inside a NodeMessage() ?
 */
public class GroupNodeMessage extends Postmaster.Message {
	public GroupNodeMessage() {
		
	}
}
