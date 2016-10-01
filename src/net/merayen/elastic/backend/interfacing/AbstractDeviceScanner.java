package net.merayen.elastic.backend.interfacing;

import java.util.HashMap;
import java.util.Map;

/**
 * Different platforms has different backends.
 * Perhaps we could implement CoreAudio for audio interfacing for example, while also allowing the Oracle audio interfacing to be used simultaneously?
 * Perhaps even DAWs can be represented as a device, like Logic (as for rewiring and as AU / VST).
 */
public abstract class AbstractDeviceScanner {
	public interface Handler {
		/**
		 * Called when we have detected that a device has been removed
		 */
		public void onDeviceAdded(AbstractDevice device);

		/**
		 * Called when we have detected that a device has been removed
		 */
		public void onDeviceRemoved(AbstractDevice device);
	}

	private final Map<String, AbstractDevice> devices = new HashMap<>();
	private final Handler handler;

	protected AbstractDeviceScanner(Handler handler) {
		this.handler = handler;
	}

	protected void addDevice(AbstractDevice device) {
		if(devices.containsKey(device.id))
			throw new RuntimeException("Device already registered: Buggy platform scanner");

		devices.put(device.id, device);

		handler.onDeviceAdded(device);
	}

	protected void removeDevice(String id) {
		if(!devices.containsKey(id))
			throw new RuntimeException("Device not registered: Buggy platform scanner");

		AbstractDevice device = devices.get(id);

		device.kill();

		devices.remove(id);

		handler.onDeviceRemoved(device);
	}

	public Map<String, AbstractDevice> getDevices() {
		return new HashMap<>(devices);
	}
}
