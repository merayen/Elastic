package net.merayen.merasynth.client.ui_test;

import net.merayen.merasynth.netlist.*;

public class Net extends Node {
	public Net(Supervisor supervisor) {
		super(supervisor);
	}

	public double onUpdate() {
		return DONE;
	}
}
