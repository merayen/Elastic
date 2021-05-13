package net.merayen.elastic.backend.analyzer;

import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO Merge this class into NetListUtil?
 */
public class NodeProperties {
	private final NetList netlist;

	/**
	 * Getters and setters for the analyzer.X-namespace
	 */
	public static class Analyzer {
		private Analyzer() {}

		public void setDecidedFormat(Port port, Format format) {
			port.properties.put("analyzer.decided_format", format.name);
		}

		public Format getDecidedFormat(Port port) {
			return Format.get((String)port.properties.get("analyzer.decided_format"));
		}
	}

	public static class Parameters {
		private Parameters() {}

		public void set(Node node, String key, Object value) {
			node.properties.put("p." + key, value);
		}

		public Object get(Node node, String key) {
			return node.properties.get("p." + key);
		}

		public Map<String, Object> getAll(Node node) {
			Map<String, Object> result = new HashMap<>();
			for(Map.Entry<String, Object> x : node.properties.entrySet())
				if(x.getKey().startsWith("p."))
					result.put(x.getKey(), x.getValue());

			return result;
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
		return ((Number)node.properties.get("version")).intValue();
	}

	public void setVersion(Node node, int version) {
		node.properties.put("version", version);
	}

	public void setParent(Node node, String parent) {
		node.properties.put("parent", parent);
	}

	public void setParent(Node node, Node parent) {
		node.properties.put("parent", parent.getID());
	}

	public String getParent(Node node) {
		if(!node.properties.containsKey("parent"))
			return null;

		return (String)node.properties.get("parent");
	}

	public void setOutput(Port port) { // Can not be changed afterwards. Drop and recreate port
		port.properties.put("output", true);
	}

	public boolean isOutput(Port port) {
		if (port == null)
			throw new NullPointerException("port can not be null");

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

	public Format getFormat(Node node, String port) {
		return getFormat(netlist.getPort(node, port));
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
}
