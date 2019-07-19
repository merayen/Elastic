package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.backend.architectures.local.exceptions.SpawnLimitException;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.backend.architectures.local.nodes.adsr_1.ADSR;
import net.merayen.elastic.backend.logicnodes.Format;
import net.merayen.elastic.backend.nodes.BaseNodeData;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;
import net.merayen.elastic.system.intercom.InputFrameData;
import net.merayen.elastic.system.intercom.ProcessMessage;

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

		// Top node
		Node top = netlist.createNode();
		local_properties.setLocalNode(top, new TopNode());

		// Generator
		Node generator_node = netlist.createNode();
		properties.setParent(generator_node, top);
		port = netlist.createPort(generator_node, "output");
		properties.setOutput(port);
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(generator_node, new GeneratorNode());

		// middle-node, contains nodes that processes
		Node middle_node = netlist.createNode();
		properties.setParent(middle_node, top);
		port = netlist.createPort(middle_node, "input");
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		port = netlist.createPort(middle_node, "output");
		properties.setOutput(port);
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(middle_node, new MiddleNode());

		// Dispatch input, subgroup in middle-node
		Node dispatch_in = netlist.createNode();
		properties.setParent(dispatch_in, middle_node);
		port = netlist.createPort(dispatch_in, "output");
		properties.setOutput(port);
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(dispatch_in, new DispatchNode());

		// Consumer, subgroup in middle-node
		Node dispatch_out = netlist.createNode();
		properties.setParent(dispatch_out, middle_node);
		netlist.createPort(dispatch_out, "input");
		local_properties.setLocalNode(dispatch_out, new DispatchNode());

		// Consumer
		Node consumer_node = netlist.createNode();
		properties.setParent(consumer_node, top);
		port = netlist.createPort(consumer_node, "input");
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(consumer_node, new ConsumerNode(1));
		//properties.setName(consumer_node, "");

		// Consumer 2, does nothing
		Node consumer2_node = netlist.createNode();
		properties.setParent(consumer2_node, top);
		port = netlist.createPort(consumer2_node, "input");
		properties.analyzer.setDecidedFormat(port, Format.AUDIO);
		local_properties.setLocalNode(consumer2_node, new ConsumerNode(2));

		// Connect nodes
		netlist.connect(generator_node, "output", middle_node, "input");
		netlist.connect(middle_node, "output", consumer_node, "input");

		// Connect nodes in the sub group
		netlist.connect(dispatch_in, "output", dispatch_out, "input");

		return netlist;
	}

	private static void check(Supervisor supervisor) {
		ProcessMessage message = new ProcessMessage();
		if(supervisor.processor_list.getAllProcessors().size() != 1) // Only main-session is running
			no();

		ProcessMessage response = supervisor.process(message); // This will make MiddleNode create a session for its children, and then process a frame

		if(supervisor.processor_list.getAllProcessors().size() != 9) // main-session with 2 processors and the additional session with 3
			no();

		// Check that the output data is good
		float[] expected = new float[] {0,1,2,3,4,10,12,14,16,18};
		int i = 0;
		for(Object x : response.getInput().values()) {
			float[][] data = (float[][])((Map)x).get("output");
			if(data != null) {
				for(float s : expected)
					if(data[0][i++] != s)
						no();
			}
		}

		if(i == 0)
			no();
	}

	public static void test() {
		NetList netlist = createNetList();
		Supervisor supervisor = new Supervisor(netlist, 44100, 10);
		//initLocalNodes(supervisor);
		supervisor.begin();

		check(supervisor);

		ADSR.test();
	}
}

class TopNode extends LocalNode { // The topmost group that spawns children

