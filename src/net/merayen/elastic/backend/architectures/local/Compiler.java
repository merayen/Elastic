package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;

import java.util.List;

import net.merayen.elastic.backend.architectures.AbstractExecutor;
import net.merayen.elastic.backend.architectures.ICompiler;
import net.merayen.elastic.netlist.Line;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

public class Compiler extends ICompiler {
	private final static String CLASS_PATH = "net.merayen.elastic.backend.architectures.local.nodes.%s_%d.%s";

	@Override
	public AbstractExecutor compile(NetList netlist, int buffer_size) {
		long t = System.currentTimeMillis();

		NetList local_netlist = netlist.copy();

		System.out.printf("Compiling took: %d ms\n", System.currentTimeMillis() - t);

		return new Executor(local_netlist);
	}

	private static LocalNode createNode(NetList netlist, Node node, int buffer_size) {
		String name = (String)node.properties.get("name");
		Integer version = (Integer)node.properties.get("version");

		if(name == null)
			throw new RuntimeException("Node is missing 'name' property");
		if(version == null)
			throw new RuntimeException("Node is missing 'version' property");

		@SuppressWarnings("unchecked")
		Class<? extends LocalNode> localnode_cls = (Class<? extends LocalNode>)loadClass(String.format(CLASS_PATH, name, version, "LNode"));

		@SuppressWarnings("unchecked")
		Class<? extends LocalProcessor> localprocessor_cls = (Class<? extends LocalProcessor>)loadClass(String.format(CLASS_PATH, name, version, "LProcessor"));

		LocalNode localnode;
		try {
			localnode = localnode_cls.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		localnode.compiler_setInfo(node.getID(), buffer_size, localprocessor_cls);

		return localnode;
	}

	/**
	 * Creates ports on our LocalNode and sets up lines.
	 * TODO Maybe differentiate between input and output ports?
	 */
	private static void routeNode(LocalNetList local_netlist, NetList netlist, Node node) {
		LocalNode local_node = local_netlist.get(node.getID());
		List<LocalNode.Port> local_ports = new ArrayList<>();

		for(String port : node.getPorts()) {
			List<LocalNode.Line> local_lines = new ArrayList<>();

			for(Line line : netlist.getConnections(node, port)) {

				if(node.getID().equals(line.node_a.getID())) // Add line from this node perspective
					local_lines.add(new LocalNode.Line(local_netlist.get(line.node_b.getID()), port));
				else
					local_lines.add(new LocalNode.Line(local_netlist.get(line.node_a.getID()), port));

			}

			local_ports.add(new LocalNode.Port(port, local_lines.toArray(new LocalNode.Line[0])));
		}

		local_node.compiler_setPorts(local_ports.toArray(new LocalNode.Port[0]));
	}

	private static Class<?> loadClass(String class_path) {
		try {
			return (Class<?>)Class.forName(class_path);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(String.format("Could not find class %s", class_path));
		}
	}
}
