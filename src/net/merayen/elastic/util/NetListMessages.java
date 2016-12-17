package net.merayen.elastic.util;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.analyzer.NodeProperties;
import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;
import net.merayen.elastic.system.intercom.*;

/**
 * Helper class to restore NetList into either UI or backend.
 * Iterates through the NetList and generates Postmaster.Message()s that can be consumed by the UI or backend so they can generate their own NetList.
 * 
 * TODO support restoring from just one certain owner, so that we don't generate messages for all groups etc too, if not really wanted. Hmm...
 */
public class NetListMessages {
	/**
	 * Creates messages for the whole NetList, including groups, subgroups etc.
	 * Returned messages will need to be executed in the order returned.
	 */
	public static List<Postmaster.Message> disassemble(NetList netlist) {
		List<Postmaster.Message> result = new ArrayList<>();
		NodeProperties np = new NodeProperties(netlist);

		// Restore the nodes
		for(Node node : netlist.getNodes()) {

			// Restore the Node() itself
			result.add(new CreateNodeMessage(node.getID(), (String)node.properties.get("name"), (Integer)node.properties.get("version")));

			// Restore the Node()'s ports
			for(String port : netlist.getPorts(node)) {
				Port p = netlist.getPort(node, port);
				result.add(new CreateNodePortMessage(
					node.getID(),
					port,
					np.isOutput(p),
					np.getFormat(p),
					np.getPortChainIdent(p) // TODO rename to chain_ident
				));
			}
		}

		// Restore all the connections between the Node()s
		for(Line line : netlist.getLines())
			result.add(new NodeConnectMessage(line.node_a.getID(), line.port_a, line.node_b.getID(), line.port_b));

		// Restore the Node()'s properties
		for(Node node : netlist.getNodes())
			for(String key : node.properties.keySet())
				if(key.startsWith("p."))
					result.add(new NodeParameterMessage(node.getID(), key, node.properties.get(key)));

		return result;
	}

	/**
	 * Restore from NetList, but filter by a certain group.
	 */
	public static List<Postmaster.Message> disassemble(NetList netlist, String group_id) {
		if(group_id == null)
			return disassemble(netlist);

		NetList filtered = netlist.copy();

		for(Node node : netlist.getNodes()) // Remove nodes that are not in the group group_id
			if(!((String)node.properties.get("group")).equals(group_id))
				filtered.remove(node);

		return disassemble(filtered);
	}

	/**
	 * Change a NetList by a message.
	 * All messages should be sent in correct order, with no loss, or we may have synchronization issues.
	 */
	public static void apply(NetList netlist, Postmaster.Message message) {
		NodeProperties np = new NodeProperties(netlist);

		if(message instanceof CreateNodeMessage) {
			CreateNodeMessage m = (CreateNodeMessage)message;
			Node node = netlist.createNode(m.node_id);
			node.properties.put("name", m.name);
			node.properties.put("version", m.version);

		} else if(message instanceof CreateNodePortMessage) {
			CreateNodePortMessage m = (CreateNodePortMessage)message;
			Port port = netlist.createPort(m.node_id, m.port);
			if(m.chain_ident != null)
				np.setPortChainIdent(port, m.chain_ident);

			if(m.output) {
				np.setOutput(port);
				np.setFormat(port, m.format);
			}

		} else if(message instanceof RemoveNodeMessage) {
			RemoveNodeMessage m = (RemoveNodeMessage)message;
			netlist.remove(m.node_id);

		} else if(message instanceof RemoveNodePortMessage) {
			RemoveNodePortMessage m = (RemoveNodePortMessage)message;
			netlist.removePort(m.node_id, m.port);

		} else if(message instanceof NodeConnectMessage) {
			NodeConnectMessage m = (NodeConnectMessage)message;
			netlist.connect(m.node_a, m.port_a, m.node_b, m.port_b);

		} else if(message instanceof NodeDisconnectMessage) {
			NodeDisconnectMessage m = (NodeDisconnectMessage)message;
			netlist.disconnect(m.node_a, m.port_a, m.node_b, m.port_b);

		} else if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;
			np.parameters.set(netlist.getNode(m.node_id), m.key, m.value);

		}
	}
}
