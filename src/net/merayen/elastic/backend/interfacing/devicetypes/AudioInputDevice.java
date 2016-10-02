package net.merayen.elastic.backend.interfacing.devicetypes;

public abstract class AudioInputDevice extends AudioDevice {
	public AudioInputDevice(String id, String description, String vendor) {
		super(id, description, vendor);
	}
}
