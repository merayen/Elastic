package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.util.Postmaster;

/**
 * Used by the backend to communicate with the processor
 */
public class Dispatch {
	public interface Handler {
		/**
		 * Message received from the backend.
		 */
		public void onMessage(Postmaster.Message message);
	}

	class Runner extends Thread {
		private final AbstractExecutor executor;
		private volatile boolean running = true;

		public Runner(AbstractExecutor executor) {
			this.executor = executor;
		}

		@Override
		public void run() {
			while(running) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					return;
				}

				Postmaster.Message message;
				while (true) {
					message = executor.to_processing.receive();
					if(message != null)
						executor.onMessage(message);
					else
						break;
				}
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

	public void executeMessage(Postmaster.Message message) {
		executor.to_processing.send(message);
		synchronized (runner) {
			runner.notify();
		}
	}

	public Postmaster.Message receiveMessageFromProcessor() {
		return executor.from_processing.receive();
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
