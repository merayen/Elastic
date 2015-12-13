package net.merayen.merasynth.netlist.util.flow;

import net.merayen.merasynth.netlist.Port;

public interface IFlowHelper {
	public static interface IHandler {
		/*
		 * Called every time data is received on a port.
		 * Your node will then need to send data
		 */
		public void onReceive(String port_name);
		public void onRequest(String port_name, int request_sample_count);
	}

	public void addInput(String port_name);
	public void addOutput(String port_name);
	public void request(String port_name, int sample_count);
}
