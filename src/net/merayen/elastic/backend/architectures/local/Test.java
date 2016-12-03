package net.merayen.elastic.backend.architectures.local;

import java.util.Map;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.backend.nodes.Format;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;
import net.merayen.elastic.system.intercom.ProcessMessage;
import net.merayen.elastic.util.Postmaster.Message;
import net.merayen.elastic.util.pack.Dict;

/**
 * Emulates the Analyzer() and fakes nodes for testing Supervisor().
 */
public class Test {
	private Test() {}

	static void no() {
		throw new RuntimeException("Nope");
	}

	/*private static void initLocalNodes(Supervisor supervisor) {
		LocalNodeProperties local_properties = new LocalNodeProperties();

		for(Node node : supervisor.netlist.getNodes())
			local_properties.getLocalNode(node).compiler_setInfo(supervisor, node, 256);
	}*/

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
		properties.setOutput(port);
		properties.setPortChainIdent(port, "asdf");
		properties.analyzer.getPortChainIds(port).add(1);
		properties.analyzer.setPortChainCreateId(port, 1); // Can create sessions
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(generator_node, new GeneratorNode());

		// middle-node, session 0 and 1
		Node middle_node = netlist.createNode();
		port = netlist.createPort(middle_node, "input");
		properties.analyzer.getPortChainIds(port).add(1);
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		port = netlist.createPort(middle_node, "output");
		properties.setOutput(port);
		properties.analyzer.setPortChainCreateId(port, 0); // Can not create sessions on its own
		properties.analyzer.getPortChainIds(port).add(1);
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(middle_node, new MiddleNode());

		// Consumer, on session 0 and 1
		Node consumer_node = netlist.createNode();
		port = netlist.createPort(consumer_node, "input");
		properties.analyzer.getPortChainIds(port).add(1);
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		port = netlist.createPort(consumer_node, "output");
		properties.setOutput(port);
		properties.analyzer.setPortChainCreateId(port, 0); // Can not create sessions on its own
		properties.analyzer.getPortChainIds(port).add(0);
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(consumer_node, new ConsumerNode(1));
		//properties.setName(consumer_node, "");

		// Consumer 2, on main-session
		Node consumer2_node = netlist.createNode();
		port = netlist.createPort(consumer2_node, "input");
		properties.analyzer.getPortChainIds(port).add(0);
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		port = netlist.createPort(consumer2_node, "output");
		properties.setOutput(port);
		properties.analyzer.setPortChainCreateId(port, 0); // Can not create sessions on its own
		//properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(consumer2_node, new ConsumerNode(2));

		// Connect nodes
		netlist.connect(generator_node, "output", middle_node, "input");
		netlist.connect(middle_node, "output", consumer_node, "input");
		netlist.connect(consumer_node, "output", consumer2_node, "input");

		return netlist;
	}

	private static void check(Supervisor supervisor) {
		ProcessMessage message = new ProcessMessage();
		if(supervisor.processor_list.getAllProcessors().size() != 2) // Only main-session is running
			no();

		supervisor.process(message); // GeneratorNode will now create a session

		if(supervisor.processor_list.getAllProcessors().size() != 5) // main-session with 2 processors and the additional session with 3
			no();

		supervisor.process(message);
	}

	public static void test() {
		NetList netlist = createNetList();
		Supervisor supervisor = new Supervisor(netlist, 44100, 256);
		//initLocalNodes(supervisor);
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
	protected void onProcess(Dict data) {
		if(tick++ == 0) {
			GeneratorProcessor gp = (GeneratorProcessor)getProcessor(spawnVoice("output", 0));
			gp.sendStuff();
		} else if(tick == 1) {
			
		}
	}

	@Override
	protected void onDestroy() {}

	@Override
	protected void onParameter(String key, Object value) {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onFinishFrame() {
		// TODO Auto-generated method stub
		
	}
}

class GeneratorProcessor extends LocalProcessor {
	AudioOutlet output;

	@Override
	protected void onInit() {
		output = (AudioOutlet)getOutlet("output");
	}

	@Override
	protected void onProcess() {}

	@Override
	protected void onMessage(Message message) {}

	void sendStuff() {
		for(int i = output.written; i < output.audio.length; i++)
			output.audio[i] = i;

		output.written += output.audio.length - output.written;
		output.push();
	}

	@Override
	protected void onPrepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}

class MiddleNode extends LocalNode {

	protected MiddleNode() {
		super(MiddleProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onProcess(Dict data) {}

	@Override
	protected void onDestroy() {}

	@Override
	protected void onParameter(String key, Object value) {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onFinishFrame() {
		// TODO Auto-generated method stub
		
	}
}

class MiddleProcessor extends LocalProcessor {
	AudioInlet input;
	AudioOutlet output;

	@Override
	protected void onInit() {
		input = (AudioInlet)getInlet("input");
		output = (AudioOutlet)getOutlet("output");
	}

	@Override
	protected void onProcess() {
		int avail = input.available();
		if(avail > 0) {
			System.out.println("MiddleProcessor: Got data :D Sending it to next");
			for(int i = output.written; i < input.outlet.written; i++)
				output.audio[i] = ((AudioOutlet)input.outlet).audio[i];

			output.written = input.outlet.written;
			output.push();
		} else {
			Test.no(); // We should always have gotten data on input when asked to process
		}
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}

class ConsumerNode extends LocalNode {
	int number;

	protected ConsumerNode(int number) {
		super(ConsumerProcessor.class);
		this.number = number;
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onProcess(Dict data) {}

	void addVoiceData(int session_id, float[] audio, int start, int stop) {
		((ConsumerProcessor)this.getProcessor(0)).emit(audio, start, stop);
	}

	@Override
	protected void onDestroy() {}

	@Override
	protected void onParameter(String key, Object value) {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onFinishFrame() {
		// TODO Auto-generated method stub
		
	}
}

class ConsumerProcessor extends LocalProcessor {
	AudioInlet input;
	AudioOutlet output;
	int sent;

	@Override
	protected void onInit() {
		input = getInlet("input") != null ? (AudioInlet)getInlet("input") : null;
		output = getOutlet("output") != null ? (AudioOutlet)getOutlet("output") : null;
	}

	@Override
	protected void onProcess() {
		int avail = input.available();
		if(avail > 0) {
			System.out.printf("Consumer %d session %d: Got %d samples (%s)\n", ((ConsumerNode)localnode).number, session_id, avail, this);
			((ConsumerNode)localnode).addVoiceData(this.session_id, ((AudioOutlet)input.outlet).audio, sent, ((AudioOutlet)input.outlet).written);
		} else {
			Test.no();
		}
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onPrepare() {
		if(output != null)
			for(int i = 0; i < output.audio.length; i++)
				output.audio[i] = 0;
	}

	void emit(float[] audio, int start, int stop) {
		if(output != null) {
			System.out.printf("Consumer %d session %d: re-emits %d samples (%s)\n", ((ConsumerNode)localnode).number, session_id, stop-start, this);

			for(int i = start; i < stop; i++)
				output.audio[i] += audio[i];
	
			output.written = stop;
	
			output.push(); // Wrong, should collect all voices before sending, but meh, just a test
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}
