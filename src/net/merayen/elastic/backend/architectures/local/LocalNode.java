package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.netlist.Node;

/**
 * A LocalNode is a node that implements the logic for local JVM processing.
 * All nodes must implement this.
 * Its processors are the ones actually performing the work.
 * LocalNodes receives parameter changes and behaves after that.
 */
public abstract class LocalNode {
	private Node node; // The NetList-node we represent
	private int buffer_size;
	private Class<? extends LocalProcessor> processor_cls;
	private ProcessorList processors = new ProcessorList();

	protected abstract void onInit();
	protected abstract void onProcess();

	protected LocalNode(Class<? extends LocalProcessor> cls) {
		processor_cls = cls;
	}

	public String getID() {
		return node.getID();
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

	void compiler_setInfo(Node node, int buffer_size, Class<? extends LocalProcessor> processor_cls) {
		this.node = node;
		this.buffer_size = buffer_size;
		this.processor_cls = processor_cls;
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
