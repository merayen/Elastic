package net.merayen.merasynth.client.adsr;

import java.util.HashMap;

import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.AudioRequest;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.util.AverageStat;

public class Net extends Node {
	/*
	 * Genererer sinuslyd
	 */

	public Net(Supervisor supervisor) {
		super(supervisor);
	}

	protected void onReceive(String port, DataPacket dp) {
		// TODO
	}

	public double onUpdate() {
		return DONE;
	}
}
