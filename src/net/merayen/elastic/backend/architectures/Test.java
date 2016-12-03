package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.util.Postmaster.Message;

public class Test {
	public static void test() {
		NetList netlist = new NetList();

		Node test1 = createTestNode(netlist);
		Node test2 = createTestNode(netlist);

		netlist.connect(test1, "output", test2, "input");

		Dispatch dispatch = new Dispatch(Architecture.LOCAL, new Dispatch.Handler() {
			@Override
			public void onMessageFromProcessing(Message message) {
				
			}
		});

		dispatch.launch(netlist, 8);

		long t = System.currentTimeMillis() + 3000;
		while(t > System.currentTimeMillis()) { // Let it run for 3 seconds
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		dispatch.stop();
	}

	private static Node createTestNode(NetList netlist) {
		Node n = netlist.createNode();
		n.properties.put("name", "test");
		n.properties.put("version", 100);

		n.properties.put("test", "Hello on you!");
		n.createPort("input");
		n.createPort("output");
		return n;
	}
}
