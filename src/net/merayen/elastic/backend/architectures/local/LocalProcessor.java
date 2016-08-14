package net.merayen.elastic.backend.architectures.local;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.architectures.local.lets.FormatMaps;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.backend.nodes.Format;
import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;
import net.merayen.elastic.util.Postmaster;

public abstract class LocalProcessor {
	private boolean need_update = true;

	LocalNode localnode; // Our parent LocalNode that keeps us. TODO implement asynchronous message system
	int chain_id;
	int session_id;
	private int buffer_size;

	private final Map<String, Outlet> outlets = new HashMap<>();
	private final Map<String, Inlet> inlets = new HashMap<>();

	private boolean active = true;

	protected abstract void onInit();

	/**
	 * Gets called when this processor has received data or is just scheduled to run.
	 * Do all your processing in this method.
	 */
	protected abstract void onProcess();

	protected abstract void onMessage(Postmaster.Message message); // For NodeParameterChange()

	LocalProcessor() {}

	void LocalProcessor_setInfo(LocalNode localnode, int chain_id, int session_id) {
		this.localnode = localnode;
		this.chain_id = chain_id;
		this.session_id = session_id;
		this.buffer_size = localnode.buffer_size;
	}

	/**
	 * Creates all necessary Inlets and Outlets.
	 * Called when all processors *are created* and ready to be connected to each others.
	 */
	void wireUp() {
		NodeProperties properties = new NodeProperties(localnode.netlist);
		for(String port_name : properties.getOutputPorts(localnode.node)) { // We only connect output-ports as they do always have an input when connected
			List<Line> lines = localnode.netlist.getConnections(localnode.node, port_name);
			if(lines.size() == 0)
				continue; // We skip ports that are not connected

			Port port = localnode.netlist.getPort(localnode.node, port_name);

			// We only add port if it is part of the chain
			for(int port_chain_id : properties.analyzer.getPortChainIds(port))
				if(port_chain_id == chain_id) {
					addOutlet(port_name, properties.analyzer.getDecidedFormat(port));
					break;
				}
		}
	}

	private Outlet addOutlet(String port_name, Format format) {
		if(outlets.containsKey(port_name) || inlets.containsKey(port_name))
			throw new RuntimeException("Outlet/Inlet already on this processor");

		Class<? extends Outlet> cls = FormatMaps.outlet_formats.get(format);

		Outlet outlet;
		try {
			outlet = cls.getConstructor(int.class).newInstance(buffer_size);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}

		outlets.put(port_name, outlet);

		// Register inlets on nodes connected to this output-port
		List<Line> lines = localnode.netlist.getConnections(localnode.node, port_name);
		for(Line line : lines) {
			Node right_node;
			String right_port;
			if(line.node_a == localnode.node) {
				right_node = line.node_b;
				right_port = line.port_b;
			} else if(line.node_b == localnode.node) {
				right_node = line.node_a;
				right_port = line.port_a;
			} else {
				throw new RuntimeException("Should not happen");
			}

			LocalNode right_localnode = localnode.supervisor.getLocalNode(right_node.getID());
			LocalProcessor right_processor = localnode.supervisor.getProcessor(right_localnode, session_id);
			right_processor.addInlet(right_port, outlet);
		}

		return outlet;
	}

	private Inlet addInlet(String name, Outlet connected_outlet) {
		if(inlets.containsKey(name) || outlets.containsKey(name))
			throw new RuntimeException("Inlet/Outlet already on this processor");

		Format format = connected_outlet.getFormat();
		Class<? extends Inlet> cls = FormatMaps.inlet_formats.get(format);

		Inlet inlet;
		try {
			inlet = cls.getConstructor(Outlet.class).newInstance(connected_outlet);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}

		inlets.put(name, inlet);

		return inlet;
	}

	/**
	 * Called when there has been data made available on one or more ports.
	 * Returns true if any data has been written
	 */
	void doProcess() {
		if(need_update || dataAvailable())
			onProcess();

		need_update = false;
	}

	private boolean dataAvailable() {
		for(Inlet inlet : inlets.values())
			if(inlet.read < inlet.outlet.written)
				return true;

		return false;
	}

	protected Outlet getOutlet(String name) {
		return outlets.get(name);
	}

	protected Inlet getInlet(String name) {
		return inlets.get(name);
	}

	/**
	 * active() and inactive() function calls. Every processor must be aware of these.
	 * If one or more processor marks itself as active, the session is kept alive.
	 * Not until every processor in the chain has called inactive(), we will
	 * actually end the session.
	 * 
	 * It is important that every processor uses/is aware of this, as we might
	 * otherwise get stuck sessions.
	 * 
	 * E.g: A sine generator with MIDI input at frequency sets this to *true* whenever
	 * a key is pressed, and sets this to false at once on key up.
	 */
	protected void inactive() {
		active = false;
	}

	protected void active() {
		active = true;
	}

	public boolean isActive() {
		return active;
	}
}