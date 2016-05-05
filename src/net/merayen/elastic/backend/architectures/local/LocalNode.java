package net.merayen.elastic.backend.architectures.local;

/**
 * A LocalNode is a node that implements the logic for local JVM processing.
 * All nodes must implement this.
 */
public abstract class LocalNode {
	/**
	 * Options set by the scanner.
	 */
	private class Options {
		public boolean isGenerator; // If *true*, this node generates without input 
	}

	static class Port {
		public final String name;
		public final Line[] destinations;

		public Port(String name, Line[] destinations) {
			this.name = name;
			this.destinations = destinations;
		}
	}

	static class Line {
		public final LocalNode node; // Receiving node
		public final String port; // Receiving port on that node

		Line(LocalNode node, String port) {
			this.node = node;
			this.port = port;
		}
	}

	private String id;
	private int buffer_size;
	private Class<? extends LocalProcessor> localprocessor_cls;
	protected Port[] ports;
	public final Options options = new Options();
	private Class<? extends LocalProcessor> processor_cls;
	private ProcessorList processors = new ProcessorList();

	public String getID() {
		return id;
	}

	LocalProcessor launchProcessor() {
		LocalProcessor localprocessor;
		try {
			localprocessor = processor_cls.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		localprocessor.LocalProcessor_setInfo(this, buffer_size);

		processors.add(localprocessor);

		return localprocessor;
	}

	void compiler_setInfo(String id, int buffer_size, Class<? extends LocalProcessor> localprocessor_cls) {
		this.id = id;
		this.buffer_size = buffer_size;
		this.localprocessor_cls = localprocessor_cls;
	}

	void compiler_setPorts(Port[] ports) {
		this.ports = ports;
	}
}
