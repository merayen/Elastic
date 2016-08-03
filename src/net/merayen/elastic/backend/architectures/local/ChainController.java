package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.netlist.NetList;

public class ChainController {
	private final NetList netlist;
	private final List<ChainTemplate> templates = new ArrayList<>();
	private final List<Chain> running_chains = new ArrayList<>(); 

	ChainController(NetList netlist) {
		this.netlist = netlist;
		build();
	}

	private void build() {
		
	}
}
