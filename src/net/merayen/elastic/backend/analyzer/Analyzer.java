package net.merayen.elastic.backend.analyzer;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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