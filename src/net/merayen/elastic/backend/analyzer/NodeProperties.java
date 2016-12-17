package net.merayen.elastic.backend.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;

public class NodeProperties {
	private static final Format[] NO_FORMATS = new Format[0];

	private final NetList netlist;

	/**
	 * Getters and setters for the analyzer.X-namespace
	 */
	public class Analyzer {
		private Analyzer() {}
		/**
		 * Which chain_id this port creates.
		 * Calculated by the Analyzer().
		 */
		public void setPortChainCreateId(Port port, int chain_id) {
			if(!isOutput(port))
				throw new RuntimeException("Should not happen");

			port.properties.put("analyzer.chain_id", chain_id);
		}

		public int getPortChainCreateId(Port port) {
			return (int)port.properties.get("analyzer.chain_id");
		}

		/**
		 * Contains all the chain_ids this port is included in.
		 * Both input- and output-ports.
		 * Modify the returned Set to make changes.
		 */
		@SuppressWarnings("unchecked")
		public Set<Integer> getPortChainIds(Port port) {
			if(port.properties.get("analyzer.chain_ids") == null)
				port.properties.put("analyzer.chain_ids", new HashSet<Integer>());

			return (Set<Integer>)port.properties.get("analyzer.chain_ids");
		}

		public void setDecidedFormat(Port port, Format format) {
			port.properties.put("analyzer.decided_format", format.name);
		}

		public Format getDecidedFormat(Port port) {
			return Format.get((String)port.properties.get("analyzer.decided_format"));
		}
	}

	public class Parameters {
		private Parameters() {}

		public void set(Node node, String key, Object value) {
			node.properties.put("p." + key, value);
		}

		public Object get(Node node, String key) {
			return node.properties.get("p." + key);
		}
	}

	public final Analyzer analyzer = new Analyzer();
	public final Parameters parameters = new Parameters();

	public NodeProperties(NetList netlist) {
		this.netlist = netlist;
	}

	public String getName(Node node) {
		return (String)node.properties.get("name");
	}

	public void setName(Node node, String name) {
		node.properties.put("name", name);
	}

	public int getVersion(Node node) {
		return (int)node.properties.get("version");
	}

	public void setVersion(Node node, int version) {
		node.properties.put("name", version);
	}

	public void setOutput(Port port) { // Can not be changed afterwards. Drop and recreate port
		port.properties.put("output", true);
	}

	public boolean isOutput(Port port) {
		if(port.properties.get("output") != null)
			return (boolean)port.properties.get("output");

		return false;
	}

	public void setFormat(Port port, Format format) {
		if(!isOutput(port))
			throw new RuntimeException("Only output-port can have a format set");

		port.properties.put("format", format.name);
	}

	public Format getFormat(Port port) {
		String format = (String)port.properties.get("format");

		return format != null ? Format.get(format) : null;
	}

	/**
	 * Nodes can spawn different voice-sessions on different output ports.
	 * The ident, which is just a string, is local for every node, and can be whatever-value that the
	 * node can use to create voices.
	 * If not set, or null, port is just a slave and can not create sessions.
	 * There can be multiple ports sharing the same ident.
	 */
	public void setPortChainIdent(Port port, String ident) {
		if(!isOutput(port))
			throw new RuntimeException("Only output-ports can have an ident to spawn voices");

		port.properties.put("chain_ident", ident);
	}

	public String getPortChainIdent(Port port) {
		if(!isOutput(port))
			return null;

		if(port.properties.containsKey("chain_ident"))
			return (String)port.properties.get("chain_ident");

		return null;
	}

	public void setChainConsumer(Port port) {
		if(isOutput(port))
			throw new RuntimeException("Port must be input to consume chains");

		port.properties.put("chain_consumer", true);
	}

	public boolean isChainConsumer(Port port) {
		if(isOutput(port))
			throw new RuntimeException("Port must be input to consume chains");

		if(port.properties.get("chain_consumer") != null)
			return (boolean)port.properties.get("chain_consumer");

		return false;
	}

	public List<String> getInputPorts(Node node) {
		List<String> result = new ArrayList<>();

		for(String port : netlist.getPorts(node))
			if(!isOutput(netlist.getPort(node, port)))
				result.add(port);

		return result;
	}

	public List<String> getOutputPorts(Node node) {
		List<String> result = new ArrayList<>();

		for(String port : netlist.getPorts(node))
			if(isOutput(netlist.getPort(node, port)))
				result.add(port);

		return result;
	}

	/**
	 * Sees if the port is "passive", meaning that it doesn't spawn or consume voices.
	 */
	public boolean isPassivePort(Port port) {
		boolean output = isOutput(port);
		if(output && getPortChainIdent(port) != null)
			return false;

		if(!output && isChainConsumer(port))
			return false;

		return true;
	}
}
