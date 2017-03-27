package net.merayen.elastic.backend.mix;

import net.merayen.elastic.backend.interfacing.AbstractDevice;

/**
 * Helper that synchronizes and calls a callback when needing to process more data.
 * Can use input-device clock, output-device clock, or just its own, internal clock if no external clock is possible.
 * 
 * The class focuses on keeping synchronized, outputting silence and draining input buffers if you are not able to
 * process in the expected timeframe.
 */
public class Synchronization {
	public interface Handler {
		/**
		 * Called when it is time to process a new frame.
		 * Don't do your processing or any other time-consuming tasks in this function, but rather notify another thread.
		 * Synchronization() needs to keep running its tight loop to make sure all lines are synchronized.
		 */
		public void needData();

		/**
		 * Called when you are not able to process fast enough for next frame.
		 * Synchronization will automatically fill the output buffers and read the input buffers to resync.
		 * You will need cancel 
		 */
		public void behind();
	}

	private class Poller extends Thread {
		volatile boolean running;

		@Override
		public void run() {
			running = true;

			internal_clock_start = System.nanoTime();
			sample_count = 0;

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
					if(in_available >= buffer_size) {
						processing = true;
						handler.needData();
						waitForData();
						continue;
					}
				} else if(out_available != Integer.MAX_VALUE) { // Only output, using that as clock, where the output buffer blocking actually keeps us in in pace
					processing = true;
					handler.needData();
					waitForData();
					continue;
				} else { // No input or outputs. Gotta use our own clock
					if(internal_clock_start == 0) {
						internal_clock_start = System.nanoTime();
						continue;
					}

					long duration = (System.nanoTime() - internal_clock_start) / 1000; // in microseconds
					long samples = (duration * sample_rate) / 1000000;
					long sample_lag = samples - sample_count;

					if(sample_lag > 0) { // TODO calculate a bit better, perhaps
						processing = true;
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void waitForData() {
			try {
				synchronized (this) {
					while(processing && running)
						wait(100);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	public final Mixer mixer;
	private volatile boolean processing; // Set to true when we have fired Handler.needData(). It will go back to false when notified by the Synchronization.push() call.
	private final Poller poller;
	private final int sample_rate; // Only used when using the internal timer, as we then need to know the duration of the buffer
	private final Handler handler;
	private int buffer_size;
	private long internal_clock_start;
	private volatile long sample_count; // samples from start. Counts upwards
	private boolean reported_behind;

	public Synchronization(Mixer mixer, int sample_rate, int buffer_size, Handler handler) {
		this.mixer = mixer;
		this.handler = handler;
		this.buffer_size = buffer_size;
		this.sample_rate = sample_rate;

		poller = new Poller();
	}

	public void start() {
		new Thread(poller).start();
	}

	/**
	 * Tells us that a frame has been processed.
	 * We will acknowledge that and plan next call.
	 */
	public synchronized void push() {
		if(!poller.running)
			return;

		if(!processing)
			throw new RuntimeException("Calling push() when not been requested is not allowed");

		processing = false;
		reported_behind = false;

		sample_count += buffer_size;

		//System.out.printf("Process time: %d ms\n", System.currentTimeMillis() - poller.last_request);

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
	private void dropFrame() {
		for(AbstractDevice ad : mixer.getOpenDevices())
			ad.spool(buffer_size);

		sample_count += buffer_size;
	}

	private void behind() {
		if(reported_behind)
			return;

		reported_behind = true;
		handler.behind();
	}
}
