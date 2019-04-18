package net.merayen.elastic.backend.midi.devices;

public interface IMIDIDeviceAdapter {
	String getName();
	void open(IMIDIReceiver handler);
	void close();
}
