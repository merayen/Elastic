package net.merayen.merasynth.buffer;

import java.util.ArrayList;

/*
 * Generic circular object buffer.
 */
public class ObjectCircularBuffer<T> implements CircularBuffer {
	private ArrayList<T> buffer;
	private int size;
	private int read_position;
	private int write_position;

	public ObjectCircularBuffer(int size) {
		buffer = new ArrayList<T>(size);
		this.size = size;
	}

	public void write(T obj) {
		normalize();
		if(getFree() == 0)
			read_position++;

		buffer.set(write_position++ % size, obj);
	}

	/*
	 * Get next available item in the buffer, without removing it.
	 */
	public T get() {
		if(read_position == write_position)
			return null;

		return buffer.get(read_position % size);
	}

	/*
	 * Pop next available item in the buffer.
	 */
	public T read() {
		if(read_position == write_position)
			return null;

		return buffer.get(read_position++);
	}

	@Override
	public void clear() {
		buffer.clear();
		read_position = 0;
		write_position = 0;
	}

	@Override
	public int available() {
		return write_position - read_position;
	}

	@Override
	public int getFree() {
		return size - (write_position - read_position);
	}

	@Override
	public int getSize() {
		return size;
	}

	private void normalize() {
		int shave = read_position - read_position % size;
		read_position -= shave;
		write_position -= shave;
	}
}
