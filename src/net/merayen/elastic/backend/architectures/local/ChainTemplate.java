package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.netlist.NetList;

/**
 * A net of LocalNodes. NetList is divided up in several ChainTemplates, which
 * are kind of groups that can be made several instances of to represents
 * voices.
 */
class ChainTemplate {
	NetList netlist;

	ChainTemplate(NetList netlist) {
		this.netlist = netlist;
	}

	/**
	 * Creates a runnable chain of ourself.
	 */
	Chain createChain() {
		
	}
}
