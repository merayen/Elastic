package net.merayen.elastic.backend.architectures;

import net.merayen.elastic.util.Postmaster;
import net.merayen.elastic.util.Postmaster.Message;

/**
 * Used by the backend to communicate with the processor
 */
public class Dispatch {
	public interface Handler {
		/**
		 * Message received from the backend.
		 */
		public void onMessageFromProcessor(Postmaster.Message message);
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
						if(executor.to_processing.isEmpty())
							wait(0);
					}
				} catch (InterruptedException e) {
					return;
				}

				synchronized (this) {
					Postmaster.Message message;
					while ((message = executor.to_processing.receive()) != null)
						executor.onMessage(message);
				}
			}
		}
	}

	public final Architecture architecture;
	private final Handler handler;
	private AbstractExecutor executor;
	private Runner runner;

	public Dispatch(Architecture architecture, Handler handler) {
		this.architecture = architecture;
		this.handler = handler;
	}

	public void executeMessage(Postmaster.Message message) {
		synchronized (runner) {
			executor.to_processing.send(message);
			runner.notifyAll();
		}
	}

	/**
	 * Sends the NetList to the chosen architecture and begins processing.
	 */
	public void launch(int buffer_size) {
		if(executor != null)
			throw new RuntimeException("Already started");

		executor = architecture.instance.getExecutor();
		executor.setHandler(new AbstractExecutor.Handler() {

			@Override
			public void onMessageFromProcessor(Message message) {
				handler.onMessageFromProcessor(message);
			}
		});
		runner = new Runner(executor);
		runner.start();
	}

	public void stop() {
		if(executor == null)
			throw new RuntimeException("Not running");

		runner.running = false;
		synchronized (runner) {
			runner.notifyAll();
		}

		try {
			runner.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		executor.stop();
		executor = null;
	}
}
