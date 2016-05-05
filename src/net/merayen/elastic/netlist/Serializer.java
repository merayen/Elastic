package net.merayen.elastic.netlist;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * Serializes a NetList to JSON.
 */
public final class Serializer {
	private Serializer() {}

	/**
	 * Dumps a NetList to JSON
	 */
	public static JSONObject dump(NetList netlist) { // TODO make it work
		JSONObject result = new JSONObject();

		JSONArray nodes = new JSONArray();
		JSONArray lines = new JSONArray();

		for(Node n : netlist.nodes) {
			JSONObject node = new JSONObject();
			JSONObject ports = new JSONObject();
			JSONObject properties = new JSONObject();

			for(String p : n.ports.keySet()) {
				JSONObject port = new JSONObject();
				JSONObject port_properties = new JSONObject();

				for(String k : n.ports.get(p).properties.keySet()) // Port properties
					port_properties.put(k, n.ports.get(p).properties.get(k));

				port.put("properties", port_properties);
				ports.put(p, port);
			}

			for(String k : n.properties.keySet()) { // Node properties
				properties.put(k, n.properties.get(k));
			}

			node.put("id", n.id);
			node.put("ports", ports);
			node.put("properties", properties);

			nodes.add(node);
		}

		for(Line l : netlist.lines) {
			JSONObject line = new JSONObject();
			line.put("node_a", l.node_a.id);
			line.put("port_a", l.port_a);
			line.put("node_b", l.node_b.id);
			line.put("port_b", l.port_b);
			lines.add(line);
		}

		result.put("lines", lines);	
		result.put("nodes", nodes);

		return result;
	}

	public static NetList restore(JSONObject dump) { // TODO make it work
		NetList netlist = new NetList();

		JSONArray nodes = (JSONArray)dump.get("nodes");
		JSONArray lines = (JSONArray)dump.get("lines");

		// Creation of nodes and their ports
		for(Object _n : nodes) {
			JSONObject n = (JSONObject)_n;

			Node node = netlist.createNode();
			node.id = (String)n.get("id");

			JSONObject j_props = (JSONObject)n.get("properties");
			for(Object k : j_props.keySet())
				node.properties.put((String)k, j_props.get((String)k));

			JSONObject j_ports = (JSONObject)n.get("ports");
			for(Object port_name : j_ports.keySet()) {
				Port port = node.createPort((String)port_name);
				JSONObject j_properties = (JSONObject)((JSONObject)j_ports.get(port_name)).get("properties");
				for(Object k : j_properties.keySet())
					port.properties.put((String)k, j_properties.get((String)k));
			}
		}

		// Connecting the ports with lines
		for(Object _l : lines) {
			JSONObject l = (JSONObject)_l;
			netlist.connect(
				netlist.getNodeByID((String)l.get("node_a")),
				(String)l.get("port_a"),
				netlist.getNodeByID((String)l.get("node_b")),
				(String)l.get("port_b")
			);
		}

		return netlist;
	}
}
