package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

/**
 * Send to UI or backend to reset everything.
 * Any NetList or states are cleared.
 * 
 * You will need to send messages again to rebuild the state in UI and/or backend afterwards.
 */
public class ResetNetListMessage extends Postmaster.Message {
	public final String group_id; // Which group to reset. null if everything

	/**
	 * Reset all groups.
	 */
	public ResetNetListMessage() {
		group_id = null;
	}

	/**
	 * Reset just one group.
	 */
	public ResetNetListMessage(String group_id) {
		this.group_id = group_id;
	}
}
