package net.merayen.elastic.backend.analyzer;

import java.util.List;

import net.merayen.elastic.netlist.NetList;

public class AnalyzeResult {
	public final List<NetList> chains;

	AnalyzeResult(List<NetList> chains) {
		this.chains = chains;
	}
}
