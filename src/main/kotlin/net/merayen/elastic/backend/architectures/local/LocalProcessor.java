package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.architectures.local.exceptions.SpawnLimitException;
import net.merayen.elastic.backend.architectures.local.lets.FormatMaps;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;
import net.merayen.elastic.system.intercom.ElasticMessage;
import net.merayen.elastic.util.AverageStat;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Base class for all processors.
 * A processor is created when a session is created.
 * A processor can also create child sessions below himself and manage those.
 * It is a processing unit (like a compressor, delay, eq etc).
 */
public abstract class LocalProcessor {
	LocalNode localnode; // Our parent LocalNode that keeps us. TODO implement asynchronous message system
	int session_id;
	private LocalProcessor parent;
	protected int buffer_size;
	protected int sample_rate;
	long process_time;
	int process_count;
	AverageStat<Long> process_times = new AverageStat<>(1000); // Used by Supervisor for statistics

	final Map<String, Outlet> outlets = new HashMap<>();
	final Map<String, Inlet> inlets = new HashMap<>();

	/**
	 * List of sessions created from this processor. Only for reference usage
	 */
	private final List<Integer> children_sessions = new ArrayList<>();

	protected abstract void onInit();

	/**
	 * Called before a frame is processed.
	 * Clear all your states and get ready to process next frame.
	 */
	protected abstract void onPrepare();

	/**
	 * Gets called when this processor has received data or is just scheduled to run.
	 * Do all your processing in this method.
	 * See if there is any data available.
	 */
	protected abstract void onProcess();

	protected abstract void onMessage(ElasticMessage message); // For NodeParameterChange() Really? Shouldn't it be interpreted by the LocalNode instead?

	protected abstract void onDestroy();

	void LocalProcessor_setInfo(LocalNode localnode, int session_id) {
		this.localnode = localnode;
		this.session_id = session_id;
		this.buffer_size = localnode.buffer_size;
		this.sample_rate = localnode.sample_rate;
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

			addOutlet(port_name, properties.analyzer.getDecidedFormat(port));
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

			outlet.connected_processors.add(right_processor);
		}

		return outlet;
	}

	private Inlet addInlet(String name, Outlet connected_outlet) {
		if(inlets.containsKey(name) || outlets.containsKey(name))
			throw new RuntimeException("Inlet/Outlet already on this processor");

		Inlet inlet;
		try {
			inlet = connected_outlet.getInletClass().getConstructor(Outlet.class).newInstance(connected_outlet);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}

		inlets.put(name, inlet);

		return inlet;
	}

	void init() {
		onInit();
	}

	protected LocalNode getLocalNode() {
		return localnode;
	}

	void doProcess() {
		process_count++;
		long t = System.nanoTime();
		onProcess();
		process_time += System.nanoTime() - t;
	}

	boolean frameFinished() {
		for(Inlet inlet : inlets.values())
			if(!inlet.satisfied())
				return false;

		for(Outlet outlet : outlets.values())
			if(!outlet.satisfied())
				return false;

		return true;
	}

	/**
	 * Schedule processing again.
	 * Usually called by inlets when they have received data or LocalNode wants this processor to react to something.
	 */
	public void schedule() {
		localnode.supervisor.schedule(this);
	}

	public Outlet getOutlet(String name) {
		return outlets.get(name);
	}

	public Inlet getInlet(String name) {
		return inlets.get(name);
	}

	void prepare(int sample_offset) {
		process_count = 0;
		// Jump the buffers to the offset when the voice was created
		for(Inlet inlet : inlets.values())
			inlet.reset(sample_offset);

		for(Outlet outlet : outlets.values())
			outlet.reset(sample_offset);

		onPrepare();
	}

	/**
	 * Convenience function to retrieve samples available.
	 * Checks all connected input-ports and returns the minimum available samples.
	 * Function is based on synchronous processing of the input-ports.
	 * If you are not doing that, then don't use this.
	 * If not inlets are connected at all, we return the full buffer size, as we are not dependent on any inlets.
	 */
	protected int available() {
		int available = buffer_size;

		for(Inlet inlet : inlets.values())
			available = Math.min(available, inlet.available());

		return available;
	}

	/**
	 * Get the parent processor that "owns" us.
	 */
	protected LocalProcessor getParent() {
		return parent;
	}

	/**
	 * Spawns a session for this node's children nodes.
	 * Returns the child session_id created.
	 */
	protected int spawnSession(int sample_offset) throws SpawnLimitException {
		int new_session_id = localnode.supervisor.spawnSession(localnode.node, sample_offset);

		for(LocalProcessor lp : localnode.supervisor.processor_list.getProcessors(new_session_id)) {
			lp.parent = this;
			lp.prepare(sample_offset);
		}

		return new_session_id;
	}

	protected void removeSession(int session_id) {
		if(session_id == this.session_id)
			throw new RuntimeException("LocalProcessor can not kill its own session");

		localnode.supervisor.removeSession(session_id);
	}

	public int getSessionID() {
		return session_id;
	}

	/**
	 * Return all session ids that have been created on this processor.
	 */
	public List<Integer> getChildrenSessionIDs() {
		return Collections.unmodifiableList(children_sessions);
	}

	protected long getSamplePosition() {
		return localnode.supervisor.samplePosition;
	}
}