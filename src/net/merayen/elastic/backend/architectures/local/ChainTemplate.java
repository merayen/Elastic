package net.merayen.elastic.backend.architectures.local;

import java.util.List;

import net.merayen.elastic.backend.analyzer.Util;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

/**
 * A net of LocalNodes. NetList is divided up in several ChainTemplates, which
 * are kind of groups that can be made several instances of to represents
 * voices.
 */
class ChainTemplate {
	NetList netlist;
	final List<LocalNode> localnodes;
	final Util util;

	ChainTemplate(NetList netlist, List<LocalNode> localnodes) {
		this.netlist = netlist;
		this.localnodes = localnodes;
		this.util = new Util(netlist);
	}

	/**
	 * Creates a runnable chain of ourself.
	 */
	Chain createChain() {
		List<Node> nodes = netlist.getNodes();

		LocalProcessor[] processors = new LocalProcessor[nodes.size()];

		for(int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			processors[i] = createLocalProcessor(n);

			// Create inlets and outlets
			for(String p : netlist.getPorts(n)) {
				if(util.isOutput(n, p))
					processors[i].addOutlet(p, util.getPolyNo(n, p));
				else
					processors[i].addInlet(p, util.getPolyNo(n, p));
			}
		}

		// Connect the LocalProcessors together
		for(int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			
		}

		Chain chain = new Chain(processors);
		return chain;
	}

	private LocalProcessor createLocalProcessor(Node node) {
		for(LocalNode ln : localnodes) {
			if(ln.getID().equals(node.getID())) {
				LocalProcessor lp = ln.launchProcessor();
				return lp;
			}
		}

		throw new RuntimeException("Could not find LocalNode for node with id " + node.getID());
	}
}
