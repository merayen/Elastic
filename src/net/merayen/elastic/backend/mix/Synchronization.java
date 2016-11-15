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
		public void needData();
	}

	private class Poller implements Runnable {
		boolean running = true;
		private final long start = System.nanoTime();
		private long sample_count; // samples from start

		@Override
		public void run() {
			int in_available = Integer.MAX_VALUE;
			int out_available = Integer.MAX_VALUE;

			while(running) {
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
					//long duration = // TODO 
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	public final Mixer mixer;
	private boolean requested;
	private final Poller poller;
	private final int buffer_sample_size;
	private final int sample_rate; // Only used when using the internal timer, as we then need to know the duration of the buffer

	public Synchronization(Mixer mixer, Handler handler, int buffer_sample_size, int sample_rate) {
		this.mixer = mixer;
		this.buffer_sample_size = buffer_sample_size;
		this.sample_rate = sample_rate;

		poller = new Poller();
		new Thread(poller).start();
	}

	public void end() {
		poller.running = false;
	}
}
