package net.merayen.merasynth.buffer;

public interface CircularBuffer {
	// Must implement write() and read()
	public int getAvailable(); // Data in the buffer
	public int getFree(); // Free space in buffer
	public int getSize(); // Buffer size
}
