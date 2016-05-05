package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.netlist.NetList;

/**
 * Dispatch the NetList to an architecture.
 */
public class Dispatch {
	/**
	 * A message that can be sent to and from the processor.
	 * A message is read by a processor, and can change its parameter.
	 * Usually sent when user turns a knob in the UI. 
	 */
	public class Message {
		public final String node_id;
		public final String key;
		public final Object value; // Must be JSON compatible

		public Message(String node_id, String key, Object value) {
			this.node_id = node_id;
			this.key = key;
			this.value = value;

			if(
				!(value instanceof String) ||
				!(value instanceof Number)
			)
				throw new RuntimeException("Invalid value type");
		}
	}

	public interface Handler {
		public void onMessage(Message message);
	}

	class Runner extends Thread {
		private final AbstractExecutor executor;
		private volatile boolean running = true;

		public Runner(AbstractExecutor executor) {
			super();
			this.executor = executor;
		}

		@Override
		public void run() {
			while(running) {
				executor.update();
			}
		}
	}

	public final Architecture architecture;
	private final Handler handler;
	private final ICompiler compiler;
	private AbstractExecutor executor;
	private Runner runner;

	public Dispatch(Architecture architecture, Handler handler) {
		this.architecture = architecture;
		this.handler = handler;
		this.compiler = architecture.instance.getCompiler();
	}

	/**
	 * Sends the NetList to the chosen architecture and begins processing.
	 */
	public void launch(NetList netlist, int buffer_size) {
		if(executor != null)
			throw new RuntimeException("Already started");

		executor = compiler.compile(netlist, buffer_size);
		runner = new Runner(executor);
		runner.start();
	}

	public void stop() {
		if(executor == null)
			throw new RuntimeException("Not running");

		runner.running = false;
		try {
			runner.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		executor.stop();
		executor = null;
	}
}