	TopNode() {
		super(TopProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onProcess(InputFrameData data) {}

	@Override
	protected void onParameter(BaseNodeData instance) {}

	@Override
	protected void onFinishFrame() {}

	@Override
	protected void onDestroy() {}
}

class TopProcessor extends LocalProcessor {
	private int session_id = -1;

	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		try {
			if(session_id == -1)
				session_id = spawnSession(0);
		} catch (SpawnLimitException e) {
			Test.no();
		}
	}

	@Override
	protected void onMessage(Object message) {}

	@Override
	protected void onDestroy() {}
}

class GeneratorNode extends LocalNode {
	GeneratorNode() {
		super(GeneratorProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onProcess(InputFrameData data) {}

	@Override
	protected void onDestroy() {}

	@Override
	protected void onParameter(BaseNodeData instance) {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onFinishFrame() {}
}

class GeneratorProcessor extends LocalProcessor {
	AudioOutlet output;
	int pos;

	@Override
	protected void onInit() {
		output = (AudioOutlet)getOutlet("output");
	}

	@Override
	protected void onProcess() {
		sendStuff();
	}

	@Override
	protected void onMessage(Object message) {}

	void sendStuff() {
		output.setChannelCount(1);
		for(int i = output.written; i < output.audio[0].length; i++)
			output.audio[0][i] = pos++;

		output.written += output.audio[0].length - output.written;
		output.push();
	}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onDestroy() {}
}

class MiddleNode extends LocalNode {

	protected MiddleNode() {
		super(MiddleProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onProcess(InputFrameData data) {}

	@Override
	protected void onDestroy() {}

	@Override
	protected void onParameter(BaseNodeData instance) {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onFinishFrame() {}

	void gotOutputData() {

	}
}

class MiddleProcessor extends LocalProcessor {
	AudioInlet input;
	AudioOutlet output;
	Map<Integer, float[][]> outputdata = new HashMap<>();

	int session_id_a = -1;
	int session_id_b = -1;

	@Override
	protected void onInit() {
		input = (AudioInlet)getInlet("input");
		output = (AudioOutlet)getOutlet("output");
	}

	@Override
	protected void onProcess() {
		if(session_id_a == -1) {
			try {
				session_id_a = spawnSession(0);
				session_id_b = spawnSession(5);
			} catch (SpawnLimitException e) {
				Test.no();
			}

		}

		int avail = input.available();
		if(avail > 0) {
			System.out.println("MiddleProcessor: Got data :D Sending it to the sub group for further processing");

			// Notify all DispatchNodes
			for(LocalNode ln : localnode.getChildrenNodes())
				((DispatchNode)ln).gotInputData(input);

			input.read += avail;
		} else {
			//Test.no(); // We should always have gotten data on input when asked to process
		}
	}

	@Override
	protected void onMessage(Object message) {}

	@Override
	protected void onPrepare() {
		output.setChannelCount(1);
		for(int i = 0; i < buffer_size; i++)
			output.audio[0][i] = 0;
	}

	@Override
	protected void onDestroy() {}

	void gotOutputData(DispatchProcessor sender) { // Called by DispatchProcessor (child of us)
		if(output.written == buffer_size)
			return;

		List<DispatchProcessor> satisfied = new ArrayList<>();
		for(LocalNode ln : localnode.getChildrenNodes()) {
			for(LocalProcessor lp : ln.getProcessors()) {
				DispatchProcessor dp = (DispatchProcessor)lp;
				if(dp.input != null && dp.input.outlet.written == buffer_size)
					satisfied.add(dp);
			}
		}

		if(satisfied.size() == 2) { // All our subsessions have gotten enough data
			output.setChannelCount(1);
			for(DispatchProcessor dp : satisfied)
				for(int i = 0; i < buffer_size; i++)
					output.audio[0][i] += dp.input.outlet.audio[0][i];

			output.written = buffer_size;
			input.read = buffer_size;
			output.push();
		}
	}
}

class DispatchNode extends LocalNode {

	protected DispatchNode() {
		super(DispatchProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onProcess(InputFrameData data) {}

	@Override
	protected void onDestroy() {}

	@Override
	protected void onParameter(BaseNodeData instance) {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onFinishFrame() {}

	void gotInputData(AudioInlet inlet) {
		for(LocalProcessor lp : getProcessors())
			((DispatchProcessor)lp).send(inlet);
	}
}

class DispatchProcessor extends LocalProcessor {
	AudioInlet input;
	AudioOutlet output;

	@Override
	protected void onInit() {
		input = getInlet("input") != null ? (AudioInlet)getInlet("input") : null;
		output = getOutlet("output") != null ? (AudioOutlet)getOutlet("output") : null;
	}

	@Override
	protected void onProcess() {
		if(input != null) {
			// Forward data to parent node so that it gets sent
			((MiddleProcessor)getParent()).gotOutputData(this);
			input.read = input.outlet.written;
		} else {

		}
	}

	@Override
	protected void onMessage(Object message) {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onDestroy() {}

	void send(AudioInlet inlet) {
		if(output != null) {
			int avail = inlet.available() - output.written;
			output.setChannelCount(1);
			for(int i = 0; i < avail; i++)
				output.audio[0][output.written++] = inlet.outlet.audio[0][output.written-1];

			if(avail > 0)
				output.push();
		}
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
	protected void onProcess(InputFrameData data) {}

	@Override
	protected void onDestroy() {}

	@Override
	protected void onParameter(BaseNodeData instance) {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onFinishFrame() {}
}

class ConsumerProcessor extends LocalProcessor {
	AudioInlet input;

	@Override
	protected void onInit() {
		input = getInlet("input") != null ? (AudioInlet)getInlet("input") : null;
	}

	@Override
	protected void onProcess() {
		if(input != null) {
			int avail = input.available();
			if(avail == buffer_size) {
				System.out.printf("Consumer %d session %d: Got %d samples (%s)\n", ((ConsumerNode)localnode).number, session_id, avail, this);
				//localnode.outgoing.put("output", input.outlet.audio);
				input.read += avail;
			}
		}
	}

	@Override
	protected void onMessage(Object message) {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onDestroy() {}
}
