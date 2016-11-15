package net.merayen.elastic.backend.interfacing.devicetypes;

import net.merayen.elastic.backend.interfacing.AbstractDevice;

public abstract class MidiOutputDevice extends AbstractDevice {

	public MidiOutputDevice(String id, String description, String vendor) {
		super(id, description, vendor);
	}

}
