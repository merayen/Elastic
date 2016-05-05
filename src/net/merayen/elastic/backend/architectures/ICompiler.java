package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.netlist.NetList;

public abstract class ICompiler {
	public abstract AbstractExecutor compile(NetList netlist, int buffer_size);
}
