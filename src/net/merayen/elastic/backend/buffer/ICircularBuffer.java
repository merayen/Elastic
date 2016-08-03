package net.merayen.elastic.backend.buffer;

public interface ICircularBuffer {
	// Must implement write() and read()
	public int available(); // Data in the buffer
	public int getFree(); // Free space in buffer
	public int size(); // Buffer size
	public void clear();
}
