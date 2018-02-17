package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator_1;

import com.laszlosystems.libresample4j.Resampler;

import java.nio.FloatBuffer;

class ResamplingFactory {
	private final float[] waveIn;
	private final int sampleRate;
	private final Resampler resampler;

	ResamplingFactory(float[] waveIn, int sampleRate) {
		this.waveIn = waveIn;
		this.sampleRate = sampleRate;

		float sampleRatio = sampleRate / (float)waveIn.length;
		resampler = new Resampler(false, sampleRatio / Math.floor(sampleRate / 4f), sampleRatio / 4f );
		System.out.println(resampler.getFilterWidth());
	}

	Resampling create(float[] output) {
		return new Resampling(new Resampler(resampler), waveIn, output, sampleRate);
	}
}

class Resampling {
	private Resampler resampler;
	private final FloatBuffer in, out;
	private final int sampleRate;

	/**
	 * Access this float-array to retrieve output data.
	 */
	public final float[] waveOut;

	Resampling(Resampler resampler, float[] waveIn, float[] waveOut, int sampleRate) {
		this.resampler = resampler;
		this.sampleRate = sampleRate;

		in = FloatBuffer.wrap(waveIn);
		out = FloatBuffer.wrap(waveOut);
		this.waveOut = waveOut;
	}

	void update(float hz) {
		if(hz < 10) hz = 10;
		else if(hz > sampleRate / 4.1f) hz = sampleRate / 4.1f;

		while(out.position() != out.capacity()) {
			if(in.position() == in.capacity())
				in.rewind();

			resampler.process((sampleRate / (float)in.capacity()) / hz, in, false, out);
		}
	}

	void update(float hz, int count) {
		if(hz < 10) hz = 10;
		else if(hz > sampleRate / 4.1f) hz = sampleRate / 4.1f;

		int stop = out.position() + count;

		if(stop > waveOut.length)
			throw new RuntimeException("Output buffer will overflow");

		out.limit(stop);

		while(out.position() != out.limit()) {
			if(in.position() == in.capacity())
				in.rewind();

			resampler.process((sampleRate / (float)in.capacity()) / hz, in, false, out);
		}
	}

	void rewind() {
		out.rewind();
		out.limit(out.capacity());
	}

	public static void main(String[] asdf) {
		float[] in = new float[] {0,1,2,3,4,5,6,7,8,9};
		float[] out = new float[10];

		ResamplingFactory resamplingFactory = new ResamplingFactory(in, 10);
		Resampling resampling = resamplingFactory.create(out);
		resampling.update(2);

		for(float x : out)
			System.out.println(x);
	}
}
