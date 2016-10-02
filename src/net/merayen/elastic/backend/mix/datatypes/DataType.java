package net.merayen.elastic.backend.mix.datatypes;

import java.util.List;

import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.MidiOutputDevice;

public abstract class DataType {
	public static DataType mix(int samples, AbstractDevice device, List<DataType> data) {
		if(device instanceof MidiOutputDevice)
			return Midi.mix(samples, data);

		else if(device instanceof AudioOutputDevice)
			return Audio.mix(samples, data);

		throw new RuntimeException("Forgotten to implement support for " + device.getClass().getSimpleName() + " DataType?");
	}
}
