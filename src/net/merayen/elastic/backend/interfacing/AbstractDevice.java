package net.merayen.elastic.backend.interfacing;

/**
 * Represents a hardware device, like a audio interface, midi keyboard etc.
 */
public abstract class AbstractDevice {
	public final String id; // an unique ID for a device. Should be namespaced by the DeviceScanner. This ID can be used by the nodes to identify a device between multiple sessions
	public final String description;
	private boolean dead;

	/**
	 * Called when device is asked to start processing.
	 */
	protected abstract void onBegin();

	/**
	 * For inputs:
	 * Return how many samples we do have available for processing. 0 to a positive number
	 * 
	 * For outputs:
	 * Return how many samples we have that are waiting to be sent. If it is 0 or a negative number, we might have stuttering
	 */
	public abstract int getBalance();

	public AbstractDevice(String id, String description) {
		this.id = id;
		this.description = description;
	}

	/**
	 * Called by the device scanner to indicate that this devices has been disconnected.
	 * A new DeviceDescriptor must be created if the device gets connected/available again!
	 */
	void kill() {
		dead = true;
	}

	/**
	 * Returns true if the device is connected and working. Returns false if it has been disconnected.
	 * A DeviceDescriptor can not be reused.
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * Makes the device start processing. This should initialize and open whatever needs to be opened.
	 */
	public final void begin() {
		
	}
}
