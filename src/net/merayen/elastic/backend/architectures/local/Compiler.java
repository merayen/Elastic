package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.backend.architectures.AbstractExecutor;
import net.merayen.elastic.backend.architectures.ICompiler;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

/**
 * Compiles a NetList to our local NetList which can then be used for execution.
 */
public class Compiler extends ICompiler {
	private final static String CLASS_PATH = "net.merayen.elastic.backend.architectures.local.nodes.%s_%d.%s";
	private Supervisor supervisor;
	private int buffer_size;
	private NetList netlist;

	@Override
	public AbstractExecutor compile(NetList source_netlist, int buffer_size) {
		long t = System.currentTimeMillis();

		this.buffer_size = buffer_size;

		netlist = source_netlist.copy();
		supervisor = new Supervisor(netlist);

		for(Node node : netlist.getNodes())
			createNode(node);

		System.out.printf("Compiling took: %d ms\n", System.currentTimeMillis() - t);

		return new Executor(netlist);
	}

	private void createNode(Node node) { // Not tested
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

		localnode.compiler_setInfo(supervisor, node, buffer_size);
	}

	private static Class<?> loadClass(String class_path) {
		try {
			return (Class<?>)Class.forName(class_path);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(String.format("Could not find class %s", class_path));
		}
	}
}
