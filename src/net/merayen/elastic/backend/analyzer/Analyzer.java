package net.merayen.elastic.backend.analyzer;

import net.merayen.elastic.netlist.NetList;

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
	private Analyzer() {}

	/**
	 * Updates all the nodes with new analyzis properties.
	 */
	public static void analyze(NetList netlist) {
		AnalyzeResult ar = new AnalyzeResult();
		analyzeState(netlist); // ???
		analyzeFormat(netlist);
		analyzeChains(ar, netlist);
	}

	/**
	 * Analyzes in what state the nodes is in, like, a session generator etc.
	 */
	private static void analyzeState(NetList netlist) {
		
	}

	/**
	 * Analyzes which format is sent and received on every port.
	 * Nodes can later poll this to see which format to expect.
	 */
	private static void analyzeFormat(NetList netlist) {
		
	}

	/**
	 * Analyzes all the sessions.
	 */
	private static void analyzeChains(AnalyzeResult ar, NetList netlist) {
		
	}
}
