package net.merayen.elastic.backend.interfacing.devicetypes;

import net.merayen.elastic.backend.interfacing.AbstractDevice;

public abstract class AudioInputDevice extends AbstractDevice {

	public AudioInputDevice(String id, String description, String vendor) {
		super(id, description, vendor);
	}
}
