package net.merayen.merasynth.netlist;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.merayen.merasynth.client.signalgenerator.Net;

public class Main {
	public static Supervisor load(String dump) {
		Supervisor supervisor = new Supervisor();
		if(dump == null) {
			Node sine_node = new Net(supervisor);
			//Node console_node = new Console(supervisor);
			supervisor.addNode(sine_node);
			//supervisor.addNode(console_node);
			//supervisor.connect(sine_node.getPort("output"), console_node.getPort("input"));
		} else {
			try {
				supervisor.restore((JSONObject)new org.json.simple.parser.JSONParser().parse(dump));
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException("Dump can not be read: " + e.toString());
			}
		}

		System.out.printf("Needs update: %b\n", supervisor.needsUpdate());
		supervisor.update(0.001);
		supervisor.update(1.0);

		return supervisor;
	}

	public static void main(String dfkljdsfs[]) {
		Supervisor supervisor = load(null);
		String dump = supervisor.dump().toJSONString();

		System.out.println(dump);

		supervisor = load(dump);

		System.out.println(supervisor.dump().toJSONString());
	}
}
