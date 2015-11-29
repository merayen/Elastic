package net.merayen.merasynth.netlist.util.flow;

import java.util.HashMap;

import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.Port;
import net.merayen.merasynth.netlist.datapacket.MidiRequest;
import net.merayen.merasynth.netlist.util.flow.AudioFlowHelper.PortNotFound;

public class MidiFlowHelper implements IFlowHelper {

	private final HashMap<String,MidiInputFlow> inputs = new HashMap<>();
	private final HashMap<String,Port> outputs = new HashMap<>();
	private Node net_node;
	private IHandler handler;

	public MidiFlowHelper(Node net_node, IHandler handler) {
		this.net_node = net_node;
		this.handler = handler;
	}

	@Override
	public void addInput(Port port) {
		MidiFlowHelper self = this;
		inputs.put(port.name, new MidiInputFlow(new MidiInputFlow.IHandler() {
			@Override
			public void onReceive() {
				self.handler.onReceive(port.name);
			}
		}));
	}

	@Override
	public void addOutput(Port port) {
		outputs.put(port.name, port);
	}

	public void send() {
		
	}

	@Override
	public void request(String port_name, int sample_count) {
		if(inputs.get(port_name) == null)
			throw new PortNotFound(port_name);

		MidiRequest mr = new MidiRequest();
		mr.sample_count = sample_count;
		net_node.send(port_name, mr);
	}

	/*
	 * Returns the count of MIDI packets available.
	 */
	public void available() {
		// TODO
	}

	/*
	 * Returns the MIDI input buffer
	 */
	//public MidiInputFlow
}
