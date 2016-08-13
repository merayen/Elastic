package net.merayen.elastic.backend.analyzer;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.nodes.Format;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;

public class NodeProperties {
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
		 */
		public void setPortChainIds(Port port, int[] chain_ids) {
			port.properties.put("analyzer.chain_ids", chain_ids);
		}

		public int[] getPortChainIds(Port port) {
			return (int[])port.properties.get("analyzer.chain_ids");
		}

		public void setDecidedFormat(Port port, Format format) {
			port.properties.put("analyzer.decided_format", format.toString());
		}

		public Format getDecidedFormat(Port port) {
			return Format.get((String)port.properties.get("analyzer.decided_format"));
		}
	}

	public final Analyzer analyzer = new Analyzer();

	public NodeProperties(NetList netlist) {
		this.netlist = netlist;
	}

	public void setOutput(Port port, boolean yes) {
		port.properties.put("output", yes);
	}

	public boolean isOutput(Node node, String port) {
		Port p = netlist.getPort(node, port);
		if(p == null)
			throw new RuntimeException("Port " + port + " does not exist");

		return (boolean)p.properties.get("output");
	}

	public boolean isOutput(Port port) {
		if(port.properties.containsKey("output"))
			return (boolean)port.properties.get("output");

		return false;
	}

	public void setAvailableFormats(Port port, Format[] formats) {
		if(formats.length == 0)
			throw new RuntimeException("All ports need to have a format available");

		if(formats.length > 1 && isOutput(port))
			throw new RuntimeException("Output ports can only have 1 output format available");

		port.properties.put("available_formats", formats);

		if(isOutput(port))
			analyzer.setDecidedFormat(port, formats[0]); // Output ports can only have 1 decided format anyway
	}

	public Format[] getAvailableFormats(Port port) {
		return (Format[])port.properties.get("available_formats");
	}

	/**
	 * Nodes can spawn different voice-sessions on different output ports.
	 * The ident, which is just a string, is local for every node, and can be whatever-value that the
	 * node can use to create voices.
	 * If not set, or null, port is just a slave and can not create sessions.
	 */
	public void setPortChainIdent(Port port, String ident) {
		if(!isOutput(port))
			throw new RuntimeException("Only output ports can spawn voices");

		port.properties.put("chain_ident", ident);
	}

	public String getPortChainIdent(Port port) {
		if(!isOutput(port))
			throw new RuntimeException("Only output ports can spawn voices");

		if(port.properties.containsKey("chain_id"))
			return (String)port.properties.get("chain_id");

		return null;
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
