package net.merayen.elastic.backend.interfacing;

import java.util.ArrayList;
import java.util.List;

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

	private final List<AbstractDevice> devices = new ArrayList<>();
	private final Handler handler;

	protected AbstractDeviceScanner(Handler handler) {
		this.handler = handler;
	}

	protected void addDevice(AbstractDevice device) {
		if(getDevice(device.id) != null) {
			//throw new RuntimeException("Device already registered: Buggy platform scanner?");
			System.out.println(String.format("Device already registered: Buggy platform scanner? (%s)", device.id));
			return;
		}

		devices.add(device);

		handler.onDeviceAdded(device);
	}

	protected void removeDevice(String id) {
		AbstractDevice device = getDevice(id);

		if(device == null)
			throw new RuntimeException("Device not registered: Buggy platform scanner?");

		device.kill();

		devices.remove(id);

		handler.onDeviceRemoved(device);
	}

	public List<AbstractDevice> getDevices() {
		return new ArrayList<>(devices);
	}

	public AbstractDevice getDevice(String id) {
		for(AbstractDevice ad : devices)
			if(ad.id.equals(id))
				return ad;

		return null;
	}
}
