package net.merayen.merasynth.client.ui_test;

import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.util.AudioNode;

public class Net extends AudioNode {
	public Net(Supervisor supervisor) {
		super(supervisor, null);
	}

	public double onUpdate() {
		return DONE;
	}
}
