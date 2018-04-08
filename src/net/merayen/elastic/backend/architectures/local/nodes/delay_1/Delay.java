package net.merayen.elastic.backend.architectures.local.nodes.delay_1;

import java.util.ArrayList;
import java.util.List;

public class Delay {
	public static class Tap {
		final int position;
		float amplitude;
		float feedback;

		public Tap(int position, float amplitude, float feedback) {
			this.position = position;
			this.amplitude = amplitude;
			this.feedback = feedback;
		}
	}

	public final float buffer[];

	private int position;

	private final List<Tap> taps = new ArrayList<>();

	public Delay(int width) {
		buffer = new float[width];
	}

	public void addTap(Tap tap) {
		if(tap.position >= buffer.length)
			throw new RuntimeException("Tap is outside the buffer constraints");

		taps.add(tap);
	}

	public int process(float[] data, final int start, final int stop) {
		if(start > stop)
			throw new RuntimeException("Invalid start/stop");

		// Clear the part of the buffer we will use
		for(int i = 0; i < stop - start; i++)
			buffer[(position + buffer.length - (stop - start) + i) % buffer.length] = 0; // TODO reduce complexity? Performance?

		// Apply all taps
		for (Tap tap : taps) {
			if(tap.position + (stop - start) > buffer.length)
				throw new RuntimeException("Too small buffer (would do a circular overflow to the buffer)");

			int p = position + tap.position;
			int u = position;
			float amplitude = tap.amplitude;
			float feedback = tap.feedback;

			for (int i = start; i < stop; i++) {
				buffer[p++ % buffer.length] += (data[i] * amplitude) + (buffer[u++ % buffer.length] * feedback);
			}
		}

		position %= buffer.length;

		int readPosition = position;
		position += stop - start;

		// Return the position for the caller to read from
		return readPosition;
	}

	private static void no() {
		throw new RuntimeException("Nope");
	}

	public static void test() {
		Delay delay = new Delay(10 + 2);
		delay.addTap(new Tap(0, 1, 1));
		delay.addTap(new Tap(2, 1, 1));

		if(delay.process(new float[]{0,1,2,3,4,5,6,7,8,9}, 0, 10) != 0)
			no();

		float[] fasit = new float[]{0, 1, 2+0, 3+1, 4+2, 5+3, 6+4, 7+5, 8+6, 9+7};
		float[] result = delay.buffer;

		for(int i = 0; i < fasit.length; i++)
			if(fasit[i] != result[i])
				no();
	}
}