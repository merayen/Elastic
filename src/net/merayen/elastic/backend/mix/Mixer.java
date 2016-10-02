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
import net.merayen.elastic.backend.mix.datatypes.DataType;

/**
 * Accumulates and mixes outgoing and incoming data.
 * Mixes both audio and midi.
 * Does not support different sample rates 
 */
public class Mixer {
	public interface Handler {
		public void onDeviceRemoved(String id);
		public void onDeviceAdded(String id);
	}

	// Default mixer settings. These will be applied on all input and output devices, if possible
	private int sample_rate = 44100;
	private int audio_depth = 16;
	private boolean configuration_dirty;

	private int channel_count; // Current count of channels detected from 

	private final AbstractDeviceScanner device_scanner;
	private final Map<String, List<DataType>> output_buffer = new HashMap<>(); // Format: Map<device_id, List<DataType to be mixed>>
	private final Map<String, List<DataType>> input_buffer = new HashMap<>();

	public Mixer() {
		device_scanner = Platform.getPlatformScanner(new AbstractDeviceScanner.Handler() {

			@Override
			public void onDeviceRemoved(AbstractDevice device) {
				synchronized (output_buffer) {
					output_buffer.remove(device.id);
				}
			}

			@Override
			public void onDeviceAdded(AbstractDevice device) {
				synchronized (output_buffer) {
					output_buffer.put(device.id, new ArrayList<>());
				}
			}
		});
	}

	public void reconfigure(int sample_rate, int depth) {
		this.sample_rate = sample_rate;
		this.audio_depth = depth;
		this.configuration_dirty = true;
	}

	/**
	 * Reconfigures all devices
	 */
	private void reconfigure() {
		for(AbstractDevice device : device_scanner.getDevices())
			if(device instanceof AudioOutputDevice)
				((AudioOutputDevice)device).configure(sample_rate, Math.max(1, channel_count), audio_depth);
			else if(device instanceof AudioInputDevice)
				((AudioInputDevice)device).configure(sample_rate, Math.max(1, channel_count), audio_depth);
	}

	public void send(String device_id, DataType data) {
		AbstractDevice ad = device_scanner.getDevice(device_id);

		if(ad == null) // Device not present. Perhaps unplugged. We just ignore the incoming data
			return;

		synchronized(output_buffer) {
			output_buffer.get(device_id).add(data);
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
		DataType[] outgoing = mixOutgoing(samples);

		// Clear buffer
		synchronized(output_buffer) {
			for(List<DataType> o : output_buffer.values())
				o.clear();
		}

		for(List<DataType> o : input_buffer.values())
			o.clear();

		// Read incoming data, or wait until it is ready
		// XXX reads every device for now
		// TODO
	}

	private DataType[] mixOutgoing(int samples) {
		DataType[] out;

		synchronized(output_buffer) {
			out = new DataType[output_buffer.size()];

			int i = 0;
			for(Map.Entry<String, List<DataType>> o : output_buffer.entrySet()) {
				out[i++] = DataType.mix(samples, device_scanner.getDevice(o.getKey()), o.getValue());
			}
		}

		return out;
	}
}
