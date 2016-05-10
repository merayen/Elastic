package net.merayen.elastic.backend.analyzer;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

public class Walker {
	public interface Inspector {
		public void onInspect(Node[] path);
	}

	private final Inspector handler;

	private Walker(Inspector handler, NetList netlist) {
		this.handler = handler;
	}

	
	/**
	 * Walk from left to right, from *node*.
	 */
	/*public static void walk(NetList netlist, Node node, Inspector inspector) {
		
	}*/
}
