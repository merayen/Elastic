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
	private boolean wired_up; // Is set to true after all outlets and inlets are wired up

	LocalNode localnode; // Our parent LocalNode that keeps us. TODO implement asynchronous message system
	int chain_id;
	int session_id;
	private int buffer_size;

	private final Map<String, Outlet> outlets = new HashMap<>();
	private final Map<String, Inlet> inlets = new HashMap<>();

	boolean keep_alive = true;

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
	void wireUp__delete() {
		if(wired_up)
			return;

		// Add inlets and outlets
		NodeProperties properties = new NodeProperties(localnode.netlist);
		for(String port_name : localnode.netlist.getPorts(localnode.node)) {
			List<Line> lines = localnode.netlist.getConnections(localnode.node, port_name);
			if(lines.size() == 0)
				continue; // We skip ports that are not connected

			Port port = localnode.netlist.getPort(localnode.node, port_name);
			Format format = properties.analyzer.getDecidedFormat(port);

			if(properties.isOutput(port)) {
				addOutlet(port_name, format);
			} else {
				// Figures out which port is connected
				if(lines.size() != 1)
					throw new RuntimeException("Input ports should only have 1 connection");

				Line line = lines.get(0);
				Node left_node;
				String left_port;
				if(line.node_a == localnode.node) {
					left_node = line.node_b;
					left_port = line.port_b;
				} else {
					left_node = line.node_a;
					left_port = line.port_a;
				}

				LocalNode left_localnode = localnode.supervisor.getLocalNode(left_node.getID());
				if(left_localnode == null)
					throw new RuntimeException("Should not happen");

				LocalProcessor lp = localnode.supervisor.getProcessor(left_localnode, session_id);
				if(lp == null)
					throw new RuntimeException("wireUp() has probably been called before all processors has been created");

				lp.addInlet(port_name, lp.getOutlet(left_port));
			}
		}

		wired_up = true;
	}

	void wireUp() {
		if(wired_up)
			return;

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

		wired_up = true;
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
			if(line.node_a == localnode.node)
				right_node = line.node_b;
			else if(line.node_b == localnode.node)
				right_node = line.node_a;
			else
				throw new RuntimeException("Should not happen");

			//localnode.supervisor.getLocalNode(line.node_b.getID()).addInlet(line.node_b, port_name, outlet);
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
	 * Every processor should set this.
	 * If one or more processor has this active, the session is kept alive.
	 * Not until every processor in the chain sets this to false, we will
	 * actually end the session.void
	 * 
	 * It is important that every processor uses/is aware of this, as we might
	 * otherwise get stuck sessions.
	 * 
	 * E.g: A sine generator with MIDI input at frequency sets this to *true* whenever
	 * a key is pressed, and sets this to false at once on key up.
	 * 
	 * Other nodes might never set this to true and is just a slave. 
	 */
	protected void setKeepAlive(boolean b) {
		keep_alive = b;
	}

	public boolean isKeepAlive() {
		return keep_alive;
	}
}