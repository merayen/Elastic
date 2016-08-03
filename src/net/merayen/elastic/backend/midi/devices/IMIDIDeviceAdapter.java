package net.merayen.elastic.backend.midi.devices;

public interface IMIDIDeviceAdapter {
	public String getName();
	public void open(IMIDIReceiver handler);
	public void close();
}
