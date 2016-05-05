package net.merayen.elastic.backend.architectures.local.nodes.ui_test;

import net.merayen.elastic.netlist.*;
import net.merayen.elastic.netlist.util.AudioNode;

public class Net extends AudioNode {
	public Net(NetList supervisor) {
		super(supervisor, null);
	}

	public double onUpdate() {
		return DONE;
	}
}
