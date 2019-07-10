package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

/**
 * Terminates resetting of NetList message stream.
 * ResetNetListMessage()
 * ...many netlist messages
 * ResetNetListDoneMessage()
 * 
 * Indicates that the NetList is finished restoring.
 * Used to disable events that shouldn't fire.
 */
public class FinishResetNetListMessage {
	public final String group_id; // Which group to reset. null if everything

	/**
	 * Reset all groups.
	 */
	public FinishResetNetListMessage() {
		group_id = null;
	}

	/**
	 * Reset just one group.
	 */
	public FinishResetNetListMessage(String group_id) {
		this.group_id = group_id;
	}
}
