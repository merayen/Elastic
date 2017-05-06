package net.merayen.elastic.backend.analyzer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.merayen.elastic.backend.logicnodes.Format;
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
		new FormatDecision(netlist);
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
		Node[] nodes = (Node[])netlist.getNodes().toArray(new Node[0]);

		// We sort so that the chain_id_counter is somewhat predictable between several runs.
		// This is only for Test.java, so that it knows which chain_id the specific nodes has gotten.
		Arrays.sort(nodes, (a,b) -> a.getID().compareTo(b.getID()));

		for(Node node : nodes) {
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

		// Assign all ports that have connections that do not have any chains, to the "main" chain 0.
		for(Node node : netlist.getNodes()) {
			for(String port_name : netlist.getPorts(node)) {
				if(!netlist.getConnections(node, port_name).isEmpty()) {
					Port port = netlist.getPort(node, port_name);
					Set<Integer> chain_ids = properties.analyzer.getPortChainIds(port);
					if(chain_ids.isEmpty())
						chain_ids.add(0);
				}
			}
		}
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

				if(!properties.isOutput(port_a) || properties.getPortChainIdent(port_a) == null) // active output-ports can *not* have chains spread through them!
					result.add(port_a);

				if(!properties.isOutput(port_b) || properties.getPortChainIdent(port_b) == null) // ...look above
					result.add(port_b);

				if(!ports_traversed.contains(netlist.getPort(line.node_a, line.port_a)) && properties.isPassivePort(port_a))
					to_check.put(port_a, line.node_a);
				else if(!ports_traversed.contains(netlist.getPort(line.node_b, line.port_b)) && properties.isPassivePort(port_b))
					to_check.put(port_b, line.node_b);
			}

			if(!nodes_traversed.contains(node) && properties.isPassivePort(port)) { // It is a passive port, means we check the ports on the nodes to spread the chain
				// Scan all the ports (left and right) on the node to see if chain can spread through it
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

class FormatDecision {
	private final NetList netlist;
	private final NodeProperties np;

	FormatDecision(NetList netlist) {
		this.netlist = netlist;
		this.np = new NodeProperties(netlist);

		scan();
	}

	private void scan() {
		for(Node n : netlist.getNodes()) {
			for(String p : netlist.getPorts(n)) {
				Port port = netlist.getPort(n, p);
				if(np.isOutput(port)) {
					Format format = np.getFormat(port);
					if(format != null) {

						List<Line> lines = netlist.getConnections(n, p);

						if(lines.size() > 0) { // If output-port is not connected, we don't set any output-formats on it, as it wont be used at all
							// Set the decided format for the output-port
							np.analyzer.setDecidedFormat(port, format);
	
							// Set the decided format for all the receiving input-ports based on the output, if possible
							for(Line l : lines)
								if(n == l.node_a)
									np.analyzer.setDecidedFormat(netlist.getPort(l.node_b, l.port_b), format);
								else if(n == l.node_b)
									np.analyzer.setDecidedFormat(netlist.getPort(l.node_a, l.port_a), format);
								else
									throw new RuntimeException("Should not happen");
						}
					} else {
						System.out.printf("Error: Analyzer.FormatDecision expected a format on the output-port. Port will not function. Node %s, port %s\n", np.getName(n), p);
					}
				}
			}
		}
	}
}