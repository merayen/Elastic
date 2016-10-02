package net.merayen.elastic.backend.interfacing;

/**
 * Represents a hardware device, like a audio interface, midi keyboard etc.
 */
public abstract class AbstractDevice {
	public interface Configuration {}

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
	 */
	protected abstract void onBegin();

	protected abstract void onStop();

	protected abstract void onKill();

	/**
	 * For inputs:
	 * Return how many samples we do have available for processing. 0 to a positive number
	 * 
	 * For outputs:
	 * Return how many samples we have that are waiting to be sent. If it is 0 or a negative number, we might have stuttering
	 */
	public abstract int getBalance();

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
	 */
	void kill() {
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

		onBegin();
		running = true;
	}

	public final void stop() {
		onStop();
		running = false;
	}
}
