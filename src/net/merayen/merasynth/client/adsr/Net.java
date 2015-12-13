package net.merayen.merasynth.client.adsr;

import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.AllowNewSessionsRequest;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.process.ProcessorController;

public class Net extends Node {
	/*
	 * Genererer sinuslyd
	 */
	private ProcessorController<Processor> pc;

	public Net(Supervisor supervisor) {
		super(supervisor);
		pc = new ProcessorController<Processor>(this, Processor.class);
	}

	protected void onReceive(String port_name, DataPacket dp) {
		if(port_name.equals("output")) {
			if(dp instanceof AllowNewSessionsRequest)
				send("input", dp);
		}

		pc.handle(port_name, dp);
	}

	public double onUpdate() {
		return DONE;
	}
}
