package net.merayen.elastic.backend.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

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

	public Analyzer(NetList netlist) {
		this.netlist = netlist;
	}

	/**
	 * Updates all the nodes with new analyzis properties.
	 */
	public AnalyzeResult analyze() {
		return new AnalyzeResult(
			new ChainAnalyzer(netlist).analyze()
		);
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
	private NetList netlist;
	private Traverser traverser;

	ChainAnalyzer(NetList netlist) {
		this.netlist = netlist;
		this.traverser = new Traverser(netlist);
	}

	List<NetList> analyze() {
		List<NetList> chains = new ArrayList<>();

		List<NetList> groups = traverser.getGroups();

		for(NetList group : groups)
			chainifyGroup(chains, group);

		return chains;
	}

	private void chainifyGroup(List<NetList> chains, NetList group) {
		List<Node> leftmost = traverser.getLeftMost(group.getNodes().get(0));

		if(leftmost.size() == 0) { // Is an infinite loop, this is a chain by itself
			chains.add(group);
			return;
		}

		walkRightward(leftmost.get(0));
	}

	/**
	 * Walks from a leftmost node and rightward.
	 */
	private void walkRightward(Node leftnode) {
		Walker walker = new Walker(netlist, leftnode);

		
	}
}