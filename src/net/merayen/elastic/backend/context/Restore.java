package net.merayen.elastic.backend.context;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.nodes.Format;
import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

/**
 * Helper class to restore NetList into either UI or backend.
 * Iterates through the NetList and generates Postmaster.Message()s that can be consumed by the UI or backend, or both.
 * 
 * TODO support restoring from just one certain owner, so that we don't generate messages for all groups etc too, if not really wanted. Hmm...
 */
class Restore {
	/**
	 * Creates messages for the whole NetList, including groups, subgroups etc.
	 * Returned messages will need to be executed in the order returned.
	 */
	static List<Postmaster.Message> restore(NetList netlist) {
		List<Postmaster.Message> result = new ArrayList<>();

		// Restore the nodes
		for(Node node : netlist.getNodes()) {

			// Restore the Node() itself
			result.add(new CreateNodeMessage(node.getID(), (String)node.properties.get("name"), (Integer)node.properties.get("version")));

			// Restore the Node()'s properties
			for(String key : node.properties.keySet())
				result.add(new NodeParameterMessage(node.getID(), key, node.properties.get(key)));

			// Restore the Node()'s ports
			for(String port : netlist.getPorts(node)) {
				Port p = netlist.getPort(node, port);
				result.add(new CreateNodePortMessage(
					node.getID(),
					port,
					(boolean)p.properties.get("output"),
					Format.fromStrings((String[])p.properties.get("format")),
					(int)p.properties.get("poly_no") // TODO rename to chain_ident
				));
			}
		}

		// Restore all the connections between the Node()s
		for(Line line : netlist.getLines())
			result.add(new NodeConnectMessage(line.node_a.getID(), line.port_a, line.node_b.getID(), line.port_b));

		return result;
	}

	/**
	 * Restore from NetList, but filter by a certain group.
	 */
	static List<Postmaster.Message> restore(NetList netlist, String group_id) {
		if(group_id == null)
			return restore(netlist);

		NetList filtered = netlist.copy();

		for(Node node : netlist.getNodes()) // Remove nodes that are not in the group group_id
			if(!((String)node.properties.get("group")).equals(group_id))
				filtered.removeNode(node);

		return restore(filtered);
	}
}
