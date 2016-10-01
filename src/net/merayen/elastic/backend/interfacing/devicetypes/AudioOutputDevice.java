package net.merayen.elastic.backend.interfacing.devicetypes;

import net.merayen.elastic.backend.interfacing.AbstractDevice;

public abstract class AudioOutputDevice extends AbstractDevice {
	public static class Configuration implements AbstractDevice.Configuration {
		public final int sample_rate;
		public final int channels;
		public final int depth; // in bits, e.g 8, 16, 24, 32

		public Configuration(int sample_rate, int channels, int depth) {
			this.sample_rate = sample_rate;
			this.channels = channels;
			this.depth = depth;
		}
	}

	public AudioOutputDevice(String id, String description, String vendor) {
		super(id, description, vendor);
	}

	public abstract void onWrite(float[] audio);

	public void configure(int sample_rate, int channels, int depth) {
		configuration = new Configuration(sample_rate, channels, depth);
	}

	public void write(float[] audio) {
		if(!isRunning())
			throw new RuntimeException("Can not write audio to device: Not running");

		if(configuration == null)
			throw new RuntimeException("Can not write audio to device: Not configured");

		onWrite(audio);
	}

	public String getVendor() {
		return vendor;
	}
}
