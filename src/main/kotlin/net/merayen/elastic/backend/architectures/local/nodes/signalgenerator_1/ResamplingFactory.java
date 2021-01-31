package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator_1;

import com.laszlosystems.libresample4j.Resampler;

public class ResamplingFactory {
	private final float[] waveIn;
	private final int sampleRate;
	private final Resampler resampler;

	ResamplingFactory(float[] waveIn, int sampleRate) {
		this.waveIn = waveIn;
		this.sampleRate = sampleRate;

		float sampleRatio = sampleRate / (float)waveIn.length;
		resampler = new Resampler(false, sampleRatio / Math.floor(sampleRate / 4f), sampleRatio / 4f );
	}

	Resampling create(float[] output) {
		return new Resampling(new Resampler(resampler), waveIn, output, sampleRate);
	}
}

