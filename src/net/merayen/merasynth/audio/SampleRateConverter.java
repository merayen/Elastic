package net.merayen.merasynth.audio;

public class SampleRateConverter {
	/*
	 * Converts a float audio array from one sample rate to another one.
	 * Stupid, non-dithering converting.
	 * TODO Really needs to be tested.
	 */
	public static float[] convert(float[] data, int channels, int source_sample_rate, int dest_sample_rate) {
		float[] result = new float[channels * (int)((double)dest_sample_rate / source_sample_rate) * data.length];

		for(int iChannel = 0; iChannel <= channels; iChannel++) {
			for(int i = iChannel; i < result.length; i += channels) {
				// TODO
			}
		}

		return result;
	}
}
