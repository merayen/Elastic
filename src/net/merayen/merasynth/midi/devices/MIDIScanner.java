package net.merayen.merasynth.midi.devices;

import java.util.List;

import net.merayen.merasynth.midi.devices.implementations.OracleJavaDevice;

public class MIDIScanner {
	public static List<IMIDIDeviceAdapter> getDevices() {
		// Only Oracle Java support now
		return OracleJavaDevice.scan();
	}
}
