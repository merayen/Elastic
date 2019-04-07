package net.merayen.elastic.backend.midi.devices;

public interface IMIDIReceiver {
	public void onReceive(short[] midi, long microseconds);
}
