package net.merayen.merasynth.audio;

/* 
 * Resizeable audio buffer.
 * Doesn't do channels and stuff, caller has to create a new buffer if this happens.
 * TODO When reading, maybe 
 */
public class AudioBuffer {
	private long write_position;
	private long read_position;
	private float[] buffer;

	public AudioBuffer(int size) {
		buffer = new float[size];
	}

	public void write(float[] data) {
		normalize();

		// Increment the read position (drop data) if buffer overflows (distance between read and write is bigger than the buffer)
		read_position += Math.max(0, data.length - getFree());
		for(int i = 0; i < data.length; i++) // TODO only write the part that actually fits us
			buffer[(int)((write_position++) % buffer.length)] = data[i]; // TODO Low performance with % ?
	}

	/*
	 * Copy data from us to the destination float array.
	 * Returns the floats actually copied.
	 */
	public int read(float[] destination) {
		long available = getAvailable();

		// Some checks. TODO remove when class really works right
		if(available < 0)
			throw new RuntimeException("AudioBuffer internal error: read_position is higher than write_position");

		if(available > buffer.length)
			throw new RuntimeException("AudioBuffer internal error: Too big distance between write_position and read_position (larger than buffer)");

		int i = 0;
		for(; i < available && i < destination.length; i++) {
			read_position++;
			destination[i] = buffer[(int)read_position];
		}

		return i;
	}

	/*
	 * Retruns the current available data
	 */
	public int getAvailable() {
		return (int)(write_position - read_position);
	}

	public long getFree() {
		return (int)(buffer.length - (write_position - read_position));
	}

	private void normalize() {
		long shave = read_position - (read_position % buffer.length);
		write_position -= shave;
		read_position -= shave;
	}

	public static void test() {
		AudioBuffer ab = new AudioBuffer(10);
		if(ab.getFree() != 10)
			throw new RuntimeException("Expected free to be 10");

		if(ab.getAvailable() != 0)
			throw new RuntimeException("Expected availble to be 0");

		float[] result = new float[5];

		if(ab.read(result) != 0)
			throw new RuntimeException("Should not read anything");

		ab.write(new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14});

		result = new float[2];
		if(ab.read(result) != 2)
			throw new RuntimeException("Expected to read only 2 floats");

		if(result[0] != 3 || result[1] != 4)
			throw new RuntimeException("Data read is wrong");

		
	}
}
