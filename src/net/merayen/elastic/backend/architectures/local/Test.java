package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.backend.nodes.Format;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;
import net.merayen.elastic.util.Postmaster.Message;

/**
 * Emulates the Analyzer() and fakes nodes for testing Supervisor().
 */
public class Test {
	private Test() {}

	private static void no() {
		throw new RuntimeException("Nope");
	}

	private static void initLocalNodes(Supervisor supervisor) {
		LocalNodeProperties local_properties = new LocalNodeProperties();

		for(Node node : supervisor.netlist.getNodes())
			local_properties.getLocalNode(node).compiler_setInfo(supervisor, node, 256);
	}

	/**
	 * Creates a fake NetList of LocalNodes. Also fakes the Analyzer(), just setting data directly.
	 */
	private static NetList createNetList() {
		NetList netlist = new NetList();
		NodeProperties properties = new NodeProperties(netlist);
		LocalNodeProperties local_properties = new LocalNodeProperties();
		Port port;

		// Generator, session 0 and 1
		Node generator_node = netlist.createNode();
		port = netlist.createPort(generator_node, "output");
		properties.setOutput(port, true);
		properties.setPortChainIdent(port, "asdf");
		properties.analyzer.setPortChainIds(port, new int[]{1});
		properties.analyzer.setPortChainCreateId(port, 1); // Can create sessions
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(generator_node, new GeneratorNode());

		// middle-node, session 0 and 1
		Node middle_node = netlist.createNode();
		port = netlist.createPort(middle_node, "input");
		properties.analyzer.setPortChainIds(port, new int[]{1});
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		port = netlist.createPort(middle_node, "output");
		properties.setOutput(port, true);
		properties.analyzer.setPortChainCreateId(port, 0); // Can not create sessions on its own
		properties.analyzer.setPortChainIds(port, new int[]{1});
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(middle_node, new MiddleNode());

		// Consumer
		Node consumer_node = netlist.createNode();
		port = netlist.createPort(consumer_node, "input");
		properties.analyzer.setPortChainIds(port, new int[]{1});
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		port = netlist.createPort(consumer_node, "output");
		properties.setOutput(port, true);
		properties.analyzer.setPortChainCreateId(port, 0); // Can not create sessions on its own
		properties.analyzer.setPortChainIds(port, new int[]{0});
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(consumer_node, new ConsumerNode());

		// Consumer 2, on main-session
		Node consumer2_node = netlist.createNode();
		port = netlist.createPort(consumer2_node, "input");
		properties.analyzer.setPortChainIds(port, new int[]{0});
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(consumer2_node, new ConsumerNode());

		// Connect nodes
		netlist.connect(generator_node, "output", middle_node, "input");
		netlist.connect(middle_node, "output", consumer_node, "input");
		netlist.connect(consumer_node, "output", consumer2_node, "input");

		return netlist;
	}

	private static void check(Supervisor supervisor) {
		if(supervisor.processor_list.getAllProcessors().size() != 2) // Only main-session is running
			no();

		supervisor.process(); // GeneratorNode will now create a session

		if(supervisor.processor_list.getAllProcessors().size() != 5) // main-session with 2 processors and the additional session with 3
			no();

		supervisor.process();
	}

	public static void test() {
		NetList netlist = createNetList();
		Supervisor supervisor = new Supervisor(netlist);
		initLocalNodes(supervisor);
		supervisor.begin();

		check(supervisor);
	}
}

class GeneratorNode extends LocalNode {
	int tick;
	boolean communicated_with_processor;

	protected GeneratorNode() {
		super(GeneratorProcessor.class);
	}

	@Override
	protected void onInit() {
		
	}

	@Override
	protected void onProcess() {
		if(tick == 0) {
			GeneratorProcessor gp = (GeneratorProcessor)this.getProcessor(spawnVoice("output"));
			gp.sendStuff();
		} else if(tick == 1) {
			
		}

		tick++;
	}
}

class GeneratorProcessor extends LocalProcessor {
	AudioOutlet output;

	@Override
	protected void onInit() {
		output = (AudioOutlet)getOutlet("output");
	}

	@Override
	protected void onProcess() {
		System.out.println("ja");
	}

	@Override
	protected void onMessage(Message message) {}

	void sendStuff() {
		for(int i = output.written; i < output.audio.length; i++)
			output.audio[i] = i;

		output.push();
	}
}

class MiddleNode extends LocalNode {

	protected MiddleNode() {
		super(MiddleProcessor.class);
	}

	@Override
	protected void onInit() {
		
	}

	@Override
	protected void onProcess() {}
}

class MiddleProcessor extends LocalProcessor {

	@Override
	protected void onInit() {}

	@Override
	protected void onProcess() {}

	@Override
	protected void onMessage(Message message) {}
}

class ConsumerNode extends LocalNode {

	protected ConsumerNode() {
		super(ConsumerProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onProcess() {}
}

class ConsumerProcessor extends LocalProcessor {

	@Override
	protected void onInit() {}

	@Override
	protected void onProcess() {}

	@Override
	protected void onMessage(Message message) {}
}
