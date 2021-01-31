package net.merayen.elastic.backend.mix;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.util.AverageStat;

/**
 * Helper that synchronizes and calls a callback when needing to process more data.
 * Can use input-device clock, output-device clock, or just its own, internal clock if no external clock is available.
 *
 * The class focuses on keeping synchronized, outputting silence and draining input buffers if you are not able to
 * process in the expected timeframe.
 */
public class Synchronization {
	public static class Statistics {
		private Statistics() {}

		/**
		 * The time taken from requesting data to actually getting it (the time Elastic uses to process).
		 */
		public final AverageStat<Float> awaiting_data = new AverageStat<>(400);
	}

	public interface Handler {
		/**
		 * Called when it is time to process a new frame.
		 * Don't do your processing or any other time-consuming tasks in this function, but rather notify another thread.
		 * Synchronization() needs to keep running its tight loop to make sure all lines are synchronized.
		 */
		void needData();

		/**
		 * Called when you are not able to process fast enough for next frame.
		 * Synchronization will automatically fill the output buffers and read the input buffers to resync.
		 * You will need cancel
		 */
		void behind();
	}

	private class Poller extends Thread {
		volatile boolean running;

		@Override
		public void run() {
			running = true;

			long internalClockStart = System.nanoTime();
			sampleCount = 0;

			// Start initial processing
			processing = true;
			handler.needData();
			waitForData();

			while(running) {
				int in_available = Integer.MAX_VALUE;
				int out_available = Integer.MAX_VALUE;

				for(AbstractDevice ad : mixer.getOpenDevices()) {
					if(ad.isOutput())
						out_available = Math.min(out_available, ad.available());
					else
						in_available = Math.min(in_available, ad.available());
				}

				if(processing)
					throw new RuntimeException("Should not happen");

				if(in_available != Integer.MAX_VALUE) { // We have active input-source, we use that as a time-source
					if(in_available >= bufferSize) {
						processing = true;
						startWaitingData = System.nanoTime();
						handler.needData();
						waitForData();
						continue;
					}
				} else if(out_available != Integer.MAX_VALUE) { // Only output, using that as clock, where the output buffer blocking actually keeps us in in pace
					processing = true;
					startWaitingData = System.nanoTime();
					handler.needData();
					waitForData();
					continue;
				} else { // No input or outputs. Gotta use our own clock
					if(internalClockStart == 0) {
						internalClockStart = System.nanoTime();
						continue;
					}

					long duration = (System.nanoTime() - internalClockStart) / 1000; // in microseconds
					long samples = (duration * sampleRate) / 1000000;
					long sample_lag = samples - sampleCount;

					if(sample_lag > 0) { // TODO calculate a bit better, perhaps
						processing = true;
						startWaitingData = System.nanoTime();
						handler.needData();
						waitForData();
						continue;
					}/* else if(sample_lag >= buffer_size) {
						behind(); // Hurry up, you are not fast enough
						//dropFrame();
					}*/
				}

				// If we get here, there is no input or output
				try {
					synchronized(this) {
						wait(1);
					}
				} catch (InterruptedException e) {
				    throw new RuntimeException(e);
				}
			}
		}

		private void waitForData() {
			try {
				synchronized (this) {
					while(processing && running)
						wait(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private final Mixer mixer;
	public final Statistics statistics = new Statistics();
	private volatile boolean processing; // Set to true when we have fired Handler.needData(). It will go back to false when notified by the Synchronization.push() call.
	private final Poller poller = new Poller();
	private final Handler handler;
	private int sampleRate; // Only used when using the internal timer, as we then need to know the duration of the buffer
	private int bufferSize;
	private volatile long sampleCount; // samples from start. Counts upwards
	private boolean reportedBehind;
	private long startWaitingData = System.nanoTime();

	public Synchronization(Mixer mixer, Handler handler) {
		this.mixer = mixer;
		this.handler = handler;
	}

	public void start() {
		new Thread(poller).start();
	}

	private long nextDebugPrint;
	/**
	 * Tells us that a frame has been processed.
	 * We will acknowledge that and plan next call.
	 */
	public synchronized void push(int sampleRate, int bufferSize) {
		this.bufferSize = bufferSize;
		this.sampleRate = sampleRate;

		if(!poller.running)
			return;

		if(!processing)
			throw new RuntimeException("Calling push() when not been requested is not allowed");

		statistics.awaiting_data.add((float)((System.nanoTime() - startWaitingData) / 1000000.0));

		if (nextDebugPrint < System.currentTimeMillis()) {
			System.out.printf("Total time waiting for audio from Elastic: min=%fms, avg=%fms, max=%fms\n", statistics.awaiting_data.getMin(), statistics.awaiting_data.getAvg(), statistics.awaiting_data.getMax());
			nextDebugPrint = System.currentTimeMillis() + 1000;
		}

		processing = false;
		reportedBehind = false;

		sampleCount += bufferSize;

		synchronized (poller) {
			poller.notifyAll();
		}
	}

	public void end() {
		poller.running = false;
		try {
			poller.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Outputs a silent frame. Consumes input until it is empty.
	 */
	private synchronized void dropFrame() {
		for(AbstractDevice ad : mixer.getOpenDevices())
			ad.spool(bufferSize);

		sampleCount += bufferSize;
	}

	private void behind() {
		if(reportedBehind)
			return;

		reportedBehind = true;
		handler.behind();
	}
}
