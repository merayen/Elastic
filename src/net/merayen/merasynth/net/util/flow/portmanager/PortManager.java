package net.merayen.merasynth.net.util.flow.portmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.Port;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.process.AbstractProcessor;

/**
 * TODO Maybe implement some cool statistics, like how much data is being transferred? Current buffer size etc 
 * PortManager helps communicating with connected nodes.
 */
public class PortManager {
	@SuppressWarnings("serial")
	public static class PortNotFound extends RuntimeException {
		public PortNotFound(String port_name) {
			super(String.format("Port '%s' not registered", port_name));
		}
	}

	private final List<ManagedPort> ports = new ArrayList<>();
	private final Node net_node;

	public PortManager(Node net_node) {
		this.net_node = net_node;
	}

	/**
	 * Only handles non-processor packets.
	 * @param port_name
	 */
	public void handle(String port_name, DataPacket dp) {
		if(dp.session_id != DataPacket.CONTROL_SESSION)
			return;

		get(port_name); // Throws exception if not registered, as PortManager must know all the ports

		// TODO
	}

	private ManagedPort get(String port_name) {
		for(ManagedPort mp : ports)
			if(mp.port_name.equals(port_name))
				return mp;

		throw new PortNotFound(port_name);
	}

	public List<ManagedPort> getPorts() {
		return new ArrayList<>(ports);
	}

	public boolean hasPort(String port_name) {
		for(ManagedPort mp : ports)
			if(mp.port_name.equals(port_name))
				return true;

		return false;
	}

	/**
	 * Call when any ports has been connected or disconnected.
	 * Resets the state of all ports and scans for new ports again.
	 */
	public void reset() {
		for(ManagedPort mp : ports)
			mp.reset();

		// Retrieve connected lines and reconfigure
		refreshPorts();
	}

	public void refreshPorts() {
		Set<String> node_has = new HashSet<>();
		List<Port> node_ports = net_node.getPorts();

		// Add ports we don't have
		for(Port p : node_ports) { // TODO threads might conflict
			node_has.add(p.name);
			try {
				get(p.name);
			} catch(PortNotFound e) {
				ports.add(new ManagedPort(this, p.name));
				System.out.printf("PortManager: ADDED %s\n", p.name);
			}
		}

		// Remove any ports that we have but does not exist on the node
		for(int i = ports.size() - 1; i > -1; i--) {
			if(!node_has.contains(ports.get(i).port_name)) {
				System.out.printf("PortManager: REMOVED %s\n", ports.get(i).port_name);
				ports.remove(i);
			}
		}

		// Update ports
		for(Port p : node_ports) {
			ManagedPort mp = get(p.name);
			mp.connection_count = net_node.getConnectionCount(p.name);
			System.out.printf("PortManager: UPDATED %s: %d connections\n", p.name, mp.connection_count);
		}
	}

	/**
	 * Returns a port manager made for processors
	 */
	public ProcessorPortManager getProcessorPortManager(AbstractProcessor processor) {
		return new ProcessorPortManager(this, processor, ports);
	}
}
