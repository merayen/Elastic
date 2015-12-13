package net.merayen.merasynth.netlist.util.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.merayen.merasynth.netlist.datapacket.AudioRequest;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.RequestPacket;
import net.merayen.merasynth.netlist.datapacket.ResponsePacket;
import net.merayen.merasynth.process.AbstractProcessor;

/*
 * Buffers packets that contains or requests samples (MidiRequest, MidiResponse, AudioRequest, AudioResponse etc).
 * Helps node managing what they should reply on the right side.
 * This allows packets to be buffered on left side and selectively picked up by the node.
 */
public class PacketFlow {
	public static interface IHandler {
		public void onReceive(String port_name);
	}

	private static class PortFlowWrapper {
		public final String port_name;
		public final boolean output;
		private final PacketFlow pf;
		public final PortFlow port_flow = new PortFlow();

		public PortFlowWrapper(PacketFlow pf, String port_name, boolean output) {
			this.port_name = port_name;
			this.output = output;
			this.pf = pf;
		}

		public void handle(DataPacket dp) {
			if(output) {
				if(dp instanceof ResponsePacket) {
					System.out.printf("WARNING: Got a response packet on output port %s, node %s. Packet is ignored.", port_name, pf.processor.net_node.getClass().getName());
				} else if(dp instanceof RequestPacket) {
					port_flow.add(dp);
				}
			} else {
				if(dp instanceof RequestPacket) {
					System.out.printf("WARNING: Got a request packet on input port %s, node %s. Packet is ignored.", port_name, pf.processor.net_node.getClass().getName());
				} else if(dp instanceof ResponsePacket) {
					port_flow.add(dp);
				}
			}
		}
	}

	public static class PortNotFound extends RuntimeException {
		public PortNotFound(String msg) {
			super(msg);
		}
	}

	private final AbstractProcessor processor;
	private List<PortFlowWrapper> ports = new ArrayList<>();

	public PacketFlow(AbstractProcessor processor, IHandler handler) {
		this.processor = processor;
	}

	public void handle(String port_name, DataPacket dp) {
		try {
			get(port_name).add(dp);
		} catch (PortNotFound e) {} // We ignore ports we are not registered to handle
	}

	public void addInput(String port_name) {
		try {
			get(port_name);
			throw new RuntimeException("Port %s is already added");
		} catch (PortNotFound e) {}

		ports.add(new PortFlowWrapper(this, port_name, false));
	}

	public void addOutput(String port_name) {
		try {
			get(port_name);
			throw new RuntimeException("Port %s is already added");
		} catch (PortNotFound e) {}

		ports.add(new PortFlowWrapper(this, port_name, true));
	}

	private PortFlow get(String port_name) {
		for(PortFlowWrapper pf : ports)
			if(pf.port_name.equals(port_name))
				return pf.port_flow;

		throw new PortNotFound("Port " + port_name + " is not registered");
	}

	/**
	 * Gets the balance on a port.
	 * If number is negative on an output port, this is the amount of samples that needs to be processed.
	 * If number is positive on an input port, this is how many samples that is available on a port. 
	 */
	public long available(String port_name) {
		return get(port_name).available();
	}

	/**
	 * Consume data on an input port.
	 * Returns a list of packets for the count of samples.
	 * You need to check getBalance() on this input port first to see how much data you can retrieve.
	 * Throws exception if you try to consume more data than there is available.
	 */
	public DataPacket consume(String port_name, int max_sample_count) {
		return get(port_name).consume(max_sample_count);
	}
}
