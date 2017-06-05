package net.merayen.elastic.backend.interfacing.devicetypes;

import net.merayen.elastic.backend.interfacing.AbstractDevice;

public abstract class AudioDevice extends AbstractDevice {
	public static class Configuration implements AbstractDevice.Configuration {
		public final int sample_rate;
		public final int channels;
		public final int depth; // in bits, e.g 8, 16, 24, 32

		public Configuration(int sample_rate, int channels, int depth) {
			this.sample_rate = sample_rate;
			this.channels = channels;
			this.depth = depth;
		}

		public String getDescription() {
			return String.format("AudioDevice.Configuration(sample_rate=%s, channels=%d, depth=%d)", sample_rate, channels, depth);
		}
	}

	public AudioDevice(String id, String description, String vendor) {
		super(id, description, vendor);
	}

	public void configure(int sample_rate, int channels, int depth) {
		configuration = new Configuration(sample_rate, channels, depth);
	}
}
