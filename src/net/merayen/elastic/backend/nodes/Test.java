package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.netlist.NetList;

public class Test {
	public static void test() {
		NetList netlist = new NetList();
		LogicNodeList list = new LogicNodeList(netlist);

		String test1 = list.createNode("test", 100);
		String test2 = list.createNode("test", 100);

		netlist.connect(test1, "output", test2, "input");

	}
}
