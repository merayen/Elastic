package net.merayen.elastic.backend.architectures.local.nodes.adsr_1;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ADSR {
	static class Entry {
		final long position;
		final float state;

		Entry(long position, float state) {
			this.position = position;
			this.state = state;
		}
	}

	private float amplitude;

	/**
	 * How many amplitude points to emit.
	 */
	private int division;

	private final int attack;
	private final int decay;
	private final float sustain;
	private final int release;

	private long attackStart = -1;
	private long decayStart = -1;
	private long releaseStart = -1;
	private float releaseAmplitude;

	private Deque<Entry> input = new ArrayDeque<>();

	private long time;
	private long lastTime;

	ADSR(int division, int attack, int decay, float sustain, int release) {
		this.division = division;
		this.attack = attack;
		this.decay = decay;
		this.sustain = sustain;
		this.release = release;
	}

	void push(long position, int state) {
		if(position < lastTime || position < time)
			throw new RuntimeException("invalid position");

		lastTime = position;

		input.add(new Entry(position, state));
	}

	List<Entry> process(int sampleCount) {
		List<Entry> result = new ArrayList<>();

		long start = time;
		long stop = time = start + sampleCount;

		for(long i = start; i < stop; i += division) {
			while (!input.isEmpty() && input.getFirst().position <= i)
				processMidi(input.pop());

			updateState(i);

			result.add(new Entry(i, amplitude));
		}

		return result;
	}

	boolean isNeutral() {
		if(attackStart == -1 && releaseStart == -1)
			return true;

		return releaseStart != -1 && time >= releaseStart + release;
	}

	private void processMidi(Entry entry) {
		if(entry.state > 0) {
			attackStart = entry.position;
			decayStart = entry.position + attack;
			releaseStart = -1;
		} else if(entry.state < 0) {
			attackStart = -1;
			decayStart = -1;
			releaseStart = entry.position;
			releaseAmplitude = amplitude;
		}
	}

	private void updateState(long position) {
		if(releaseStart != -1 && releaseStart + release <= position) {
			amplitude = 0;
		} else if(releaseStart != -1 && releaseStart <= position) {
			amplitude = ((releaseStart + release) - position) / (float) release * releaseAmplitude;
		} else if(decayStart != -1 && decayStart + decay <= position) {
			amplitude = sustain;
		} else if(decayStart != -1 && decayStart <= position) {
			amplitude = 1 - ((position - decayStart) / (float)decay) * (1 - sustain);
		} else if(attackStart != -1 && attackStart < position) {
			amplitude = 1 - Math.min(1, (decayStart - position) / (float)(decayStart - attackStart));
		} else {
			amplitude = 0;
		}
	}

	public static void test() {
		ADSR adsr = new ADSR(5, 10, 5, 0.5f, 10);
		adsr.push(10, 1);
		adsr.push(30, -1);

		for(Entry e : adsr.process(41))
			System.out.println(e.position + " " + e.state);
	}
}
