package net.merayen.elastic.backend.midi.devices;

public interface IMIDIReceiver {
	void onReceive(short[] midi, long microseconds);
}
