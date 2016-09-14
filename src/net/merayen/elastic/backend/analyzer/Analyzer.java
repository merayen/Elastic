package net.merayen.elastic.backend.analyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;

/**
 * After the compiler has created our local NetList for execution,
 * we scan that NetList and sets properties on all the nodes,
 * depending how they are placed.
 * 
 * We also figure out which format is sent between the nodes.
 * 
 * This analyzer must be run before the NetList is sent to a processor architecture.
 */
public class Analyzer {
	private NetList netlist;

	private Analyzer(NetList netlist) {
		this.netlist = netlist;
		clean();
		new ChainAnalyzer(netlist);
	}

	public static void analyze(NetList netlist) {
		new Analyzer(netlist);
	}

	/**
	 * Cleans away existing properties
	 */
	private void clean() {
		for(Node node : netlist.getNodes()) {
			Iterator<Entry<String, Object>> iter = node.properties.entrySet().iterator();

			while(iter.hasNext())
				if(iter.next().getKey().startsWith("analyzer."))
					iter.remove();

			// Remove port-properties
			for(String port_name : netlist.getPorts(node)) {
				Port port = netlist.getPort(node, port_name);
				iter = port.properties.entrySet().iterator();

				while(iter.hasNext())
					if(iter.next().getKey().startsWith("analyzer."))
						iter.remove();
			}
		}
	}

	/**
	 * Analyzes which format is sent and received on every port.
	 * Nodes can later poll this to see which format to expect.
	 */
	private static void analyzeFormat(NetList netlist) {
		
	}
}

/**
 * Splits the NetList into several, individual executable chains.
 */
class ChainAnalyzer {
	private final NetList netlist;
	private final NodeProperties properties;

	private int chain_id_counter;

	ChainAnalyzer(NetList netlist) {
		this.netlist = netlist;
		this.properties = new NodeProperties(netlist);

		chainifySources();
		traverse();
	}

	/**
	 * Lays out all the chain_ids on source-ports.
	 */
	private void chainifySources() {
		for(Node node : netlist.getNodes()) {
			Map<String, Integer> idents = new HashMap<>(); // Port-idents for this node

			for(String port_name : properties.getOutputPorts(node)) {
				Port port = netlist.getPort(node, port_name);
				String ident = properties.getPortChainIdent(port);
				if(ident != null) {
					if(!idents.containsKey(ident))
						idents.put(ident, ++chain_id_counter);

					properties.analyzer.getPortChainIds(port).add(idents.get(ident));
				}
			}
		}
	}

	/**
	 * Traverses through all nodes and ports and assigns chain_ids to every port based on what chainifySources() did.
	 */
	private void traverse() {
		for(Node node : netlist.getNodes()) {
			for(String port_name : properties.getOutputPorts(node)) {
				Port port = netlist.getPort(node, port_name);
				String port_ident = properties.getPortChainIdent(port);
				if(port_ident != null) { // We only traverse nodes that spawns voices
					System.out.println("======= Chain " + port_ident + " =======");
					traversePort(node, port_name);
				}
			}
		}

		// TODO set all other ports to be chain_id=0, as they will be main-session
	}

	/**
	 * Spreads out a chain over nodes and their ports.
	 */
	private void traversePort(Node source_node, String source_port) {
		Set<Integer> new_chain_ids = properties.analyzer.getPortChainIds(netlist.getPort(source_node, source_port));

		Set<Port> chain_ports = explode(source_node, netlist.getPort(source_node, source_port));

		for(Port port : chain_ports)
			properties.analyzer.getPortChainIds(port).addAll(new_chain_ids);

	}

	private Set<Port> explode(Node source_node, Port source_port) {
		Set<Port> result = new HashSet<>();
		Set<Port> ports_traversed = new HashSet<>(); // List of all ports we are done traversing. Keeps us from going in loop
		Set<Node> nodes_traversed = new HashSet<>(); // All nodes we have traversed, so that we don't check nodes multiple times
		HashMap<Port, Node> to_check = new HashMap<>(); // Ports to check

		to_check.put(source_port, source_node);

		while(!to_check.isEmpty()) {
			Node node = null;
			Port port = null;
			String port_name = null;
			for(Entry<Port, Node> x : to_check.entrySet()) {// Ugh, only way to get random from map?
				port = x.getKey();
				node = to_check.remove(port);
				port_name = netlist.getPortName(node, port);
				break;
			}

			ports_traversed.add(port);

			List<Line> lines = netlist.getConnections(node, port_name);

			// Add all connected ports we should check
			for(Line line : lines) {
				Port port_a = netlist.getPort(line.node_a, line.port_a);
				Port port_b = netlist.getPort(line.node_b, line.port_b);

				result.add(port_a);
				result.add(port_b);

				if(!ports_traversed.contains(netlist.getPort(line.node_a, line.port_a)))
					to_check.put(port_a, line.node_a);
				else if(!ports_traversed.contains(netlist.getPort(line.node_b, line.port_b)))
					to_check.put(port_b, line.node_b);
			}

			if(!nodes_traversed.contains(node) && properties.isPassivePort(port)) { // It is a passive port, means we check the ports on the nodes to spread the chain
				// Scan all the ports on the node to see if chain can be spread through it
				for(String p : netlist.getPorts(node)) {
					Port node_port = netlist.getPort(node, p);
					if(!ports_traversed.contains(node_port))
						if(properties.isPassivePort(node_port)) // Only spread the chain through passive ports
							to_check.put(node_port, node);
				}

				nodes_traversed.add(node); // Only scan nodes once
			}
		}

		return result;
	}
}