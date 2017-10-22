package net.merayen.elastic.backend.interfacing;

import java.util.List;

import net.merayen.elastic.util.AverageStat;

/**
 * Represents a hardware device, like a audio interface, midi keyboard etc.
 */
public abstract class AbstractDevice {
	public interface Configuration {}

	/**
	 * Holy shit. Need more information on what happens under the hood.
	 */
	public static class Statistics {
		/**
		 * Time taken to write/read from the buffer.
		 */
		public final AverageStat<Float> buffer_time = new AverageStat<>(100);

		/**
		 * Time outside writing to the buffer.
		 */
		public final AverageStat<Float> outside_buffer_time = new AverageStat<>(100);

		/**
		 * Samples available before reading/writing.
		 */
		public final AverageStat<Integer> available_before = new AverageStat<>(100);

		/**
		 * Samples available after reading/writing.
		 */
		public final AverageStat<Integer> available_after = new AverageStat<>(100);

		/**
		 * Samples processed on read/write.
		 */
		public final AverageStat<Integer> samples_processed = new AverageStat<>(100);

		/**
		 * Everytime the buffer is empty upon writing or reading.
		 */
		public int hunger;

		public String describe() {
			return String.format("buffer_time=%s, outside_buffer_time=%s, available_before=%s available_after=%s, samples_processed=%s, hunger=%d", buffer_time.info(), outside_buffer_time.info(), available_before.info(), available_after.info(), samples_processed.info(), hunger);
		}
	}

	public final Statistics statistics = new Statistics();

	public final String id; // an unique ID for a device. Should be namespaced by the DeviceScanner. This ID can be used by the nodes to identify a device between multiple sessions
	public final String description;
	private boolean dead;
	private boolean running;
	protected Configuration configuration;

	protected String vendor;

	/**
	 * Called when device is asked to start processing.
	 * Not required to start, but if devices has some start-up time, it is advised
	 * to at least prepare the device in this call.
	 * 
	 * XXX remove? Every device should read their config and check for changes, and care if only necessary?
	 */
	//protected abstract void onBegin();

	/**
	 * Called when a configuration has been changed.
	 * Device should be re-inited to confirm to the new configuration.
	 */
	public abstract void onReconfigure();

	protected abstract void onStop();

	protected abstract void onKill();

	/**
	 * Either reads or writes silence to device. This one is usually called to assert that no lines are starving/overflowing
	 */
	public abstract void spool(int samples);

	/**
	 * Returns all the available configurations this device supports
	 */
	public abstract List<Configuration> getAvailableConfigurations();

	/**
	 * For inputs:
	 * Return how many samples we do have available for processing. 0 to a positive number
	 * 
	 * For outputs:
	 * Return how many samples we have that are waiting to be sent. If it is 0 or a negative number, we might have stuttering
	 */
	public abstract int available();

	public abstract boolean isOutput();

	public AbstractDevice(String id, String description, String vendor) {
		this.id = id;
		this.description = description;
		this.vendor = vendor;
	}

	public String getID() {
		return id;
	}

	/**
	 * Called by the device scanner to indicate that this devices has been disconnected.
	 * A new DeviceDescriptor must be created if the device gets connected/available again!
	 * Should most likely not be called manually, unless testing.
	 */
	public void kill() {
		if(running)
			stop();

		onKill();

		dead = true;
	}

	/**
	 * Returns true if the device is connected and working. Returns false if it has been disconnected.
	 * A DeviceDescriptor can not be reused.
	 */
	public boolean isDead() {
		return dead;
	}

	public boolean isRunning() {
		return running;
	}

	/**
	 * Makes the device start processing. This should initialize and open whatever needs to be opened.
	 */
	public final void begin() {
		if(configuration == null)
			throw new RuntimeException("Device must be configured before it can be started");

		if(dead)
			throw new RuntimeException("Device has been closed. Future processing is not possible");

		running = true;
	}

	public final void stop() {
		onStop();
		running = false;
	}
}
