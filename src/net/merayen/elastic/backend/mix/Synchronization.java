package net.merayen.elastic.backend.mix;

import net.merayen.elastic.backend.interfacing.AbstractDevice;

/**
 * Helper that synchronizes and calls a callback when needing to process more data.
 * Can use input-device clock, output-device clock, or just its own, internal clock if no external clock is possible
 */
public class Synchronization {
	public interface Handler {
		/**
		 * Called when there is time to process a new frame.
		 */
		public void needData(long samples_lag);
	}

	private class Poller implements Runnable {
		volatile boolean running = true;
		private final long start = System.nanoTime();
		private long sample_count; // samples from start. Counts upwards

		@Override
		public void run() {
			while(running) {
				int in_available = Integer.MAX_VALUE;
				int out_available = Integer.MAX_VALUE;

				for(AbstractDevice ad : mixer.getOpenDevices()) {
					if(ad.isOutput())
						in_available = Math.min(in_available, ad.available());
					else
						out_available = Math.min(out_available, ad.available());
				}

				if(in_available != Integer.MAX_VALUE) { // We have active input-source, we use that as a time-source
					// TODO
				} else if(out_available != Integer.MAX_VALUE) { // Only outputs, we use them as clock
					// TODO
				} else { // No input or outputs. Gotta use our own clock
					long duration = (System.nanoTime() - start) / 1000; // in microseconds
					long samples = (duration * sample_rate) / 1000000;
					long sample_lag = samples - sample_count;

					if(sample_lag > 0) { // Needs to calibrate this live, depending how long processing takes
						processing = true;
						handler.needData(sample_lag);
					}
				}

				try {
					Thread.sleep(10);
					while(processing && running)
						Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	public final Mixer mixer;
	private boolean processing; // Set to true when we have fired Handler.needData(). It will go back to false when notified by the Synchronization.push() call.
	private final Poller poller;
	//private final int buffer_sample_size;
	private final int sample_rate; // Only used when using the internal timer, as we then need to know the duration of the buffer
	private final Handler handler;

	public Synchronization(Mixer mixer, int sample_rate, Handler handler) {
		this.mixer = mixer;
		this.handler = handler;
		//this.buffer_sample_size = buffer_sample_size;
		this.sample_rate = sample_rate;

		poller = new Poller();
		new Thread(poller).start();
	}

	/**
	 * Tell us that the buffer_sample_size has been processed.
	 * We will acknowledge that and plan next call.
	 */
	public void push(int samples) {
		if(!processing)
			throw new RuntimeException("Calling push() when not been requested is not allowed");
		processing = false;
		poller.sample_count += samples;
	}

	public void end() {
		poller.running = false;
	}
}
