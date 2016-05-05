package net.merayen.elastic.netlist;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.merayen.elastic.netlist.NetList.AlreadyConnected;
import net.merayen.elastic.netlist.NetList.NodeNotFound;
import net.merayen.elastic.netlist.NetList.PortNotFound;

public class Test {
	public static void nope() {
		throw new RuntimeException("Nope");
	}

	private static NetList create() { // Just create some example node structure
		NetList netlist = new NetList();
		Node midi_in = netlist.createNode();
		midi_in.createPort("output");
		midi_in.properties.put("some midi setting", "hokay");

		Node gen = netlist.createNode();
		gen.createPort("frequency");
		gen.properties.put("some gen setting", 1337);
		netlist.connect(midi_in, "output", gen, "frequency");

		Node gen2 = netlist.createNode();
		gen2.createPort("frequency");
		netlist.connect(gen2, "frequency", midi_in, "output");

		if(netlist.getNodeByID(midi_in.id) != midi_in)
			nope();

		try {
			netlist.removeNode(new Node());
			nope();
		} catch(NodeNotFound e) {
			// OK
		}

		if(gen.getPort("frequency") == null)
			nope();

		try { // Try connecting with non-existent port
			netlist.connect(midi_in, "output", gen, "port_does_not_exist");
			nope();
		} catch(PortNotFound e) {
			// OK
		}

		try { // Try connecting with non-existent port
			netlist.connect(midi_in, "port_does_not_exist", gen, "frequency");
			nope();
		} catch(PortNotFound e) {
			// OK
		}

		try { // Try to connect over an existing connection
			netlist.connect(midi_in, "output", gen, "frequency");
			nope();
		} catch(AlreadyConnected e) {
			// OK
		}

		try { // Try to connect opposite way. Should fail
			netlist.connect(gen, "frequency", midi_in, "output");
			nope();
		} catch(AlreadyConnected e) {
			// OK
		}

		List<Line> lines = netlist.getConnections(midi_in, "output");
		if(lines.size() != 2)
			nope();

		// ...should test what lines there is... but meh

		return netlist;
	}

	private static JSONObject parse(String dump) {
		try {
			return (JSONObject)new org.json.simple.parser.JSONParser().parse(dump);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static void test() {
		NetList netlist = create();

		String first_dump = Serializer.dump(netlist).toJSONString();
		//System.out.println(first_dump);
		String second_dump = Serializer.dump(Serializer.restore(parse(first_dump))).toJSONString();
		//System.out.println(second_dump);

		if(!first_dump.equals(second_dump))
			nope();

		//System.out.println(primær_bæsj);
		//System.out.println(sekundær_bæsj);
		/*String dump = supervisor.dump().toJSONString();

		System.out.println(dump);

		supervisor = load(dump);

		System.out.println(supervisor.dump().toJSONString());*/
	}
}