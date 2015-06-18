package net.merayen.merasynth;

import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.nodes.*;

public class Main {
	public static void main(String dfkljdsfs[]) {
		Supervisor supervisor = new Supervisor();
		Node sine_node = new SineGenerator(supervisor);
		Node console_node = new Console(supervisor);
		supervisor.addNode(sine_node);
		supervisor.addNode(console_node);
		
		supervisor.connect(sine_node.getPort("output"), console_node.getPort("input"));
		
		System.out.printf("Needs update: %b\n", supervisor.needsUpdate());
		supervisor.update(0.001);
		supervisor.update(1.0);
		supervisor.dump();
		
		System.out.println(supervisor.dump().toJSONString());
	}
}
