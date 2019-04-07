package net.merayen.elastic.backend.interfacing.devicetypes;

import net.merayen.elastic.backend.interfacing.types.MidiPacket;

public abstract class MidiInputDevice extends MidiDevice {
	public MidiInputDevice(String id, String description, String vendor) {
		super(id, description, vendor);
	}

	public final MidiPacket[] read(int sample_count) {
		return onRead(sample_count);
	}

	public abstract MidiPacket[] onRead(int sample_count);
}
