package net.merayen.elastic.backend.buffer;

/* 
 * Resizeable audio buffer.
 * Doesn't do channels and stuff, caller has to create a new buffer if this happens.
 * TODO Get rid of "stop" argument, and use "length" instead 
 * Not thread safe
 */
public class FloatCircularBuffer implements ICircularBuffer {
	private long write_position;
	private long read_position;
	private float[] buffer;

	public FloatCircularBuffer(int size) {
		buffer = new float[size];
	}

	public void write(float[] data, int start, int stop) { // TODO check out Java's built in array copy for performance
		if(buffer.length == 0)
			return;

		normalize();

		int i_source = start;

		// Increment the read position (drop data) if buffer overflows (distance between read and write is bigger than the buffer)
		read_position += Math.max(0, (stop - start) - getFree());
		for(int i = 0; i < data.length && i_source < stop; i++, i_source++) // TODO only write the part that actually fits us
			buffer[(int)((write_position++) % buffer.length)] = data[i_source]; // TODO Low performance with % ?
	}

	public void write(float[] data) {
		write(data, 0, data.length);
	}

	/*
	 * Copy data from us to the destination float array.
	 * Returns the floats actually copied.
	 */
	public int read(float[] destination) {
		long available = available();

		// Some checks. TODO remove when class really works right
		if(available < 0)
			throw new RuntimeException("AudioBuffer internal error: read_position is higher than write_position");

		if(available > buffer.length)
			throw new RuntimeException("AudioBuffer internal error: Too big distance between write_position and read_position (larger than buffer)");

		int i = 0;
		for(; i < available && i < destination.length; i++)
			destination[i] = buffer[(int)(read_position++ % buffer.length)];

		// Fill the rest with zero, if any
		for(int j = i; j < destination.length; j++)
			destination[j] = 0;

		return i;
	}

	public int read(int dest_start, int length, float[] destination) { // TODO Test this
		long available = available();

		int i = 0;
		for(; i < available && i < destination.length && i < length; i++)
			destination[i + dest_start] = buffer[(int)(read_position++ % buffer.length)];

		// Fill the rest with zero, if any
		for(int j = i; j < destination.length; j++)
			destination[j + dest_start] = 0;

		return i;
	}

	/*
	 * Retruns the current available data
	 */
	public int available() {
		return (int)(write_position - read_position);
	}

	public int getFree() {
		return (int)(buffer.length - (write_position - read_position));
	}

	public int size() {
		return buffer.length;
	}

	public void clear() {
		read_position = 0;
		write_position = 0;
	}

	private void normalize() {
		long shave = read_position - (read_position % buffer.length);
		write_position -= shave;
		read_position -= shave;
	}

	public static void test() {
		FloatCircularBuffer ab = new FloatCircularBuffer(10);
		if(ab.getFree() != 10)
			throw new RuntimeException("Expected free to be 10");

		if(ab.available() != 0)
			throw new RuntimeException("Expected availble to be 0");

		float[] result = new float[5];

		if(ab.read(result) != 0)
			throw new RuntimeException("Should not read anything");

		ab.write(new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14});

		result = new float[2];
		if(ab.read(result) != 2)
			throw new RuntimeException("Expected to read only 2 floats");

		//System.out.println(result[0] + " " + result[1]);

		if(result[0] != 5 || result[1] != 6)
			throw new RuntimeException("Data read is wrong");

		if(ab.available() != 8)
			throw new RuntimeException("Expected 8 bytes data available");

		result = new float[]{10,11,12,13,14,15,16,17,18,19};
		ab.read(result);

		//for(int i = 0; i < result.length; i++)
		//	System.out.printf(result[i] + ", ");

		if(result[7] != 14 || result[8] != 0f)
			throw new RuntimeException();
	}
}
