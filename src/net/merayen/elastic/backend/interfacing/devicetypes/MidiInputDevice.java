package net.merayen.elastic.backend.interfacing.devicetypes;

import net.merayen.elastic.backend.interfacing.AbstractDevice;

public abstract class MidiInputDevice extends AbstractDevice {
	public MidiInputDevice(String id, String description, String vendor) {
		super(id, description, vendor);
	}
}
