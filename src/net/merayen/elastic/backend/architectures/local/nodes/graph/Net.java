package net.merayen.elastic.backend.architectures.local.nodes.graph;

import java.util.HashMap;

import net.merayen.elastic.netlist.*;
import net.merayen.elastic.netlist.datapacket.AudioResponse;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.DataRequest;
import net.merayen.elastic.util.AverageStat;

public class Net extends Node {
	/*
	 * Genererer sinuslyd
	 */

	public Net(NetList supervisor) {
		super(supervisor);
	}

	protected void onReceive(String port, DataPacket dp) {
		// TODO
	}

	public double onUpdate() {
		return DONE;
	}
}
