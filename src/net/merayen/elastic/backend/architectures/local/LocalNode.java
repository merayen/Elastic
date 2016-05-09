package net.merayen.elastic.backend.architectures.local;

/**
 * A LocalNode is a node that implements the logic for local JVM processing.
 * All nodes must implement this.
 * Its processors are the ones actually performing the work.
 * LocalNodes receives parameter changes and behaves after that.
 */
public abstract class LocalNode {
	static public class Port {
		public final String name;

		Port(String name) {
			this.name = name;
		}
	}

	static public class OutputPort extends Port {
		public final float[] buffer;

		OutputPort(String name, int buffer_size) {
			super(name);
			buffer = new float[buffer_size];
		}
	}

	private String node_id; // Same node ID as in the rest of the system
	private int buffer_size;
	protected Port[] ports;
	private Class<? extends LocalProcessor> processor_cls;
	private ProcessorList processors = new ProcessorList();

	protected abstract void onInit();

	public LocalNode(Class<? extends LocalProcessor> cls) {
		processor_cls = cls;
	}

	public String getID() {
		return node_id;
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

	void compiler_setInfo(String id, int buffer_size, Class<? extends LocalProcessor> processor_cls) {
		this.node_id = id;
		this.buffer_size = buffer_size;
		this.processor_cls = processor_cls;
	}

	void compiler_setPorts(Port[] ports) {
		this.ports = ports;
	}

	/**
	 * Spawn voice from an output port, creating a new instance of all the nodes on the left.
	 */
	protected int spawnVoice() {
		// TODO
		return 0;
	}

	/**
	 * Returns LocalProcessor for this LocalNode
	 */
	LocalProcessor spawnProcessor() {
		LocalProcessor lp;

		try {
			lp = (LocalProcessor)processor_cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return lp;
	}

	void init() {
		onInit();
	}
}
