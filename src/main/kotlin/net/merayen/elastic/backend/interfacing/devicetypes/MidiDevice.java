package net.merayen.elastic.backend.interfacing.devicetypes;

import net.merayen.elastic.backend.interfacing.AbstractDevice;

public abstract class MidiDevice extends AbstractDevice {
	public static class Configuration implements AbstractDevice.Configuration {
		public final int sample_rate; // MIDI doesn't care much about sample rates, only used for clocking

		public Configuration(int sample_rate) {
			this.sample_rate = sample_rate;
		}
	}

	public MidiDevice(String id, String description, String vendor) {
		super(id, description, vendor);
	}
}
