package net.merayen.elastic.backend.mix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.AbstractDeviceScanner;
import net.merayen.elastic.backend.interfacing.Platform;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioInputDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice;
import net.merayen.elastic.backend.mix.datatypes.Audio;
import net.merayen.elastic.backend.mix.datatypes.DataType;

/**
 * Accumulates and mixes outgoing and incoming data.
 * Mixes both audio and midi.
 * Does not support different sample rates.
 */
public class Mixer { // Rename to e.g "IODispatch"?
	public interface Handler {
		public void onDeviceRemoved(String id);
		public void onDeviceAdded(String id);
	}

	// Default mixer settings. These will be applied on all input and output devices, if possible
	private int sample_rate = 44100;
	private int audio_channels = 1;
	private int audio_depth = 16;

	private final AbstractDeviceScanner device_scanner;
	private final Map<String, List<DataType>> output_buffer = new HashMap<>(); // Format: Map<device_id, List<DataType to be mixed>>
	private final Map<String, List<DataType>> input_buffer = new HashMap<>();

	public Mixer() {
		device_scanner = Platform.getPlatformScanner(new AbstractDeviceScanner.Handler() {

			@Override
			public void onDeviceRemoved(AbstractDevice device) {
				if(device.isOutput()) {
					synchronized (output_buffer) {
						output_buffer.remove(device.id);
					}
				} else {
					synchronized (input_buffer) {
						input_buffer.remove(device.id);
					}
				}
			}

			@Override
			public void onDeviceAdded(AbstractDevice device) {
				if(device.isOutput()) {
					synchronized (output_buffer) {
						output_buffer.put(device.id, new ArrayList<>());
					}
				} else {
					synchronized (input_buffer) {
						input_buffer.remove(device.id);
					}
				}
			}
		});

		reconfigure();
	}

	public void reconfigure(int sample_rate, int channels, int depth) {
		this.sample_rate = sample_rate;
		this.audio_channels = channels;
		this.audio_depth = depth;

		reconfigure();
	}

	/**
	 * Reconfigures all devices
	 */
	private void reconfigure() {
		for(AbstractDevice device : device_scanner.getDevices()) {
			if(device instanceof AudioOutputDevice)
				((AudioOutputDevice)device).configure(sample_rate, audio_channels, audio_depth);
			else if(device instanceof AudioInputDevice)
				((AudioInputDevice)device).configure(sample_rate, audio_channels, audio_depth);

			device.onReconfigure(); // Notify device that we might have changed something
		}
	}

	public List<AbstractDevice> getAvailableDevices() {
		return device_scanner.getDevices();
	}

	public List<AbstractDevice> getOpenDevices() {
		List<AbstractDevice> result = new ArrayList<>();

		for(AbstractDevice ad : device_scanner.getDevices())
			if(ad.isRunning())
				result.add(ad);

		return result;
	}

	/**
	 * Writes data parallel. All calls must contain a fixed length of data.
	 */
	public void send(String device_id, DataType data) {
		AbstractDevice ad = device_scanner.getDevice(device_id);

		if(ad == null) // Device not present. Perhaps unplugged. We just ignore the incoming data
			return;

		synchronized(output_buffer) {
			output_buffer.get(device_id).add(data);
		}
	}

	/**
	 * Ends this mixer, closing all lines.
	 * Mixer can not be reused after this.
	 */
	public void end() {
		for(AbstractDevice device : device_scanner.getDevices()) {
			if(device.isRunning())
				device.kill();
		}
	}

	/**
	 * Read the input buffer on a device.
	 * Do not alter the received data by any means.
	 */
	public DataType read(String device_id) {
		// TODO
		return null;
	}

	/**
	 * Sends all the waiting data to the interfaces and retrieves new data.
	 * Do this when all output (interfacing) nodes has output data.
	 * @param samples How many samples to output and to read from input
	 */
	public void dispatch(int samples) {
		sendToDevices(mixOutgoing(samples));

		// Read incoming data, or wait until it is ready
		// XXX reads every device for now
		// TODO

		// Clear buffer
		synchronized(output_buffer) {
			for(List<DataType> o : output_buffer.values())
				o.clear();
		}

		for(List<DataType> o : input_buffer.values())
			o.clear();
	}

	/*
	 * Called by Synchronization() when needing to fill the outgoing pipes.
	 * Current buffer is not cleared, only the backend device is being read/written to.
	 * Synchronization() uses this function to make sure that no lines are starving or overflowing.
	 */
	/*void spool(AbstractDevice ad, int samples) {
		ad.spool(samples);
	}*/

	private Map<AbstractDevice, DataType> mixOutgoing(int samples) {
		Map<AbstractDevice, DataType> out = new HashMap<>();

		synchronized(output_buffer) {
			for(Map.Entry<String, List<DataType>> o : output_buffer.entrySet()) {

				if(o.getValue().size() > 0) {
					AbstractDevice device = device_scanner.getDevice(o.getKey());
					out.put(device, DataType.mix(samples, device, o.getValue()));
				}
			}
		}

		return out;
	}

	private void sendToDevices(Map<AbstractDevice, DataType> data) {
		for(Map.Entry<AbstractDevice, DataType> o : data.entrySet()) {
			if(!o.getKey().isRunning())
				o.getKey().begin();

			if(o.getKey() instanceof AudioOutputDevice)
				((AudioOutputDevice)o.getKey()).write(((Audio)o.getValue()).audio);
		}
	}
}
