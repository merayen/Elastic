package net.merayen.elastic.system.intercom;

import net.merayen.elastic.netlist.NetList;

/**
 * Send this to the UI with a NetList to have it initialize from it.
 * Doing this will clear the UI and reinitialize it.
 * NodeCreatedMessage()
 */
public class UILoadFromNetListMessage {
	public final NetList netlist;

	public UILoadFromNetListMessage(NetList netlist) {
		this.netlist = netlist;
	}
}
