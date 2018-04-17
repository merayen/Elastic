package net.merayen.elastic.backend.architectures.local.nodes.compressor_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.util.Postmaster;

public class LProcessor extends LocalProcessor {
	double amplitude = 1;
	//private final int PROBE_RESOLUTI0N = 1;
	private float[] maxAmplitudes;
	float[] amplitudes;

	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {
		if(maxAmplitudes == null)
			maxAmplitudes = new float[buffer_size]; // TODO rather clear
		else
			for(int i = 0; i < maxAmplitudes.length; i++)
				maxAmplitudes[i] = 0;

		if(amplitudes == null)
			amplitudes = new float[buffer_size];
	}

	@Override
	protected void onProcess() {
		Inlet in = getInlet("input");
		Inlet sidechain = getInlet("sidechain");
		Outlet out = getOutlet("output");

		if (in instanceof AudioInlet && out instanceof AudioOutlet) {
			AudioInlet input = (AudioInlet)in;
			AudioOutlet output = (AudioOutlet)out;
			LNode lnode = (LNode)getLocalNode();

			final int start = in.read;
			int stop = in.outlet.written;
			if(sidechain != null && sidechain.outlet.written < stop)
				stop = sidechain.outlet.written;

			int channelCount = input.outlet.getChannelCount();

			double attack = lnode.attack;
			double release = lnode.release;
			double threshold = lnode.threshold;
			float inputAmplitude = (float)lnode.inputAmplitude;
			float inputSidechainAmplitude = (float)lnode.inputSidechainAmplitude;
			float outputAmplitude = (float)lnode.outputAmplitude;

			output.setChannelCount(channelCount);

			double attackDiv = 1.442740497 * attack * sample_rate;
			double releaseDiv = 1.442740497 * release * sample_rate;

			// Analyze all channels for peak values TODO support RMS?
			if(sidechain == null) {
				for (int channel = 0; channel < channelCount; channel++) {
					float[] inAudio = input.outlet.audio[channel];

					for (int i = start; i < stop; i++) {
						float sample = Math.abs(inAudio[i]);

						if (sample > maxAmplitudes[i])
							maxAmplitudes[i] = sample;
					}
				}
			} else if(sidechain instanceof AudioInlet) {
				float[] inAudio = ((AudioInlet)sidechain).outlet.audio[0];

				for (int i = start; i < stop; i++) {
					float sample = Math.abs(inAudio[i]);

					if (sample > maxAmplitudes[i])
						maxAmplitudes[i] = sample * inputSidechainAmplitude;
				}
			}

			// Convert max amplitudes to correspondingly correction amplitudes
			for (int i = start; i < stop; i++) {
				float maxAmplitude = maxAmplitudes[i];

				if (maxAmplitude * amplitude > threshold || -maxAmplitude * amplitude < -threshold)
					amplitudes[i] = (float)(amplitude -= amplitude / attackDiv);
				else if (amplitude < 1)
					amplitudes[i] = (float)(amplitude += amplitude / releaseDiv);
				else
					amplitudes[i] = (float)amplitude;
			}

			// Apply compression
			for(int channel = 0; channel < channelCount; channel++) {
				float[] inAudio = input.outlet.audio[channel];
				float[] outAudio = output.audio[channel];

				for (int i = start; i < stop; i++)
					outAudio[i] = inAudio[i] * amplitudes[i] * outputAmplitude;
			}

			if(amplitude < 0.001)
				amplitude = 0.001;

			in.read = stop;
			output.written = stop;
			output.push();

			if(sidechain != null)
				sidechain.read = stop;
		} else {
			if (in != null) {
				in.read = buffer_size;
			}
			if (out != null) {
				out.written = buffer_size;
				out.push();
			}
			if(sidechain != null)
				sidechain.read = buffer_size;
		}
	}

	@Override
	protected void onMessage(Postmaster.Message message) {}

	@Override
	protected void onDestroy() {}
}