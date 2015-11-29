package net.merayen.merasynth.midi.devices;

public interface IMIDIReceiver {
	public void onReceive(short[] midi, long microseconds);
}
