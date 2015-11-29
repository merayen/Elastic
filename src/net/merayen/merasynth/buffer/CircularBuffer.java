package net.merayen.merasynth.buffer;

public interface CircularBuffer {
	// Must implement write() and read()
	public int available(); // Data in the buffer
	public int getFree(); // Free space in buffer
	public int getSize(); // Buffer size
	public void clear();
}
