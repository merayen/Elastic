package net.merayen.elastic.backend.architectures.local.nodes.adsr;

import net.merayen.elastic.netlist.*;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.process.AbstractProcessor;
import net.merayen.elastic.process.ProcessorController;

public class Net extends Node { // TODO Convert to AudioNode
	/*
	 * Genererer sinuslyd
	 */
	private ProcessorController<Processor> pc;

	public Net(NetList supervisor) {
		super(supervisor);
	}

	protected void onReceive(String port_name, DataPacket dp) {
		if(port_name.equals("output")) {

		}

		pc.handle(port_name, dp);
	}

	public double onUpdate() {
		return DONE;
	}
}
